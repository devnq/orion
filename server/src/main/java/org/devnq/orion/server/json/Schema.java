package org.devnq.orion.server.json;

import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableMap.of;
import static java.text.MessageFormat.format;
import static java.util.stream.Stream.concat;
import static org.devnq.orion.server.etc.Strings.nvl;

public class Schema {

    public void validate(final String schema, final JSONObject obj) {
        final String schemaFile = format("/json-schema/{0}.json", schema);
        try (final InputStream inputStream = getClass().getResourceAsStream(schemaFile)) {
            final JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            SchemaLoader.load(rawSchema).validate(obj);
        } catch (final IOException e) {
            throw new RuntimeException("Error loading schema to validate against.", e);
        }
    }

    private static Map extractError(final ValidationException ex) {
        return of("message", ex.getErrorMessage(),
                  "schema", ex.getSchemaLocation(),
                  "keyword", nvl(ex.getKeyword(), ""),
                  "path", ex.getPointerToViolation());
    }

    private static Stream<ValidationException> extractErrors(final ValidationException ex) {
        return concat(Stream.of(ex),
                      ex.getCausingExceptions()
                          .stream()
                          .flatMap(Schema::extractErrors));
    }

    public static List<Map> errorsOf(final ValidationException ex) {
        return extractErrors(ex)
            .map(Schema::extractError)
            .collect(Collectors.toList());
    }
}
