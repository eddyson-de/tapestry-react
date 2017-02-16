package de.eddyson.tapestry.react.services;

import java.io.IOException;

public interface BabelCompiler {

  String compile(String input, final String fileName, boolean productionMode) throws IOException;

}
