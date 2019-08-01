const _ = require('underscore')

const truncate = function (str, length) {
  if (_.isString(str)) {
    const dots = length < str.length ? '...' : ''
    return str.substring(0, length) + dots
  }
  return null
}

module.exports = truncate
