package be.datafarmhouse.luminadocs;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class LuminaDocsRequest {

    private Template template = new Template();
    private PDF pdf = new PDF();
    private Options options = new Options();

    @Data
    public static class Template {

        private boolean debug = false;
        private CSS css = new CSS();
        private String code;
        private String content = "<h1>You need to supply template.content and optionally template.variables.</h1>";
        private String engine;
        private Map<String, Object> variables = new HashMap<>();
    }

    @Data
    public static final class CSS {

        private String code;
        private String content;
    }

    @Data
    public static final class Options {

        private String filename = "document.pdf";
        private boolean debug = false;
    }

    @Data
    public static class PDF {

        private String engine;
    }
}
