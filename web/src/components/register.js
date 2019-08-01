const m = require('mithril')
const renderErrors = require('./errors')
const extractFormData = require('../js/extractFormData')

function Register (API, cancel, registrationComplete) {
  let errors = {}

  const submit = e => {
    e.preventDefault()
    const formData = extractFormData(e.target)
    API.register(formData)
      .then(registrationComplete)
      .catch(e => {
        errors = API.errorMap(e, {409: 'The username is taken.'})
      })
  }

  return {
    view: () => (
      <form class='register' onsubmit={submit}>
        <h3>Register</h3>
        <label htmlFor='username'>Username</label>
        {renderErrors(errors, '#/username')}
        <input id='username' name='username' type='text' placeholder='Username'/>
        <label htmlFor='name'>Name</label>
        {renderErrors(errors, '#/name')}
        <input id='name' name='name' type='text' placeholder='Name'/>
        <label htmlFor='password'>Password</label>
        {renderErrors(errors, '#/password')}
        <input id='password' name='password' type='password' placeholder='Password'/>
        {renderErrors(errors, '#')}
        <button primary m-full type='submit' col='1/1'>Register</button>
        <button m-full type='button' col='1/1' onclick={cancel}>Cancel</button>
      </form>
    )
  }
}

module.exports = Register
