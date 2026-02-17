package dev.dead.servicea;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
public class ServiceAApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ServiceAApplication.class, args);

        String repoPath = context.getEnvironment().getProperty("test.demo" +
                ".property1");
        log.info("repoPath: {}", repoPath);
    }

}
@RestController
class ServiceAController {
    @GetMapping
    public String serviceA() {
        return "Hello from Service A!";
    }
}
