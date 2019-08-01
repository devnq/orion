package org.devnq.orion.server.filters;

import com.auth0.jwt.exceptions.JWTDecodeException;
import ninja.Context;
import ninja.FilterChain;
import ninja.Route;
import org.devnq.orion.server.crypt.Tokens;
import org.devnq.orion.server.dal.UserDal;
import org.devnq.orion.server.etc.Authenticated;
import org.devnq.orion.server.etc.UnauthorizedException;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.devnq.orion.server.Generators.genNewUser;
import static org.devnq.orion.server.filters.AuthenticationFilter.AUTH_USER_ATTRIBUTE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    private final UserDal userDal = mock(UserDal.class);
    private final Context context = mock(Context.class);
    private final FilterChain filterChain = mock(FilterChain.class);
    private final Tokens tokens = new Tokens("secret");
    private final AuthenticationFilter filter;

    AuthenticationFilterTest() {
        filter = new AuthenticationFilter(tokens, userDal);
    }

    @Nested
    @DisplayName("Throws UnauthorizedException when the annotation is present and")
    class UnauthorizedTests {

        @Test
        @DisplayName("there is no authorization header")
        void noAuthorizationHeader() {
            //Arrange
            setAuthenticated(true);

            //Act
            final Throwable thrown = catchThrowable(() -> filter.filter(filterChain, context));

            //Assert
            assertThat(thrown).isInstanceOf(UnauthorizedException.class);
            verify(context).getRoute();
            verify(context).getHeader("authorization");
            verifyZeroInteractions(userDal, context, filterChain);
        }

        @Test
        @DisplayName("the token prefix is invalid")
        void invalidTokenPrefixTest() {
            //Arrange
            final UUID userId = randomUUID();
            final String token = tokens.encode(userId);
            setAuthenticated(true);
            when(context.getHeader("authorization")).thenReturn(token);

            //Act
            final Throwable thrown = catchThrowable(() -> filter.filter(filterChain, context));

            //Assert
            assertThat(thrown).isInstanceOf(UnauthorizedException.class);
            verify(context).getRoute();
            verify(context).getHeader("authorization");
            verifyZeroInteractions(userDal, context, filterChain);
        }

        @Test
        @DisplayName("the token is invalid")
        void invalidTokenTest() {
            //Arrange
            final String token = "thisIsNotAValidString";
            setAuthenticated(true);
            when(context.getHeader("authorization")).thenReturn("bearer " + token);

            //Act
            final Throwable thrown = catchThrowable(() -> filter.filter(filterChain, context));

            //Assert
            assertThat(thrown).isInstanceOf(JWTDecodeException.class);
            verify(context).getRoute();
            verify(context).getHeader("authorization");
            verifyZeroInteractions(userDal, context, filterChain);
        }

        @Test
        @DisplayName("the token does not match a user")
        void unknownUserTest() {
            //Arrange
            final UUID userId = randomUUID();
            final String token = tokens.encode(userId);
            setAuthenticated(true);
            when(context.getHeader("authorization")).thenReturn("bearer " + token);

            //Act
            final Throwable thrown = catchThrowable(() -> filter.filter(filterChain, context));

            //Assert
            assertThat(thrown).isInstanceOf(UnauthorizedException.class);
            verify(context).getRoute();
            verify(context).getHeader("authorization");
            verify(userDal).find(userId);
            verifyZeroInteractions(userDal, context, filterChain);
        }
    }

    @Test
    @DisplayName("Sets the user as a an attribute on the context")
    void knownUserTest() {
        //Arrange
        final UUID userId = randomUUID();
        final User user = genNewUser().build();
        final String token = tokens.encode(userId);
        setAuthenticated(true);
        when(context.getHeader("authorization")).thenReturn("bearer " + token);
        when(userDal.find(userId)).thenReturn(user);

        //Act
        filter.filter(filterChain, context);

        //Assert
        verify(context).getRoute();
        verify(context).getHeader("authorization");
        verify(userDal).find(userId);
        verify(context).setAttribute(AUTH_USER_ATTRIBUTE, user);
        verify(filterChain).next(context);
        verifyZeroInteractions(userDal, context, filterChain);
    }

    @Test
    @DisplayName("Goes straight to the next in the filter chain if no annotation is present ")
    void noAnnotationTest() {
        //Arrange
        setAuthenticated(false);

        //Act
        filter.filter(filterChain, context);

        //Assert
        verify(context).getRoute();
        verify(filterChain).next(context);
        verifyZeroInteractions(userDal, context, filterChain);
    }

    private void setAuthenticated(final boolean authenticated) {
        final Route route = mock(Route.class);
        final Method method;
        try {
            method = authenticated ?
                AuthenticationFilterTest.class.getMethod("authenticated") :
                AuthenticationFilterTest.class.getMethod("notAuthenticated");
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException("Unable to specify method for tests", e);
        }
        when(context.getRoute()).thenReturn(route);
        when(route.getControllerMethod()).thenReturn(method);
    }

    @SuppressWarnings("WeakerAccess")
    @Authenticated
    public static void authenticated() {}

    @SuppressWarnings("WeakerAccess")
    public static void notAuthenticated() {}
}