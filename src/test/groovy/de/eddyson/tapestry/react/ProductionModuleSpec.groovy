package de.eddyson.tapestry.react

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.internal.InternalSymbols
import org.apache.tapestry5.internal.services.assets.ResourceChangeTracker;
import org.apache.tapestry5.internal.test.PageTesterContext
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.Resource
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.modules.AssetsModule;
import org.apache.tapestry5.modules.TapestryModule
import org.apache.tapestry5.services.ApplicationGlobals
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.assets.StreamableResource
import org.apache.tapestry5.services.assets.StreamableResourceProcessing;
import org.apache.tapestry5.services.assets.StreamableResourceSource;
import org.apache.tapestry5.services.javascript.ModuleManager
import org.apache.tapestry5.webresources.modules.WebResourcesModule;

import de.eddyson.tapestry.webjars.WebjarsModule
import spock.lang.Issue
import spock.lang.Shared;
import spock.lang.Specification

@SubModule([TapestryModule, de.eddyson.tapestry.react.modules.ReactModule, ProductionModuleSpec.TestModule, AssetsModule, WebjarsModule, WebResourcesModule])
class ProductionModuleSpec extends Specification {

  @Inject
  private ModuleManager moduleManager

  @Inject
  @Shared
  private ApplicationGlobals applicationGlobals

  @Inject
  @Shared
  private RequestGlobals requestGlobals

  @Inject
  private StreamableResourceSource streamableResourceSource

  @Inject
  private ResourceChangeTracker resourceChangeTracker

  def setupSpec(){
    applicationGlobals.storeContext(new PageTesterContext("/test"));
    Request request = Mock()
    requestGlobals.storeRequestResponse(request, null)
  }

  @Issue("#5")
  def "Development code is disabled in production"(){

    when:
    Resource reactResource = moduleManager.findResourceForModule("react")
    then:
    reactResource != null
    when:
    def content = reactResource.openStream().getText('utf-8')
    then:
    !content.contains('"development')
  }

  @Issue("#5")
  def "Generated production mode resource is available as a StreamableResource"(){
    when:
    Resource reactResource = moduleManager.findResourceForModule("react")
    StreamableResource streamableResource = streamableResourceSource.getStreamableResource(reactResource, StreamableResourceProcessing.COMPRESSION_DISABLED, resourceChangeTracker)
    then:
    streamableResource != null
  }

  public static class TestModule {

    def contributeApplicationDefaults(MappedConfiguration configuration){
      configuration.add(InternalSymbols.APP_NAME, "test")
      configuration.add("tapestry.app-package", "react")
      configuration.add(SymbolConstants.MINIFICATION_ENABLED, false)
    }
  }
}
