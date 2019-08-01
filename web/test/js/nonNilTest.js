import test from 'ava'

const nonNil = require('../../src/js/nonNil')

test('for null', t => {
    t.falsy(nonNil(null))
  }
)

test('for undefined', t => {
    t.falsy(nonNil(undefined))
  }
)

test('for strings', t => {
    t.truthy(nonNil("a String"))
  }
)

test('for numbers', t => {
    t.truthy(nonNil(123))
  }
)

test('for objects', t => {
    t.truthy(nonNil({}))
  }
)
