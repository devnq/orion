# Orion Functional Tests

## Frameworks/Dependencies Used

### [FluentLenium](https://fluentlenium.com/)

> FluentLenium is a React ready website automation framework which extends Selenium to write readable, reusable, 
reliable and resilient UI functional tests. It’s written and maintained by people who are automating browser-based
tests on a daily basis.

[Selenium](https://www.seleniumhq.org/) is a popular Web Browser Automation tool that allows developers to perform
scripted browser interactions. These interactions are designed to mimic the behaviour of a person interacting with
the website; and assert appropriate outcomes based on expected results.

For example with Orion: 

* A new user should be able to create an account, login, create a listing in the future, and see it listed alongside 
all other events.

```java
goTo(loginPage)
	.waitFor(loginForm)
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
	.click(newEventPage.navLink);
newEventPage
	.waitFor(newEventForm)
	.waitFor(titleInput)
	.typeIn(titleInput, title)
	.typeIn(descriptionInput, description)
	.typeIn(dateInput, eventDate)
	.click(submitButton);
eventsPage
	.waitForEvents()
	.assertSeeEvent(eventTitle, eventDescription);
```

