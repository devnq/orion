package org.devnq.orion.server.json;

import com.github.javafaker.Faker;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.fail;

@DisplayName("Schema - NewUser.json")
class LoginSchemaTest {

    Schema schema;
    final Faker faker = new Faker();
    final JSONObject validObject = new JSONObject()
        .put("username", faker.name().username())
        .put("password", faker.internet().password());

    @BeforeEach
    void setUp() {
        schema = new Schema();
    }

    @Test
    @DisplayName("The validObject should pass the JsonSchema")
    void validObjectTest() {

        final Throwable actual = catchThrowable(() -> validate(validObject));

        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("The username should be provided as a string without length restrictions")
    void usernameTest() {
        fail("FIXME");
    }

    @Test
    @DisplayName("The password should be provided as a string without length restrictions")
    void passwordTest() {
        fail("FIXME");
    }

    private void validate(final JSONObject object) {
        schema.validate("Login", object);
    }
}