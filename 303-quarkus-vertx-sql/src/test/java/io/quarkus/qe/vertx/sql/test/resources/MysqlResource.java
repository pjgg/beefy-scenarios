package io.quarkus.qe.vertx.sql.test.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;


import static io.quarkus.qe.vertx.sql.test.resources.MysqlTestProfile.PROFILE;

public class MysqlResource implements QuarkusTestResourceLifecycleManager {

    GenericContainer mysqlContainer;

    @Override
    public Map<String, String> start() {
        Map<String, String> config = new HashMap<>();
        String profile = System.getProperty("quarkus.test.profile");
        if(profile.equals(PROFILE)) defaultMysqlContainer(config);

        return config;
    }

    private void defaultMysqlContainer(Map<String, String> config) {
        mysqlContainer = new GenericContainer(DockerImageName.parse("quay.io/bitnami/mysql:5.7.32"))
                .withEnv("MYSQL_ROOT_PASSWORD", "test")
                .withEnv("MYSQL_USER", "test")
                .withEnv("MYSQL_PASSWORD", "test")
                .withEnv("MYSQL_DATABASE", "amadeus")
                .withExposedPorts(3306);

        mysqlContainer.waitingFor(new HostPortWaitStrategy()).waitingFor(
                Wait.forLogMessage(".*MySQL Community Server.*", 1)
        ).start();

        config.put("quarkus.datasource.mysql.jdbc.url", String.format("jdbc:mysql://%s:%d/amadeus", mysqlContainer.getHost(), mysqlContainer.getFirstMappedPort()));
        config.put("quarkus.datasource.mysql.reactive.url", String.format("mysql://%s:%d/amadeus", mysqlContainer.getHost(), mysqlContainer.getFirstMappedPort()));
        config.put("app.selected.db","mysql");
        config.put("quarkus.flyway.migrate-at-start","false");
        config.put("quarkus.flyway.db2.migrate-at-start","false");
    }

    @Override
    public void stop() {
        if (Objects.nonNull(mysqlContainer)) mysqlContainer.stop();
    }
}