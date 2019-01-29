package me.escoffier.reactive_summit.demo3;

import net.redpipe.engine.core.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

  private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());

  public static void main(String[] args) {
    new Server()
      .start(MyWebResource.class)
      .subscribe(
        () -> LOGGER.info("Server started"),
        err -> LOGGER.error("Unable to start the application", err));
  }
}
