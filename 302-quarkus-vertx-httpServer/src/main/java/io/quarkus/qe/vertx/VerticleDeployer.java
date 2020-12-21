package io.quarkus.qe.vertx;

import io.quarkus.qe.vertx.config.HttpServerConfig;
import io.quarkus.qe.vertx.verticles.HttpServer;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class VerticleDeployer {

    @Inject
    HttpServerConfig config;

    public void run(@Observes StartupEvent e, Vertx vertx) {
        JsonObject conf = config.toJson();
        DeploymentOptions config = new DeploymentOptions().setConfig(conf);
        vertx.rxDeployVerticle(HttpServer.class.getName(), config).subscribe();
    }

}
