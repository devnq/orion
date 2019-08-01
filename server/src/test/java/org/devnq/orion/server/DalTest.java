package org.devnq.orion.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ninja.conf.NinjaBaseModule;
import ninja.flyway.NinjaFlyway;
import ninja.jdbc.NinjaDbCoreModule;
import ninja.jdbi.NinjaJdbiModule;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import java.util.Optional;

import static ninja.utils.NinjaModeHelper.determineModeFromSystemProperties;

public abstract class DalTest {
    private Injector injector;

    private final NinjaMode ninjaMode;

    public DalTest() {
        final Optional<NinjaMode> mode = determineModeFromSystemProperties();
        ninjaMode = mode.orElse(NinjaMode.test);
    }

    public final void init() {
        final NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
        injector = Guice.createInjector(new NinjaBaseModule(ninjaProperties),
                                        new NinjaDbCoreModule(),
                                        new NinjaFlyway(),
                                        new NinjaJdbiModule());
    }

    protected <T> T getInstance(final Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
