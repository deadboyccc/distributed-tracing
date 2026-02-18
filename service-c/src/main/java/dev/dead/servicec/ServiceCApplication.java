package dev.dead.servicec;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ServiceCApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCApplication.class, args);
    }

}

@Component
class CustomObservation {

	private static final Logger log = LoggerFactory.getLogger(CustomObservation.class);
	private final ObservationRegistry observationRegistry;

	CustomObservation(ObservationRegistry observationRegistry) {
		this.observationRegistry = observationRegistry;
	}

	void someOperation() {
		Observation observation = Observation.createNotStarted("some-operation", this.observationRegistry);
		observation.lowCardinalityKeyValue("some-tag", "some-value");
		observation.observe(() -> {
			log.info("Executing some-operation");
			// Business logic ...
		});
	}

}
@RestController
class ServiceCController {

	private static final Logger log = LoggerFactory.getLogger(ServiceCController.class);
	private final CustomObservation customObservation;

	ServiceCController(CustomObservation customObservation) {
		this.customObservation = customObservation;
	}

	@GetMapping("/")
	public String hello() {
		log.info("Hello endpoint called");
		customObservation.someOperation();
		return "Hello from Service C!";
	}
}