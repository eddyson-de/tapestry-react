package de.eddyson.tapestry.react.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.mozilla.javascript.NativeObject;

import de.eddyson.tapestry.react.FixedRhinoExecutorPool;
import de.eddyson.tapestry.react.services.BabelCompiler;

public class RhinoBabelCompiler implements BabelCompiler {

  private final FixedRhinoExecutorPool executorPool;

  @Inject
  public RhinoBabelCompiler(final OperationTracker tracker) {
    this(tracker, new ClasspathResource(RhinoBabelCompiler.class.getClassLoader(),
        "de/eddyson/tapestry/react/services/babel.min.js"));
  }

  public RhinoBabelCompiler(final OperationTracker tracker, final Resource mainCompiler) {
    executorPool = new FixedRhinoExecutorPool(tracker,
        CollectionFactory.<Resource, Resource>newList(mainCompiler,
            new ClasspathResource(RhinoBabelCompiler.class.getClassLoader(),
                "de/eddyson/tapestry/react/services/babel-compiler-wrapper.js")));
  }

  private static String getString(final NativeObject object, final String key) {
    return object.get(key).toString();
  }

  @Override
  public Map<String, String> compile(final Map<String, String> inputs, final boolean outputAMD,
      final boolean useColoredOutput, final boolean includeReactPreset, final boolean productionMode,
      final boolean enableStage3Transformations) {

    RhinoExecutor executor = executorPool.get();

    try {
      NativeObject inputsAsNativeObject = new NativeObject();
      for (Map.Entry<String, String> entry : inputs.entrySet()) {
        inputsAsNativeObject.defineProperty(entry.getKey(), entry.getValue(), NativeObject.READONLY);
      }

      NativeObject result = (NativeObject) executor.invokeFunction("compileJSX", inputsAsNativeObject, outputAMD,
          useColoredOutput, includeReactPreset, productionMode, enableStage3Transformations);

      if (result.containsKey("exception")) {
        throw new RuntimeException(getString(result, "exception"));
      }
      NativeObject output = (NativeObject) result.get("output");
      Map<String, String> compiled = new HashMap<>(inputs.size());
      for (String fileName : inputs.keySet()) {
        compiled.put(fileName, getString(output, fileName));
      }
      return compiled;

    } finally {
      executor.discard();
    }
  }

}
