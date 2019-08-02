# Orion Web

## Setup

Change into the web directory and install the dependencies

```bash
$ cd ./web
$ npm install
```

## Running

### Development Mode

To run the project in development, issue

```bash
$ npm start
```

* A browser window should open at http://locahost:9000/
* Changing the code will automatically trigger a re-compile and page refresh

### Tests

To run the tests once:

```bash
$ npm test
```

To watch files for changes and re-evaluate the tests:

```bash
$ npm run-script test-watch
```

## Structure
```
├── README.md
├── dist         
├── package.json                <- Production and Development dependencies
├── src
│   ├── api.js                  <- All the Orion Server interactions in one place
│   ├── bare.min.css            <- Simple CSS Styling
│   ├── components              <- Separate Views and Rendering components
│   │   ├── createEvent.js
│   │   ├── errors.js
│   │   ├── events.js
│   │   ├── login.js
│   │   ├── loginRegister.js
│   │   ├── pageLayout.js
│   │   └── register.js
│   ├── index.html              <- Root HTML Page
│   ├── index.js                <- Root web app entry point
│   ├── js                      <- SRP helper functions
│   │   ├── extractFormData.js
│   │   ├── humanizeEpoch.js
│   │   ├── nonNil.js
│   │   └── truncate.js
│   └── style.css               <- Style overriding
├── test
│   ├── _setup-browser-env.js   <- Emulates browser API (dom nodes, etc) in Node.js
│   ├── apiTest.js              <- Tests for the server interactions (akin to end-to-end tests)
│   ├── js                      <- Utility test functions
│   │   ├── extractFormDataTest.js
│   │   ├── humanizeEpochTest.js
│   │   ├── nonNilTest.js
│   │   └── truncateTest.js
│   └── test.js
└── webpack.config.js
```

### Frameworks/Dependencies Used

#### [Mithril](https://mithril.js.org/)

> Mithril is a modern client-side JavaScript framework for building Single Page Applications. It's small (< 10kb gzip), 
fast and provides routing and XHR utilities out of the box.

The Mithril framework provides an easy to use, web framework that behaves similar to React. The built in component
structure data mechanics are a good introduction for anyone new to the web development area.

#### [AVA](https://github.com/avajs/ava)

> AVA is a test runner for Node.js with a concise API, detailed error output, embrace of new language features and
process isolation that let you write tests more effectively. So you can ship more awesome code.

AVA is a Node.js replacement for the common Karma, Mocha, Chai test runner combinations. Tests written in AVA are
run in parallel and with complete isolation (forcing you to write clean tests).

#### [Rewire](https://github.com/jhnns/rewire)

> rewire adds a special setter and getter to modules so you can modify their behaviour for better unit testing.

Sometimes unit testing in Node.js can be difficult because modules can have private functions that are complete
in-accessibly from external sources. Rewire gives us a simple way of monkey-patching a module for testing and mocking.