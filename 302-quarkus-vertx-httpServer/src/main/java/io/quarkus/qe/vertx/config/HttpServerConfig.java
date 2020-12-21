package io.quarkus.qe.vertx.config;

import io.quarkus.arc.config.ConfigProperties;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "vertx.http.server")
public class HttpServerConfig {

    @ConfigProperty(name = "port", defaultValue = "8081")
    public int port;

    @ConfigProperty(name = "authn.enabled", defaultValue = "true")
    public boolean authNEnabled;

    @ConfigProperty(name = "authn.alg", defaultValue = "HS256")
    public String algorithm;

    @ConfigProperty(name = "authn.pwd", defaultValue = "secret")
    public String password;

    public JsonObject toJson() {
        return new JsonObject().put("port", port).put("authNEnabled", authNEnabled).put("algorithm", algorithm).put("password", password);
    }
}
