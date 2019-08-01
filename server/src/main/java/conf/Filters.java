package conf;

import ninja.Filter;
import ninja.application.ApplicationFilters;
import org.devnq.orion.server.filters.AuthenticationFilter;
import org.devnq.orion.server.filters.CorsFilter;

import java.util.List;

public class Filters implements ApplicationFilters {

    @Override
    public void addFilters(final List<Class<? extends Filter>> filters) {
        filters.add(CorsFilter.class);
        filters.add(AuthenticationFilter.class);
    }
}
