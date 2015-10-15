package de.eddyson.tapestry.react;

import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.webresources.CacheMode;
import org.apache.tapestry5.internal.webresources.ResourceTransformerFactory;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.JavaScriptModuleConfiguration;
import org.apache.tapestry5.services.javascript.ModuleManager;

import de.eddyson.tapestry.react.services.CJSXCompiler;
import de.eddyson.tapestry.react.services.JSXCompiler;

public final class ReactModule {

  @Contribute(ModuleManager.class)
  public static void setupJSModules(final MappedConfiguration<String, JavaScriptModuleConfiguration> configuration,
      @Path("webjars:react:react.js") final Resource react,
      @Path("webjars:react:react-dom.js") final Resource reactDOM) {
    configuration.add("react", new JavaScriptModuleConfiguration(react));
    configuration.add("react-dom", new JavaScriptModuleConfiguration(reactDOM));
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

  @Contribute(ComponentClassResolver.class)
  public static void addLibraryMapping(final Configuration<LibraryMapping> configuration) {
    configuration.add(new LibraryMapping("react", "de.eddyson.tapestry.react"));
  }

  private ReactModule() {
  }

}
