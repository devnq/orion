package org.devnq.orion.server.api;

import com.jcabi.http.Response;
import com.jcabi.http.response.JsonResponse;
import org.devnq.orion.server.ApiTest;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.devnq.orion.server.Generators.genNewUser;
import static org.eclipse.jetty.http.HttpStatus.CONFLICT_409;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.UNPROCESSABLE_ENTITY_422;

@DisplayName("/api/users")
class UsersApiTest extends ApiTest {

    @Test
    @DisplayName("New user requests are validated with JsonSchema")
    void postNewUserValidation() throws IOException {

        //Act
        final Response response = postNewUser(null);

        //Assert
        assertThat(response.status()).isEqualTo(UNPROCESSABLE_ENTITY_422);
        final JsonArray errors = response.as(JsonResponse.class)
            .json()
            .readObject()
            .getJsonArray("errors");
        final boolean foundError = errors
            .parallelStream()
            .map(o -> ((JsonObject) o).getString("message"))
            .anyMatch(s -> s.equals("required key [name] not found"));
        assertThat(foundError).isTrue();
    }

    @Test
    @DisplayName("Creating a new user will result in a CREATED statusCode, returning the user user")
    void postNewUser() throws IOException {
        //Arrange
        final User newUser = genNewUser().build();

        //Act
        final Response fetch = postNewUser(newUser);

        //Assert
        assertThat(fetch.status()).isEqualTo(CREATED_201);
        assertThat(fetch.body()).doesNotContain("password");
        final User actual = mapper.readValue(fetch.body(), User.class);
        assertThat(actual).isEqualToIgnoringGivenFields(newUser, "id", "password");
        assertThat(actual.id()).isInstanceOf(UUID.class);
        assertThat(actual.password()).isNull();
    }

    @Test
    @DisplayName("Username must be unique. A CONFLICT status code is given if the username is already taken")
    void usernameMustBeUnique() throws IOException {
        //Arrange
        final User newUser = genNewUser().build();
        final Response createResponse = postNewUser(newUser);
        assertThat(createResponse.status()).isEqualTo(CREATED_201);

        //Act
        final Response conflictResponse = postNewUser(newUser);

        //Assert
        assertThat(conflictResponse.status()).isEqualTo(CONFLICT_409);
    }
}
