package de.eddyson.tapestry.react.services.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.json.JSONObject;

import de.eddyson.tapestry.react.ReactSymbols;
import de.eddyson.tapestry.react.services.BabelCompiler;

public class NodeBabelCompiler implements BabelCompiler {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final boolean useColoredOutput;

  private final String compilerText;

  private final boolean enableStage3Transformations;

  @Inject
  public NodeBabelCompiler(@Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(ReactSymbols.ENABLE_STAGE_3_TRANSFORMATIONS) final boolean enableStage3Transformations)
      throws InterruptedException, IOException {
    this(new ClasspathResource("/de/eddyson/tapestry/react/services/browser.js"), useColoredOutput,
        enableStage3Transformations);
  }

  public NodeBabelCompiler(final Resource mainCompiler,
      @Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(ReactSymbols.ENABLE_STAGE_3_TRANSFORMATIONS) final boolean enableStage3Transformations)
      throws InterruptedException, IOException {
    this.useColoredOutput = useColoredOutput;
    this.enableStage3Transformations = enableStage3Transformations;

    try (InputStream is = mainCompiler.openStream()) {
      this.compilerText = IOUtils.toString(is, StandardCharsets.UTF_8);
    }
  }

  @Override
  public String compile(final String input, final String fileName, final boolean productionMode) throws IOException {
    boolean isES6Module = false;
    boolean withReact = false;
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
      params.put("content", input);
      params.put("filename", fileName);
      params.put("isES6Module", isES6Module);
      params.put("useColoredOutput", useColoredOutput);
      params.put("withReact", withReact);
      params.put("productionMode", productionMode);
      params.put("enableStage3Transformations", enableStage3Transformations);
      bw.append("var params = " + params.toCompactString() + ";");

      bw.append(
          "process.stdout.write(JSON.stringify(compileJSX(params.content, params.filename, params.isES6Module, params.useColoredOutput, params.withReact, params.productionMode, params.enableStage3Transformations)));");
    }

    ProcessBuilder pb = new ProcessBuilder("node", tempFile.toString());
    Process process = pb.start();
    try {
      int exitCode = process.waitFor();
      if (exitCode == 0) {
        try (InputStream is = process.getInputStream()) {
          String result = IOUtils.toString(is, UTF8);
          JSONObject resultJSON = new JSONObject(result);
          if (resultJSON.has("exception")) {
            throw new RuntimeException(resultJSON.getString("exception"));
          }
          return resultJSON.getString("output");

        }
      } else {
        try (InputStream err = process.getErrorStream()) {
          String result = IOUtils.toString(err);

          throw new RuntimeException("Compiler process exited with code " + exitCode + ", message: " + result);
        }
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      Files.delete(tempFile);
    }

  }

}
