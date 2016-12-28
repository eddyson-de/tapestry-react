package de.eddyson.tapestry.react.services.nashorn;

import java.io.IOException;
import java.io.InputStream;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.apache.tapestry5.services.assets.StreamableResourceProcessing;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nashorn module loader get's inserted into JavaScript executed by Nashorn and
 * loads AMD modules from classpath
 *
 * @author Felix Gonschorek
 */
public class NashornModuleLoader {

  private static final Logger log = LoggerFactory.getLogger(NashornModuleLoader.class);

  private final ScriptEngine engine;
  private final ModuleManager moduleManager;
  private final Bindings bindings;
  private final StreamableResourceSource srs;
  private final ResourceChangeTracker tracker;

  private final AssetSource assetSource;

  public NashornModuleLoader(ScriptEngine engine, ModuleManager moduleManager, Bindings bindings,
      StreamableResourceSource srs, ResourceChangeTracker tracker, AssetSource assetSource) {
    super();
    this.engine = engine;
    this.moduleManager = moduleManager;
    this.bindings = bindings;
    this.srs = srs;
    this.tracker = tracker;
    this.assetSource = assetSource;
  }

  public void loadModule(String name) throws IOException, ScriptException {

    Resource resource = null;
    // special case "underscore" - TODO: investigate later
    if ("underscore".equals(name)) {
      resource = this.assetSource.getClasspathAsset("/META-INF/assets/tapestry5/underscore-1.8.3.js").getResource();
    } else {
      resource = this.moduleManager.findResourceForModule(name);
    }
    if (resource == null) {
      throw new RuntimeException("Could not load module / resource not found for module: " + name);
    }
    StreamableResource streamableResource = this.srs.getStreamableResource(resource,
        StreamableResourceProcessing.COMPRESSION_DISABLED, this.tracker);
    try (InputStream is = streamableResource.openStream()) {
      log.debug("Evaluating {} in nashorn javascript engine", resource.getFile());
      this.engine.eval(IOUtils.toString(is), this.bindings);
      log.debug("Finished evaluating {} in nashorn javascript engine", resource.getFile());
    }
  }
}
