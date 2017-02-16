package de.eddyson.tapestry.react.services;

import java.io.IOException;
import java.util.Map;

public interface BabelCompiler {

  Map<String, String> compile(Map<String, String> inputs, boolean outputAMD, boolean useColoredOutput,
      boolean includeReactPreset, boolean productionMode, boolean enableStage3Transformations) throws IOException;

}
