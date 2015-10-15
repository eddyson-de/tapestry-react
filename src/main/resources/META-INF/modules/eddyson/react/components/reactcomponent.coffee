define ["react", "react-dom", "require"], (React, ReactDOM, require)->

  (module, clientId, parameters) ->
    element = document.getElementById clientId
    require [module], (componentClass)->
      ReactDOM.render (React.createElement componentClass, parameters), element