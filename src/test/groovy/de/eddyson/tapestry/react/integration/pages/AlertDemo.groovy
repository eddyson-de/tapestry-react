package de.eddyson.tapestry.react.integration.pages

import de.eddyson.tapestrygeb.TapestryPage
import geb.Page

class AlertDemo extends TapestryPage {

  static url = "alertdemo"

  static at = { title == "Alerts Component Demo" }

  static content = {
    helloTapestry { $('a', text: contains('Hello Tapestry')) }
    helloWorld(required:false) { $('.alert').has(text: contains('Hello World!')) }
    sayHello { $('a', text: contains('Say hello')) }
    helloRoger(required:false) { $('.alert').has(text: contains('Hello Roger!')) }
    dismissHelloWorld(required:false) { helloWorld.find('.close') }
    dismissHelloRoger(required:false) { helloRoger.find('.close') }
  }
}
