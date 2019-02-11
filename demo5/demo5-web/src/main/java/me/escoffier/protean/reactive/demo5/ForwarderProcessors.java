package me.escoffier.protean.reactive.demo5;

import io.reactivex.processors.FlowableProcessor;
import io.smallrye.reactive.messaging.annotations.Acknowledgment;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletionStage;

import static io.smallrye.reactive.messaging.annotations.Acknowledgment.Mode.*;

/**
 * Consumes the data from Kafka, acknowledge the messages and forward to an internal topic.
 */
@ApplicationScoped
public class ForwarderProcessors {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlowableProcessor.class.getName());

  @Incoming("kafka-leaps")
  @Outgoing("leaps")
  @Acknowledgment(MANUAL)
  @Broadcast
  public CompletionStage<Message<JsonObject>> forwardLeaps(Message<JsonObject> json) {
    return json.ack().thenApply(x -> {
      System.out.println("LEAPS: " + json.getPayload().encode());
      LOGGER.info("Forwarding leaps {}", json.getPayload().encode());
      return Message.of(json.getPayload());
    });
  }

  @Incoming("kafka-heartbeat")
  @Outgoing("heartbeat")
  @Acknowledgment(PRE_PROCESSING)
  @Broadcast
  public JsonObject forwardHB(JsonObject json) {
    System.out.println("HB: " + json.encode());
    LOGGER.info("Forwarding heartbeat {}", json.encode());
    return json;
  }

  @Incoming("kafka-state")
  @Outgoing("state")
  @Acknowledgment(POST_PROCESSING)
  @Broadcast
  public JsonObject forwardState(JsonObject json) {
    System.out.println("STATE: " + json.encode());
    LOGGER.info("Forwarding state {}", json.encode());
    return json;
  }
}
