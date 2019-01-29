package me.escoffier.reactive_summit.demo3.snapshot;


import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("/snapshot")
public class SnapshotService {

  private static final Logger LOGGER = LogManager.getLogger(SnapshotService.class);

  private JsonObject pressure;
  private JsonObject heartbeat;
  private JsonObject temperature;

  private long timestamp;

  @POST
  public String save(JsonObject json) {
    pressure = json.getJsonObject("pressure");
    heartbeat = json.getJsonObject("heartbeat");
    temperature = json.getJsonObject("temperature");
    timestamp = json.getLong("timestamp");
    LOGGER.info("Snapshot saved");
    return "OK";
  }

  @GET
  @Path("/temperature")
  public double temperature() {
    return temperature.getDouble("value");
  }

  @GET
  @Path("/systolic")
  public double systolic() {
    return pressure.getDouble("systolic");
  }

  @GET
  @Path("/diastolic")
  public double diastolic() {
    return pressure.getDouble("diastolic");
  }

  @GET
  @Path("/heartbeat")
  public double heartbeat() {
    return heartbeat.getDouble("heartbeat");
  }

  @GET
  @Path("/timestamp")
  public double timestamp() {
    return timestamp;
  }

}
