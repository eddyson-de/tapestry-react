package de.eddyson.tapestry.react

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalSymbols
import org.apache.tapestry5.internal.test.PageTesterContext;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OperationTracker
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.SubModule
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.modules.AssetsModule;
import org.apache.tapestry5.modules.TapestryModule
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.webresources.modules.WebResourcesModule

import de.eddyson.tapestry.react.services.JSXCompiler;
import de.eddyson.tapestry.webjars.WebjarsModule
import spock.lang.Shared;
import spock.lang.Specification
@SubModule([TapestryModule, ReactModule, TestModule, AssetsModule, WebjarsModule, WebResourcesModule])
class JSXCompilerSpec extends Specification {
  
  @Autobuild
  private JSXCompiler jsxCompiler
  
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
    def result = jsxCompiler.transform(resource, null)
    then:
    result.text == ''''use strict';

ReactDOM.render(React.createElement(
  'h1',
  null,
  'Hello, world!'
), document.getElementById('example'));'''
  }
  
  
  public static class TestModule {
    
        def contributeApplicationDefaults(MappedConfiguration configuration){
          configuration.add(InternalSymbols.APP_NAME, "test")
          configuration.add("tapestry.app-package", "react")
          configuration.add(SymbolConstants.MINIFICATION_ENABLED, false)
        }
      }
}
