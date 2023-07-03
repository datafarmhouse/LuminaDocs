package be.datafarmhouse.luminadocs;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class LuminaDocsController {

    private final LuminaDocsService luminaDocsService;

    @SneakyThrows
    @PostMapping("/")
    public void generatePDF(
            @RequestBody final DocumentRequest documentRequest,
            final HttpServletResponse response
    ) {
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "inline; filename=\"" + documentRequest.getOptions().getFilename() + "\"");

        luminaDocsService.generateDocument(documentRequest, response.getOutputStream());
    }
}
