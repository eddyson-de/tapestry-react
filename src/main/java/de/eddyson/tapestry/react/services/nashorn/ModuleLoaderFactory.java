package de.eddyson.tapestry.react.services.nashorn;

import javax.script.ScriptEngine;

/**
 *
 * ModuleLoaderFactory is responsible for building a ModuleLoader for a ScriptEngine instance 
 *
 */
public interface ModuleLoaderFactory {
  
  ModuleLoader buildModuleLoader(ScriptEngine engine);
  
}
