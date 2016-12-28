define(function(require) {
    var React = require('react');
    var ReactDomServer = require('react-dom-server');
    
    return function(componentModuleToRender, properties) {
        var component = require(componentModuleToRender);
        if (component.default !== undefined) {
            component = component.default;
        }
        return ReactDomServer.renderToString(React.createElement(component, properties))
    }
});