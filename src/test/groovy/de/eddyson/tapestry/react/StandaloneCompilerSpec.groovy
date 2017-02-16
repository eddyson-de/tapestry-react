package de.eddyson.tapestry.react

import org.apache.tapestry5.ioc.internal.util.ClasspathResource

import spock.lang.Specification
class StandaloneCompilerSpec extends Specification {


  def "Compile a JSX template"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/template.jsx")
    def compiler = new StandaloneCompiler()

    expect:
    resource.exists()

    when:
    def result = compiler.compile(resource.openStream().text, resource.file)
    then:
    result == ''''use strict';

ReactDOM.render(React.createElement(
  'h1',
  null,
  'Hello, world!'
), document.getElementById('example'));'''
  }
}
