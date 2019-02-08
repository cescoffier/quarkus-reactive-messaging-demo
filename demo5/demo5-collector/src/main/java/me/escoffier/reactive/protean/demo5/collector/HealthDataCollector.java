package me.escoffier.reactive.protean.demo5.collector;

import hu.akarnokd.rxjava2.math.MathFlowable;
import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.annotations.Merge;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class HealthDataCollector {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthDataCollector.class);

    @Incoming("health")
    @Outgoing("output")
    public ProcessorBuilder<MqttMessage, KafkaMessage<String, JsonObject>> filterTemperature() {
        return ReactiveStreams.<MqttMessage>builder()
                .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
                .map(json -> json.getJsonObject("temperature"))
                .peek(x -> LOGGER.info("Received {} as temperature", x))
                .map(json -> KafkaMessage.of("temperature", "neo", json));
    }

    @Incoming("health")
    @Outgoing("output")
    public PublisherBuilder<KafkaMessage<String, JsonObject>> filterHeartbeat(PublisherBuilder<MqttMessage> input) {
        return input
                .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
                .map(json -> json.getJsonObject("heartbeat"))
                .peek(x -> LOGGER.info("Received {} as heartbeat", x))
                .map(json -> KafkaMessage.of("heartbeat", "neo", json));
    }

    @Incoming("health")
    @Outgoing("output")
    public Publisher<KafkaMessage<String, JsonObject>> filterState(Flowable<MqttMessage> input) {
        return input
                .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
                .map(json -> json.getJsonObject("state"))
                .distinctUntilChanged(json -> json.getString("state")) // Filter on the "State" key of the json object.
                .doOnNext(json -> LOGGER.info("Forwarding new state '{}' to Kafka", json.encode()))
                .map(json -> KafkaMessage.of("state", "neo", json));
    }

    @Incoming("health")
    @Outgoing("output")
    public Flowable<KafkaMessage<String, JsonObject>> processSteps(Flowable<MqttMessage> input) {
        return input
                .doOnNext(json -> LOGGER.info("Got: {}", json))
                .map(message -> Buffer.buffer(message.getPayload()).toJsonObject())
                .doOnNext(json -> LOGGER.info("Got as json:  {}", json))
                .map(json -> json.getJsonObject("leaps"))
                .map(json -> json.getLong("value"))
                .doOnNext(j -> LOGGER.info("Received {} as number of leaps", j))
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
