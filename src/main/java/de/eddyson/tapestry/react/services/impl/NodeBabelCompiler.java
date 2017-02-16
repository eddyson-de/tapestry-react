package de.eddyson.tapestry.react.services.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.json.JSONObject;

import de.eddyson.tapestry.react.services.BabelCompiler;

public class NodeBabelCompiler implements BabelCompiler {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final String compilerText;

  @Inject
  public NodeBabelCompiler() throws IOException {
    this(new ClasspathResource(NodeBabelCompiler.class.getClassLoader(),
        "/de/eddyson/tapestry/react/services/browser.js"));
  }

  public NodeBabelCompiler(final Resource mainCompiler) throws IOException {
    try (InputStream is = mainCompiler.openStream()) {
      this.compilerText = IOUtils.toString(is, StandardCharsets.UTF_8);
    }
  }

  @Override
  public Map<String, String> compile(final Map<String, String> inputs, final boolean outputAMD,
      final boolean useColoredOutput, final boolean includeReactPreset, final boolean productionMode,
      final boolean enableStage3Transformations) throws IOException {

    java.nio.file.Path tempFile = Files.createTempFile("babel-compile", ".js");
    // TODO use a shared compiler file and pass the parameters to node -e
    try (BufferedWriter bw = Files.newBufferedWriter(tempFile, UTF8)) {
      bw.append(compilerText);
      bw.newLine();
      JSONObject inputsParam = new JSONObject();
      for (Entry<String, String> e : inputs.entrySet()) {
        inputsParam.put(e.getKey(), e.getValue());
      }
      JSONObject params = new JSONObject();
      params.put("inputs", inputsParam);
      params.put("outputAMD", outputAMD);
      params.put("useColoredOutput", useColoredOutput);
      params.put("withReact", includeReactPreset);
      params.put("productionMode", productionMode);
      params.put("enableStage3Transformations", enableStage3Transformations);
      bw.append("var params = " + params.toCompactString() + ";");

      bw.append(
          "process.stdout.write(JSON.stringify(compileJSX(params.inputs, params.outputAMD, params.useColoredOutput, params.withReact, params.productionMode, params.enableStage3Transformations)));");
    }

    ProcessBuilder pb = new ProcessBuilder("node", tempFile.toString());
    Process process = pb.start();
    String result;
    try {
      try (InputStream is = process.getInputStream()) {
        result = IOUtils.toString(is, UTF8);
        int exitCode = process.waitFor();
        if (exitCode == 0) {

          JSONObject resultJSON = new JSONObject(result);
          if (resultJSON.has("exception")) {
            throw new RuntimeException(resultJSON.getString("exception"));
          }
          Map<String, String> compiled = new HashMap<>(inputs.size());
          JSONObject output = resultJSON.getJSONObject("output");
          for (String fileName : inputs.keySet()) {
            compiled.put(fileName, output.getString(fileName));
          }
          return compiled;

        } else {
          try (InputStream err = process.getErrorStream()) {
            result = IOUtils.toString(err);

            throw new RuntimeException("Compiler process exited with code " + exitCode + ", message: " + result);
          }
        }
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      Files.delete(tempFile);
    }

  }

}
