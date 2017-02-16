package de.eddyson.tapestry.react

import org.apache.tapestry5.ioc.internal.util.ClasspathResource

import de.eddyson.tapestry.react.readers.CompilingBabelReader
import spock.lang.Specification
class CompilingBabelReaderSpec extends Specification {


  def "Compile a JSX template"(){
    setup:

    def resource = new ClasspathResource("de/eddyson/tapestry/react/template.jsx")
    def srcReader = new StringReader(resource.openStream().text)
    def wrapperReader = new CompilingBabelReader(srcReader)

    expect:
    wrapperReader.text == '''define([], function () {
  'use strict';

  ReactDOM.render(React.createElement(
    'h1',
    null,
    'Hello, world!'
  ), document.getElementById('example'));
});'''
  }
}
