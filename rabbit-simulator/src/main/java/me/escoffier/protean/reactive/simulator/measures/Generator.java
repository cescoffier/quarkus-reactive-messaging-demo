package me.escoffier.protean.reactive.simulator.measures;

import io.vertx.core.json.JsonObject;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public interface Generator {

  String TIMESTAMP = "timestamp";
  String NAME = "name";

  JsonObject generate();

  String name();

}
