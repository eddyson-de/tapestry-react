package de.eddyson.tapestry.react;

import java.io.IOException;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;

import de.eddyson.tapestry.react.modules.ReactCoreModule;
import de.eddyson.tapestry.react.services.BabelCompiler;

public class StandaloneCompiler {

  private final BabelCompiler compiler;

  public StandaloneCompiler() {
    final Registry registry = RegistryBuilder.buildAndStartupRegistry(ReactCoreModule.class);
    compiler = registry.getService(BabelCompiler.class);
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

      @Override
      public void run() {
        registry.shutdown();
      }
    }));
  }

  public String compile(final String input, final String fileName) throws IOException {
    return compile(input, fileName, true);
  }

  public String compile(final String input, final String fileName, final boolean productionMode) throws IOException {
    return compiler.compile(input, fileName, productionMode);
  }

}
