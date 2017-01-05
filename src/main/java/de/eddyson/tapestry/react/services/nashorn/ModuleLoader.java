package de.eddyson.tapestry.react.services.nashorn;

import java.io.IOException;

import javax.script.ScriptException;

/**
 * 
 * Module loaders are responsible for loading require.js / AMD modules into a
 * JavaScript Script Engine.
 * 
 */
public interface ModuleLoader {

  void loadModule(String moduleName) throws IOException, ScriptException;

}
