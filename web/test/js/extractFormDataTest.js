import test from 'ava'

const extractFormData = require('../../src/js/extractFormData')

test('null if the object is not an instance of HTMLFormElement', t => {

    // Act
    const actual = extractFormData(null)

    // Assert
    t.is(actual, null)
  }
)

test('converts the HTMLFormElement into a js object', t => {
    // Arrange
    const form = document.createElement('form');

    // Act
    const actual = extractFormData(form)

    // Assert
    t.deepEqual(actual, {})
  }
)

test('converts the HTMLFormElement into a js object with named inputs', t => {
    // Arrange
    const form = document.createElement('form');
    const input = document.createElement('input');
    input.type = 'text'
    input.name = 'username'
    input.value = 'axrs'
    form.appendChild(input)

    // Act
    const actual = extractFormData(form)

    // Assert
    t.deepEqual(actual, {username: 'axrs'})
  }
)

