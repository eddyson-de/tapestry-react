package de.eddyson.tapestry.react.services.nashorn;

import org.apache.tapestry5.json.JSONObject;

public interface NashornReactRenderEngine {

    /**
     * Render a react component to a string on the server
     * 
     * @param moduleName the module name of the component
     * @param parameters informal parameters
     * @return string representation of component
     */
    String renderReactComponent(String moduleName, JSONObject parameters);

}
