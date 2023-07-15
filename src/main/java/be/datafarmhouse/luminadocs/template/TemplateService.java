package be.datafarmhouse.luminadocs.template;

import be.datafarmhouse.luminadocs.LuminaDocsRequest;
import be.datafarmhouse.luminadocs.template.data.*;
import be.datafarmhouse.luminadocs.template.engine.TemplateEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final CSSRepository cssRepository;
    private final TemplateRepository templateRepository;
    private final ImageRepository imageRepository;
    private final List<TemplateEngine> engines;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${images.address.url:http://localhost:8080}")
    private String imagesURL;
    @Value("${images.address.url.preview:http://localhost:8080}")
    private String imagesPreviewURL;

    @PostConstruct
    public void init() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public TemplateData save(final TemplateData template) {
        if (StringUtils.isNotBlank(template.getTestVars())) {
            try {
                final JsonNode jsonNode = mapper.readValue(template.getTestVars(), JsonNode.class);
                template.setTestVars(mapper.writeValueAsString(jsonNode));
            } catch (final Throwable t) {
                //
            }
        }
        if (StringUtils.isNotBlank(template.getContent())) {
            final Document doc = Jsoup.parseBodyFragment(template.getContent());
            doc.outputSettings().indentAmount(4);
            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            doc.outputSettings().charset("UTF-8");
            doc.outputSettings().prettyPrint(true);
            template.setContent(doc.body().html());

        }
        return templateRepository.save(template);
    }

    @SneakyThrows
    public TemplateResult generateHTML(final LuminaDocsRequest.Template requestTemplate) {
        final TemplateEngine engine = getEngineForName(requestTemplate.getEngine());
        final StopWatch stopWatch = StopWatch.createStarted();

        String pdfEngine = null;
        final String css;
        final String template;
        if (StringUtils.isNotBlank(requestTemplate.getCode())) {
            final TemplateData storedTemplate = templateRepository.findByCode(requestTemplate.getCode());
            css = getCSS(requestTemplate.getCss(), storedTemplate.getCss());
            template = storedTemplate.getContent();
            pdfEngine = storedTemplate.getPdfEngine().name();
        } else {
            css = getCSS(requestTemplate.getCss(), null);
            template = requestTemplate.getContent();
        }


        final String html = resolveImages(
                requestTemplate.isPreview(),
                new StringBuilder()
                        .append("<html><head>")
                        .append(css)
                        .append("</head><body>")
                        .append(engine.process(template, requestTemplate.getVariables()))
                        .append("</body></html>")
        );

        if (requestTemplate.isDebug()) {
            log.info(html);
        }

        log.info("HTML generation took {}ms.", stopWatch.getTime(TimeUnit.MILLISECONDS));
        return TemplateResult.builder()
                .html(html)
                .pdfEngine(pdfEngine)
                .build();
    }

    protected String resolveImages(final boolean isPreview, final StringBuilder html) {
        final Document doc = Jsoup.parseBodyFragment(html.toString());
        doc.outputSettings().charset("UTF-8");
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        final Elements imgs = doc.getElementsByTag("img");
        for (final Element img : imgs) {
            if (img.hasAttr("src")) {
                final String src = img.attr("src");
                if (StringUtils.isNotBlank(src) && !src.startsWith("http")) {
                    final StringBuilder resolvedSRC =
                            new StringBuilder(isPreview ? imagesPreviewURL : imagesURL)
                                    .append("/images/")
                                    .append(src);

                    img.attr("src", resolvedSRC.toString());
                }
            }
        }
        return doc.outerHtml();
    }

    protected String getCSS(final LuminaDocsRequest.CSS requestCSS, CSSData templateCSS) {
        final StringBuilder sb = new StringBuilder("<style>");
        if (StringUtils.isNotBlank(requestCSS.getCode())) {
            final CSSData cssData = cssRepository.findByCode(requestCSS.getCode());
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

    @SneakyThrows
    public void serveImage(final String imageCode, final ServletOutputStream outputStream) {
        final ImageData image = imageRepository.findByCode(imageCode);
        IOUtils.copy(image.getContent().getBinaryStream(), outputStream);
    }
}
