package de.eddyson.tapestry.react.integration
import de.eddyson.tapestry.react.integration.pages.ReactDemo;
import de.eddyson.tapestrygeb.JettyGebSpec
import org.openqa.selenium.Keys


class ReactDemoSpec extends JettyGebSpec {
 
  def "Select some values"(){
    given:
    to ReactDemo
    expect:
    hello.text().contains ('Hello John!')
  }
}