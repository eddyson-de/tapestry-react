package de.eddyson.tapestry.react;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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
    return compile(Objects.requireNonNull(input), Objects.requireNonNull(fileName), true);
  }

  public Map<String, String> compile(final Map<String, String> inputs) throws IOException {
    return compile(Objects.requireNonNull(inputs), true);
  }

  public String compile(final String input, final String fileName, final boolean productionMode) throws IOException {
    return compiler.compile(Collections.singletonMap(Objects.requireNonNull(fileName), Objects.requireNonNull(input)),
        true, true, true, productionMode, false).get(fileName);
  }

  public Map<String, String> compile(final Map<String, String> inputs, final boolean productionMode)
      throws IOException {
    return compiler.compile(inputs, true, true, true, productionMode, false);
  }
}
