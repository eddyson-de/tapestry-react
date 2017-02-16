package de.eddyson.tapestry.react.services.impl;

import java.util.Collections;

import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.mozilla.javascript.NativeObject;

import de.eddyson.tapestry.react.FixedRhinoExecutorPool;
import de.eddyson.tapestry.react.services.BabelCompiler;

public class RhinoBabelCompiler implements BabelCompiler {

  private final FixedRhinoExecutorPool executorPool;

  @Inject
  public RhinoBabelCompiler(final OperationTracker tracker) {
    this(tracker, new ClasspathResource("de/eddyson/tapestry/react/services/browser.js"));
  }

  public RhinoBabelCompiler(final OperationTracker tracker, final Resource mainCompiler) {

    executorPool = new FixedRhinoExecutorPool(tracker, Collections.<Resource>singletonList(mainCompiler));
  }

  private static String getString(final NativeObject object, final String key) {
    return object.get(key).toString();
  }

  @Override
  public String compile(final String input, final String fileName, final boolean outputAMD,
      final boolean useColoredOutput, final boolean includeReactPreset, final boolean productionMode,
      final boolean enableStage3Transformations) {

    RhinoExecutor executor = executorPool.get();

    try {

      NativeObject result = (NativeObject) executor.invokeFunction("compileJSX", input, fileName, outputAMD,
          useColoredOutput, includeReactPreset, productionMode, enableStage3Transformations);

      if (result.containsKey("exception")) {
        throw new RuntimeException(getString(result, "exception"));
      }

      return getString(result, "output");

    } finally {
      executor.discard();
    }
  }

}
