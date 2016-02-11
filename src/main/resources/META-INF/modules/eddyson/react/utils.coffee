define ['./application-config','t5/core/console'], (config, console)->

  pageBaseURL = document.documentElement.getAttribute 'data-page-base-url'

  pageBaseURL : pageBaseURL
  createAPIURL : (operation) ->
   "#{config['tapestry.context-path']}#{config['react-api-path']}?operation=#{operation}"
  createEventURI : (event, context...)->
    currentPath = window.location.pathname
    
    activationContext = currentPath.substring pageBaseURL.length
    if (activationContext.indexOf '/') is 0
      activationContext = activationContext.substring 1
    else if activationContext.length isnt 0
      console.warn "Unable to extract page activation context, base URL = #{pageBaseURL}, current path = #{currentPath}, please report a bug at https://github.com/eddyson-de/tapestry-react/issues."
      activationContext = ''
    queryParams = window.location.search
    if activationContext isnt ''
      if queryParams is ''
        queryParams = '?t:ac=' + activationContext
      else
        queryParams = queryParams + '&t:ac=' + activationContext
    eventUrl = pageBaseURL.replace /($|;)/, ":#{event}$1"
    for item in context
      eventUrl = eventUrl + '/' + item
    eventUrl + queryParams