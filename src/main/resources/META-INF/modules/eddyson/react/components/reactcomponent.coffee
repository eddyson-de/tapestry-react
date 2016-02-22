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
  

  zoneUpdateListener = (event)->
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

  stopListener = dom.onDocument events.zone.willUpdate, zoneUpdateListener
  windowUnloadListener = ->
    stopListener()
    elementsWithMountedComponents = null
    window.removeEventListener 'unload', windowUnloadListener
    return

  window.addEventListener 'unload', windowUnloadListener
    
  convertNode = (node, key)->
    if node.nodeType is 3
      node.wholeText
    else if node.nodeType is 1
      children = (convertNode n, "c#{idx}" for n, idx in node.childNodes)
      # TODO isn't there an easier way to copy a node's properties?
      props = key: key
      props[k] = node[k] for k in ['className', 'title']
      props[a.nodeName] = a.value for a in node.attributes when (a.nodeName.indexOf 'data-') is 0
      for key in node.style
        value = node.style[key]
        if value isnt ''
          throw new Error("Cannot handle inline styles on children of ReactComponent.")
      props.style = {}
      props.style[key] = value for own key, value of node.style when value isnt ''

      React.createElement node.nodeName, props, children
    # TODO else?

  (module, clientId, parameters) ->
    element = document.getElementById clientId
    require [module], (componentClass)->
      children = (convertNode c, "c#{idx}" for c, idx in element.childNodes)
      reactElement = React.createElement (if componentClass.__esModule then componentClass.default else componentClass), parameters, children
      reactComponent = ReactDOM.render reactElement, element
      unless reactComponent
        throw new Error("Stateless components are not supported")
      elementsWithMountedComponents.push clientId