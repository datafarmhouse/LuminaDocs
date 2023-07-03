package be.datafarmhouse.luminadocs.pdf.engine;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.util.XRLog;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class PDFEnginePDFBox implements PDFEngine {

    @Value("${pdf.lib.xr.log.enabled:false}")
    private boolean xrLogEnabled;

    @PostConstruct
    public void init() {
        XRLog.setLoggingEnabled(xrLogEnabled);
    }

    @Override
    public String getName() {
        return "pdfbox";
    }

    @Override
    public void generate(String html, OutputStream outputStream) throws IOException {
        new PdfRendererBuilder()
                .useFastMode()
                .withHtmlContent(html, "classpath:/")
                .toStream(outputStream)
                .run();
    }
}
