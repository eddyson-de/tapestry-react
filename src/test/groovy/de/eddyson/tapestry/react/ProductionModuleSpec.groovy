package de.eddyson.tapestry.react

import org.apache.tapestry5.internal.InternalSymbols;
import org.apache.tapestry5.internal.test.PageTesterContext
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OperationTracker

import org.apache.tapestry5.ioc.Resource
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.internal.OperationTrackerImpl
import org.apache.tapestry5.ioc.internal.util.ClasspathResource
import org.apache.tapestry5.modules.AssetsModule;
import org.apache.tapestry5.modules.TapestryModule
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.javascript.ModuleManager
import org.apache.tapestry5.webresources.modules.WebResourcesModule;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.eddyson.tapestry.react.services.JSXCompiler
import de.eddyson.tapestry.webjars.WebjarsModule
import spock.lang.Issue;
import spock.lang.Specification

@SubModule([TapestryModule, ReactModule, AppNameModule, AssetsModule, WebjarsModule, WebResourcesModule])
class ProductionModuleSpec extends Specification {

  @Inject
  private ModuleManager moduleManager


  @Inject
  private ApplicationGlobals applicationGlobals

  @Issue("#5")
  def "Development code is disabled in production"(){
    setup:
    applicationGlobals.storeContext(new PageTesterContext("/test"));

    when:
    Resource reactResource = moduleManager.findResourceForModule("react")
    then:
    reactResource != null
    when:
    def content = reactResource.openStream().getText('utf-8')
    then:
    !content.contains('"development')
  }
  
  public static class AppNameModule {
    
        def contributeFactoryDefaults(MappedConfiguration configuration){
          configuration.add(InternalSymbols.APP_NAME, "test")
          configuration.add("tapestry.app-package", "react")
        }
      }
    
}
