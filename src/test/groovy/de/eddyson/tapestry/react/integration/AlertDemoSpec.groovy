package de.eddyson.tapestry.react.integration

import de.eddyson.tapestry.react.integration.pages.AlertDemo
import de.eddyson.tapestrygeb.JettyGebSpec
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.services.ApplicationStateManager

class AlertDemoSpec extends JettyGebSpec {

  @Inject
  ApplicationStateManager applicationStateManager

  def "Trigger alert with traditional event link"(){
    given:
    to AlertDemo
    expect:
    waitFor {
      helloTapestry.displayed
    }
    !helloWorld.displayed
    when:
    helloTapestry.click(AlertDemo)
    then:
    // TODO: this will need a custom ApplicationStatePersistenceStrategy
    // applicationStateManager.get(AlertStorage).alerts.size() == 1
    waitFor {
      helloWorld.displayed
    }
    when:
    driver.navigate().refresh();
    then:
    waitFor {
      helloWorld.displayed
    }
    when:
    dismissHelloWorld.click(AlertDemo)
    then:
    // TODO: this will need a custom ApplicationStatePersistenceStrategy
    // applicationStateManager.get(AlertStorage).alerts.size() == 0
    !helloWorld.displayed
    when:
    driver.navigate().refresh();
    then:
    !helloWorld.displayed
  }

  def "Trigger alert with async event link"(){
    given:
    to AlertDemo
    expect:
    waitFor {
      sayHello.displayed
    }
    !helloRoger.displayed
    when:
    sayHello.click()
    then:
    // TODO: this will need a custom ApplicationStatePersistenceStrategy
    // applicationStateManager.get(AlertStorage).alerts.size() == 1
    waitFor {
      helloRoger.displayed
    }

  }


}