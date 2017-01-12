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

  private ScriptEngineUtilities() {
  }
}
