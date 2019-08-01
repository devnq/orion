package org.devnq.orion.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.http.response.JsonResponse;
import ninja.utils.NinjaTestServer;
import org.apache.http.HttpHeaders;
import org.devnq.orion.server.models.Login;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.annotation.Nullable;
import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.jcabi.http.Request.GET;
import static com.jcabi.http.Request.POST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class ApiTest {
    protected NinjaTestServer ninjaTestServer;
    protected final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(NON_NULL);
    protected static final String USERS_URL = "/api/users";
    protected static final String AUTH_URL = "/api/auth";

    public Injector getInjector() {
        return ninjaTestServer.getInjector();
    }

    protected Request requestTo(final String path) {
        return new ApacheRequest(ninjaTestServer.getServerUrl())
            .method(GET)
            .uri().path(path).back()
            .header(HttpHeaders.ACCEPT, APPLICATION_JSON);
    }

    @AfterEach
    public void shutdownServerAndBrowser() {
        ninjaTestServer.shutdown();
    }

    @BeforeEach
    public void startupServerAndBrowser() {
        ninjaTestServer = new NinjaTestServer();
    }

    protected Response postNewUser(@Nullable final User user) throws IOException {
        final String json = mapper.writeValueAsString(user);
        return requestTo(USERS_URL)
            .method(POST)
            .body()
            .set(json)
            .back()
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .fetch();
    }

    protected Response postCredentials(@Nullable final Login credentials) throws IOException {
        final String json = mapper.writeValueAsString(credentials);
        return requestTo(AUTH_URL)
            .method(POST)
            .body()
            .set(json)
            .back()
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .fetch();
    }

    protected Response authenticatedGet(final String token, final String url) throws IOException {
        return requestTo(url)
            .method(GET)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header("authorization", "bearer " + token)
            .fetch();
    }

    protected String extractToken(final Response loginResponse) {
        return loginResponse.as(JsonResponse.class).json().readObject().getString("token");
    }

    protected String authToken(final User user) throws IOException {
        final Response loginResponse = postCredentials(Login.valueOf(user));
        return extractToken(loginResponse);
    }
}
