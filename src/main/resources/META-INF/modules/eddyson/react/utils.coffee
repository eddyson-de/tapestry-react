define ['./application-config'], (config)->

  pageBaseURL : document.documentElement.getAttribute 'data-page-base-url'

  createAPIURL : (operation) ->
   "#{config['baseURL']}#{config['tapestry.context-path']}#{config['react-api-path']}?operation=#{operation}"

  createEventURI : (event, context...)->
    currentPath = window.location.pathname
    indexOfParams = currentPath.indexOf '?'
    params = ''
    if indexOfParams > -1
      currentPath = currentPath.substring 0, indexOfParams
      params = currentPath.substring indexOfParams
    activationContext = currentPath.substring this.pageBaseURL.length
    if (activationContext.indexOf '/') is 0
      activationContext = activationContext.substring 1
    if activationContext isnt ''
      if params is ''
        params = '?t:ac=' + activationContext
      else
        params = '&t:ac=' + activationContext
    eventUrl = this.pageBaseURL + ':' + event
    for item in context
      eventUrl = eventUrl + '/' + item
    eventUrl + params