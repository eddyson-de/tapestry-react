package de.eddyson.tapestry.react.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.internal.webresources.RhinoExecutorPool;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
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

  public JSXCompiler(final OperationTracker tracker) {
    Resource mainCompiler = new ClasspathResource("de/eddyson/tapestry/react/services/browser.js");
    executorPool = new RhinoExecutorPool(tracker, Arrays.<Resource> asList(mainCompiler,
        new ClasspathResource("de/eddyson/tapestry/react/services/jsx-compiler-wrapper.js")));
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
    String fileName = source.getFile();
    if (fileName != null && fileName.endsWith(".jsxm")) {
      isES6Module = true;
    }

    try {

      NativeObject result = (NativeObject) executor.invokeFunction("compileJSX", content, source.toString(),
          isES6Module);

      if (result.containsKey("exception")) {
        throw new RuntimeException(getString(result, "exception"));
      }

      return IOUtils.toInputStream(getString(result, "output"), UTF8);

    } finally {
      executor.discard();
    }

  }
}
