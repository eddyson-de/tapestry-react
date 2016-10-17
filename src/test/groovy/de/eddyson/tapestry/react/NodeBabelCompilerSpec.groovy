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
import de.eddyson.tapestry.react.services.NodeBabelCompiler
import de.eddyson.tapestry.webjars.WebjarsModule
@SubModule([TapestryModule, ReactModule, TestModule, AssetsModule, WebjarsModule, WebResourcesModule])
class NodeBabelCompilerSpec extends Specification {

  @Autobuild
  private NodeBabelCompiler nodeBabelCompiler

  @Inject
  @Shared
  private ApplicationGlobals applicationGlobals

  def setupSpec(){
    applicationGlobals.storeContext(new PageTesterContext("/test"));
  }

  def "Compile a JSX template"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/regexp.jsxm")

    expect:
    resource.exists()

    when:
    def result = nodeBabelCompiler.transform(resource, null)
    then:
    result.text == NodeBabelCompilerSpec.class.getResourceAsStream('regexp.jsxm.out').text
  }

  def "Development code is removed in production"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/module-with-dev-code.jsm")

    expect:
    resource.exists()

    when:
    def result = nodeBabelCompiler.transform(resource, null)
    then:
    result.text == '''define(["exports"], function (exports) {
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
