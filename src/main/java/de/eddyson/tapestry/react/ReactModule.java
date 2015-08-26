package de.eddyson.tapestry.react;

import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.webresources.CacheMode;
import org.apache.tapestry5.internal.webresources.ResourceTransformerFactory;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.JavaScriptModuleConfiguration;
import org.apache.tapestry5.services.javascript.ModuleManager;

import de.eddyson.tapestry.react.services.CJSXCompiler;
import de.eddyson.tapestry.react.services.JSXCompiler;

public final class ReactModule {

  @Contribute(ModuleManager.class)
  public static void setupJSModules(final MappedConfiguration<String, JavaScriptModuleConfiguration> configuration,
      // we need to qualify the js file because there are multiple matches for
      // "react.js". Can probably be removed in a later version
      @Path("webjars:react:0.13.3/react.js") final Resource react) {
    configuration.add("react", new JavaScriptModuleConfiguration(react));
  }

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
