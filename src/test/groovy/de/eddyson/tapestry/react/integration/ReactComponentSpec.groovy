package de.eddyson.tapestry.react.integration
import de.eddyson.tapestry.react.integration.pages.ReactDemo;
import de.eddyson.tapestry.react.integration.pages.SFCDemo
import de.eddyson.tapestrygeb.JettyGebSpec


class ReactComponentSpec extends JettyGebSpec {

  def "Simple component test"(){
    given:
    to ReactDemo
    expect:
    waitFor {
      hello.text().contains ('Hello John!')
    }
  }

  def "Unmount component inside Zone"(){
    given:
    to ReactDemo
    expect:
    waitFor {
      mountedTalkativeComponent.displayed
    }
    when:
    updateZone.click()
    then:
    waitFor { ummountingTalkativeComponent.displayed }
  }

  def "Unmount nested components inside SFC inside Zone"(){
    given:
    to SFCDemo
    expect:
    waitFor {
      mountedTalkativeComponent.displayed
    }
    when:
    updateZone.click()
    then:
    waitFor { ummountingTalkativeComponent.displayed }
  }
}