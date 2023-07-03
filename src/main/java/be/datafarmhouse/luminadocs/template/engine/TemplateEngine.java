package be.datafarmhouse.luminadocs.template.engine;

import java.util.Map;

public interface TemplateEngine {

    String getName();

    String process(String content, Map<String, Object> variables) throws Exception;
}
