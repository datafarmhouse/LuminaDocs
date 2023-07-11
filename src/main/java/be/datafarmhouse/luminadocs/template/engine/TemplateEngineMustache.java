package be.datafarmhouse.luminadocs.template.engine;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateEngineMustache implements TemplateEngine {

    private final MustacheFactory mustacheFactory;

    @Override
    public String getName() {
        return "mustache";
    }

    @Override
    public String process(final String templateContent, final Map<String, Object> templateVariables) throws IOException {
        final Mustache mustache = mustacheFactory.compile(templateContent);
        final Writer writer = new StringWriter();

        mustache.execute(writer, templateVariables);

        return writer.toString();
    }
}
