package me.escoffier.reactive_summit.demo5.snapshot;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class SnapshotService extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(SnapshotService.class.getName());
  }

  private JsonObject temperature = new JsonObject();

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create("*"));
    router.get("/snapshots").handler(rc -> rc.response().end(temperature.encode()));
    router.post().handler(BodyHandler.create());
    router.post("/snapshots").handler(rc -> {
      temperature = rc.getBodyAsJson();
      rc.response().end("OK");
    });
    vertx.createHttpServer().requestHandler(router::accept).listen(9001);
  }
}
