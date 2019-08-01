package org.devnq.orion.server.filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;

public class CorsFilter implements Filter {

    @Override
    public Result filter(final FilterChain filterChain, final Context context) {
        final Result next = filterChain.next(context);
        return addCorsHeaders(next);
    }

    public static Result addCorsHeaders(final Result next) {
        return next
            .addHeader("Access-Control-Allow-Origin", "*")
            .addHeader("Access-Control-Allow-Headers", "authorization,accept,content-type")
            .addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, OPTIONS, DELETE");
    }
}
