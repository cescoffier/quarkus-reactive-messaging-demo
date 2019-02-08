package me.escoffier.protean.reactive;


import io.vertx.reactivex.core.Vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class RxVertxProducer {

    @Inject
    io.vertx.core.Vertx vertx;

    @Produces
    public Vertx vertx() {
        return new Vertx(vertx);
    }
}
