package me.escoffier.protean.reactive.demo4;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class HealthDataProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(HealthDataProcessor.class);

  @Inject
  @ConfigProperty(name = "snapshot.host")
  String host;

  @Inject
  @ConfigProperty(name = "snapshot.port")
  int port;

  @Inject
  Vertx vertx;

  private WebClient client;

  @PostConstruct
  public void init() {
    client = WebClient.create(vertx, new WebClientOptions().setDefaultHost(host).setDefaultPort(port));
  }

  @Incoming("health")
  @Outgoing("heartbeat")
  @Broadcast
  public PublisherBuilder<Message<JsonObject>> process(PublisherBuilder<MqttMessage> input) {
    return input
      .map(MqttMessage::getPayload)
      .map(array -> Buffer.buffer(array).toJsonObject())
      .flatMapCompletionStage(json -> invokeSnapshotService(json).thenApply(x -> {
        LOGGER.info("The snapshot has been sent to the snapshot service");
        return json;
      }))
      .map(json -> Message.of(json.getJsonObject("heartbeat")));
  }

  /**
   * Uses an asynchronous and non-blocking HTTP client to invoke a (not-so) remote service.
   *
   * @param data the payload to send
   * @return a future indicating when the upload has completed (or failed).
   */
  private CompletionStage<Void> invokeSnapshotService(JsonObject data) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    client.post("/snapshot").sendJsonObject(data, resp -> {
      if (resp.failed()) {
        future.completeExceptionally(resp.cause());
      } else {
        future.complete(null);
      }
    });
    return future;
  }
}
