package de.eddyson.testapp.pages;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;

public class AlertDemo {

  @Inject
  private AlertManager alertManager;

  @OnEvent("addalert")
  void updateZone() {
    alertManager.alert(Duration.UNTIL_DISMISSED, Severity.INFO, "Hello Roger!");
  }

}
