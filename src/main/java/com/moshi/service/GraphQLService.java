package com.moshi.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class GraphQLService {
  public static final GraphQLService me = new GraphQLService();

  private Vertx vertx;

  private GraphQLService() {
    vertx = Vertx.vertx();
  }

  public void start(String host, int port) {
    vertx.deployVerticle(new GraphQLVerticle(host, port));
  }

  public void stop() {
    vertx.close();
  }

  class GraphQLVerticle extends AbstractVerticle {
    private String host;
    private int port;

    GraphQLVerticle(String host, int port) {
      this.host = host;
      this.port = port;
    }

    @Override
    public void start() throws Exception {
      HttpServer server = vertx.createHttpServer();

      Router router = Router.router(vertx);

      router.route(HttpMethod.POST, "/").handler(ctx -> {
        JsonObject body = ctx.getBodyAsJson();

      });

      server.requestHandler(router).listen(port, host);
    }
  }

  // for test, unuseful
  public static void main(String[] args) {
    GraphQLService.me.start("0.0.0.0", 8080);
  }
}

