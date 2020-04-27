package com.equiqo.olx;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext context) throws IOException {
    vertx = Vertx.vertx();
    vertx.deployVerticle(MainVerticle.class.getName(), context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testMyApplication(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8888, "localhost", "/", response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("Olx offers") || body.toString().contains("Result"));
        async.complete();
      });
    });
  }
}
