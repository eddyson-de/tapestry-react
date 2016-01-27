package de.eddyson.tapestry.react.integration.pages

import de.eddyson.tapestrygeb.TapestryPage
import geb.Page

class AlertDemo extends TapestryPage {

  static url = "alertdemo"

  static at = { title == "Alerts Component Demo" }

  static content = {
    
    sayHello { $('a', text: contains('Say hello')) }
    alert(required:false) { $('.alert').has(text: contains('Hello Roger!')) }
    dismiss(required:false) { alert.find('.close') }
  }
}
