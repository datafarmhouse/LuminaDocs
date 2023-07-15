package be.datafarmhouse.luminadocs;

import be.datafarmhouse.luminadocs.pdf.PDFService;
import be.datafarmhouse.luminadocs.template.TemplateResult;
import be.datafarmhouse.luminadocs.template.TemplateService;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class LuminaDocsService {

    private final TemplateService templateService;
    private final PDFService pdfService;

    public void generateDocument(final LuminaDocsRequest request, final OutputStream outputStream) {
        final TemplateResult templateResult = templateService.generateHTML(request.getTemplate());

        pdfService.generatePDF(
                request.getPdf(),
                templateResult,
                outputStream
        );
    }

    public void serveImage(final String imageCode, final ServletOutputStream outputStream) {
        templateService.serveImage(imageCode, outputStream);
    }
}
