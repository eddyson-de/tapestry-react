package de.eddyson.tapestry.react.integration.pages

import de.eddyson.tapestrygeb.TapestryPage

class SFCDemo extends TapestryPage {

  static url = "sfcdemo"

  static at = { title == "Stateless Functional Component Demo" }

  static content = {
    hello { $('#reactcomponent') }
    zone { $('#zone') }
    talkativeComponent { zone.find('span') }
    updateZone { zone.find('a') }
    mountedTalkativeComponent { $('.alert').has( text: contains('Mounted TalkativeComponent')) }
    ummountingTalkativeComponent { $('.alert').has( text: contains('Unmounting TalkativeComponent')) }
  }
}
