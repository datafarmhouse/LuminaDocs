package be.datafarmhouse.luminadocs.ui.views;

import be.datafarmhouse.luminadocs.LuminaDocsRequest;
import be.datafarmhouse.luminadocs.LuminaDocsService;
import be.datafarmhouse.luminadocs.template.TemplateService;
import be.datafarmhouse.luminadocs.template.data.*;
import be.datafarmhouse.luminadocs.ui.Layout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTargetValue;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

@Log4j2
@PageTitle("LuminaDocs | Templates")
@Route(value = "", layout = Layout.class)
public class TemplatesView extends Div {

    private final TemplateRepository templateRepository;
    private final CSSRepository cssRepository;
    private final TemplateService templateService;
    private final LuminaDocsService luminaDocsService;

    private ObjectMapper mapper = new ObjectMapper();
    private TemplateData selection;
    private Grid<TemplateData> grid;
    private TextField codeField;
    private TextField nameField;
    private TextArea contentField;
    private TextArea testVarField;
    private IFrame preview;
    private ComboBox<PDFEngineData> pdfEngineComboBox;
    private ComboBox<TemplateEngineData> templateEngineComboBox;
    private ComboBox<CSSData> cssComboBox;
    private VerticalLayout rightLayout;
    private Binder<TemplateData> binder;
    private Html templateName;

    private Button createButton;
    private Button saveButton;
    private DataProvider<TemplateData, ?> gridData;

    @Autowired
    public TemplatesView(final TemplateRepository templateRepository, final CSSRepository cssRepository, final TemplateService templateService, final LuminaDocsService luminaDocsService) {
        this.templateRepository = templateRepository;
        this.cssRepository = cssRepository;
        this.templateService = templateService;
        this.luminaDocsService = luminaDocsService;
        initView();
        loadData();
    }

    private void initView() {
        binder = new Binder<>(TemplateData.class);
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        grid = new Grid<>(TemplateData.class);
        grid.setColumns("code");
        grid.asSingleSelect().addValueChangeListener(event -> setSelection(event.getValue()));

        createButton = new Button("ADD", VaadinIcon.FILE_ADD.create());
        createButton.addClickListener(event -> setSelection(new TemplateData()));

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setSizeFull();
        leftLayout.add(new HorizontalLayout(createButton));
        leftLayout.add(grid);

        rightLayout = new VerticalLayout();
        rightLayout.setVisible(false);
        rightLayout.setSizeFull();
        rightLayout.add(createButtonPanel(), createForm());

        splitLayout.addToPrimary(leftLayout);
        splitLayout.addToSecondary(rightLayout);
        splitLayout.setSplitterPosition(15);

        add(splitLayout);
    }

    private void loadData() {
        gridData = DataProvider.fromCallbacks(
                // First callback fetches items based on the requested range
                query -> templateRepository.findAll(
                        PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit())
                ).stream(),
                // Second callback fetches the total count of items
                query -> Math.toIntExact(templateRepository.count())
        );
        grid.setDataProvider(gridData);
    }

    private void setSelection(final TemplateData template) {
        if (template != null) {
            rightLayout.setVisible(true);
            selection = template;
            binder.setBean(template);
            templateName.setHtmlContent("<h2>" + template.getName() + "</h2>");
        } else {
            rightLayout.setVisible(false);
            binder.setBean(null);
            selection = null;
            grid.deselectAll();
        }
    }

    private Component createButtonPanel() {
        templateName = new Html("<h2></h2>");

        saveButton = new Button("SAVE", VaadinIcon.FILE.create());
        saveButton.addClickListener(event -> {
            if (selection.getId() == null) {
                templateRepository.save(selection);
                setSelection(null);
                gridData.refreshAll();
            } else {
                setSelection(templateService.save(selection));
            }
        });

        final StreamResource resource = new StreamResource("preview.pdf", new StreamResourceWriter() {
            @Override
            public void accept(final OutputStream outputStream, final VaadinSession vaadinSession) throws IOException {
                final LuminaDocsRequest request = new LuminaDocsRequest();
                request.getTemplate().setCode(selection.getCode());
                request.getTemplate().setVariables(mapper.readValue(selection.getTestVars(), Map.class));
                luminaDocsService.generateDocument(request, outputStream);
            }
        });
        final Anchor downloadButton = new Anchor(resource, "download");
        downloadButton.setTarget(AnchorTargetValue.forString("_new"));
        downloadButton.removeAll();
        downloadButton.add(new Button("DOWNLOAD PDF", VaadinIcon.DOWNLOAD.create()));

        final Button deleteButton = new Button("DELETE", VaadinIcon.FILE_REMOVE.create());
        deleteButton.addClickListener(event -> {
            templateRepository.delete(selection);
            setSelection(null);
            gridData.refreshAll();
        });

        return new VerticalLayout(templateName, new HorizontalLayout(saveButton, downloadButton, deleteButton));
    }

    private VerticalLayout createForm() {
        codeField = new TextField("Code");
        codeField.setWidthFull();
        binder.forField(codeField).bind(TemplateData::getCode, TemplateData::setCode);
        nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addValueChangeListener(event -> templateName.setHtmlContent("<h2>" + event.getValue() + "</h2>"));
        binder.forField(nameField).bind(TemplateData::getName, TemplateData::setName);
        contentField = new TextArea("Content");
        contentField.setWidthFull();
        contentField.setHeight("500px");
        contentField.addValueChangeListener(event -> render());
        preview = new IFrame("data:text/html;charset=utf-8,<html></html>");
        preview.setWidthFull();
        preview.setHeight("500px");
        binder.forField(contentField).bind(TemplateData::getContent, TemplateData::setContent);

        testVarField = new TextArea("Test Variables");
        testVarField.setWidthFull();
        testVarField.setHeight("500px");
        testVarField.addValueChangeListener(event -> {
            render();
        });
        binder.forField(testVarField).bind(TemplateData::getTestVars, TemplateData::setTestVars);

        pdfEngineComboBox = new ComboBox<>("PDF Engine", Arrays.asList(PDFEngineData.values()));
        pdfEngineComboBox.setWidthFull();
        binder.forField(pdfEngineComboBox).bind(TemplateData::getPdfEngine, TemplateData::setPdfEngine);
        templateEngineComboBox = new ComboBox<>("Template Engine", Arrays.asList(TemplateEngineData.values()));
        templateEngineComboBox.setWidthFull();
        binder.forField(templateEngineComboBox).bind(TemplateData::getTemplateEngine, TemplateData::setTemplateEngine);
        cssComboBox = new ComboBox<>("CSS", cssRepository.findAll());
        cssComboBox.setItemLabelGenerator(CSSData::getName);
        cssComboBox.setWidthFull();
        binder.forField(cssComboBox).bind(TemplateData::getCss, TemplateData::setCss);

        VerticalLayout tab1Content = new VerticalLayout(
                codeField, nameField, pdfEngineComboBox,
                templateEngineComboBox, cssComboBox
        );
        tab1Content.setSizeFull();
        tab1Content.setPadding(true);
        tab1Content.setSpacing(true);
        final VerticalLayout previewLayout = new VerticalLayout(new Html("<label>Preview</label>"), preview);
        previewLayout.setSpacing(true);
        previewLayout.setPadding(false);
        HorizontalLayout tab2Content = new HorizontalLayout(contentField, previewLayout);
        tab2Content.setVisible(false);
        tab2Content.setSizeFull();
        HorizontalLayout tab3Content = new HorizontalLayout(testVarField);
        tab3Content.setVisible(false);
        tab3Content.setSizeFull();


        final Tab tab1 = new Tab();
        final Tab tab2 = new Tab();
        final Tab tab3 = new Tab();
        tab1.setLabel("General");
        tab2.setLabel("Content");
        tab3.setLabel("Test Variables");
        final Tabs tabs = new Tabs(tab1, tab2, tab3);

        tabs.addSelectedChangeListener(event -> {
            final Tab tab = event.getSelectedTab();
            tab1Content.setVisible(false);
            tab2Content.setVisible(false);
            tab3Content.setVisible(false);
            if (tab == tab1) {
                tab1Content.setVisible(true);
            } else if (tab == tab2) {
                tab2Content.setVisible(true);
            } else if (tab == tab3) {
                tab3Content.setVisible(true);
            }
        });

        final VerticalLayout tabsContent = new VerticalLayout(tab1Content, tab2Content, tab3Content);
        tabsContent.setPadding(false);
        tabsContent.setSpacing(false);

        return new VerticalLayout(tabs, tabsContent);
    }

    protected void setPreview(final String html) {
        preview.setSrc("data:text/html;charset=utf-8," + html);
    }

    protected void render() {
        if (selection == null) {
            setPreview("<div>preview</div>");
        } else {
            try {
                final LuminaDocsRequest.Template template = new LuminaDocsRequest.Template();
                final LuminaDocsRequest.CSS css = new LuminaDocsRequest.CSS();
                template.setDebug(false);
                template.setContent(contentField.getValue());
                template.setVariables(mapper.readValue(selection.getTestVars(), Map.class));
                template.setEngine(selection.getTemplateEngine().name());
                template.setCss(css);
                css.setCode(selection.getCss() == null ? null : selection.getCss().getCode());

                setPreview(
                        templateService.generateHTML(template).getHtml()
                );
            } catch (final Throwable t) {
                setPreview(
                        "<div>" + t.getMessage() + "</div>"
                );
            }
        }
    }
}

