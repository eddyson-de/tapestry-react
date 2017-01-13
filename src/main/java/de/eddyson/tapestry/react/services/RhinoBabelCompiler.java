package de.eddyson.tapestry.react.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.NativeObject;

import de.eddyson.tapestry.react.ReactSymbols;

public class RhinoBabelCompiler implements ResourceTransformer {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final FixedRhinoExecutorPool executorPool;

  private final boolean useColoredOutput;

  private final boolean productionMode;

  @Override
  public ContentType getTransformedContentType() {
    return InternalConstants.JAVASCRIPT_CONTENT_TYPE;
  }

  public RhinoBabelCompiler(final OperationTracker tracker,
      @Path("de/eddyson/tapestry/react/services/browser.js") final Resource mainCompiler,
      @Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {
    this.useColoredOutput = useColoredOutput;
    this.productionMode = productionMode;
    executorPool = new FixedRhinoExecutorPool(tracker, Arrays.<Resource>asList(mainCompiler));
  }

  private static String getString(final NativeObject object, final String key) {
    return object.get(key).toString();
  }

  @Override
  public InputStream transform(final Resource source, final ResourceDependencies dependencies) throws IOException {
    InputStream is = null;
    String content;

    try {
      is = source.openStream();
      content = IOUtils.toString(is, UTF8);
    } finally {
      InternalUtils.close(is);
    }

    RhinoExecutor executor = executorPool.get();

    boolean isES6Module = false;
    boolean withReact = false;
    String fileName = source.getFile();
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

    try {

      NativeObject result = (NativeObject) executor.invokeFunction("compileJSX", content, source.toString(),
          isES6Module, useColoredOutput, withReact, productionMode);

      if (result.containsKey("exception")) {
        throw new RuntimeException(getString(result, "exception"));
      }

      return IOUtils.toInputStream(getString(result, "output"), UTF8);

    } finally {
      executor.discard();
    }

  }
}
