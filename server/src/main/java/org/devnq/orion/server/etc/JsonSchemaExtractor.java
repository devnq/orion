package org.devnq.orion.server.etc;

import com.google.inject.Inject;
import ninja.Context;
import ninja.params.ArgumentExtractor;
import org.devnq.orion.server.json.Schema;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JsonSchemaExtractor implements ArgumentExtractor<JSONObject> {
    private static final Logger logger = LoggerFactory.getLogger(JsonSchemaExtractor.class);

    private final Schema schema;

    @Inject
    public JsonSchemaExtractor(final Schema schema) {
        this.schema = schema;
    }

    @Override
    public JSONObject extract(final Context context) {
        final Map map = context.parseBody(Map.class);
        final JSONObject obj;
        obj = nonNull(map) ? (JSONObject) JSONObject.wrap(map) : new JSONObject();
        final JsonBodySchema annotation = context.getRoute()
            .getControllerMethod()
            .getAnnotation(JsonBodySchema.class);
        if (isNull(annotation)) {
            logger.warn("No defined schema to validate against for route: {}. The JSONObject is unvalidated.",
                        context.getRoute().getUri());
        } else {
            schema.validate(annotation.schema(), obj);
        }
        return obj;
    }

    @Override
    public Class<JSONObject> getExtractedType() {
        return JSONObject.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }
}
