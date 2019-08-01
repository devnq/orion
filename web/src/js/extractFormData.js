const fromEntries = require('fromentries')
const isForm = element => element instanceof window.HTMLFormElement

module.exports = htmlFormElement => {
  if (isForm(htmlFormElement)) {
    const formData = new window.FormData(htmlFormElement)
    return fromEntries(formData)
  }
  return null
}
