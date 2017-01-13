package de.eddyson.tapestry.react.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.util.VirtualResource;
import org.apache.tapestry5.internal.webresources.CoffeeScriptCompiler;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.services.assets.ResourceDependencies;
import org.apache.tapestry5.services.assets.ResourceTransformer;

import de.eddyson.tapestry.react.ScriptEngineUtilities;

public class CJSXCompiler implements ResourceTransformer {
  private final static Charset UTF8 = StandardCharsets.UTF_8;

  private final CoffeeScriptCompiler coffeescritptCompiler;

  private final ScriptEngine engine;

  private final static Set<String> expectedKeysInEngineScope = CollectionFactory.newSet("self", "coffeeReactTransform");

  @Override
  public ContentType getTransformedContentType() {
    return InternalConstants.JAVASCRIPT_CONTENT_TYPE;
  }

  public CJSXCompiler(@Autobuild final CoffeeScriptCompiler coffeescritptCompiler) throws IOException, ScriptException {
    this.coffeescritptCompiler = coffeescritptCompiler;

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    if (!(engine instanceof Invocable)) {
      throw new IllegalStateException("ScriptEngine for JavaScript does not implement Invocable");
    }
    try (Reader reader = new InputStreamReader(CJSXCompiler.class.getResourceAsStream(
        "/de/eddyson/tapestry/react/services/coffee-react-transform-standalone.js"), StandardCharsets.UTF_8)) {
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

    try {
      final Map<String, String> result = (Map<String, String>) ((Invocable) engine)
          .invokeFunction("coffeeReactTransform", content);
      Resource resource = new VirtualResource() {

        @Override
        public InputStream openStream() throws IOException {
          return IOUtils.toInputStream(result.get("output"), UTF8);
        }
      };
      ScriptEngineUtilities.checkNothingLeaked(engine, expectedKeysInEngineScope);

      return coffeescritptCompiler.transform(resource, dependencies);

    } catch (NoSuchMethodException | ScriptException e) {
      throw new RuntimeException(e);
    } finally {
    }

  }
}
