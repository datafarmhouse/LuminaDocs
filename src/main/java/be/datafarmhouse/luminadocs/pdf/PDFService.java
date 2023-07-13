package be.datafarmhouse.luminadocs.pdf;

import be.datafarmhouse.luminadocs.LuminaDocsRequest;
import be.datafarmhouse.luminadocs.pdf.engine.PDFEngine;
import be.datafarmhouse.luminadocs.template.TemplateResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class PDFService {

    private final List<PDFEngine> engines;

    @SneakyThrows
    public void generatePDF(final LuminaDocsRequest.PDF pdf, final TemplateResult templateResult, final OutputStream outputStream) {
        final String selectedEngine = StringUtils.isBlank(pdf.getEngine()) ? templateResult.getPdfEngine() : pdf.getEngine();
        final PDFEngine engine = getEngineForName(selectedEngine);
        final StopWatch stopWatch = StopWatch.createStarted();

        engine.generate(templateResult.getHtml(), outputStream);

        log.info("PDF generation took {}ms.", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    protected PDFEngine getEngineForName(final String name) {
        final String selection = StringUtils.isBlank(name) ? "pdfbox" : name;
        for (final PDFEngine engine : engines) {
            if (StringUtils.equalsIgnoreCase(engine.getName(), selection)) {
                return engine;
            }
        }
        throw new RuntimeException("PDF Engine not found for name: " + name);
    }

}

