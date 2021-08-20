package de.eddyson.tapestry.react.modules;

import de.eddyson.tapestry.react.ReactSymbols;
import de.eddyson.tapestry.react.requestfilters.ReactAPIFilter;
import de.eddyson.tapestry.react.services.impl.BabelResourceTransformer;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.commons.OrderedConfiguration;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.http.Link;
import org.apache.tapestry5.http.services.RequestFilter;
import org.apache.tapestry5.http.services.RequestGlobals;
import org.apache.tapestry5.http.services.RequestHandler;
import org.apache.tapestry5.internal.util.VirtualResource;
import org.apache.tapestry5.internal.webresources.CacheMode;
import org.apache.tapestry5.internal.webresources.ResourceTransformerFactory;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.MarkupRenderer;
import org.apache.tapestry5.services.MarkupRendererFilter;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.JavaScriptModuleConfiguration;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@ImportModule(ReactCoreModule.class)
public final class ReactModule {

  private final static Logger logger = LoggerFactory.getLogger(ReactModule.class);

  @Contribute(ModuleManager.class)
  public static void setupJSModules(final MappedConfiguration<String, JavaScriptModuleConfiguration> configuration,
      final AssetSource assetSource, @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode,
      @Symbol(ReactSymbols.REACT_ASSET_PATH) final String reactAssetPath,
      @Symbol(ReactSymbols.REACT_ASSET_PATH_PRODUCTION) final String reactAssetPathProduction,
      @Symbol(ReactSymbols.REACT_DOM_ASSET_PATH) final String reactDomAssetPath,
      @Symbol(ReactSymbols.REACT_DOM_ASSET_PATH_PRODUCTION) final String reactDomAssetPathProduction,
      @Symbol(ReactSymbols.PROP_TYPES_ASSET_PATH) final String propTypesAssetPath,
      @Symbol(ReactSymbols.PROP_TYPES_ASSET_PATH_PRODUCTION) final String propTypesAssetPathProduction) {

    configuration.add("react", new JavaScriptModuleConfiguration(
        assetSource.resourceForPath(productionMode ? reactAssetPathProduction : reactAssetPath)));
    configuration.add("react-dom", new JavaScriptModuleConfiguration(
        assetSource.resourceForPath(productionMode ? reactDomAssetPathProduction : reactDomAssetPath)));
    configuration.add("prop-types", new JavaScriptModuleConfiguration(
        assetSource.resourceForPath(productionMode ? propTypesAssetPathProduction : propTypesAssetPath)));
  }

  @Contribute(StreamableResourceSource.class)
  public static void provideCompilers(final MappedConfiguration<String, ResourceTransformer> configuration,
      final ResourceTransformerFactory factory, @Autobuild final BabelResourceTransformer babelResourceTransformer) {
    // contribution ids are file extensions:

    // regular module with React support
    configuration.add("jsx", factory.createCompiler("text/javascript", "JSX", "JavaScript", babelResourceTransformer,
        CacheMode.SINGLE_FILE));
    // ES6 module with React support
    configuration.add("jsxm", factory.createCompiler("text/javascript", "JSXM", "JavaScript", babelResourceTransformer,
        CacheMode.SINGLE_FILE));
    // ES6 module
    configuration.add("jsm", factory.createCompiler("text/javascript", "JSXM", "JavaScript", babelResourceTransformer,
        CacheMode.SINGLE_FILE));

  }

  @Contribute(ComponentClassResolver.class)
  public static void addLibraryMapping(final Configuration<LibraryMapping> configuration) {
    configuration.add(new LibraryMapping("react", "de.eddyson.tapestry.react"));
  }

  @FactoryDefaults
  @Contribute(SymbolProvider.class)
  public static void setupDefaultConfiguration(final MappedConfiguration<String, Object> configuration) {
    configuration.add(ReactSymbols.REACT_ASSET_PATH,
        "classpath:de/eddyson/tapestry/react/services/react.development.js");
    configuration.add(ReactSymbols.REACT_ASSET_PATH_PRODUCTION,
        "classpath:de/eddyson/tapestry/react/services/react.production.min.js");
    configuration.add(ReactSymbols.REACT_DOM_ASSET_PATH,
        "classpath:de/eddyson/tapestry/react/services/react-dom.development.js");
    configuration.add(ReactSymbols.REACT_DOM_ASSET_PATH_PRODUCTION,
        "classpath:de/eddyson/tapestry/react/services/react-dom.production.min.js");
    configuration.add(ReactSymbols.PROP_TYPES_ASSET_PATH, "classpath:de/eddyson/tapestry/react/services/prop-types.js");
    configuration.add(ReactSymbols.PROP_TYPES_ASSET_PATH_PRODUCTION,
        "classpath:de/eddyson/tapestry/react/services/prop-types.min.js");
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
