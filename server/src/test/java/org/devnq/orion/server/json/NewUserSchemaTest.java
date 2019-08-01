package org.devnq.orion.server.json;

import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.devnq.orion.server.json.Util.assoc;
import static org.devnq.orion.server.json.Util.dissoc;

@DisplayName("Schema - NewUser.json")
class NewUserSchemaTest {

    Schema schema;
    final JSONObject validObject = new JSONObject()
        .put("name", "Alexander Scott")
        .put("username", "axrs")
        .put("password", "Secret");

    @BeforeEach
    void setUp() {
        schema = new Schema();
    }

    @Test
    @DisplayName("The validObject should pass the JsonSchema")
    void validObjectTest() {

        final Throwable thrown = catchThrowable(() -> validate(validObject));

        assertThat(thrown).isNull();
    }

    @Nested
    @DisplayName("Username validation")
    class Username extends BasicStringValidation {
        @BeforeEach
        void setUp() {
            setKey("username");
        }
    }

    @Nested
    @DisplayName("Name validation")
    class Name extends BasicStringValidation {
        @BeforeEach
        void setUp() {
            setKey("name");
        }
    }

    @Nested
    @DisplayName("Password validation")
    class Password extends BasicStringValidation {
        @BeforeEach
        void setUp() {
            setKey("password");
        }
    }

    class BasicStringValidation {
        private String key;
        private String path;

        void setKey(final String key) {
            this.key = key;
            path = "#/" + key;
        }

        @Test
        @DisplayName("is missing")
        void missing() {
            final JSONObject obj = dissoc(validObject, key);

            assertValidationErrorMessage(obj,
                                         "#",
                                         format("required key [{0}] not found", key));
        }

        @Test
        @DisplayName("is the wrong type")
        void wrongType() {
            final JSONObject obj = assoc(validObject, key, 123);

            assertValidationErrorMessage(obj,
                                         path,
                                         "expected type: String, found: Integer");
        }

        @Test
        @DisplayName("is too short")
        void minLength() {
            final JSONObject obj = assoc(validObject, key, "a");

            assertValidationErrorMessage(obj,
                                         path,
                                         "expected minLength: 2, actual: 1");
        }

        @Test
        @DisplayName("is too long")
        void tooLong() {
            final JSONObject obj = assoc(validObject, key, repeat("a", 257));

            assertValidationErrorMessage(obj,
                                         path,
                                         "expected maxLength: 256, actual: 257");
        }
    }

    private void assertValidationErrorMessage(final JSONObject obj, final String path, final String expectedMessage) {

        // ACT
        final Throwable thrown = catchThrowable(() -> validate(obj));

        // ASSERT
        assertThat(thrown).isInstanceOf(ValidationException.class);
        final List<Map> errors = Schema.errorsOf((ValidationException) thrown);
        final boolean foundError = errors
            .parallelStream()
            .anyMatch(e -> e.get("message").equals(expectedMessage) && e.get("path").equals(path));
        assertThat(foundError)
            .as("Expecting to find '%s' in the error messages", expectedMessage)
            .isTrue();
    }

    private void validate(final JSONObject object) {
        schema.validate("NewUser", object);
    }
}