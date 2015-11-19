define ["react", "react-dom", "require", "t5/core/dom", "t5/core/events", "t5/core/console"], (React, ReactDOM, require, dom, events, console)->

  elementsWithMountedComponents = []
  
  isAncestor = (element, ancestor) ->
    if not element?
      return false
    parent = element.parent()
    while parent?
      if ancestor.element is parent.element
        return true
      parent = parent.parent()
    return false
  

  dom.onDocument events.zone.willUpdate, (event)->
    newElementsWithMountedComponents = []
    for clientId in elementsWithMountedComponents
      element = (dom clientId)
      if isAncestor element, this
        if ReactDOM.unmountComponentAtNode element.element
          console.debug "Umounted ReactComponent instance at " + clientId
        else
          console.warn "Failed to unmount ReactComponent instance at " + clientId
      else
        newElementsWithMountedComponents.push clientId
    elementsWithMountedComponents = newElementsWithMountedComponents
    return  
    

  (module, clientId, parameters) ->
    element = document.getElementById clientId
    require [module], (componentClass)->
      
      reactElement = React.createElement (if componentClass.__esModule then componentClass.default else componentClass), parameters
      reactComponent = ReactDOM.render reactElement, element
      unless reactComponent
        throw "Stateless components are not supported "
      elementsWithMountedComponents.push clientId