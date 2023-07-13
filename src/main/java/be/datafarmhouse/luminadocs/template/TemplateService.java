package be.datafarmhouse.luminadocs.template;

import be.datafarmhouse.luminadocs.LuminaDocsRequest;
import be.datafarmhouse.luminadocs.template.data.CSSData;
import be.datafarmhouse.luminadocs.template.data.CSSRepository;
import be.datafarmhouse.luminadocs.template.data.TemplateData;
import be.datafarmhouse.luminadocs.template.data.TemplateRepository;
import be.datafarmhouse.luminadocs.template.engine.TemplateEngine;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final CSSRepository cssRepository;
    private final TemplateRepository templateRepository;
    private final List<TemplateEngine> engines;

    @SneakyThrows
    public TemplateResult generateHTML(final LuminaDocsRequest.Template requestTemplate) {
        final TemplateEngine engine = getEngineForName(requestTemplate.getEngine());
        final StopWatch stopWatch = StopWatch.createStarted();

        String pdfEngine = null;
        final String css;
        final String template;
        if (StringUtils.isNotBlank(requestTemplate.getCode())) {
            final TemplateData storedTemplate = templateRepository.getReferenceById(requestTemplate.getCode());
            css = getCSS(requestTemplate.getCss(), storedTemplate.getCss());
            template = storedTemplate.getContent();
            pdfEngine = storedTemplate.getPdfEngine().name();
        } else {
            css = getCSS(requestTemplate.getCss(), null);
            template = requestTemplate.getContent();
        }


        final StringBuilder html = new StringBuilder()
                .append("<html><head>")
                .append(css)
                .append("</head><body>")
                .append(engine.process(template, requestTemplate.getVariables()))
                .append("</body></html>");

        if (requestTemplate.isDebug()) {
            log.info(html.toString());
        }

        log.info("HTML generation took {}ms.", stopWatch.getTime(TimeUnit.MILLISECONDS));
        return TemplateResult.builder()
                .html(html.toString())
                .pdfEngine(pdfEngine)
                .build();
    }

    protected String getCSS(final LuminaDocsRequest.CSS requestCSS, CSSData templateCSS) {
        final StringBuilder sb = new StringBuilder("<style>");
        if (StringUtils.isNotBlank(requestCSS.getCode())) {
            final CSSData cssData = cssRepository.getReferenceById(requestCSS.getCode());
            sb.append(cssData.getContent());
        } else if (StringUtils.isNotBlank(requestCSS.getContent())) {
            sb.append(requestCSS.getContent());
        } else if (templateCSS != null) {
            sb.append(templateCSS.getContent());
        }
        sb.append("</style>");
        return sb.toString();
    }

    protected TemplateEngine getEngineForName(final String name) {
        final String selection = StringUtils.isBlank(name) ? "freemarker" : name;
        for (final TemplateEngine engine : engines) {
            if (StringUtils.equalsIgnoreCase(engine.getName(), selection)) {
                return engine;
            }
        }
        throw new RuntimeException("Template Engine not found for name: " + name);
    }
}
