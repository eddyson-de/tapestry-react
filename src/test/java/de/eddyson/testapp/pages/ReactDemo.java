package de.eddyson.testapp.pages;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

public class ReactDemo {

  @Inject
  private AjaxResponseRenderer ajaxResponseRenderer;

  @InjectComponent
  private Zone zone;

  @OnEvent("updatezone")
  void updateZone() {
    ajaxResponseRenderer.addRender(zone);
  }

}
