package com.equiqo.olx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HttpServerVerticle extends AbstractVerticle {

  private static final String OFFER_ID = "id";
  private static final String OFFER_NAME = "name";
  private static final String OFFER_PRICE = "price";

  //WebClient client = WebClient.create(vertx);

  @Override
  public void start(Promise<Void> promise) throws Exception {
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.get("/").handler(this::indexHandler);
    router.get("/offers/olx/:keyword").handler(this::offersRenderingHandler);
    router.post().handler(BodyHandler.create());

    server.requestHandler(router).listen(8888);
  }

  private void indexHandler(RoutingContext context) {
    HttpServerResponse response = context.response();
    response.putHeader("content-type", "text/plain");
    response.end("Hello World from Vert.x-Web!");
  }

  private void offersRenderingHandler(RoutingContext context){
    String keyword = context.request().getParam("keyword");
    WebClient client = WebClient.create(vertx);

    client.get(443, "olx.pl", "/oferty/q-" + keyword)
      .addQueryParam("page", "1")
      .ssl(true)
      .send(ar -> {
        if (ar.succeeded()) {

          HttpResponse<Buffer> response = ar.result();
          parseToJson(response.bodyAsString());

          System.out.println("Received response with status code" + response.statusCode());
        } else {
          System.out.println("Something went wrong " + ar.cause().getMessage());
        }
      });
    context.response().end("Done");
  }

  private void parseToJson(String response){
    Document document = Jsoup.parse(response);
    Elements elements = document.select(".offer table");
    JsonObject offerData = getOfferData(elements.stream().findAny().get());
    System.out.println("Number of elements: " + elements.size());
  }

  private JsonObject getOfferData(Element element){
    JsonObject object = new JsonObject();
    final String offerId = element.dataset().get("id");
    final String name = element.select("h3 strong").first().text();
    final String price = element.select(".price strong").first().text();
    object.put(OFFER_ID, offerId);
    object.put(OFFER_NAME, name);
    object.put(OFFER_PRICE, price);
    return object;
  }

}
