const m = require('mithril')
const renderErrors = require('./errors')
const extractFormData = require('../js/extractFormData')

function RegisterEvent (API, registrationComplete) {
  let errors = {}
  const minDate = new Date().toISOString().split("T")[0];
  let form = {date: minDate}

  const reset = () => {
    errors = {}
    form = {date: minDate}
  }

  const submit = e => {
    e.preventDefault()
    const formData = extractFormData(e.target)
    formData.epoch = Math.floor(new Date(formData.date).getTime() / 1000)
    form = formData
    API.events.register(formData)
      .then(registrationComplete)
      .catch(e => {
        errors = API.errorMap(e, {409: 'The username is taken.'})
      })
  }

  return {
    requireAuth: true,
    onremove: reset,
    view: () => (
      <form class='new-event' onsubmit={submit}>
        <h3>New Event</h3>
        <grid>
          <div col='3/4'>
            <label htmlFor='title'>Title</label>
            {renderErrors(errors, '#/title')}
            <input id='title'
                   name='title'
                   type='text'
                   placeholder='Event Name'
                   value={form.title}/>

          </div>
          <div col='1/4'>
            <label htmlFor='date'>Date</label>
            <input type='date'
                   name='date'
                   value={form.date}
                   min={minDate}/>
          </div>
          <div col='1/1'>
            <label htmlFor='description'>Description</label>
            {renderErrors(errors, '#/description')}
            <textarea id='description'
                      name='description'
                      placeholder='Description of the event'
                      value={form.description}/>
            {renderErrors(errors, '#')}
            <button primary m-full type='submit' col='1/1'>Submit</button>
          </div>
        </grid>
      </form>
    )
  }
}

module.exports = RegisterEvent
