package conf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ninja.flyway.NinjaFlyway;
import ninja.jdbi.NinjaJdbiModule;

@Singleton
public class Module extends AbstractModule {
    @Override
    protected void configure() {
        install(new NinjaFlyway());
        install(new NinjaJdbiModule());
        bind(StartupActions.class);
    }
}
