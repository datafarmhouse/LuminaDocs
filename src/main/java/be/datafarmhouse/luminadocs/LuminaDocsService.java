package be.datafarmhouse.luminadocs;

import be.datafarmhouse.luminadocs.pdf.PDFService;
import be.datafarmhouse.luminadocs.template.TemplateService;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LuminaDocsService {

    private final TemplateService templateService;
    private final PDFService pdfService;

    public void generateDocument(final DocumentRequest documentRequest, final ServletOutputStream outputStream) {
        final String html = templateService.generateHTML(
                documentRequest.getEngine().getTemplate(),
                documentRequest.getTemplate().getContent(),
                documentRequest.getTemplate().getVariables()
        );

        pdfService.generatePDF(
                documentRequest.getEngine().getPdf(),
                html,
                outputStream
        );
    }
}
