import test from 'ava'

const truncate = require('../../src/js/truncate')
const string = "The Quick Brown Fox Jumped Over The Lazy Dog"

test('truncate is null for all types except string', t => {
    t.is(truncate(null), null)
    t.is(truncate(undefined), null)
    t.is(truncate([]), null)
    t.is(truncate(123), null)
    t.is(truncate({}), null)
  }
)

test('truncate will shorten a string to the maximum length and add elide dots', t => {

    // Act
    const actual = truncate(string, 5)

    // Assert
    t.is(actual, "The Q...")
  }
)

test('truncate will NOT shorten the string if the length is under the maximum size', t => {

    // Act
    const actual = truncate(string, 512)

    // Assert
    t.is(actual, string)
  }
)

