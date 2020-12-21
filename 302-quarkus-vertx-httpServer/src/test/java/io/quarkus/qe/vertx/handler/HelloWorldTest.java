package io.quarkus.qe.vertx.handler;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class HelloWorldTest {

    private static final String JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.hm7tt3WIT_j7R4wVXOmakBJQv_6L1jhHdiWmFq3BBeA";

    @Test
    @DisplayName("Hello World without JWT ")
    public void httpServer() {
        given().port(8082).
        when().get("/hello/pablo/world")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Hello World + JWT ")
    public void httpSecuredServer() {
        given().port(8082).headers(
                "Authorization",
                "Bearer " + JWT).
                when().get("/hello/pablo/world")
                .then()
                .statusCode(200);
    }

}
