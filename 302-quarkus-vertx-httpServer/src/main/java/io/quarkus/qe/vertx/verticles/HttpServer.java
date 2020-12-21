package io.quarkus.qe.vertx.verticles;

import io.quarkus.qe.vertx.handler.HelloWorld;
import io.reactivex.Completable;
import io.reactivex.Single;


import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import org.jboss.logging.Logger;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.reactivex.ext.web.handler.*;

import java.util.Arrays;
import javax.enterprise.context.ApplicationScoped;

public class HttpServer extends AbstractVerticle {

    private static final Logger LOGGER =  Logger.getLogger(HttpServer.class);
    final static String DEFAULT_HOST = "0.0.0.0";

    private HelloWorld helloWorld;

    @Override
    public Completable rxStart() {
        vertx.exceptionHandler(error -> LOGGER.info(
                error.getMessage() + error.getCause() + Arrays.toString(error.getStackTrace()) + error.getLocalizedMessage()));

        initHandlers();
        JsonObject config = vertx.getOrCreateContext().config();
        return OpenAPI3RouterFactory
                .rxCreate(vertx, getClass().getClassLoader().getResource("META-INF/resources/webroot/swagger/swagger.yml").getPath())
                .doOnError(LOGGER::error)
                .map(routerFactory -> {
                    addGlobalHandlers(routerFactory);
                    routeHandlersBySwaggerOperationId(routerFactory);
                    routerAuthNHandlers(routerFactory);
                    Router router = routerFactory.getRouter();
                    routeCommonsHandlers(router);
                    return router;
                }).flatMap(router -> startHttpServer(DEFAULT_HOST, config.getInteger("port"), router)).flatMapCompletable(httpServer -> {
                    LOGGER.info(String.format("HTTP server started on http://%s:%d", DEFAULT_HOST, config.getInteger("port")));
                    return Completable.complete();
                });
    }

    private void initHandlers() {
        helloWorld = new HelloWorld();
    }

    private void routeHandlersBySwaggerOperationId(OpenAPI3RouterFactory routerFactory) {
        routerFactory.addHandlerByOperationId("helloByName", helloWorld::helloWorld);
    }

    private void addGlobalHandlers(OpenAPI3RouterFactory routerFactory) {
        routerFactory
                .addGlobalHandler(CorsHandler.create("*"))
                .addGlobalHandler(LoggerHandler.create())
                .addGlobalHandler(ResponseTimeHandler.create())
                .addGlobalHandler(BodyHandler.create());
    }

    private void routeCommonsHandlers(Router router) {
        router.get("/*").handler(StaticHandler.create());
    }

    private void routerAuthNHandlers(OpenAPI3RouterFactory routerFactory){
        JsonObject config = vertx.getOrCreateContext().config();
        if(config.getBoolean("authNEnabled")) {
            routerFactory.setOptions(new RouterFactoryOptions().setRequireSecurityHandlers(true));
            JWTAuth provider = JWTAuth.create(vertx, new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm(config.getString("algorithm"))
                            .setPublicKey(config.getString("password"))
                            .setSymmetric(true)));

            JWTAuthHandler authProvider = JWTAuthHandler.create(provider);
            routerFactory.addSecurityHandler("bearerAuth", authProvider);
        }
    }

    private Single<io.vertx.reactivex.core.http.HttpServer> startHttpServer(String httpHost, Integer httpPort, Router router) {
        return vertx.createHttpServer().requestHandler(router).rxListen(httpPort, httpHost);
    }
}