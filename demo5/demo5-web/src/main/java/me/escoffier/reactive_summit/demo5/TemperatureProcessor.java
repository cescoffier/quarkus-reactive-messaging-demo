package me.escoffier.reactive_summit.demo5;

import io.smallrye.reactive.messaging.annotations.Acknowledgment;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static io.smallrye.reactive.messaging.annotations.Acknowledgment.Mode.POST_PROCESSING;

@ApplicationScoped
public class TemperatureProcessor {

  private static final Logger LOGGER = LogManager.getLogger(TemperatureProcessor.class);

  @Inject
  private Vertx vertx;

  private WebClient client;

  @PostConstruct
  public void init() {
    client = WebClient.create(vertx, new WebClientOptions().setDefaultHost("localhost").setDefaultPort(9001));
  }


  @Incoming("temperature")
  @Acknowledgment(POST_PROCESSING)
  public CompletionStage<Void> saveSnapshot(JsonObject temperature) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    LOGGER.info("Saving snapshot {}", temperature.encode());
    client.post("/snapshots").rxSendJsonObject(temperature)
      .ignoreElement()
      .subscribe(
        () -> {
         LOGGER.info("Snapshot sent successfully");
         future.complete(null);
        },
        future::completeExceptionally
      );
    return future;
  }

}
