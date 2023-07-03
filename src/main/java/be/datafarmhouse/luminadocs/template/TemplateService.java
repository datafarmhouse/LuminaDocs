package be.datafarmhouse.luminadocs.template;

import be.datafarmhouse.luminadocs.template.engine.TemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final List<TemplateEngine> engines;

    @SneakyThrows
    public String generateHTML(final String selectedEngine, final String template, final Map<String, Object> variables, final String cssLib) {
        for (final TemplateEngine engine : engines) {
            if (StringUtils.equalsIgnoreCase(engine.getName(), selectedEngine)) {
                final StopWatch stopWatch = StopWatch.createStarted();
                final String html =
                        "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssLib + ".css\"/></head><body>" +
                                engine.process(template, variables) +
                                "</body></html>";
                log.info("HTML generation took {}ms.", stopWatch.getTime(TimeUnit.MILLISECONDS));
                return html;
            }
        }

        return "TemplateEngine could not be found.";
    }
}
