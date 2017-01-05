package de.eddyson.tapestry.react.services.nashorn;

import java.io.IOException;

import javax.script.ScriptException;

public interface ModuleLoader {

  void loadModule(String name) throws IOException, ScriptException;

}
