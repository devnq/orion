package org.devnq.orion.server.etc;

import ninja.Context;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.devnq.orion.server.Generators.genNewUser;
import static org.devnq.orion.server.filters.AuthenticationFilter.AUTH_USER_ATTRIBUTE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserExtractorTest {
    private final Context context = mock(Context.class);
    private final AuthUserExtractor extractor = new AuthUserExtractor();

    @Test
    @DisplayName("Throws UnauthorizedException if it cannot get the user from the context attributes")
    void noUserFoundTest() {

        //Act
        final Throwable thrown = catchThrowable(() -> extractor.extract(context));

        //Assert
        assertThat(thrown).isInstanceOf(UnauthorizedException.class);
        verify(context).getAttribute(AUTH_USER_ATTRIBUTE);
    }

    @Test
    @DisplayName("Returns the user from the context attributes")
    void userFoundTest() {
        //Arrange
        final User user = genNewUser().build();
        when(context.getAttribute(AUTH_USER_ATTRIBUTE)).thenReturn(user);

        //Act
        final User actual = extractor.extract(context);

        //Assert
        assertThat(actual).isEqualTo(user);
        verify(context).getAttribute(AUTH_USER_ATTRIBUTE);
    }
}
