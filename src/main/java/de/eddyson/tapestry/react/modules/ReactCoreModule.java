package de.eddyson.tapestry.react.modules;

import de.eddyson.tapestry.react.ReactSymbols;
import de.eddyson.tapestry.react.components.ReactUtilities;
import de.eddyson.tapestry.react.services.BabelCompiler;
import de.eddyson.tapestry.react.services.impl.NodeBabelCompiler;
import de.eddyson.tapestry.react.services.impl.RhinoBabelCompiler;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.commons.ObjectLocator;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;

public final class ReactCoreModule {

  @FactoryDefaults
  @Contribute(SymbolProvider.class)
  public static void setupDefaultConfiguration(final MappedConfiguration<String, Object> configuration) {
    configuration.add(ReactSymbols.USE_COLORED_BABEL_OUTPUT, true);
    configuration.add(ReactSymbols.USE_NODE_IF_AVAILABLE, true);
    configuration.add(ReactSymbols.ENABLE_STAGE_3_TRANSFORMATIONS, false);
  }

  public static BabelCompiler build(final ObjectLocator objectLocator,
      @Symbol(ReactSymbols.USE_NODE_IF_AVAILABLE) final boolean useNodeIfAvailable) {
    boolean canUseNode = false;
    if (useNodeIfAvailable) {
      canUseNode = ReactUtilities.canUseNode();
    }
    return canUseNode ? objectLocator.autobuild(NodeBabelCompiler.class)
        : objectLocator.autobuild(RhinoBabelCompiler.class);
  }

  private ReactCoreModule() {

  }
}
