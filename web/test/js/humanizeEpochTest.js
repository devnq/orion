import test from 'ava'

const rewire = require('rewire')
const humanizeEpoch = rewire('../../src/js/humanizeEpoch')

const mockLanguage = lang => {
  humanizeEpoch.__set__('browserLanguage', () => lang)
}

test('converts an epoch time into a human readable format', t => {
    // Arrange
    const epoch = 0
    const expected = 'Thursday, 1 January 1970'
    mockLanguage('en-au')

    // Act
    const actual = humanizeEpoch(epoch)

    //Assert
    t.is(actual, expected)
  }
)

test('the humanization is language dependent', t => {
    // Arrange
    const epoch = 0
    const expected = 'Donnerstag, 1. Januar 1970'
    mockLanguage('de-DE')

    // Act
    const actual = humanizeEpoch(epoch)

    //Assert
    t.is(actual, expected)
  }
)

test('supports negative epoch', t => {
    // Arrange
    const epoch = -12341234
    const expected = 'Monday, 11 August 1969'
    mockLanguage('en-au')

    // Act
    const actual = humanizeEpoch(epoch)

    //Assert
    t.is(actual, expected)
  }
)

test('supports positive epoch', t => {
    // Arrange
    const epoch = 1564652401
    const expected = 'Thursday, 1 August 2019'
    mockLanguage('en-au')

    // Act
    const actual = humanizeEpoch(epoch)

    //Assert
    t.is(actual, expected)
  }
)
