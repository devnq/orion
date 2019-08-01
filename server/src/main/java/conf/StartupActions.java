package conf;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;

import javax.inject.Singleton;

@Singleton
public class StartupActions {

    private final NinjaProperties ninjaProperties;
    private final ObjectMapper objectMapper;

    @Inject
    public StartupActions(final NinjaProperties ninjaProperties, final ObjectMapper objectMapper) {
        this.ninjaProperties = ninjaProperties;
        this.objectMapper = objectMapper;
    }

    @Start(order = 90)
    public void configureObjectMapper() {
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }
}
