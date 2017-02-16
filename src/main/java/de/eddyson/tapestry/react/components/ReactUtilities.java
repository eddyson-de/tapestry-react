package de.eddyson.tapestry.react.components;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.eddyson.tapestry.react.modules.ReactCoreModule;

public final class ReactUtilities {
  private final static Logger logger = LoggerFactory.getLogger(ReactCoreModule.class);

  public static boolean canUseNode() {
    try {
      ProcessBuilder pb = new ProcessBuilder("node", "-v");
      int exitCode = pb.start().waitFor();

      if (exitCode == 0) {
        return true;
      } else {
        logger.warn("Received exit code {} from call to node executable, falling back to Rhino compiler.");
      }
    } catch (IOException e) {
      logger.warn("Failed to call node executable, make sure it is on the PATH. Falling back to Rhino compiler.");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  private ReactUtilities() {

  }

}
