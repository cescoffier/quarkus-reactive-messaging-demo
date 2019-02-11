package me.escoffier.protean.reactive.demo5;

import io.smallrye.reactive.messaging.annotations.Stream;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/app")
public class MyWebResource {

  @Inject
  @Stream("heartbeat")
  private PublisherBuilder<JsonObject> heartbeat;

  @Inject
  @Stream("state")
  private PublisherBuilder<JsonObject> state;

  @Inject
  @Stream("leaps")
  private PublisherBuilder<JsonObject> leaps;

  @Produces(MediaType.SERVER_SENT_EVENTS)
  @GET
  @Path("/neo")
  public Publisher<String> stream() {
    return state.map(json -> json.getString("state")).buildRs();
  }

  @Produces(MediaType.SERVER_SENT_EVENTS)
  @GET
  @Path("/beat")
  public Publisher<String> beat() {
    return heartbeat.map(JsonObject::encode).buildRs();
  }

  @Produces(MediaType.SERVER_SENT_EVENTS)
  @GET
  @Path("/leaps")
  public Publisher<String> leaps() {
    return leaps.map(JsonObject::encode).buildRs();
  }
}
