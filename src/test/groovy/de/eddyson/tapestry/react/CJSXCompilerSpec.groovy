package de.eddyson.tapestry.react

import org.apache.tapestry5.internal.webresources.CoffeeScriptCompiler
import org.apache.tapestry5.ioc.internal.util.ClasspathResource

import de.eddyson.tapestry.react.services.CJSXCompiler;
import spock.lang.Specification

class CJSXCompilerSpec extends Specification {

  def "Compile a CJSX template"(){
    setup:
    CoffeeScriptCompiler coffeeScriptCompiler = Mock()

    CJSXCompiler jsxCompiler = new CJSXCompiler( coffeeScriptCompiler)
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
