package be.datafarmhouse.luminadocs.pdf.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Objects;

@Component
@RequiredArgsConstructor
// this implementation uses https://hub.docker.com/r/openlabs/docker-wkhtmltopdf-aas
public class PDFEngineWKHTML implements PDFEngine {

    private final RestTemplate wkhtmlClient;

    @Value("${wkhtml.url:http://wkhtml}")
    private String url;

    @Override
    public String getName() {
        return "wkhtml";
    }

    @Override
    public void generate(final String html, final OutputStream outputStream) throws IOException {
        final String encodedHTML = Base64.getEncoder().encodeToString(html.getBytes());
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<WKHTMLRequest> entity = new HttpEntity<>(new WKHTMLRequest(encodedHTML), headers);
        final ResponseEntity<String> response = wkhtmlClient.postForEntity(url, entity, String.class);
        outputStream.write(Objects.requireNonNull(response.getBody()).getBytes());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WKHTMLRequest {

        private String contents;
    }
}
