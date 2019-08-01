const m = require('mithril')
const R = require('ramda')
const renderErrors = require('./errors')
const extractFormData = require('../js/extractFormData')

const getUsername = R.path(['attrs', 'user', 'username'])
const hideTitle = R.path(['attrs', 'hideTitle'])

function Login (API, register) {
  let errors = {}

  const reset = () => errors = {}

  const submit = e => {
    e.preventDefault()
    const formData = extractFormData(e.target)
    reset()
    API.login(formData)
      .catch(e => {
        errors = API.errorMap(e, {401: "Unknown user or invalid password."})
      })
  }

  return {
    onremove: reset,
    view: (vnode) => (
      <form onsubmit={submit}>
        {hideTitle(vnode) ? null : m('h3', 'Login')}
        <label htmlFor='username'>Username</label>
        <input id='username'
               autocomplete='off'
               name='username'
               type='text'
               placeholder='Username'
               value={getUsername(vnode)}/>
        <label htmlFor='password'>Password</label>
        <input id='password' name='password' type='password' placeholder='Password'/>
        {renderErrors(errors, '#')}
        <button primary m-full col='1/1' type='submit'>Login</button>
        <button m-full
                col='1/1'
                type='button'
                onclick={register}
                style={vnode.attrs.user ? "display: none" : null}>Sign Up
        </button>
      </form>
    )
  }
}

module.exports = Login
