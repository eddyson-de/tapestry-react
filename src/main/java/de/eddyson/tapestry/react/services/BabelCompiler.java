package de.eddyson.tapestry.react.services;

import java.io.IOException;

public interface BabelCompiler {

  String compile(String input, final String fileName, boolean outputAMD, boolean useColoredOutput,
      boolean includeReactPreset, boolean productionMode, boolean enableStage3Transformations) throws IOException;

}
