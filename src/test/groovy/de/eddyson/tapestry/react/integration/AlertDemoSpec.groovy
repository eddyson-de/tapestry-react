package de.eddyson.tapestry.react.integration
import de.eddyson.tapestry.react.integration.pages.AlertDemo;
import de.eddyson.tapestrygeb.JettyGebSpec

import org.apache.tapestry5.alerts.AlertStorage;
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.services.ApplicationStateManager;
import org.openqa.selenium.Keys


class AlertDemoSpec extends JettyGebSpec {
  
  @Inject
  ApplicationStateManager applicationStateManager
 
  def "Say hello"(){
    given:
    to AlertDemo
    expect:
    !alert.displayed
    when:
    sayHello.click(AlertDemo)
    then:
    // TODO: this will need a custom ApplicationStatePersistenceStrategy
    // applicationStateManager.get(AlertStorage).alerts.size() == 1
    alert.displayed
    when:
    driver.navigate().refresh();
    then:
    waitFor {
      alert.displayed
    }
    when:
    dismiss.click(AlertDemo)
    then:
    // TODO: this will need a custom ApplicationStatePersistenceStrategy
    // applicationStateManager.get(AlertStorage).alerts.size() == 0
    !alert.displayed
    when:
    driver.navigate().refresh();
    then:
    !alert.displayed
  }
  
  
}