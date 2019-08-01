const m = require('mithril')
const truncate = require('../js/truncate')
const humanizeEpoch = require('../js/humanizeEpoch')

const renderEvents = function (events) {
  if (events) {
    return (
      <grid class='events'>
        {events.map(({title, epoch, host, description}) => (
          <div col='1/1'>
            <card>
              <h5 col='2/3'>{title}</h5>
              <div class='attrs' col='1/3'>
                <p class='attr'><strong>Date:</strong> {humanizeEpoch(epoch)}</p>
                <p class='attr'><strong>Host:</strong> {host.name}</p>
              </div>
              <hr/>
              <p>{truncate(description, 500)}</p>
            </card>
          </div>))}
      </grid>
    );
  }
}

function Events (API) {
  let events = null
  return {
    requireAuth: true,
    onremove: vnode => {
      events = null
    },
    oninit: vnode => {
      API.events.future()
        .then((evts) => {
          events = evts
        })
    },
    view: vnode =>
      (
        [
          <h1>Upcoming Events</h1>,
          renderEvents(events)
        ]
      )
  }
}

module.exports = Events
