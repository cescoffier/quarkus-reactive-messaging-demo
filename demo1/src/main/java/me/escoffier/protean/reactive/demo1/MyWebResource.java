package me.escoffier.protean.reactive.demo1;

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

    @Produces(MediaType.SERVER_SENT_EVENTS)
    @GET
    @Path("/neo")
    public Publisher<String> stream() {
        return neo.state();
    }

}
