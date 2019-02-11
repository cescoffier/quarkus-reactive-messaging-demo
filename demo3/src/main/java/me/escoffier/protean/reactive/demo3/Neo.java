package me.escoffier.protean.reactive.demo3;

import io.reactivex.Flowable;
import me.escoffier.protean.reactive.simulator.measures.BloodPressureGenerator;
import me.escoffier.protean.reactive.simulator.measures.BodyTemperatureGenerator;
import me.escoffier.protean.reactive.simulator.measures.HeartBeatGenerator;
import me.escoffier.protean.reactive.simulator.measures.Patient;
import org.reactivestreams.Publisher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class Neo {

  @Produces
  Patient getPatient() {
    return new Patient(
      "neo",
      new BloodPressureGenerator("pressure", 70, 170, 3, 48, 78, 3),
      new HeartBeatGenerator("heartbeat", 120, 150, 5),
      new BodyTemperatureGenerator("temperature", 38.0, 39.9, 0.5)
    );
  }

  public Publisher<String> state() {
    return Flowable.fromArray("sleeping", "awake", "eating")
      .zipWith(Flowable.interval(5, TimeUnit.SECONDS), (a, b) -> a)
      .repeat();
  }
}
