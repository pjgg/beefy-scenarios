package io.quarkus.qe.vertx.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

public class HelloWorld {
    public void helloWorld(final RoutingContext context) {
        // JWT claims, in case you want to do a custom authZ
        JsonObject token = context.user().principal();

        String name = context.request().getParam("name");
        context.response()
                .putHeader("content-type", "text/plain")
                .setStatusCode(HttpResponseStatus.OK.code())
                .end(String.format("hello world, %s!", name));
    }

    // example
   // curl -v -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.hm7tt3WIT_j7R4wVXOmakBJQv_6L1jhHdiWmFq3BBeA" http://localhost:8082/hello/pablo/world

}
