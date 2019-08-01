import test from 'ava'

const rewire = require('rewire')
const API = rewire('../src/api')
const authRequest = API.__get__('authenticatedRequest')

const randomString = (len = 24) => Math.random().toString(len);

test('authenticatedRequest adds an authorization bearer to the request header', t => {
    // Arrange
    const token = randomString()
    const m = {request: (opts) => opts}
    const apiInst = {getToken: () => token}

    // Act
    const actual = authRequest(m, apiInst, {method: 'GET'})

    // Assert
    t.deepEqual(actual, {
      method: 'GET',
      headers: {'authorization': `bearer ${token}`}
    })
  }
)

test('authenticatedRequest preserves any existing headers', t => {
    // Arrange
    const token = randomString()
    const m = {request: (opts) => opts}
    const apiInst = {getToken: () => token}

    // Act
    const actual = authRequest(m, apiInst, {
      method: 'POST',
      headers: {'content-type': 'application/json'}
    })

    // Assert
    t.deepEqual(actual, {
      method: 'POST',
      headers: {
        'content-type': 'application/json',
        'authorization': `bearer ${token}`
      }
    })
  }
)

test('login is a POST with data to api/auth, which sets a token and refreshes a profile', t => {
  // Arrange
  const token = randomString()
  const username = randomString()
  const password = randomString()
  const name = randomString()
  let setRoute = null
  const m = {
    route: {
      set: (route) => setRoute = route
    },
    request: (opts) => new Promise((resolve) => {
      if (opts.method === 'POST') {
        t.deepEqual(opts, {
          method: 'POST',
          url: 'http://localhost:8080/api/auth',
          body: {username, password}
        })
        resolve({token})
      } else {
        t.deepEqual(opts, {
          method: 'GET',
          url: 'http://localhost:8080/api/auth',
          headers: {authorization: `bearer ${token}`}
        })
        resolve({username, name})
      }
    })
  }
  const apiInst = API(m)

  // Act
  return apiInst.login({username, password})
    .then(() => {
      // Assert
      t.is(apiInst.getToken(), token)
      t.deepEqual(apiInst.profile.get(), {username, name})
      t.is(setRoute, '/events')
    })
})
