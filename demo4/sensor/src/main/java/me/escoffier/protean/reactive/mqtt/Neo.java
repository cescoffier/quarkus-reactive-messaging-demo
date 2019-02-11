package me.escoffier.protean.reactive.mqtt;

import me.escoffier.protean.reactive.simulator.measures.BloodPressureGenerator;
import me.escoffier.protean.reactive.simulator.measures.BodyTemperatureGenerator;
import me.escoffier.protean.reactive.simulator.measures.HeartBeatGenerator;
import me.escoffier.protean.reactive.simulator.measures.Patient;

public class Neo {

  public Patient getPatient() {
    return new Patient(
      "neo",
      new BloodPressureGenerator("pressure", 70, 170, 3, 48, 78, 3),
      new HeartBeatGenerator("heartbeat", 120, 150, 5),
      new BodyTemperatureGenerator("temperature", 38.0, 39.9, 0.5)
    );
  }
}
