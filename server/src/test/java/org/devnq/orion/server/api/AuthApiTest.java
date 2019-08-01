package org.devnq.orion.server.api;

import com.jcabi.http.Response;
import org.devnq.orion.server.ApiTest;
import org.devnq.orion.server.crypt.Tokens;
import org.devnq.orion.server.models.ImmutableLogin;
import org.devnq.orion.server.models.Login;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.devnq.orion.server.Generators.genLogin;
import static org.devnq.orion.server.Generators.genNewUser;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401;

@DisplayName("/api/auth")
class AuthApiTest extends ApiTest {

    @Nested
    @DisplayName("Login")
    class LoginTests {

        @Nested
        @DisplayName("results in UNAUTHORIZED")
        class UnauthorizedTests {

            @Test
            @DisplayName("if they are unknown")
            void unknownUserTest() throws IOException {
                //Arrange
                final Login login = genLogin().build();

                //Act
                final Response response = postCredentials(login);

                //Assert
                assertThat(response.status()).isEqualTo(UNAUTHORIZED_401);
            }

            @Test
            @DisplayName("if the credentials do not match")
            void nonMatchingPasswordTest() throws IOException {
                //Arrange
                final User user = genNewUser().build();
                final Response createResponse = postNewUser(user);
                assertThat(createResponse.status()).isEqualTo(CREATED_201);
                final Login invalidCredentials = ImmutableLogin.builder()
                    .username(user.username())
                    .password(user.password() + "1")
                    .build();

                //Act
                final Response response = postCredentials(invalidCredentials);

                //Assert
                assertThat(response.status()).isEqualTo(UNAUTHORIZED_401);
            }
        }

        @Nested
        @DisplayName("results in OK with token")
        class SuccessfulTests {

            @Test
            @DisplayName("valid credentials")
            void validCredentialsTest() throws IOException {
                //Arrange
                final User user = genNewUser().build();
                final Login credentials = Login.valueOf(user);
                final Response createResponse = postNewUser(user);
                assertThat(createResponse.status()).isEqualTo(CREATED_201);

                //Act
                final Response response = postCredentials(credentials);

                //Assert
                assertThat(response.status()).isEqualTo(OK_200);
                assertThat(extractToken(response)).isNotEmpty();
            }

            @Test
            @DisplayName("case insensitive username")
            void usernameIsCaseInsensitive() throws IOException {
                //Arrange
                final User user = genNewUser().build();
                final Response createResponse = postNewUser(user);
                assertThat(createResponse.status()).isEqualTo(CREATED_201);
                final Login credentials = ImmutableLogin.builder()
                    .username(user.username().toUpperCase())
                    .password(user.password())
                    .build();

                //Act
                final Response response = postCredentials(credentials);

                //Assert
                assertThat(response.status()).isEqualTo(OK_200);
                assertThat(extractToken(response)).isNotEmpty();
            }
        }
    }

    @Nested
    @DisplayName("Get")
    class GetTests {

        @Test
        @DisplayName("results in UNAUTHORIZED if the token is invalid")
        void invalidTokenTest() throws IOException {

            final Response createResponse = authenticatedGet("thisTokenIsInvalid", AUTH_URL);

            assertThat(createResponse.status()).isEqualTo(UNAUTHORIZED_401);
        }

        @Test
        @DisplayName("results in UNAUTHORIZED if the token doesn't match a user")
        void unknownUserId() throws IOException {
            final UUID userId = randomUUID();
            final Tokens tokens = getInjector().getBinding(Tokens.class).getProvider().get();
            final String token = tokens.encode(userId);

            final Response createResponse = authenticatedGet(token, AUTH_URL);

            assertThat(createResponse.status()).isEqualTo(UNAUTHORIZED_401);
        }

        @Test
        @DisplayName("results in OK with the users details")
        void knownUser() throws IOException {
            final User user = genNewUser().build();
            final Response response = postNewUser(user);
            assertThat(response.status()).isEqualTo(CREATED_201);
            final Response loginResponse = postCredentials(Login.valueOf(user));
            final String token = extractToken(loginResponse);

            final Response createResponse = authenticatedGet(token, AUTH_URL);

            assertThat(createResponse.status()).isEqualTo(OK_200);
            final User actual = mapper.readValue(response.body(), User.class);
            assertThat(actual).isEqualToIgnoringGivenFields(user, "password", "id");
            assertThat(actual.id()).isInstanceOf(UUID.class);
        }
    }
}
