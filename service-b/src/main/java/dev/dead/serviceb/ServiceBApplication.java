package dev.dead.serviceb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class ServiceBApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceBApplication.class, args);
    }

}

@RestController
class ServiceBController {

    @GetMapping("/span")
    public String span() {
        return "Hello from Service B! This is a span endpoint.";
    }

    @GetMapping("/")
    public String serviceB() throws UnknownHostException {
        // + log ip through java util api
        return "Hello from Service B!" + " Your IP is: " + getClientIp();
    }

    public String getClientIp() throws UnknownHostException {
        return InetAddress.getLocalHost()
                .getHostAddress();
    }
}