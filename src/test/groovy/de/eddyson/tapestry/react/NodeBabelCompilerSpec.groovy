package de.eddyson.tapestry.react

import de.eddyson.tapestry.react.modules.ReactModule
import de.eddyson.tapestry.react.services.impl.NodeBabelCompiler
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
class NodeBabelCompilerSpec extends Specification {

  @Autobuild
  private NodeBabelCompiler nodeBabelCompiler

  @Inject
  @Shared
  private ApplicationGlobals applicationGlobals

  def setupSpec(){
    applicationGlobals.storeContext(new PageTesterContext("/test"));
  }

  def compile(Resource resource){
    Map<String,String> inputs = [(resource.file):resource.openStream().text]
    return nodeBabelCompiler.compile(inputs, true, false, true, true, false)[resource.file]
  }

  def "Compile a JSX template"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/regexp.jsxm")

    expect:
    resource.exists()

    when:
    def result = compile(resource)
    then:
    result == NodeBabelCompilerSpec.class.getResourceAsStream('regexp.jsxm.out').text
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
      configuration.add("tapestry.app-name", "test")
      configuration.add("tapestry.app-package", "react")
      configuration.add(SymbolConstants.MINIFICATION_ENABLED, false)
    }
  }
}
