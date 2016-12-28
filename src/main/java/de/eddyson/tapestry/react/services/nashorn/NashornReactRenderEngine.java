package de.eddyson.tapestry.react.services.nashorn;

import org.apache.tapestry5.json.JSONObject;

public interface NashornReactRenderEngine {

    String renderReactComponent(String moduleName, JSONObject parameters);

}
