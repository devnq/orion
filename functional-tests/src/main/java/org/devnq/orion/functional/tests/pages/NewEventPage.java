package org.devnq.orion.functional.tests.pages;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.concurrent.TimeUnit.SECONDS;

@PageUrl("http://127.0.0.1:9000/#!/events/new")
class NewEventPage extends FluentPage {

    private static final DateTimeFormatter formatter = ofPattern("dd-MM-yyyy");

    @FindBy(xpath = "//a[text()=\"New Event\"]")
    FluentWebElement navLink;

    @FindBy(css = "form.new-event")
    FluentWebElement newEventForm;

    @FindBy(xpath = "//input[@name=\"title\"]")
    FluentWebElement titleInput;

    @FindBy(xpath = "//textarea[@name=\"description\"]")
    FluentWebElement descriptionInput;

    @FindBy(xpath = "//input[@name=\"date\"]")
    FluentWebElement dateInput;

    @FindBy(xpath = "//button[text()=\"Submit\"]")
    FluentWebElement submitButton;

    NewEventPage typeIn(final FluentWebElement element, final String value) {
        element.write(value);
        return this;
    }

    NewEventPage click(final FluentWebElement element) {
        element.click();
        return this;
    }

    NewEventPage waitFor(final FluentWebElement el) {
        await()
            .atMost(5, SECONDS)
            .until(el)
            .present();
        return this;
    }

    NewEventPage registerEvent(final String title, final String description, final LocalDateTime date) {
        final String eventDate = formatter.format(date);
        return waitFor(newEventForm)
            .waitFor(titleInput)
            .typeIn(titleInput, title)
            .typeIn(descriptionInput, description)
            .typeIn(dateInput, eventDate)
            .click(submitButton);
    }
}
