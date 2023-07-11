package be.datafarmhouse.luminadocs.template.engine;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateEnginePebble implements TemplateEngine {

    private final PebbleEngine pebbleEngine;

    @Override
    public String getName() {
        return "pebble";
    }

    @Override
    public String process(final String templateContent, final Map<String, Object> templateVariables) throws IOException {
        final PebbleTemplate template = pebbleEngine.getTemplate(templateContent);
        final Writer writer = new StringWriter();

        template.evaluate(writer, templateVariables);

        return writer.toString();
    }
}
