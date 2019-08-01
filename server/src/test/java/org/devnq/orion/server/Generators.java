package org.devnq.orion.server;

import com.github.javafaker.Faker;
import org.devnq.orion.server.models.ImmutableEvent;
import org.devnq.orion.server.models.ImmutableLogin;
import org.devnq.orion.server.models.ImmutableUser;

import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess")
public final class Generators {

    private static final Random random = new Random();

    private Generators() {}

    public static ImmutableUser.Builder genNewUser() {
        final Faker faker = new Faker();
        return ImmutableUser.builder()
            .username(faker.name().username())
            .name(faker.name().fullName())
            .password(faker.internet().password());
    }

    public static ImmutableLogin.Builder genLogin() {
        final Faker faker = new Faker();
        return ImmutableLogin.builder()
            .username(faker.name().username())
            .password(faker.internet().password());
    }

    public static ImmutableEvent.Builder genEvent() {
        final Faker faker = new Faker();
        return ImmutableEvent.builder()
            .title(faker.book().title())
            .description(faker.lorem().paragraphs(1).parallelStream().collect(Collectors.joining("\n")))
            .epoch(random.nextInt());
    }
}
