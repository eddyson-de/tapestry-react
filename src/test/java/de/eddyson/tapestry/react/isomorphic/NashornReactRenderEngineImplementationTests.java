package de.eddyson.tapestry.react.isomorphic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.webjars.WebJarAssetLocator;

import de.eddyson.tapestry.react.ReactModule;
import de.eddyson.tapestry.react.isomorphic.mock.MappedConfigurationMock;
import de.eddyson.tapestry.react.services.nashorn.ModuleLoader;
import de.eddyson.tapestry.react.services.nashorn.ModuleLoaderFactory;
import de.eddyson.tapestry.react.services.nashorn.NashornReactRenderEngineImplementation;

public class NashornReactRenderEngineImplementationTests {

  protected NashornReactRenderEngineImplementation getEngine() {
    final MappedConfigurationMock configuration = new MappedConfigurationMock();
    ReactModule.setupDefaultConfiguration(configuration);

    final WebJarAssetLocator locator = new WebJarAssetLocator();

    ModuleLoaderFactory moduleLoaderFactoryMock = new ModuleLoaderFactory() {

      @Override
      public ModuleLoader buildModuleLoader(final ScriptEngine engine) {
        return new ModuleLoader() {

          @Override
          public void loadModule(String name) throws IOException, ScriptException {
            switch (name) {
            case "react":
              engine.eval(read(classpathResource(locator.getFullPath("react.js"))));
              break;
            case "react-dom-server":
              engine.eval(read(classpathResource(locator.getFullPath("react-dom-server.js"))));
              break;
            case "test":
              engine.eval(read(classpathResource("de/eddyson/tapestry/react/isomorphic/test.js")));
              break;
            default:
              throw new RuntimeException("no definition for " + name + " set in test");
            }
          }
        };
      }
    };
    return new NashornReactRenderEngineImplementation(moduleLoaderFactoryMock);
  }

  @Test
  public void testEngineInitialization() {
    try {
      getEngine();
    } catch (Exception ex) {
      throw new AssertionError("Engine could not be constructed", ex);
    }
  }

  @Test
  public void testSimpleModule() {
    String result = getEngine().renderReactComponent("test", new JSONObject("name", "John"));

    // expected result looks like this:
    // <div data-reactroot="" data-reactid="1"
    // data-react-checksum="653202173">Hello John</div>

    Assert.assertTrue("result of isomorphic rendering does not contain \"data-reactroot\"",
        result.contains("data-reactroot"));
    Assert.assertTrue("result of isomorphic rendering does not contain greeting \"Hello John\"",
        result.contains("Hello John"));
  }

  @Test
  public void runThreadpoolTest() {
    ExecutorService pool = Executors.newFixedThreadPool(16);
    final NashornReactRenderEngineImplementation engine = getEngine();

    for (int i = 0; i < 1000; i++) {
      final int j = i;
      pool.submit(new Runnable() {

        @Override
        public void run() {
          String result = engine.renderReactComponent("test", new JSONObject("name", "John" + j));
          System.out.println(result);
          Assert.assertTrue("Greeting not found",
              result.contains("Hello John" + j));
        }
      });
    }
    try {
      pool.awaitTermination(1, TimeUnit.HOURS);
    } catch (InterruptedException e) {
      throw new AssertionError("Unexpected test-pool shutdown", e);
    }

  }

  private String read(InputStream resource) throws IOException {
    try {
      return IOUtils.toString(resource, Charset.forName("UTF-8"));
    } finally {
      IOUtils.closeQuietly(resource);
    }
  }

  private InputStream classpathResource(String resource) {
    return getClass().getClassLoader().getResourceAsStream(resource);
  }
}
