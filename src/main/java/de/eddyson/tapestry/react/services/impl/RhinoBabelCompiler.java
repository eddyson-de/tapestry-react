package de.eddyson.tapestry.react.services.impl;

import java.util.Collections;

import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.mozilla.javascript.NativeObject;

import de.eddyson.tapestry.react.FixedRhinoExecutorPool;
import de.eddyson.tapestry.react.ReactSymbols;
import de.eddyson.tapestry.react.services.BabelCompiler;

public class RhinoBabelCompiler implements BabelCompiler {

  private final FixedRhinoExecutorPool executorPool;

  private final boolean useColoredOutput;

  private final boolean enableStage3Transformations;

  @Inject
  public RhinoBabelCompiler(final OperationTracker tracker,
      @Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(ReactSymbols.ENABLE_STAGE_3_TRANSFORMATIONS) final boolean enableStage3Transformations) {
    this(tracker, new ClasspathResource("de/eddyson/tapestry/react/services/browser.js"), useColoredOutput,
        enableStage3Transformations);
  }

  public RhinoBabelCompiler(final OperationTracker tracker, final Resource mainCompiler,
      @Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(ReactSymbols.ENABLE_STAGE_3_TRANSFORMATIONS) final boolean enableStage3Transformations) {
    this.useColoredOutput = useColoredOutput;
    this.enableStage3Transformations = enableStage3Transformations;
    executorPool = new FixedRhinoExecutorPool(tracker, Collections.<Resource>singletonList(mainCompiler));
  }

  private static String getString(final NativeObject object, final String key) {
    return object.get(key).toString();
  }

  @Override
  public String compile(final String input, final String fileName, final boolean productionMode) {
    boolean isES6Module = false;
    boolean withReact = false;
    if (fileName != null) {
      int idx = fileName.lastIndexOf('.');
      if (idx >= -1) {
        String extension = fileName.substring(idx + 1);
        switch (extension) {
        case "jsm":
          isES6Module = true;
          break;
        case "jsxm":
          isES6Module = true;
          withReact = true;
          break;
        case "jsx":
          withReact = true;
          break;
        }
      }
    }
    RhinoExecutor executor = executorPool.get();

    try {

      NativeObject result = (NativeObject) executor.invokeFunction("compileJSX", input, fileName, isES6Module,
          useColoredOutput, withReact, productionMode, enableStage3Transformations);

      if (result.containsKey("exception")) {
        throw new RuntimeException(getString(result, "exception"));
      }

      return getString(result, "output");

    } finally {
      executor.discard();
    }
  }

}
