package de.eddyson.tapestry.react.services.nashorn;

import java.io.IOException;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class NashornReactRenderEngineImplementation implements NashornReactRenderEngine {

  private static final Logger log = LoggerFactory.getLogger(NashornReactRenderEngineImplementation.class);

  private final StreamableResourceSource srs;
  private final ResourceChangeTracker tracker;
  private final ModuleManager moduleManager;
  private final AssetSource assetSource;
  
  private ScriptEngine _engine;
  private SimpleBindings _bindings;

  public NashornReactRenderEngineImplementation(ModuleManager moduleManager, StreamableResourceSource srs,
      ResourceChangeTracker tracker, AssetSource assetSource) {
    super();
    this.moduleManager = moduleManager;
    this.srs = srs;
    this.tracker = tracker;
    this.assetSource = assetSource;
  }

  @Override
  public String renderReactComponent(String moduleName, JSONObject parameters) {
    Stopwatch watch = Stopwatch.createStarted();
    try {
      StringBuilder builder = new StringBuilder();
      builder.append("var reactResult = null;\n");
      builder.append(
          "require(['eddyson/react/isomorphic/run', '%1$s'], function(ri) { reactResult = ri('%1$s', %2$s); } );\n");
      builder.append("reactResult;");
      String script = String.format(builder.toString(), moduleName, parameters.toCompactString());
      ScriptEngine engine = getEngine();
      Bindings bindings = copyGlobalBindings();
      Object result = engine.eval(script, bindings);
      if (result == null) {
        resetEngine();
        return null;
      }
      return result.toString();
    } catch (Throwable e) {
      resetEngine();
      throw new RuntimeException(e);
    } finally {
      log.info("Rendering module '" + moduleName + "' took " + watch.toString());
    }
  }

  // when an exception occurs while rendering react component discard the engine
  // require.js set's some internal timeout values for modules that could not be loaded and
  // throws an exception on next invocation of that module
  private void resetEngine() {
    this._engine = null; 
  }

  // TODO: check if necessary or if fresh bindings would work too...
  private Bindings copyGlobalBindings() {
    Bindings bindingsCopy = new SimpleBindings();
    bindingsCopy.putAll(this._bindings);
    return bindingsCopy;
  }

  private ScriptEngine getEngine() throws ScriptException, IOException {
    if (this._engine == null) {
      synchronized (this) {
        if (this._engine == null) {
          this._engine = buildEngine();
        }
      }
    }
    return this._engine;
  }

  private ScriptEngine buildEngine() throws ScriptException, IOException {
    ScriptEngine nashorn = new ScriptEngineManager().getEngineByName("nashorn");
    Map<String, Object> data = Maps.newHashMap();
    this._bindings = new SimpleBindings(data);
    
    // "__tapestry" is available in JavaScript code to load AMD modules from Tapestry's ModuleManager
    data.put("__tapestry", buildNashornModuleLoader(nashorn));
    
    // polyfill.js contains some basic polyfills
    nashorn.eval(read("META-INF/modules/eddyson/react/isomorphic/polyfill.js"), this._bindings);
    
    // r.js is the server side implementaiton of require.js for Nashorn/Rhino/Node
    nashorn.eval(read("META-INF/modules/eddyson/react/isomorphic/r.js"), this._bindings);
    
    // r-config.js injects the tapestry module loading mechanism in the r.js implementation
    nashorn.eval(read("META-INF/modules/eddyson/react/isomorphic/r-config.js"), this._bindings);
    return nashorn;
  }

  private NashornModuleLoader buildNashornModuleLoader(ScriptEngine nashorn) {
    return new NashornModuleLoader(nashorn, this.moduleManager, this._bindings, this.srs, this.tracker,
        this.assetSource);
  }

  private String read(String resource) throws IOException {
    return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(resource));
  }

}