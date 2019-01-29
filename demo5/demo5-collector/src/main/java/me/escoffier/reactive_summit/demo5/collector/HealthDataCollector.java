package me.escoffier.reactive_summit.demo5.collector;

import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.annotations.Merge;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.ReactiveStreams;
import org.reactivestreams.Publisher;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class HealthDataCollector {

  private static final Logger LOGGER = LogManager.getLogger(HealthDataCollector.class);

  @Incoming("health")
  @Outgoing("output")
  public ProcessorBuilder<MqttMessage, KafkaMessage<String, JsonObject>> filterTemperature() {
    return ReactiveStreams.<MqttMessage>builder()
      .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
      .map(json -> json.getJsonObject("temperature"))
      .map(json -> KafkaMessage.of("temperature", "neo", json));
  }

  @Incoming("health")
  @Outgoing("output")
  public PublisherBuilder<KafkaMessage<String, JsonObject>> filterHeartbeat(PublisherBuilder<MqttMessage> input) {
      return input
        .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
        .map(json -> json.getJsonObject("heartbeat"))
        .map(json -> KafkaMessage.of("heartbeat", "neo", json));
  }

  @Incoming("health")
  @Outgoing("output")
  public Publisher<KafkaMessage<String, JsonObject>> filterState(Flowable<MqttMessage> input) {
    return input
      .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
      .map(json -> json.getJsonObject("state"))
      .distinctUntilChanged(json -> json.getString("state")) // Filter on the "State" key of the json object.
      .doOnNext(json -> LOGGER.info("Forwarding {} to Kafka", json.encode()))
      .map(json -> KafkaMessage.of("state", "neo", json));
  }

  @Incoming("health")
  @Outgoing("output")
  public Flowable<KafkaMessage<String, JsonObject>> processSteps(Flowable<MqttMessage> input) {
    return input
      .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
      .doOnNext(j -> LOGGER.info("Got " + j))
      .map(json -> json.getJsonObject("leaps"))
      .map(json -> json.getLong("value"))
      .window(10, TimeUnit.SECONDS)
      .flatMap(MathFlowable::sumLong)
      .map(res -> new JsonObject().put("name", "leaps").put("value", res))
      .doOnNext(json -> LOGGER.info("Computed number of leaps: {}", json))
      .map(json -> KafkaMessage.of("leaps", "neo", json));
  }

  /**
   * Should not be required. It was working without in Fluid.
   * The issue is that sink are subscribers that can only subscribed one. It was not the case in Fluid.
   * To be discussed.
   */
  @Incoming("output")
  @Outgoing("data")
  @Merge
  public Message forward(Message m) {
    return m;
  }

}
