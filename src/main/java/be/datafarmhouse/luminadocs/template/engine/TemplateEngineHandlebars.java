package be.datafarmhouse.luminadocs.template.engine;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateEngineHandlebars implements TemplateEngine {

    private final Handlebars handlebars;

    @Override
    public String getName() {
        return "handlebars";
    }

    @Override
    public String process(final String templateContent, final Map<String, Object> templateVariables) throws IOException {
        final Template template = handlebars.compileInline(templateContent);
        return template.apply(templateVariables);
    }
}
