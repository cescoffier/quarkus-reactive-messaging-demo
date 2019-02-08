package me.escoffier.protean.reactive.demo5;

import io.smallrye.reactive.messaging.annotations.Acknowledgment;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static io.smallrye.reactive.messaging.annotations.Acknowledgment.Mode.POST_PROCESSING;

@ApplicationScoped
public class TemperatureProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureProcessor.class);


    @Inject
    @ConfigProperty(name = "snapshot.host")
    String host;

    @Inject
    @ConfigProperty(name = "snapshot.port")
    int port;


    @Inject
    private Vertx vertx;

    private WebClient client;

    @PostConstruct
    public void init() {
        client = WebClient.create(vertx, new WebClientOptions().setDefaultHost(host).setDefaultPort(port));
    }


    @Incoming("temperature")
    @Acknowledgment(POST_PROCESSING)
    public CompletionStage<Void> saveSnapshot(JsonObject temperature) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        LOGGER.info("Saving snapshot {}", temperature.encode());
        client.post("/snapshot").rxSendJsonObject(temperature)
                .ignoreElement()
                .subscribe(
                        () -> {
                            LOGGER.info("Snapshot sent successfully");
                            future.complete(null);
                        },
                        err -> {
                            err.printStackTrace();
                            future.completeExceptionally(err);
                        }
                );
        return future;
    }

}
