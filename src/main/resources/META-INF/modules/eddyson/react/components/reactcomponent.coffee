define ["react", "require"], (React, require)->

  (module, clientId, parameters) ->
    element = document.getElementById clientId
    require [module], (componentClass)->
      React.render (React.createElement componentClass, parameters), element