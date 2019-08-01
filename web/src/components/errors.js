const m = require('mithril')
const renderErrors = (errors, path) => {
  const pathErrors = errors && errors[path]
  if (pathErrors) {
    return m('.error', pathErrors.map(error => {
      return m('p.validation-error', error.message)
    }))
  }
}
module.exports = renderErrors
