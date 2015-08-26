package de.eddyson.tapestry.react.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.internal.webresources.RhinoExecutorPool;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.NativeObject;

public class JSXCompiler implements ResourceTransformer {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final RhinoExecutorPool executorPool;

  @Override
  public ContentType getTransformedContentType() {
    return InternalConstants.JAVASCRIPT_CONTENT_TYPE;
  }

  public JSXCompiler(@Path("webjars:react:JSXTransformer.js") final Resource mainCompiler,
      @Path("classpath:de/eddyson/toolbox/services/impl/jsx-compiler-wrapper.js") final Resource shim,
      final OperationTracker tracker) {

    executorPool = new RhinoExecutorPool(tracker, Arrays.asList(mainCompiler, shim));
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

    try {

      NativeObject result = (NativeObject) executor.invokeFunction("compileJSX", content, source.toString());

      if (result.containsKey("exception")) {
        throw new RuntimeException(getString(result, "exception"));
      }

      return IOUtils.toInputStream(getString(result, "output"), UTF8);

    } finally {
      executor.discard();
    }

  }
}
