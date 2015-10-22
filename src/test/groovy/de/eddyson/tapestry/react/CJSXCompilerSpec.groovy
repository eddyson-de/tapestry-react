package de.eddyson.tapestry.react

import org.apache.tapestry5.internal.webresources.CoffeeScriptCompiler
import org.apache.tapestry5.ioc.OperationTracker
import org.apache.tapestry5.ioc.Resource
import org.apache.tapestry5.ioc.internal.OperationTrackerImpl
import org.apache.tapestry5.ioc.internal.util.ClasspathResource
import org.apache.tapestry5.modules.TapestryModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.Specification
import de.eddyson.tapestry.react.services.CJSXCompiler;
 
class CJSXCompilerSpec extends Specification {

  def "Compile a CJSX template"(){
    setup:
    Logger logger = LoggerFactory.getLogger(OperationTracker)
    OperationTracker operationTracker = new OperationTrackerImpl(logger)
    CoffeeScriptCompiler coffeeScriptCompiler = Mock()
 
    CJSXCompiler jsxCompiler = new CJSXCompiler(operationTracker, coffeeScriptCompiler)
    def resource = new ClasspathResource("de/eddyson/tapestry/react/template.cjsx")

    expect:
    resource.exists()

    when:
    def result = jsxCompiler.transform(resource, null)
    then:
    1 * coffeeScriptCompiler.transform({
      it.openStream().text == CJSXCompilerSpec.class.getResourceAsStream("template.cjsx.out").text
    }, null)
  }
}
