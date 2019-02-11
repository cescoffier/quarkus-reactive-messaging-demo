package me.escoffier.protean.reactive.simulator.measures;

import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class Patient {

  private final String name;

  private final List<Generator> generators = new ArrayList<>();

  public Patient(String name, Generator... generators) {
    this.name = Objects.requireNonNull(name);
    Objects.requireNonNull(generators);
    this.generators.addAll(Arrays.asList(generators));
  }

  public String name() {
    return name;
  }

  public JsonObject measure() {
    JsonObject json = new JsonObject();
    json.put("name", name);
    json.put("timestamp", System.currentTimeMillis());
    for (Generator generator : generators) {
      json.put(generator.name(), generator.generate());
    }
    return json;
  }
}
