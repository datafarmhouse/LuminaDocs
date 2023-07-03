package be.datafarmhouse.luminadocs;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LuminaDocsRequest {

    private Engine engine = new Engine();
    private Template template = new Template();
    private Options options = new Options();

    @Data
    public static class Engine {

        private String template = "freemarker";
        private String pdf = "pdfbox";
    }

    @Data
    public static class Template {

        private CSS css = new CSS();
        private String body = "<h1>You need to supply template.content and optionally template.variables.</h1>";
        private Map<String, Object> variables = new HashMap<>();
    }

    @Data
    public static final class Options {

        private String filename = "document.pdf";
    }

    @Data
    public static final class CSS {

        private String lib = "tailwind";
    }
}
