package de.eddyson.tapestry.react.integration.pages

import de.eddyson.tapestrygeb.TapestryPage
import geb.Page

class ReactDemo extends TapestryPage {

  static url = "reactdemo"

  static at = { title == "React Component Demo" }

  static content = {
    hello { $('#reactcomponent') }
  }
}
