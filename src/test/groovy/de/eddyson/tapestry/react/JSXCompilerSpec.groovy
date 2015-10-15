package de.eddyson.tapestry.react

import org.apache.tapestry5.ioc.OperationTracker
import org.apache.tapestry5.ioc.Resource
import org.apache.tapestry5.ioc.internal.OperationTrackerImpl
import org.apache.tapestry5.ioc.internal.util.ClasspathResource
import org.apache.tapestry5.modules.TapestryModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.eddyson.tapestry.react.services.JSXCompiler
import de.eddyson.tapestry.webjars.WebjarsModule;
import de.eddyson.tapestry.webjars.WebjarsResource;
import spock.lang.Specification

class JSXCompilerSpec extends Specification {

  def "Compile a JSX template"(){
    setup:
    Logger logger = LoggerFactory.getLogger(OperationTracker)
    OperationTracker operationTracker = new OperationTrackerImpl(logger)


    Resource compiler = new ClasspathResource("de/eddyson/tapestry/react/services/browser.js")

    JSXCompiler jsxCompiler = new JSXCompiler(compiler, operationTracker)
    def resource = new ClasspathResource("de/eddyson/tapestry/react/template.jsx")

    expect:
    compiler.exists()
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
}
