package me.escoffier.protean.reactive.demo3;

import io.smallrye.reactive.messaging.annotations.Stream;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/app")
public class MyWebResource {

    @Inject
    private Neo neo;

    @Inject
    @Stream("heartbeat")
    Publisher<JsonObject> heartbeat;

    @Produces(MediaType.SERVER_SENT_EVENTS)
    @GET
    @Path("/neo")
    public Publisher<String> stream() {
        return neo.state();
    }

    @Produces(MediaType.SERVER_SENT_EVENTS)
    @GET
    @Path("/beat")
    public Publisher<String> beat() {
        return ReactiveStreams.fromPublisher(heartbeat).map(JsonObject::encode).buildRs();
    }

}
