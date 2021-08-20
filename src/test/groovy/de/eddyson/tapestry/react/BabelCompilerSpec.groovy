package de.eddyson.tapestry.react

import de.eddyson.tapestry.react.modules.ReactModule
import de.eddyson.tapestry.react.services.impl.RhinoBabelCompiler
import org.apache.tapestry5.SymbolConstants
import org.apache.tapestry5.commons.MappedConfiguration
import org.apache.tapestry5.commons.Resource
import org.apache.tapestry5.http.services.ApplicationGlobals
import org.apache.tapestry5.internal.test.PageTesterContext
import org.apache.tapestry5.ioc.annotations.Autobuild
import org.apache.tapestry5.ioc.annotations.ImportModule
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.internal.util.ClasspathResource
import org.apache.tapestry5.modules.AssetsModule
import org.apache.tapestry5.modules.TapestryModule
import org.apache.tapestry5.webresources.modules.WebResourcesModule
import spock.lang.Shared
import spock.lang.Specification

@ImportModule([TapestryModule, ReactModule, TestModule, AssetsModule, WebResourcesModule])
class BabelCompilerSpec extends Specification {

  @Autobuild
  private RhinoBabelCompiler babelCompiler

  @Inject
  @Shared
  private ApplicationGlobals applicationGlobals

  def setupSpec(){
    applicationGlobals.storeContext(new PageTesterContext("/test"));
  }

  def compile(Resource resource, boolean enableStage3 = false){
    def inputs = [:]
    inputs.put(resource.file, resource.openStream().text)
    return babelCompiler.compile(inputs, true, false, true, true, enableStage3)[resource.file]
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

  def "Compile with stage-3 transformations"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/template.jsx")

    expect:
    resource.exists()

    when:
    def result = compile(resource, true)
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


  public static class TestModule {

    def contributeApplicationDefaults(MappedConfiguration configuration){
      configuration.add("tapestry.app-name", "test")
      configuration.add("tapestry.app-package", "react")
      configuration.add(SymbolConstants.MINIFICATION_ENABLED, false)
    }
  }
}
