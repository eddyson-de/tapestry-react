package de.eddyson.tapestry.react.integration.pages

import de.eddyson.tapestrygeb.TapestryPage
import geb.Page

class ReactDemo extends TapestryPage {

  static url = "reactdemo"

  static at = { title == "React Component Demo" }

  static content = {
    hello { $('#reactcomponent') }
    zone { $('#zone') }
    talkativeComponent { zone.find('span') }
    updateZone { zone.find('a') }
    mountedTalkativeComponent { $('.alert').has( text: contains('Mounted TalkativeComponent')) }
    ummountingTalkativeComponent { $('.alert').has( text: contains('Unmounting TalkativeComponent')) }
  }
}
