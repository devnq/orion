import './bare.min.css'
import './style.css'

const m = require('mithril')
const API = require('./api')(m)
const Events = require('./components/events')
const CreateEvent = require('./components/createEvent')
const LoginRegister = require('./components/loginRegister')
const PageLayout = require('./components/pageLayout')

const authOnly = page => {
  const pageLayout = PageLayout(API)
  return ({
    render: () => m(pageLayout, m(page)),
    onmatch: (args, requestedPath, route) => {
      console.debug('Routing to', route)
      if (page.requireAuth && !API.loggedIn()) {
        API.setRedirect(route)
        m.route.set('/login')
      } else {
        return page
      }
    }
  })
}

m.route(document.body, '/events', {
  '/events': authOnly(Events(API)),
  '/events/new': authOnly(CreateEvent(API, () => m.route.set('/events'))),
  '/login': authOnly(LoginRegister(API)),
  '/logout': {
    onmatch: () => {
      API.logout()
      API.profile.clear()
      m.route.set('/login')
    }
  }
})
