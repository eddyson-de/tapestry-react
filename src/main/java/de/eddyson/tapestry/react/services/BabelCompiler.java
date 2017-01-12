package de.eddyson.tapestry.react.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;

import de.eddyson.tapestry.react.ReactSymbols;
import de.eddyson.tapestry.react.ScriptEngineUtilities;

public class BabelCompiler implements ResourceTransformer {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final boolean useColoredOutput;

  private final boolean productionMode;

  private final ScriptEngine engine;

  private final static Set<String> expectedKeysInEngineScope = CollectionFactory.newSet("__core-js_shared__",
      "compileJSX", "Babel");

  @Override
  public ContentType getTransformedContentType() {
    return InternalConstants.JAVASCRIPT_CONTENT_TYPE;
  }

  public BabelCompiler(@Path("de/eddyson/tapestry/react/services/browser.js") final Resource mainCompiler,
      @Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) throws IOException, ScriptException {
    this.useColoredOutput = useColoredOutput;
    this.productionMode = productionMode;

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    if (!(engine instanceof Invocable)) {
      throw new IllegalStateException("ScriptEngine for JavaScript does not implement Invocable");
    }
    try (Reader reader = new InputStreamReader(mainCompiler.openStream(), StandardCharsets.UTF_8)) {
      engine.eval(reader);
    }
    ScriptEngineUtilities.checkNothingLeaked(engine, expectedKeysInEngineScope);
    this.engine = engine;
  }

  @Override
  public InputStream transform(final Resource source, final ResourceDependencies dependencies) throws IOException {
    InputStream is = null;
    String content;

    try {
      is = source.openStream();
      content = IOUtils.toString(is, UTF8);
    } finally {
      InternalUtils.close(is);
    }

    boolean isES6Module = false;
    boolean withReact = false;
    String fileName = source.getFile();
    if (fileName != null) {
      int idx = fileName.lastIndexOf('.');
      if (idx >= -1) {
        String extension = fileName.substring(idx + 1);
        switch (extension) {
        case "jsm":
          isES6Module = true;
          break;
        case "jsxm":
          isES6Module = true;
          withReact = true;
          break;
        case "jsx":
          withReact = true;
          break;
        }
      }
    }

    try {
      String result = (String) ((Invocable) engine).invokeFunction("compileJSX", content, source.toString(),
          isES6Module, useColoredOutput, withReact, productionMode);
      ScriptEngineUtilities.checkNothingLeaked(engine, expectedKeysInEngineScope);

      return IOUtils.toInputStream(result, UTF8);

    } catch (ScriptException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }

  }
}
