const _ = require('underscore')
const R = require('ramda')
const nonNil = require('./js/nonNil')

const getErrors = R.path(['response', 'errors'])
const groupByPath = R.groupBy(R.prop('path'))

function authenticatedRequest (m, apiInst, request) {
  console.debug(`[${request.method}] ${request.url}`)
  const options = R.assocPath(['headers', 'authorization'], `bearer ${apiInst.getToken()}`, request)
  return m.request(options)
}

function addProfileApi (m, base, apiInst) {
  let profile = null
  return _.extend(apiInst, {
    profile: {
      get: () => profile,
      clear: () => profile = null,
      welcomeMessage: () => {
        if (profile) {
          return `Welcome ${profile.name}`
        }
      },
      refresh: () => {
        console.debug("Profile Refresh...")
        authenticatedRequest(m, apiInst, {
          url: `${base}/api/auth`,
          method: 'GET'
        }).then(user => profile = user)
      }
    }
  })
}

function addEventsApi (m, base, apiInst) {
  return _.extend(apiInst, {
    events: {
      future: () => authenticatedRequest(m, apiInst, {
        url: `${base}/api/events`,
        method: 'GET'
      }),
      register: (data) => authenticatedRequest(m, apiInst, {
        url: `${base}/api/events`,
        method: "POST",
        body: data
      }),
    }
  })
}

function API (m, base = 'http://localhost:8080') {
  let redirect, token
  let inst = {
    getToken: () => token,
    loggedIn: () => nonNil(token),
    logout: () => token = null,
    login: (data, redirect = '/events') => m.request({
      url: `${base}/api/auth`,
      method: "POST",
      body: data
    }).then(data => {
      console.debug("Login Success...")
      token = data.token
      inst.profile.refresh()
      m.route.set(redirect)
    }),
    register: (data) => {
      return m.request({
        url: `${base}/api/users`,
        method: "POST",
        body: data
      })
    },
    errorMap: (xhrError, overrideMessages = {}) => {
      const status = xhrError.code
      let overrideMessage = overrideMessages[status]
      if (overrideMessage) {
        return {'#': [{message: overrideMessage}]}
      } else if (status >= 400 && status < 500) {
        const serverErrors = getErrors(xhrError) || []
        const errors = groupByPath(serverErrors)
        delete errors['#']
        return errors
      } else {
        return {
          '#': [{message: 'Server Exception. Please try again later.'}]
        }
      }
    },
    setRedirect: (value) => {
      redirect = value
    }
  }
  inst = addProfileApi(m, base, inst)
  inst = addEventsApi(m, base, inst)
  return inst
}

module.exports = API
