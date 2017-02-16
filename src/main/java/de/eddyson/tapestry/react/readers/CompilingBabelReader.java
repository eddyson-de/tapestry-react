package de.eddyson.tapestry.react.readers;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.Pattern;

import de.eddyson.tapestry.react.StandaloneCompiler;

public class CompilingBabelReader extends FilterReader {

  private static final StandaloneCompiler compiler = new StandaloneCompiler();
  private final static Pattern delimiter = Pattern.compile("\\A");

  public CompilingBabelReader(final Reader reader) throws IOException {
    super(createJavaScriptReader(reader));

  }

  private static Reader createJavaScriptReader(final Reader coffeeScriptReader) throws IOException {
    try (Scanner sc = new Scanner(coffeeScriptReader)) {
      sc.useDelimiter(delimiter);
      String content = sc.next();
      String compiled = compiler.compile(content, null);
      return new StringReader(compiled);

    }
  }

}
