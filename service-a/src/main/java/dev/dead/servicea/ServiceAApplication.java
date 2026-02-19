package dev.dead.servicea;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.function.Supplier;

@Slf4j
@SpringBootApplication
public class ServiceAApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ServiceAApplication.class, args);
        String repoPath = context.getEnvironment()
                .getProperty("test.demo" +
                        ".property1");
        String encrypted = context.getEnvironment()
                .getProperty("test.demo.encrypted");
        log.info("Config Server started! Looking demo property: {}", repoPath);
        log.info("Config Server started! Looking encrypted property: {}", encrypted);
    }

    // rest client bean
    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

}

@Slf4j
@RequiredArgsConstructor
@RestController
class ServiceAController {
    private final MyComponent myComponent;
    private final RestClient restClient;


    @GetMapping("/")
    public String serviceA() throws UnknownHostException {

        //create span
        myComponent.doWork();
        // call service B
        ResponseEntity<String> response = restClient.get()
                .uri("http://service-b:8081/")
                .retrieve()
                .toEntity(String.class); // Use toEntity() directly on the retrieve() spec
        // creating  more spans
        var res = myComponent.observe("serviceA.callServiceB",
                "callServiceB", "service", "serviceA", "target", "serviceB",
                () -> {
                    // Call service B and return the response body
                    ResponseEntity<String> resp = restClient.get()
                            .uri("http://service-b:8081/span")
                            .retrieve()
                            .toEntity(String.class);
                    return resp.getBody();
                });
        // use spring cloud stream to send message to service B

        return "Hello from Service A: IP " + Inet4Address.getLocalHost()
                .getHostAddress() +
                " - - - - - response from service B : " +
                " " + response.getBody()
                + " - - - - - response from service B through observation " +
                "span" +
                " : " + res;
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

    public <T> T observe(String observationName,
                         String contextualName, String lowCardinalityKey,
                         String lowCardinalityValue,
                         String highCardinalityKey,
                         String highCardinalityValue,
                         Supplier<T> supplier) {

        return Observation.createNotStarted(observationName, registry)
                .contextualName(contextualName)
                .lowCardinalityKeyValue(lowCardinalityKey, lowCardinalityValue)
                .highCardinalityKeyValue(highCardinalityKey, highCardinalityValue)
                .observe(supplier);
    }
}

class BuildInfoObservationFilter implements ObservationFilter {
    private final BuildProperties buildProperties;

    public BuildInfoObservationFilter(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Override
    public Observation.Context map(final Observation.Context context) {
        KeyValue buildVersion = KeyValue.of("build.version",
                Objects.requireNonNull(buildProperties.getVersion()));
        return context.addLowCardinalityKeyValue(buildVersion);
    }
}