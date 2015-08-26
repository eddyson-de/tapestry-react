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
import org.apache.tapestry5.internal.util.VirtualResource;
import org.apache.tapestry5.internal.webresources.CoffeeScriptCompiler;
import org.apache.tapestry5.internal.webresources.RhinoExecutor;
import org.apache.tapestry5.internal.webresources.RhinoExecutorPool;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.mozilla.javascript.NativeObject;

public class CJSXCompiler implements ResourceTransformer {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final RhinoExecutorPool executorPool;

  private final CoffeeScriptCompiler coffeescritptCompiler;

  @Override
  public ContentType getTransformedContentType() {
    return InternalConstants.JAVASCRIPT_CONTENT_TYPE;
  }

  public CJSXCompiler(
      @Path("classpath:de/eddyson/tapestry/react/services/coffee-react-transform-standalone.js") final Resource compiler,
      final OperationTracker tracker, @Autobuild final CoffeeScriptCompiler coffeescritptCompiler) {

    this.coffeescritptCompiler = coffeescritptCompiler;
    executorPool = new RhinoExecutorPool(tracker, Arrays.asList(compiler));
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

      final NativeObject result = (NativeObject) executor.invokeFunction("coffeeReactTransform", content);
      Resource resource = new VirtualResource() {

        @Override
        public InputStream openStream() throws IOException {
          return IOUtils.toInputStream(getString(result, "output"), UTF8);
        }
      };

      return coffeescritptCompiler.transform(resource, dependencies);

    } finally {
      executor.discard();
    }

  }
}
