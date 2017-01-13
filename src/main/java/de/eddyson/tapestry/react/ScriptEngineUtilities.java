package de.eddyson.tapestry.react;

import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public final class ScriptEngineUtilities {

  public static void checkNothingLeaked(final ScriptEngine engine, final Set<String> expectedKeysInEngineScope) {
    Bindings globalBindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
    if (!globalBindings.isEmpty()) {
      throw new RuntimeException("Global scope values leaked: " + globalBindings.keySet());
    }
    Bindings engineBindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
    if (!engineBindings.keySet().equals(expectedKeysInEngineScope)) {
      throw new RuntimeException("Unexpected engine scope values: " + engineBindings.keySet());
    }
  }

  public static boolean isSupportedScriptEngine() {
    String javaVersion = System.getProperty("java.version");
    String[] parts = javaVersion.split("_");
    if ("1.8".equals(parts[0]) && Integer.parseInt(parts[1]) < 91) {
      return false;
    }
    return true;
  }

  private ScriptEngineUtilities() {
  }
}
