package me.escoffier.quarkus.reactive.demo2;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthDataProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(HealthDataProcessor.class);

  @Incoming("health")
  @Outgoing("heartbeat")
  @Broadcast
  public JsonObject filtered(JsonObject input) {
    LOGGER.info("Received {}", input.encode());
    return input.getJsonObject("heartbeat");
  }
}
