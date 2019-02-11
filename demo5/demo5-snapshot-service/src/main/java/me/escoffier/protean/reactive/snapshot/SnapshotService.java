package me.escoffier.protean.reactive.snapshot;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/snapshot")
public class SnapshotService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotService.class);

  private JsonObject temperature;

  @POST
  public String save(String payload) {
    JsonObject json = new JsonObject(payload);
    System.out.println("GOT: " + json);
    temperature = json;
    LOGGER.info("Snapshot saved: {}", temperature);
    return "Saved";
  }

  @GET
  @Path("/temperature")
  public double temperature() {
    if (temperature != null) {
      return temperature.getDouble("value");
    } else {
      return 0.0;
    }
  }
}
