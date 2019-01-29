package me.escoffier.reactive_summit.demo5;

import io.reactivex.processors.FlowableProcessor;
import io.smallrye.reactive.messaging.annotations.Acknowledgment;
import io.smallrye.reactive.messaging.annotations.Multicast;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletionStage;

import static io.smallrye.reactive.messaging.annotations.Acknowledgment.Mode.*;

/**
 * Consumes the data from Kafka, acknowledge the messages and forward to an internal topic.
 */
@ApplicationScoped
public class ForwarderProcessors {

  private static final Logger LOGGER = LogManager.getLogger(FlowableProcessor.class.getName());

  @Incoming("kafka-leaps")
  @Outgoing("leaps")
  @Acknowledgment(MANUAL)
  @Multicast
  public CompletionStage<Message<JsonObject>> forwardLeaps(Message<JsonObject> json) {
    return json.ack().thenApply(x -> {
      LOGGER.info("Forwarding leaps {}", json.getPayload().encode());
      return Message.of(json.getPayload());
    });
  }

  @Incoming("kafka-heartbeat")
  @Outgoing("heartbeat")
  @Acknowledgment(PRE_PROCESSING)
  @Multicast
  public JsonObject forwardHB(JsonObject json) {
    LOGGER.info("Forwarding heartbeat {}", json.encode());
    return json;
  }

  @Incoming("kafka-state")
  @Outgoing("state")
  @Acknowledgment(POST_PROCESSING)
  @Multicast
  public JsonObject forwardState(JsonObject json) {
    LOGGER.info("Forwarding state {}", json.encode());
    return json;
  }

}
