package be.datafarmhouse.luminadocs;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
public class LuminaDocsController {

    private final LuminaDocsService luminaDocsService;

    @SneakyThrows
    @PostMapping("/api")
    public void generatePDF(
            @RequestBody final LuminaDocsRequest documentRequest,
            final HttpServletResponse response
    ) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + documentRequest.getOptions().getFilename() + "\"");

        luminaDocsService.generateDocument(documentRequest, response.getOutputStream());
    }

    @SneakyThrows
    @GetMapping("/images/{imageCode}")
    public void serveImage(
            @PathVariable final String imageCode,
            final HttpServletResponse response
    ) {
        response.setContentType("image/jpg");
        response.setHeader("Content-Disposition", "filename=\"" + imageCode + ".jpg\"");

        luminaDocsService.serveImage(imageCode, response.getOutputStream());
    }
}
