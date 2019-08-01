package org.devnq.orion.functional.tests.pages;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;

import static java.lang.Integer.min;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.openqa.selenium.By.xpath;

@PageUrl("http://127.0.0.1:9000/#!/events")
class EventsPage extends FluentPage {

    private static String truncate(final String s, final int length) {
        final int min = min(length, s.length());
        return s.substring(0, min);
    }

    EventsPage assertSeeEvent(final String title) {
        final String selector = format("//card//*[text()=\"%s\"]", title);
        System.out.println(selector);
        assertThat(find(xpath(selector))).isNotEmpty();
        return this;
    }

    EventsPage assertSeeEvent(final String title, final String description) {
        assertSeeEvent(title);
        final String truncatedDescription = truncate(description, 100);
        final String selector = format("//card//*[contains(text(),\"%s\")]", truncatedDescription);
        assertThat(find(xpath(selector))).isNotEmpty();
        return this;
    }

    EventsPage waitForEvents() {
        final FluentWebElement el = el(".events card");
        await()
            .atMost(5, SECONDS)
            .until(el)
            .present();
        return this;
    }
}
