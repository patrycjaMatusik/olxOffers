package com.equiqo.olx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.StaticHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

import static java.util.Objects.nonNull;

public class HttpServerVerticle extends AbstractVerticle {

  private static final String OFFER_ID = "id";
  private static final String OFFER_NAME = "name";
  private static final String OFFER_PRICE = "price";

  @Override
  public void start(Future<Void> fut) throws Exception {
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.get("/").handler(StaticHandler.create("templates"));
    router.route("/static/*").handler(StaticHandler.create("static"));
    router.get("/offers/olx/:keyword").handler(this::offersRenderingHandler);

    server.requestHandler(router).listen(8888, result -> {
      if (result.succeeded()) {
        fut.complete();
      } else {
        fut.fail(result.cause());
      }
    });
  }

  private void offersRenderingHandler(RoutingContext context){
    String keyword = context.request().getParam("keyword");
    WebClient client = WebClient.create(vertx);

    client.get(443, "olx.pl", "/oferty/q-" + keyword)
      .addQueryParam("page", "1")
      .ssl(true)
      .timeout(5000)
      .send(ar -> {
        if (ar.succeeded()) {

          HttpResponse<Buffer> response = ar.result();
          final JsonArray offersInJSONFormat = getDataInJSONFormat(response.bodyAsString());
          context.response()
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(offersInJSONFormat.encodePrettily());

          System.out.println("Received response with status code " + response.statusCode());
        } else {
          System.out.println("Something went wrong " + ar.cause().getMessage());
        }
      });
  }

  private JsonArray getDataInJSONFormat(String response){
    final Document document = Jsoup.parse(response);
    Elements elements = document.select(".offer table");
    JsonArray jsonParentObject = new JsonArray();
    elements.stream().map(this::getOfferData).forEach(jsonParentObject::add);
    return jsonParentObject;
  }

  private JsonObject getOfferData(Element element){
    JsonObject object = new JsonObject();
    final Map<String, String> dataset = element.dataset();
    final Element nameElement = element.select("h3 strong").first();
    final Element priceElement = element.select(".price strong").first();
    if(!dataset.isEmpty()) {
      object.put(OFFER_ID, dataset.get(OFFER_ID));
    }
    if(nonNull(nameElement)){
      object.put(OFFER_NAME, nameElement.text());
    }
    if(nonNull(priceElement)){
      object.put(OFFER_PRICE, priceElement.text());
    }
    return object;
  }

}
