package de.eddyson.tapestry.react.requestfilters;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.Alert;
import org.apache.tapestry5.alerts.AlertStorage;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestFilter;
import org.apache.tapestry5.http.services.RequestHandler;
import org.apache.tapestry5.http.services.Response;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

public class ReactAPIFilter implements RequestFilter {

  // TODO this should be configurable via a symbol
  public static final String path = "/API/react";

  private Logger logger;

  private final ApplicationStateManager applicationStateManager;

  private final boolean productionMode;

  public ReactAPIFilter(final ApplicationStateManager applicationStateManager, final Logger logger,
      @Symbol(SymbolConstants.PRODUCTION_MODE) final boolean productionMode) {
    this.applicationStateManager = applicationStateManager;
    this.logger = logger;
    this.productionMode = productionMode;
  }

  /**
   * Filter interface for the HttpServletRequestHandler pipeline. A filter
   * should delegate to the handler. It may perform operations before or after
   * invoking the handler, and may modify the request and response passed in to
   * the handler.
   *
   * @param request
   * @param response
   * @param handler
   *
   * @return true if the request has been handled, false otherwise
   */
  @Override
  public boolean service(final Request request, final Response response, final RequestHandler handler)
      throws IOException {
    {

      if (path.equals(request.getPath())) {
        String operation = request.getParameter("operation");
        switch (operation) {
        case "retrieve-alerts":
          handleRetrieveAlerts(request, response);
          break;
        case "dismiss-alerts":

          handleDismissAlerts(request, response);
          break;
        default:
          response.sendError(400, "Invalid operation: " + operation);
          break;
        }

        return true;

      } else {
        return handler.service(request, response);
      }
    }
  }

  protected void handleRetrieveAlerts(final Request request, final Response response) throws IOException {
    // See TAP5-1941
    if (!request.isXHR()) {
      response.sendError(400, "Expecting XMLHttpRequest");
    }
    JSONObject result = new JSONObject();
    AlertStorage storage = applicationStateManager.getIfExists(AlertStorage.class);
    if (storage != null) {

      for (Alert alert : storage.getAlerts()) {
        result.append("alerts", alert.toJSON());
      }
      storage.dismissNonPersistent();
    }
    try (PrintWriter printWriter = response.getPrintWriter("application/json")) {
      printWriter.write(result.toString(productionMode));
    }
  }

  protected void handleDismissAlerts(final Request request, final Response response) throws IOException {
    AlertStorage storage = applicationStateManager.getIfExists(AlertStorage.class);
    if (storage != null) {
      String id = request.getParameter("id");
      if (id != null) {
        storage.dismiss(Long.parseLong(id));
      } else {
        storage.dismissAll();
      }
    }
    if (request.isXHR()) {
      try (PrintWriter printWriter = response.getPrintWriter("application/json")) {
        printWriter.write("{}");
      }
    }
  }

}
