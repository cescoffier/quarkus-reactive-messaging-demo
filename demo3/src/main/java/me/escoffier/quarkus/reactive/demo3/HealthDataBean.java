package me.escoffier.quarkus.reactive.demo3;

import io.vertx.core.json.JsonObject;
import me.escoffier.quarkus.reactive.simulator.measures.Patient;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.*;

@ApplicationScoped
public class HealthDataBean {

  @Inject
  Patient patient;

  @Outgoing("health")
  public CompletionStage<JsonObject> health() {
    return CompletableFuture.supplyAsync(patient::measure, this::delay);
  }

  // Just here to delay the emissions
  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private void delay(Runnable runnable) {
    executor.schedule(runnable, 5, TimeUnit.SECONDS);
  }
}
