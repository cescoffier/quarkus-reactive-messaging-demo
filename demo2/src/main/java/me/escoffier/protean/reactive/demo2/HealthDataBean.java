package me.escoffier.protean.reactive.demo2;

import io.vertx.core.json.JsonObject;
import me.escoffier.protean.reactive.simulator.measures.Patient;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.*;

@ApplicationScoped
public class HealthDataBean {

    @Inject
    private Patient patient;
    // Just here to delay the emissions
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Outgoing("health")
    public CompletionStage<JsonObject> health() {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        delay(() -> future.complete(patient.measure()));
        return future;
    }

    private void delay(Runnable runnable) {
        executor.schedule(runnable, 5, TimeUnit.SECONDS);
    }


}
