package be.datafarmhouse.luminadocs.pdf;

import be.datafarmhouse.luminadocs.pdf.engine.PDFEngine;
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
    public void generatePDF(final String selectedEngine, final String html, final OutputStream outputStream) {
        for (final PDFEngine engine : engines) {
            if (StringUtils.equalsIgnoreCase(engine.getName(), selectedEngine)) {
                final StopWatch stopWatch = StopWatch.createStarted();
                engine.generate(html, outputStream);
                log.info("PDF generation took {}ms.", stopWatch.getTime(TimeUnit.MILLISECONDS));
                break;
            }
        }
    }
}
