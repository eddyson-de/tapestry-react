package de.eddyson.tapestry.react.modules;

import java.io.IOException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eddyson.tapestry.react.ReactSymbols;
import de.eddyson.tapestry.react.services.BabelCompiler;
import de.eddyson.tapestry.react.services.impl.NodeBabelCompiler;
import de.eddyson.tapestry.react.services.impl.RhinoBabelCompiler;

public final class ReactCoreModule {

  private final static Logger logger = LoggerFactory.getLogger(ReactCoreModule.class);

  @FactoryDefaults
  @Contribute(SymbolProvider.class)
  public static void setupDefaultConfiguration(final MappedConfiguration<String, Object> configuration) {
    configuration.add(ReactSymbols.USE_COLORED_BABEL_OUTPUT, true);
    configuration.add(ReactSymbols.USE_NODE_IF_AVAILABLE, true);
    configuration.add(ReactSymbols.ENABLE_STAGE_3_TRANSFORMATIONS, false);
  }

  public static BabelCompiler build(final ObjectLocator objectLocator,
      @Symbol(ReactSymbols.USE_NODE_IF_AVAILABLE) final boolean useNodeIfAvailable) throws InterruptedException {
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
    return canUseNode ? objectLocator.autobuild(NodeBabelCompiler.class)
        : objectLocator.autobuild(RhinoBabelCompiler.class);
  }

  private ReactCoreModule() {

  }
}
