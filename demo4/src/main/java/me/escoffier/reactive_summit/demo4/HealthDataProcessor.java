package me.escoffier.reactive_summit.demo4;

import io.smallrye.reactive.messaging.annotations.Multicast;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.ReactiveStreams;
import org.reactivestreams.Publisher;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class HealthDataProcessor {

  @Inject
  private Vertx vertx;
  private WebClient client;

  @PostConstruct
  public void init() {
    client = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setDefaultPort(9000));
  }

  @Incoming("health")
  @Outgoing("heartbeat")
  @Multicast
  public PublisherBuilder<Message<JsonObject>> process(PublisherBuilder<MqttMessage> input) {
    return input
      // MqttMessage is the envelope - it could also have been retrieved using PublisherBuilder<byte[]> to access the payload directly
      .map(Message::getPayload)
      // MQTT message payloads are byte[]
      .map(bytes -> Buffer.buffer(bytes).toJsonObject())

      .flatMapCompletionStage(json -> invokeStoreService(json).thenApply(x -> json))
      .map(json -> json.getJsonObject("heartbeat"))
      // Create a message using the Message.of method
      .map(Message::of);
  }

  private CompletionStage<Void> invokeStoreService(JsonObject data) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    client.post("/snapshot").rxSendJsonObject(data)
      .ignoreElement()
      .subscribe(
        () -> future.complete(null),
        future::completeExceptionally
      );
    return future;
  }

}
