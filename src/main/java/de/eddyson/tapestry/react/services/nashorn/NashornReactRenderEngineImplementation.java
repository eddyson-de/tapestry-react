package de.eddyson.tapestry.react.services.nashorn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class NashornReactRenderEngineImplementation implements ReactRenderEngine {

  private static final Logger log = LoggerFactory.getLogger(NashornReactRenderEngineImplementation.class);

  private final StreamableResourceSource srs;
  private final ResourceChangeTracker tracker;
  private final ModuleManager moduleManager;
  private final Resource rJsResource;
  
  private ScriptEngine _engine;
  private SimpleBindings _bindings;


  public NashornReactRenderEngineImplementation(@Inject @Path("webjars:requirejs-node:$version/r.js") Resource rJsResource, ModuleManager moduleManager, StreamableResourceSource srs,
      ResourceChangeTracker tracker) {
    super();
    this.rJsResource = rJsResource;
    this.moduleManager = moduleManager;
    this.srs = srs;
    this.tracker = tracker;
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
      log.info("Rendering module '{}' took {}", moduleName, watch);
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
    NashornModuleLoader nashornModuleLoader = buildNashornModuleLoader(nashorn);
    data.put("__tapestry", nashornModuleLoader);
    
    // polyfill.js contains some basic polyfills
    nashorn.eval(read(classpathResource("de/eddyson/tapestry/react/services/isomorphic/polyfill.js")), this._bindings);
    
    // r.js is the server side implementaiton of require.js for Nashorn/Rhino/Node
    nashorn.eval(read(rJsResource.openStream()), this._bindings);
    
    // r-config.js injects the tapestry module loading mechanism in the r.js implementation
    nashorn.eval(read(classpathResource("de/eddyson/tapestry/react/services/isomorphic/r-config.js")), this._bindings);
    
    return nashorn;
  }

  private NashornModuleLoader buildNashornModuleLoader(ScriptEngine nashorn) {
    return new NashornModuleLoader(nashorn, this.moduleManager, this._bindings, this.srs, this.tracker);
  }

  private String read(InputStream resource) throws IOException {
    
    try {
      return IOUtils.toString(resource, Charset.forName("UTF-8"));
    }
    finally {
      IOUtils.closeQuietly(resource);
    }
  }

  private InputStream classpathResource(String resource) {
    return getClass().getClassLoader().getResourceAsStream(resource);
  }

}
