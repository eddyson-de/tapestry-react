package de.eddyson.tapestry.react;

import java.io.IOException;

import org.apache.tapestry5.ioc.internal.OperationTrackerImpl;
import org.slf4j.LoggerFactory;

import de.eddyson.tapestry.react.components.ReactUtilities;
import de.eddyson.tapestry.react.services.BabelCompiler;
import de.eddyson.tapestry.react.services.impl.NodeBabelCompiler;
import de.eddyson.tapestry.react.services.impl.RhinoBabelCompiler;

public class StandaloneCompiler {

  private final BabelCompiler compiler;

  public StandaloneCompiler() {
    try {
      compiler = ReactUtilities.canUseNode() ? new NodeBabelCompiler()
          : new RhinoBabelCompiler(new OperationTrackerImpl(LoggerFactory.getLogger(RhinoBabelCompiler.class)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String compile(final String input, final String fileName) throws IOException {
    return compile(input, fileName, true);
  }

  public String compile(final String input, final String fileName, final boolean productionMode) throws IOException {
    return compiler.compile(input, fileName, true, true, true, productionMode, false);
  }

}
