package me.escoffier.protean.reactive.demo5.sensor;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import me.escoffier.protean.reactive.simulator.measures.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Neo {

  private Patient patient = new Patient(
    "neo",
    new BloodPressureGenerator("pressure", 70, 170, 3, 48, 78, 3),
    new HeartBeatGenerator("heartbeat", 120, 150, 5),
    new BodyTemperatureGenerator("temperature", 38.0, 39.9, 0.5),
    new StepGenerator("leaps", 10, 50, 20),
    new StateGenerator()
  );

  public Patient getPatient() {
    return patient;
  }

  private class StepGenerator implements Generator {
    private final String name;
    private final BoundedRandomVariable generator;

    StepGenerator(String name, int min, int max, int dev) {
      this.name = Objects.requireNonNull(name);
      this.generator = new BoundedRandomVariable(dev, min, max);
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public JsonObject generate() {
      return new JsonObject()
        .put(NAME, name)
        .put(TIMESTAMP, System.currentTimeMillis())
        .put("value", (long) generator.next());
    }
  }

  private class StateGenerator implements Generator {

    AtomicReference<String> reference = new AtomicReference<>("awake");

    StateGenerator() {
      Flowable.fromArray("sleeping", "awake", "eating")
        .observeOn(Schedulers.computation())
        .zipWith(Flowable.interval(10, TimeUnit.SECONDS), (a, b) -> a)
        .doOnNext(s -> reference.set(s))
        .repeat()
        .subscribe();
    }

    @Override
    public JsonObject generate() {
      return new JsonObject()
        .put(NAME, name())
        .put(TIMESTAMP, System.currentTimeMillis())
        .put("state", reference.get());
    }

    @Override
    public String name() {
      return "state";
    }
  }
}
