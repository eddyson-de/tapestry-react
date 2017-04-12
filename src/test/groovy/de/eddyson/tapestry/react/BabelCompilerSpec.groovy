package de.eddyson.tapestry.react

import org.apache.tapestry5.SymbolConstants
import org.apache.tapestry5.internal.InternalSymbols
import org.apache.tapestry5.internal.test.PageTesterContext
import org.apache.tapestry5.ioc.MappedConfiguration
import org.apache.tapestry5.ioc.Resource
import org.apache.tapestry5.ioc.annotations.Autobuild
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.ioc.internal.util.ClasspathResource
import org.apache.tapestry5.modules.AssetsModule
import org.apache.tapestry5.modules.TapestryModule
import org.apache.tapestry5.services.ApplicationGlobals
import org.apache.tapestry5.webresources.modules.WebResourcesModule

import de.eddyson.tapestry.react.services.impl.RhinoBabelCompiler
import spock.lang.Shared
import spock.lang.Specification
@SubModule([TapestryModule, de.eddyson.tapestry.react.modules.ReactModule, BabelCompilerSpec.TestModule, AssetsModule, WebResourcesModule])
class BabelCompilerSpec extends Specification {

  @Autobuild
  private RhinoBabelCompiler babelCompiler

  @Inject
  @Shared
  private ApplicationGlobals applicationGlobals

  def setupSpec(){
    applicationGlobals.storeContext(new PageTesterContext("/test"));
  }

  def compile(Resource resource){
    def inputs = [:]
    inputs.put(resource.file, resource.openStream().text)
    return babelCompiler.compile(inputs, true, false, true, true, false)[resource.file]
  }

  def "Compile a JSX template"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/template.jsx")

    expect:
    resource.exists()

    when:
    def result = compile(resource)
    then:
    result == '''define([], function () {
  'use strict';

  ReactDOM.render(React.createElement(
    'h1',
    null,
    'Hello, world!'
  ), document.getElementById('example'));
});'''
  }

  def "Compile a regular ES6 module"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/module.jsm")

    expect:
    resource.exists()

    when:
    def result = compile(resource)
    then:
    result == '''define(["exports"], function (exports) {
  "use strict";

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  var foo = exports.foo = "bar";
});'''
  }


  def "Development code is removed in production"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/module-with-dev-code.jsm")

    expect:
    resource.exists()

    when:
    def result = compile(resource)
    then:
    result == '''define(["exports"], function (exports) {
  "use strict";

  Object.defineProperty(exports, "__esModule", {
    value: true
  });
  var _exports = {};
  if (false) {
    _exports.dev = "yes";
  }

  exports.default = _exports;
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
