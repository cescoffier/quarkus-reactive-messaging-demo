package me.escoffier.reactive_summit.demo5;

import io.smallrye.reactive.messaging.annotations.Stream;
import io.vertx.core.json.JsonObject;
import net.redpipe.engine.resteasy.FileResource;
import org.eclipse.microprofile.reactive.streams.PublisherBuilder;
import org.reactivestreams.Publisher;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@ApplicationScoped
@Path("/")
public class MyWebResource extends FileResource {

  @Inject
  @Stream("heartbeat")
  private Publisher<JsonObject> heartbeat;

  @Inject
  @Stream("state")
  private PublisherBuilder<JsonObject> state;

  @Inject
  @Stream("leaps")
  private Publisher<JsonObject> leaps;

  @Produces(MediaType.SERVER_SENT_EVENTS)
  @GET
  @Path("/neo")
  public Publisher<String> stream() {
    return state.map(json -> json.getString("state")).buildRs();
  }

  @Produces(MediaType.SERVER_SENT_EVENTS)
  @GET
  @Path("/beat")
  public Publisher<JsonObject> beat() {
    return heartbeat;
  }

  @Produces(MediaType.SERVER_SENT_EVENTS)
  @GET
  @Path("/leaps")
  public Publisher<JsonObject> leaps() {
    return leaps;
  }

  @Path("webroot{path:(/.*)?}")
  @GET
  public Response serveStaticFiles(@PathParam("path") String path) throws IOException {
    return super.getFile(path);
  }

}
