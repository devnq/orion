const m = require('mithril')

function Nav () {
  return {
    view: (vnode) => (
      <nav fx>
        <label>
          <input type='checkbox'/>
          <header>
            <a>{vnode.attrs.message || 'Orion'}</a>
          </header>
          <ul>
            {vnode.attrs.message
              ? [<li><a href='#!/events'>Events</a></li>,
                <li><a href='#!/events/new'>New Event</a></li>,
                <li><a href='#!/logout'>Logout</a></li>]
              : [<li><a href='#!/login'>Login</a></li>]}
          </ul>
        </label>
      </nav>
    )
  }
}

const PageLayout = function (API) {
  return {
    view: function (vnode) {
      return m('div', [<Nav message={API.profile.welcomeMessage()}/>, m("section", vnode.children)])
    }
  }
}

module.exports = PageLayout
