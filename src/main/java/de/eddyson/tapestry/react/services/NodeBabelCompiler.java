package de.eddyson.tapestry.react.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.OperationTracker;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;

import de.eddyson.tapestry.react.ReactSymbols;

public class NodeBabelCompiler implements ResourceTransformer {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final boolean useColoredOutput;

  private final String compilerText;

  private final boolean productionMode;

  @Override
  public ContentType getTransformedContentType() {
    return InternalConstants.JAVASCRIPT_CONTENT_TYPE;
  }

  public NodeBabelCompiler(final OperationTracker tracker,
      @Path("de/eddyson/tapestry/react/services/browser.js") final Resource mainCompiler,
      @Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) throws InterruptedException, IOException {
    this.useColoredOutput = useColoredOutput;
    this.productionMode = productionMode;

    try (InputStream is = mainCompiler.openStream()) {
      this.compilerText = IOUtils.toString(is, StandardCharsets.UTF_8);
    }
  }

  @Override
  public InputStream transform(final Resource source, final ResourceDependencies dependencies) throws IOException {
    String content;

    try (InputStream is = source.openStream()) {
      content = IOUtils.toString(is, UTF8);
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
    java.nio.file.Path tempFile = Files.createTempFile("babel-compile", ".js");
    // TODO use a shared compiler file and pass the parameters to node -e
    try (BufferedWriter bw = Files.newBufferedWriter(tempFile, UTF8)) {
      bw.append(compilerText);
      bw.newLine();
      JSONObject params = new JSONObject();
      params.put("content", content);
      params.put("filename", source.toString());
      params.put("isES6Module", isES6Module);
      params.put("useColoredOutput", useColoredOutput);
      params.put("withReact", withReact);
      params.put("productionMode", productionMode);
      bw.append("var params = " + params.toCompactString() + ";");

      bw.append(
          "process.stdout.write(compileJSX(params.content, params.filename, params.isES6Module, params.useColoredOutput, params.withReact, params.productionMode));");
    }

    ProcessBuilder pb = new ProcessBuilder("node", tempFile.toString());
    Process process = pb.start();
    try {
      process.waitFor();
      if (process.exitValue() == 0) {
        try (InputStream is = process.getInputStream()) {
          String result = IOUtils.toString(is, UTF8);
          return IOUtils.toInputStream(result, UTF8);

        }
      } else {
        try (InputStream is = process.getErrorStream()) {
          String errorMessage = IOUtils.toString(is, UTF8);
          throw new RuntimeException(errorMessage);
        }
      }

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      Files.delete(tempFile);
    }

  }
}
