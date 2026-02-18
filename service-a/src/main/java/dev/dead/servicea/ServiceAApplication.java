package dev.dead.servicea;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
public class ServiceAApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ServiceAApplication.class, args);
        String repoPath = context.getEnvironment().getProperty("test.demo" +
                ".property1");
        String encrypted = context.getEnvironment().getProperty("test.demo.encrypted");
        log.info("Config Server started! Looking demo property: {}", repoPath);
        log.info("Config Server started! Looking encrypted property: {}", encrypted);
    }

    }


@RestController
class ServiceAController {
    private final MyComponent myComponent;

    public ServiceAController(MyComponent myComponent) {
        this.myComponent = myComponent;
    }

    @GetMapping("/")
    public String serviceA() {

        myComponent.doWork();
        return "Hello from Service A!";
    }
}

@Component
@Slf4j
class MyComponent {

    private final ObservationRegistry registry;

    public MyComponent(ObservationRegistry registry) {
        this.registry = registry;
    }

    public void doWork() {
        // Use .observe() to wrap the functional logic
        int y = Observation.createNotStarted("my.observation", registry)
                .lowCardinalityKeyValue("action", "calculation") // Best practice: add metadata
                .observe(() -> {
                    int x = 1;
                    log.info("Inside observation: will return {}", x);
                    return x;
                });

        log.info("Observation result: {}", y);
    }
}