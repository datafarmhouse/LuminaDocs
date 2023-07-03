package be.datafarmhouse.luminadocs.pdf.engine;

import java.io.OutputStream;

public interface PDFEngine {

    String getName();

    void generate(String html, OutputStream outputStream) throws Exception;
}
