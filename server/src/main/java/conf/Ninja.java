package conf;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.google.common.collect.ImmutableMap;
import ninja.Context;
import ninja.NinjaDefault;
import ninja.Result;
import ninja.Results;
import org.devnq.orion.server.etc.UnauthorizedException;
import org.devnq.orion.server.json.Schema;
import org.everit.json.schema.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static ninja.Result.APPLICATION_JSON;
import static ninja.Result.APPLICATION_XML;
import static org.devnq.orion.server.etc.Strings.nvl;
import static org.devnq.orion.server.filters.CorsFilter.addCorsHeaders;
import static org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401;
import static org.eclipse.jetty.http.HttpStatus.UNPROCESSABLE_ENTITY_422;

public class Ninja extends NinjaDefault {
    private static final Logger logger = LoggerFactory.getLogger(Ninja.class);

    @Override
    public Result getInternalServerErrorResult(final Context context,
                                               final Exception exception,
                                               final Result underlyingResult) {
        if (exception instanceof ValidationException) {
            return jsonSchemaValidationError(context, (ValidationException) exception);
        } else if (exception instanceof UnauthorizedException) {
            return unauthorizedError(context, exception);
        } else if (exception instanceof JWTDecodeException) {
            return unauthorizedError(context, exception);
        } else {
            return internalServerError(context, exception);
        }
    }

    private static Result unauthorizedError(final Context context, final Exception exception) {
        logger.warn(
            "Emitting unauthorized exception 401. Failed for route: {} (method: {})",
            context.getRequestPath(),
            context.getRoute().getControllerMethod(),
            exception);
        final ImmutableMap<String, String> error = of("message", "Invalid or missing access token.",
                                                      "path", "#");
        final Result response = Results
            .status(UNAUTHORIZED_401)
            .supportedContentTypes(APPLICATION_JSON, APPLICATION_XML)
            .fallbackContentType(APPLICATION_JSON)
            .render(of("errors", singleton(error)));
        return addCorsHeaders(response);
    }

    private static Result jsonSchemaValidationError(final Context context, final ValidationException exception) {
        logger.warn(
            "Emitting unprocessable entity 422. JSONSchema validation failed for route: {} (method: {}, schema: {})",
            context.getRequestPath(),
            context.getRoute().getControllerMethod(),
            exception.getSchemaLocation(),
            exception);
        final List<Map> errors = Schema.errorsOf(exception);
        final Result response = Results
            .status(UNPROCESSABLE_ENTITY_422)
            .supportedContentTypes(APPLICATION_JSON, APPLICATION_XML)
            .fallbackContentType(APPLICATION_JSON)
            .render(of("errors", errors));
        return addCorsHeaders(response);
    }

    private static Result internalServerError(final Context context, final Exception exception) {
        logger.error(
            "Emitting bad request 500. Something really wrong when calling route: {} (class: {} method: {})",
            context.getRequestPath(),
            context.getRoute().getControllerClass(),
            context.getRoute().getControllerMethod(),
            exception);
        final ImmutableMap<String, String> error = of("message", nvl(exception.getMessage(), "Unknown Error."));
        final Result response = Results
            .internalServerError()
            .supportedContentTypes(APPLICATION_JSON, APPLICATION_XML)
            .fallbackContentType(APPLICATION_JSON)
            .render(of("errors", singletonList(error)));
        return addCorsHeaders(response);
    }

    @Override
    public Result onException(final Context context, final Exception exception) {
        return super.onException(context, exception)
            .supportedContentTypes(APPLICATION_JSON, APPLICATION_XML)
            .fallbackContentType(APPLICATION_JSON);
    }

    @Override
    public Result onException(final Context context, final Exception exception, final Result underlyingResult) {
        final Result result = super.onException(context, exception, underlyingResult)
            .supportedContentTypes(APPLICATION_JSON, APPLICATION_XML)
            .fallbackContentType(APPLICATION_JSON);
        return addCorsHeaders(result);
    }
}
