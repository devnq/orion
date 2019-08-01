package org.devnq.orion.functional.tests.pages;

import com.github.javafaker.Faker;
import org.fluentlenium.core.annotation.Page;
import org.junit.Test;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

public class RegisterAndCreateEventTest extends AbstractChromeTest {

    @Page
    private LoginPage loginPage;

    @Page
    private NewEventPage newEventPage;

    @Page
    private EventsPage eventsPage;

    @Test
    public void register() {
        final Faker faker = new Faker();
        final String name = faker.name().fullName();
        final String username = faker.name().username();
        final String password = faker.internet().password();
        goTo(loginPage)
            .registerAndLoginWith(username, name, password)
            .click(newEventPage.navLink);

        final String eventTitle = faker.book().title();
        final String eventDescription = faker.lorem().paragraphs(3).toString();
        final LocalDateTime eventDate = now().plusDays(1);

        newEventPage
            .registerEvent(eventTitle, eventDescription, eventDate);

        eventsPage
            .waitForEvents()
            .assertSeeEvent(eventTitle, eventDescription);
    }
}