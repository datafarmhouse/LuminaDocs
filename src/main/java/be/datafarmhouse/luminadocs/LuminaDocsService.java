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

    public void generateDocument(final LuminaDocsRequest documentRequest, final ServletOutputStream outputStream) {
        final LuminaDocsRequest.Engine engine = documentRequest.getEngine();
        final LuminaDocsRequest.Template template = documentRequest.getTemplate();

        final String html = templateService.generateHTML(
                engine.getTemplate(),
                template.getBody(),
                template.getVariables(),
                template.getCss().getLib()
        );

        pdfService.generatePDF(
                engine.getPdf(),
                html,
                outputStream
        );
    }
}
