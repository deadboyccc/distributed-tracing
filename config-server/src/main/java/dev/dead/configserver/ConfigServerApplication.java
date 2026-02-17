package dev.dead.configserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ConfigServerApplication.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ConfigServerApplication.class, args);

        String repoPath = context.getEnvironment().getProperty("test.demo" +
                ".property1");
        String encrypted = context.getEnvironment().getProperty("test.demo.encrypted");
        log.info("Config Server started! Looking demo property: {}", repoPath);
        log.info("Config Server started! Looking encrypted property: {}", encrypted);
    }

}
