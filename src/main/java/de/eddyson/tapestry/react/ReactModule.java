package de.eddyson.tapestry.react;

import org.apache.tapestry5.internal.webresources.CacheMode;
import org.apache.tapestry5.internal.webresources.ResourceTransformerFactory;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.apache.tapestry5.services.assets.StreamableResourceSource;

import de.eddyson.tapestry.react.services.CJSXCompiler;
import de.eddyson.tapestry.react.services.JSXCompiler;

public final class ReactModule {

  @Contribute(StreamableResourceSource.class)
  public static void provideCompilers(final MappedConfiguration<String, ResourceTransformer> configuration,
      final ResourceTransformerFactory factory, @Autobuild final JSXCompiler jsxCompiler,
      @Autobuild final CJSXCompiler cjsxCompiler) {
    // contribution ids are file extensions:

    configuration.add("jsx",
        factory.createCompiler("text/javascript", "JSX", "JavaScript", jsxCompiler, CacheMode.NONE));
    configuration.add("cjsx",
        factory.createCompiler("text/javascript", "CJSX", "JavaScript", cjsxCompiler, CacheMode.NONE));

  }

  private ReactModule() {
  }

}
