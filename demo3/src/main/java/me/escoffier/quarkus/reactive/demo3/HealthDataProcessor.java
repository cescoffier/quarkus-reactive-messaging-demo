package me.escoffier.quarkus.reactive.demo3;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.vertx.axle.core.Vertx;
import io.vertx.axle.ext.web.client.WebClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import org.eclipse.microprofile.reactive.messaging.Incoming;
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
  Vertx vertx;

  private WebClient client;

  @PostConstruct
  public void init() {
    client = WebClient.create(vertx, new WebClientOptions()
      .setDefaultHost("localhost")
      .setDefaultPort(8080));
  }

  @Incoming("health")
  @Outgoing("heartbeat")
  @Broadcast
  public PublisherBuilder<JsonObject> process(PublisherBuilder<JsonObject> input) {
    return input
      .flatMapCompletionStage(json -> invokeSnapshotService(json).thenApply(x -> {
        LOGGER.info("The snapshot has been sent to the snapshot service");
        return json;
      }))
      .map(json -> json.getJsonObject("heartbeat"));
  }

  /**
   * Uses an asynchronous and non-blocking HTTP client to invoke a (not-so) remote service.
   *
   * @param data the payload to send
   * @return a future indicating when the upload has completed (or failed).
   */
  private CompletionStage<Void> invokeSnapshotService(JsonObject data) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    return client.post("/snapshot").sendJsonObject(data).thenApply(resp -> null);
  }
}
