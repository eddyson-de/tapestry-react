package de.eddyson.tapestry.react.services.nashorn;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.assets.StreamableResource;
import org.apache.tapestry5.services.assets.StreamableResourceProcessing;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nashorn module loader gets inserted into JavaScript executed by Nashorn and
 * loads AMD modules from the Tapestry asset pipline
 */
public class NashornModuleLoader implements ModuleLoader {

  private static final Logger log = LoggerFactory.getLogger(NashornModuleLoader.class);

  private final ModuleManager moduleManager;
  private final StreamableResourceSource srs;
  private final ResourceChangeTracker tracker;
  private final ScriptEngine engine;

  public NashornModuleLoader(ModuleManager moduleManager, StreamableResourceSource srs, ResourceChangeTracker tracker,
      ScriptEngine scriptEngine) {
    super();
    this.moduleManager = moduleManager;
    this.srs = srs;
    this.tracker = tracker;
    this.engine = scriptEngine;
  }

  @Override
  public void loadModule(String name) throws IOException, ScriptException {
    Resource resource = this.moduleManager.findResourceForModule(name);
    if (resource == null) {
      throw new RuntimeException("Could not load module / resource not found for module: " + name);
    }
    StreamableResource streamableResource = this.srs.getStreamableResource(resource,
        StreamableResourceProcessing.COMPRESSION_DISABLED, this.tracker);
    try (InputStream is = streamableResource.openStream()) {
      log.debug("Evaluating {} in nashorn javascript engine", resource.getFile());
      this.engine.eval(IOUtils.toString(is, Charset.forName("UTF-8")));
      log.debug("Finished evaluating {} in nashorn javascript engine", resource.getFile());
    }
  }
}
