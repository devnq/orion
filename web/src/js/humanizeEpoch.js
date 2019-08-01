const R = require('ramda')
const userLanguage = R.path(['navigator', 'userLanguage'])
const systemLanguage = R.path(['navigator', 'language'])

const browserLanguage = () => userLanguage(window) || systemLanguage(window)

const options = {
  weekday: 'long',
  year: 'numeric',
  month: 'long',
  day: 'numeric'
}

const humanizeEpoch = function (epoch) {
  const d = new Date(0)
  const lang = browserLanguage()
  d.setUTCSeconds(epoch)
  return d.toLocaleDateString(lang, options)
}

module.exports = humanizeEpoch
