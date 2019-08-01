package conf;

import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;
import org.devnq.orion.server.api.Auth;
import org.devnq.orion.server.api.Events;
import org.devnq.orion.server.api.Users;

import static org.devnq.orion.server.filters.CorsFilter.addCorsHeaders;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.eclipse.jetty.http.HttpStatus.OK_200;

public class Routes implements ApplicationRoutes {

    @Inject
    NinjaProperties ninjaProperties;

    @Override
    public void init(final Router router) {
        //
        //AUTH
        //
        router.GET().route("/api/auth").with(Auth::get);
        router.POST().route("/api/auth").with(Auth::post);

        //
        //EVENTS
        //
        router.GET().route("/api/events").with(Events::get);
        router.POST().route("/api/events").with(Events::post);

        //
        //USERS
        //
        router.POST().route("/api/users").with(Users::post);

        //
        //MISC
        //
        router.GET().route("/.*").with(routes -> notFound());
        router.OPTIONS().route("/.*").with(routes -> cors());
    }

    private static Result cors() {
        final Result response = Results
            .status(OK_200)
            .render(Result.NO_HTTP_BODY);
        return addCorsHeaders(response);
    }

    private static Result notFound() {
        return Results
            .status(NOT_FOUND_404);
    }
}
