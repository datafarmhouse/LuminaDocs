package be.datafarmhouse.luminadocs.template.engine;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateEngineFreemarker implements TemplateEngine {

    private final Configuration freemarker;

    @Override
    public String getName() {
        return "freemarker";
    }

    @Override
    public String process(final String templateContent, final Map<String, Object> templateVariables) throws IOException, TemplateException {
        final Template template = new Template("tmp", new StringReader(templateContent), freemarker);
        final Writer writer = new StringWriter();

        template.process(templateVariables, writer);

        return writer.toString();
    }
}
