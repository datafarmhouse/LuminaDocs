package be.datafarmhouse.luminadocs.template.engine;

import com.github.jknack.handlebars.Handlebars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateEngineHandlebarsConfiguration {

    @Bean
    public Handlebars handlebars() {
        final Handlebars handlebars = new Handlebars();

        return handlebars;
    }
}
