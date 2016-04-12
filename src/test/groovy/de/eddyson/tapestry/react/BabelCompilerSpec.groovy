package de.eddyson.tapestry.react

import org.apache.tapestry5.SymbolConstants
import org.apache.tapestry5.internal.InternalSymbols
import org.apache.tapestry5.internal.test.PageTesterContext
import org.apache.tapestry5.ioc.MappedConfiguration
import org.apache.tapestry5.ioc.annotations.Autobuild
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.ioc.internal.util.ClasspathResource
import org.apache.tapestry5.modules.AssetsModule
import org.apache.tapestry5.modules.TapestryModule
import org.apache.tapestry5.services.ApplicationGlobals
import org.apache.tapestry5.webresources.modules.WebResourcesModule

import spock.lang.Shared
import spock.lang.Specification
import de.eddyson.tapestry.react.services.BabelCompiler
import de.eddyson.tapestry.webjars.WebjarsModule
@SubModule([TapestryModule, ReactModule, TestModule, AssetsModule, WebjarsModule, WebResourcesModule])
class BabelCompilerSpec extends Specification {

  @Autobuild
  private BabelCompiler babelCompiler

  @Inject
  @Shared
  private ApplicationGlobals applicationGlobals

  def setupSpec(){
    applicationGlobals.storeContext(new PageTesterContext("/test"));
  }

  def "Compile a JSX template"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/template.jsx")

    expect:
    resource.exists()

    when:
    def result = babelCompiler.transform(resource, null)
    then:
    result.text == ''''use strict';

ReactDOM.render(React.createElement(
  'h1',
  null,
  'Hello, world!'
), document.getElementById('example'));'''
  }

  def "Compile a regular ES6 module"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/module.jsm")

    expect:
    resource.exists()

    when:
    def result = babelCompiler.transform(resource, null)
    then:
    result.text == '''define(["exports"], function (exports) {
  "use strict";

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  var foo = exports.foo = "bar";
});'''
  }


  public static class TestModule {

    def contributeApplicationDefaults(MappedConfiguration configuration){
      configuration.add(InternalSymbols.APP_NAME, "test")
      configuration.add("tapestry.app-package", "react")
      configuration.add(SymbolConstants.MINIFICATION_ENABLED, false)
    }
  }
}
