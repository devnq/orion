package org.devnq.orion.server.dal;

import com.google.inject.Inject;
import ninja.jdbi.NinjaJdbi;
import org.devnq.orion.server.DalTest;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.devnq.orion.server.Generators.genNewUser;

class UserDalTest extends DalTest {

    @Inject
    private final UserDal dal;

    UserDalTest() {
        init();
        final NinjaJdbi instance = getInstance(NinjaJdbi.class);
        dal = new UserDal(instance);
    }

    @Test
    @DisplayName("Inserts a user and returns an updated User object with an id")
    void insert() {
        //Arrange
        final User user = genNewUser().build();

        //Act
        final User actual = dal.insert(user);

        //Assert
        assertThat(actual).isEqualToIgnoringGivenFields(user, "password", "id");
        assertThat(actual.id()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Duplicate inserts of the same username should thrown an exception")
    void insertDuplicate() {
        //Arrange
        final User user = genNewUser().build();

        //Act
        dal.insert(user);
        final Throwable thrown = catchThrowable(() -> dal.insert(user));

        //Assert
        assertThat(thrown).isInstanceOf(UnableToExecuteStatementException.class);
    }

    @Test
    @DisplayName("True if the username is available")
    void isUsernameAvailableTest() {
        //Arrange
        final User user = genNewUser().build();
        final String username = user.username();
        assertThat(dal.isUsernameAvailable(username)).isTrue();
        dal.insert(user);

        //Act
        final boolean actual = dal.isUsernameAvailable(username);

        //Assert
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("True if the password matches the hashed value")
    void validatePassword() {
        //Arrange
        final User user = genNewUser().build();
        final String username = user.username();
        final String password = user.password();
        dal.insert(user);

        //Act
        boolean actual = dal.validatePassword(username, password);

        //Assert
        assertThat(actual).isTrue();

        //Act again
        actual = dal.validatePassword(username, password + "invalidation");

        //Assert again
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Validate password should return false if the user does not exist")
    void validatePasswordNoMatchingUsername() {
        final String username = "AUserThatDoesn'tExist";
        assertThat(dal.isUsernameAvailable(username)).isTrue();

        final boolean actual = dal.validatePassword(username, "Anything");

        assertThat(actual).isFalse();
    }

    @Test
    void find() {
        //Arrange
        final User user = genNewUser().build();
        final UUID id = dal.insert(user).id();

        //Act
        final User actual = dal.find(id);

        //Assert
        assertThat(actual).isEqualToIgnoringGivenFields(user, "id", "password");
        assertThat(actual.id()).isEqualTo(id);
    }

    @Test
    void findId() {
        //Arrange
        final User user = genNewUser().build();
        final String username = user.username();
        final UUID id = dal.insert(user).id();

        //Act
        final UUID actual = dal.findId(username);

        //Assert
        assertThat(actual).isEqualTo(id);
    }
}