package de.eddyson.testapp.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(stack = InternalConstants.CORE_STACK_NAME, stylesheet = "layout.less")
public class Layout {

  @Property
  @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
  private String title;

  @Inject
  private ComponentResources resources;

  @Inject
  private JavaScriptSupport javaScriptSupport;

}
