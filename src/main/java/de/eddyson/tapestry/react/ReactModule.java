package de.eddyson.tapestry.react;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
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
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.assets.ResourceTransformer;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.JavaScriptModuleConfiguration;
import org.apache.tapestry5.services.javascript.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eddyson.tapestry.react.requestfilters.ReactAPIFilter;
import de.eddyson.tapestry.react.services.BabelCompiler;
import de.eddyson.tapestry.react.services.CJSXCompiler;
import de.eddyson.tapestry.react.services.NodeBabelCompiler;
import de.eddyson.tapestry.react.services.nashorn.ReactRenderEngine;
import de.eddyson.tapestry.react.services.nashorn.NashornReactRenderEngineImplementation;

public final class ReactModule {

  private final static Logger logger = LoggerFactory.getLogger(ReactModule.class);

  public static void bind(ServiceBinder binder) {
    binder.bind(ReactRenderEngine.class, NashornReactRenderEngineImplementation.class);
  }

  @Contribute(ModuleManager.class)
  public static void setupJSModules(final MappedConfiguration<String, JavaScriptModuleConfiguration> configuration,
      final AssetSource assetSource, @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode,
      @Symbol(ReactSymbols.USE_REACT_WITH_ADDONS) final boolean useReactWithAddons,
      @Symbol(ReactSymbols.REACT_ASSET_PATH) final String reactAssetPath,
      @Symbol(ReactSymbols.REACT_ASSET_PATH_PRODUCTION) final String reactAssetPathProduction,
      @Symbol(ReactSymbols.REACT_WITH_ADDONS_ASSET_PATH) final String reactWithAddonsAssetPath,
      @Symbol(ReactSymbols.REACT_WITH_ADDONS_ASSET_PATH_PRODUCTION) final String reactWithAddonsAssetPathProduction,
      @Symbol(ReactSymbols.REACT_DOM_ASSET_PATH) final String reactDomAssetPath,
      @Symbol(ReactSymbols.REACT_DOM_ASSET_PATH_PRODUCTION) final String reactDomAssetPathProduction,
      @Path("webjars:react:$version/react-dom-server.js") final Resource reactDomServer,
      @Path("de/eddyson/tapestry/react/services/isomorphic/run.js") final Resource reactRunServerRendering) {

    String reactAssetPathToUse = useReactWithAddons
        ? (productionMode ? reactWithAddonsAssetPathProduction : reactWithAddonsAssetPath)
        : (productionMode ? reactAssetPathProduction : reactAssetPath);

    configuration.add("react", new JavaScriptModuleConfiguration(assetSource.resourceForPath(reactAssetPathToUse)));
    configuration.add("react-dom", new JavaScriptModuleConfiguration(
        assetSource.resourceForPath(productionMode ? reactDomAssetPathProduction : reactDomAssetPath)));
    
    // for isomorphic server side rendering:
    configuration.add("react-dom-server", new JavaScriptModuleConfiguration(reactDomServer));
    configuration.add("eddyson/react/isomorphic/run", new JavaScriptModuleConfiguration(reactRunServerRendering));
  }

  @Contribute(StreamableResourceSource.class)
  public static void provideCompilers(final MappedConfiguration<String, ResourceTransformer> configuration,
      final ResourceTransformerFactory factory, final ObjectLocator objectLocator,
      @Autobuild final CJSXCompiler cjsxCompiler,
      @Symbol(ReactSymbols.USE_NODE_IF_AVAILABLE) final boolean useNodeIfAvailable) throws InterruptedException {
    // contribution ids are file extensions:

    boolean canUseNode = false;
    if (useNodeIfAvailable) {
      try {
        ProcessBuilder pb = new ProcessBuilder("node", "-v");
        int exitCode = pb.start().waitFor();

        if (exitCode == 0) {
          canUseNode = true;
        } else {
          logger.warn("Received exit code {} from call to node executable, falling back to Rhino compiler.");
        }
      } catch (IOException e) {
        logger.warn("Failed to call node executable, make sure it is on the PATH. Falling back to Rhino compiler.");
      }
    }
    ResourceTransformer jsxCompiler = canUseNode ? objectLocator.autobuild(NodeBabelCompiler.class)
        : objectLocator.autobuild(BabelCompiler.class);

    // regular module with React support
    configuration.add("jsx",
        factory.createCompiler("text/javascript", "JSX", "JavaScript", jsxCompiler, CacheMode.SINGLE_FILE));
    // ES6 module with React support
    configuration.add("jsxm",
        factory.createCompiler("text/javascript", "JSXM", "JavaScript", jsxCompiler, CacheMode.SINGLE_FILE));
    // ES6 module
    configuration.add("jsm",
        factory.createCompiler("text/javascript", "JSXM", "JavaScript", jsxCompiler, CacheMode.SINGLE_FILE));
    // regular CoffeeScript module with React support
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
    configuration.add(ReactSymbols.USE_NODE_IF_AVAILABLE, true);
    configuration.add(ReactSymbols.USE_REACT_WITH_ADDONS, false);
    configuration.add(ReactSymbols.REACT_ASSET_PATH, "webjars:react:$version/react.js");
    configuration.add(ReactSymbols.REACT_ASSET_PATH_PRODUCTION, "webjars:react:$version/react.min.js");
    configuration.add(ReactSymbols.REACT_WITH_ADDONS_ASSET_PATH, "webjars:react:$version/react-with-addons.js");
    configuration.add(ReactSymbols.REACT_WITH_ADDONS_ASSET_PATH_PRODUCTION,
        "webjars:react:$version/react-with-addons.min.js");
    configuration.add(ReactSymbols.REACT_DOM_ASSET_PATH, "webjars:react:$version/react-dom.js");
    configuration.add(ReactSymbols.REACT_DOM_ASSET_PATH_PRODUCTION, "webjars:react:$version/react-dom.min.js");
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
