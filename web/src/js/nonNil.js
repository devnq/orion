const R = require('ramda')
module.exports = R.compose(R.not, R.isNil)
