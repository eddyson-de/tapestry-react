package de.eddyson.tapestry.react.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;

import de.eddyson.tapestry.react.ReactSymbols;
import de.eddyson.tapestry.react.services.BabelCompiler;

public class BabelResourceTransformer implements ResourceTransformer {

  private final BabelCompiler babelCompiler;
  private final boolean productionMode;
  private boolean useColoredOutput;
  private boolean enableStage3Transformations;

  public BabelResourceTransformer(final BabelCompiler babelCompiler,
      @Symbol(ReactSymbols.USE_COLORED_BABEL_OUTPUT) final boolean useColoredOutput,
      @Symbol(ReactSymbols.ENABLE_STAGE_3_TRANSFORMATIONS) final boolean enableStage3Transformations,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {
    this.babelCompiler = babelCompiler;
    this.useColoredOutput = useColoredOutput;
    this.enableStage3Transformations = enableStage3Transformations;
    this.productionMode = productionMode;
  }

  @Override
  public ContentType getTransformedContentType() {
    return InternalConstants.JAVASCRIPT_CONTENT_TYPE;
  }

  @Override
  public InputStream transform(final Resource source, final ResourceDependencies dependencies) throws IOException {
    try (InputStream is = source.openStream()) {
      String content = IOUtils.toString(is, StandardCharsets.UTF_8);
      String fileName = source.getFile();
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

      String result = babelCompiler.compile(content, fileName, isES6Module, useColoredOutput, withReact, productionMode,
          enableStage3Transformations);
      return IOUtils.toInputStream(result, StandardCharsets.UTF_8);
    }
  }

}
