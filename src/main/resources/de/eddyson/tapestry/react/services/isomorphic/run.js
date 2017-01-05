/* 
 * This is sort of a hack. 
 * We are running synchronously inside the Java ScriptEngine, but we can not assign results from require() calls to a 
 * variable like in nodejs, at least not as long as we are running inside a require() or define() call.
 * But since the code is executed synchronously, we can assign global variable from inside the require call and be sure 
 * it's being executed and available as soon as the script is interpreted.
 * 
 */

var _React = null;
var _ReactDomServer = null;

require(['react', 'react-dom-server'], function(React, ReactDomServer) {
    _React = React;
    _ReactDomServer = ReactDomServer
});
    
global.TAPESTRY_REACT_RENDER = function(componentModuleToRender, properties) {
        
    /* same trick here, code is executed synchronously! */
    var result = null;;
    require([componentModuleToRender], function(component){
        if (component.default !== undefined) {
            component = component.default;
        }
        result = _ReactDomServer.renderToString(_React.createElement(component, JSON.parse(properties)));
    });
    return result;
}
