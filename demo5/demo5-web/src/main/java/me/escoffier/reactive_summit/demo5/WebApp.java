package me.escoffier.reactive_summit.demo5;

import net.redpipe.engine.core.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebApp {

  private static final Logger LOGGER = LogManager.getLogger(WebApp.class.getName());

  public static void main(String[] args) {
    new Server()
      .start(MyWebResource.class)
      .subscribe(
        () -> LOGGER.info("Server started"),
        err -> LOGGER.error("Unable to start the application", err));
  }
}
