package de.eddyson.tapestry.react;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.internal.util.VirtualResource;
import org.apache.tapestry5.internal.webresources.CacheMode;
import org.apache.tapestry5.internal.webresources.ResourceTransformerFactory;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.MarkupRenderer;
import org.apache.tapestry5.services.MarkupRendererFilter;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.JavaScriptModuleConfiguration;
import org.apache.tapestry5.services.javascript.ModuleManager;

import de.eddyson.tapestry.react.requestfilters.ReactAPIFilter;
import de.eddyson.tapestry.react.services.CJSXCompiler;
import de.eddyson.tapestry.react.services.JSXCompiler;

public final class ReactModule {

  @Contribute(ModuleManager.class)
  public static void setupJSModules(final MappedConfiguration<String, JavaScriptModuleConfiguration> configuration,
      @Path("webjars:react:react.js") final Resource react, @Path("webjars:react:react-dom.js") final Resource reactDOM,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {

    Resource reactResource = react;
    if (productionMode) {
      // issue #5
      final Pattern development = Pattern.compile(Pattern.quote("\"development\" !== 'production'"));
      reactResource = new VirtualResource() {

        @Override
        public InputStream openStream() throws IOException {
          try (InputStream reactResourceStream = react.openStream()) {
            String content = IOUtils.toString(reactResourceStream, StandardCharsets.UTF_8);
            String alteredContent = development.matcher(content).replaceAll("false");
            return IOUtils.toInputStream(alteredContent, StandardCharsets.UTF_8);
          }
        }

        @Override
        public String getFile() {
          return "react-production.generated.js";
        }
      };
    }

    configuration.add("react", new JavaScriptModuleConfiguration(reactResource));
    configuration.add("react-dom", new JavaScriptModuleConfiguration(reactDOM));
  }

  @Contribute(StreamableResourceSource.class)
  public static void provideCompilers(final MappedConfiguration<String, ResourceTransformer> configuration,
      final ResourceTransformerFactory factory, @Autobuild final JSXCompiler jsxCompiler,
      @Autobuild final CJSXCompiler cjsxCompiler) {
    // contribution ids are file extensions:

    configuration.add("jsx",
        factory.createCompiler("text/javascript", "JSX", "JavaScript", jsxCompiler, CacheMode.SINGLE_FILE));
    configuration.add("jsxm",
        factory.createCompiler("text/javascript", "JSXM", "JavaScript", jsxCompiler, CacheMode.SINGLE_FILE));
    configuration.add("cjsx",
        factory.createCompiler("text/javascript", "CJSX", "JavaScript", cjsxCompiler, CacheMode.SINGLE_FILE));

  }

  @Contribute(ComponentClassResolver.class)
  public static void addLibraryMapping(final Configuration<LibraryMapping> configuration) {
    configuration.add(new LibraryMapping("react", "de.eddyson.tapestry.react"));
  }

  @FactoryDefaults
  @Contribute(SymbolProvider.class)
  public static void setupDefaultConfiguration(final MappedConfiguration<String, Object> configuration) {
    configuration.add(ReactSymbols.USE_COLORED_BABEL_OUTPUT, true);
  }

  @Contribute(ModuleManager.class)
  public static void addApplicationConfigModule(
      final MappedConfiguration<String, JavaScriptModuleConfiguration> configuration, final SymbolSource symbolSource,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {

    final JSONObject config = new JSONObject();

    for (String symbolName : new String[] { SymbolConstants.CONTEXT_PATH, SymbolConstants.EXECUTION_MODE,
        SymbolConstants.PRODUCTION_MODE, SymbolConstants.START_PAGE_NAME, SymbolConstants.TAPESTRY_VERSION,
        SymbolConstants.SUPPORTED_LOCALES }) {
      String value = symbolSource.valueForSymbol(symbolName);
      config.put(symbolName, value);
    }
    config.put("react-api-path", ReactAPIFilter.path);

    StringBuilder sb = new StringBuilder();
    sb.append("define(");
    sb.append(config.toString(productionMode));
    sb.append(");");
    final byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);

    configuration.add("eddyson/react/application-config", new JavaScriptModuleConfiguration(new VirtualResource() {

      @Override
      public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(bytes);
      }

      @Override
      public String getFile() {
        return "application-config.js";
      }

      @Override
      public URL toURL() {
        return null;
      }
    }));

  }

  @Contribute(MarkupRenderer.class)
  public static void prepareHTMLPageOnRender(final OrderedConfiguration<MarkupRendererFilter> configuration,
      final RequestGlobals requestGlobals, final PageRenderLinkSource pageRenderLinkSource) {
    configuration.add("AddPageName", new MarkupRendererFilter() {

      @Override
      public void renderMarkup(final MarkupWriter writer, final MarkupRenderer renderer) {

        renderer.renderMarkup(writer);
        Element html = writer.getDocument().find("html");
        if (html != null) {
          Link link = pageRenderLinkSource.createPageRenderLinkWithContext(requestGlobals.getActivePageName());
          for (String parameterName : link.getParameterNames()) {
            link = link.removeParameter(parameterName);
          }
          String url = link.toURI();
          html.attributes("data-page-base-url", url);
        }
      }
    });
  }

  @Contribute(RequestHandler.class)
  public static void addReactAPIRequestFilter(final OrderedConfiguration<RequestFilter> configuration) {
    configuration.addInstance("react-api", ReactAPIFilter.class);
  }

  private ReactModule() {
  }

}
