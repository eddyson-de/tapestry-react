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

import de.eddyson.tapestry.react.services.BabelCompiler;

public class BabelResourceTransformer implements ResourceTransformer {

  private final BabelCompiler babelCompiler;
  private final boolean productionMode;

  public BabelResourceTransformer(final BabelCompiler babelCompiler,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {
    this.babelCompiler = babelCompiler;
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
      String result = babelCompiler.compile(content, source.getFile(), productionMode);
      return IOUtils.toInputStream(result, StandardCharsets.UTF_8);
    }
  }

}
