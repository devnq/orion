const Login = require('./login')
const Register = require('./register')
const m = require('mithril')
const nonNil = require('../js/nonNil')

function LoginRegister (API) {
  let isLogin = true
  let user = null

  const reset = () => {
    isLogin = true
    user = null
  }

  const toggleForm = () => {
    isLogin = !isLogin
  }

  const registrationComplete = registeredUser => {
    isLogin = true
    user = registeredUser
  }

  const login = Login(API, toggleForm)
  const register = Register(API, toggleForm, registrationComplete)

  const renderUser = () => {
    if (user) {
      return [
        m("p", "Welcome to Orion ", m("code", user.name), "."),
        m("p", "Your account setup is complete and you may now log in using your username (", m("code", user.username), ") and password.")]
    }
  }
  return {
    onremove: reset,
    view: () => (
      <section>
        <grid>
          <div col='1/2' style='margin-left:25%'>
            {renderUser()}

            {isLogin
              ? m(login, {
                user,
                hideTitle: nonNil(user)
              })
              : m(register)}
          </div>
        </grid>
      </section>
    )
  }
}

module.exports = LoginRegister
