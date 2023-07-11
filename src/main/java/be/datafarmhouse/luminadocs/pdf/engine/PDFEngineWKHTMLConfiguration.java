package be.datafarmhouse.luminadocs.pdf.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PDFEngineWKHTMLConfiguration {

    @Bean
    public RestTemplate wkhtmlClient() {
        return new RestTemplate();
    }
}
