package be.datafarmhouse.luminadocs.template.engine;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateEnginePebbleConfiguration {

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder().loader(new StringLoader()).build();
    }
}
