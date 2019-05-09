package me.escoffier.quarkus.reactive.simulator.measures;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class HeartBeatGenerator implements Generator {

  private static final String HEARTBEAT_MEASURE = "heartbeat";
  private final String name;
  private final BoundedRandomVariable heartbeat;

  public HeartBeatGenerator(String name,
                            long min,
                            long max,
                            double deviation
  ) {
    this.name = Objects.requireNonNull(name);
    heartbeat = new BoundedRandomVariable(deviation, min, max);
  }

  @Override
  public String name() {
    return name;
  }

  public JsonObject generate() {
    return new JsonObject()
      .put(TIMESTAMP, System.currentTimeMillis())
      .put(NAME, name)
      .put(HEARTBEAT_MEASURE, heartbeat.next());

  }

}
