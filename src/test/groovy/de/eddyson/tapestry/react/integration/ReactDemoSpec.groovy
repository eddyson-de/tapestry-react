package de.eddyson.tapestry.react.integration
import de.eddyson.tapestry.react.integration.pages.ReactDemo;
import de.eddyson.tapestrygeb.JettyGebSpec
import org.openqa.selenium.Keys


class ReactDemoSpec extends JettyGebSpec {
 
  def "Simple component test"(){
    given:
    to ReactDemo
    expect:
    hello.text().contains ('Hello John!')
  }
  
  def "Unmount component inside Zone"(){
    given:
    to ReactDemo
    expect:
    mountedTalkativeComponent.displayed
    when:
    updateZone.click()
    then:
    waitFor {
      ummountingTalkativeComponent.displayed
    }
  }
}