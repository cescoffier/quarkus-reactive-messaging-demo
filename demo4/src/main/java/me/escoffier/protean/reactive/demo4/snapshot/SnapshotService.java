package me.escoffier.protean.reactive.demo4.snapshot;


import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/snapshot")
public class SnapshotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotService.class);

    private JsonObject pressure;
    private JsonObject heartbeat;
    private JsonObject temperature;

    private long timestamp;

    @POST
    public String save(String payload) {
        JsonObject json = new JsonObject(payload);
        pressure = json.getJsonObject("pressure");
        heartbeat = json.getJsonObject("heartbeat");
        temperature = json.getJsonObject("temperature");
        timestamp = json.getLong("timestamp");
        LOGGER.info("Snapshot saved");
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
