package com.equiqo.olx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    vertx.deployVerticle("com.equiqo.olx.HttpServerVerticle", res -> {
      if (res.succeeded()) {
        System.out.println("Deployment id is: " + res.result());
        startFuture.complete();
      } else {
        System.out.println("Deployment failed!");
        startFuture.fail(res.cause());
      }
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
