package org.devnq.orion.functional.tests.pages;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import static java.util.concurrent.TimeUnit.SECONDS;

@PageUrl("http://127.0.0.1:9000/#!/login")
class LoginPage extends FluentPage {
    @FindBy(xpath = "//a[text()=\"Logout\"]")
    FluentWebElement logoutAnchor;

    @FindBy(css = "form.login")
    FluentWebElement loginForm;

    @FindBy(css = "form.register")
    FluentWebElement registerForm;

    @FindBy(xpath = "//input[@name=\"username\"]")
    FluentWebElement usernameInput;

    @FindBy(xpath = "//input[@name=\"name\"]")
    FluentWebElement nameInput;

    @FindBy(xpath = "//input[@name=\"password\"]")
    FluentWebElement passwordInput;

    @FindBy(xpath = "//button[text()=\"Login\"]")
    FluentWebElement loginButton;

    @FindBy(xpath = "//button[text()=\"Sign Up\"]")
    FluentWebElement signupButton;

    @FindBy(xpath = "//button[text()=\"Register\"]")
    FluentWebElement registerButton;

    LoginPage typeIn(final FluentWebElement element, final String value) {
        element.write(value);
        return this;
    }

    LoginPage click(final FluentWebElement element) {
        element.click();
        return this;
    }

    LoginPage waitFor(final FluentWebElement el) {
        await()
            .atMost(5, SECONDS)
            .until(el)
            .present();
        return this;
    }

    LoginPage registerAndLoginWith(final String username, final String name, final String password) {
        return waitFor(loginForm)
            .click(signupButton)
            .waitFor(registerForm)
            .typeIn(usernameInput, username)
            .typeIn(nameInput, name)
            .typeIn(passwordInput, password)
            .click(registerButton)
            .waitFor(loginForm)
            .typeIn(passwordInput, password)
            .click(loginButton)
            .waitFor(logoutAnchor);
    }
}
