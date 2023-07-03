package be.datafarmhouse.luminadocs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "be.datafarmhouse")
public class LuminaDocsServer {

    public static void main(final String[] args) {
        SpringApplication.run(be.datafarmhouse.luminadocs.LuminaDocsServer.class, args);
    }
}
