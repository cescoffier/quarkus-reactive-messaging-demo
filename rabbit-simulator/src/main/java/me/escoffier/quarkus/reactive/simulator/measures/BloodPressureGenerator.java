package me.escoffier.quarkus.reactive.simulator.measures;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class BloodPressureGenerator implements Generator {

  public static final String SYSTOLIC = "systolic";
  public static final String DIASTOLIC = "diastolic";
  private final String name;
  private final BoundedRandomVariable systolicGenerator;
  private final BoundedRandomVariable diastolicGenerator;

  public BloodPressureGenerator(String name,
                                long systolicMin,
                                long systolicMax,
                                double systolicStdDeviation,
                                long diastolicMin,
                                long diastolicMax,
                                double diastolicStdDeviation
  ) {
    this.name = Objects.requireNonNull(name);
    systolicGenerator = new BoundedRandomVariable(systolicStdDeviation, systolicMin, systolicMax);
    diastolicGenerator = new BoundedRandomVariable(diastolicStdDeviation, diastolicMin, diastolicMax);
  }

  @Override
  public String name() {
    return name;
  }

  public JsonObject generate() {
    return new JsonObject()
      .put(TIMESTAMP, System.currentTimeMillis())
      .put(NAME, name)
      .put(SYSTOLIC, systolicGenerator.next())
      .put(DIASTOLIC, diastolicGenerator.next());

  }

}
