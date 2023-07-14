package be.datafarmhouse.luminadocs.ui.views;

import be.datafarmhouse.luminadocs.template.data.CSSData;
import be.datafarmhouse.luminadocs.template.data.CSSRepository;
import be.datafarmhouse.luminadocs.ui.Layout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@Log4j2
@PageTitle("LuminaDocs | CSS")
@Route(value = "css", layout = Layout.class)
public class CSSView extends Div {

    private final CSSRepository cssRepository;

    private ObjectMapper mapper = new ObjectMapper();
    private CSSData selection;
    private Grid<CSSData> grid;
    private TextField codeField;
    private TextField nameField;
    private TextArea contentField;
    private VerticalLayout rightLayout;
    private Binder<CSSData> binder;
    private Html templateName;

    private Button createButton;
    private Button saveButton;
    private DataProvider<CSSData, ?> gridData;

    @Autowired
    public CSSView(final CSSRepository cssRepository) {
        this.cssRepository = cssRepository;
        initView();
        loadData();
    }

    private void initView() {
        binder = new Binder<>(CSSData.class);
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        grid = new Grid<>(CSSData.class);
        grid.setHeight(75, Unit.VH);
        grid.setColumns("code");
        grid.asSingleSelect().addValueChangeListener(event -> setSelection(event.getValue()));

        createButton = new Button("ADD", VaadinIcon.FILE_ADD.create());
        createButton.addClickListener(event -> setSelection(new CSSData()));

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
                query -> cssRepository.findAll(
                        PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit())
                ).stream(),
                // Second callback fetches the total count of items
                query -> Math.toIntExact(cssRepository.count())
        );
        grid.setDataProvider(gridData);
    }

    private void setSelection(final CSSData template) {
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
                cssRepository.save(selection);
                setSelection(null);
                gridData.refreshAll();
            } else {
                setSelection(cssRepository.save(selection));
            }
        });

        final Button deleteButton = new Button("DELETE", VaadinIcon.FILE_REMOVE.create());
        deleteButton.addClickListener(event -> {
            cssRepository.delete(selection);
            setSelection(null);
            gridData.refreshAll();
        });

        return new VerticalLayout(templateName, new HorizontalLayout(saveButton, deleteButton));
    }

    private VerticalLayout createForm() {
        codeField = new TextField("Code");
        codeField.setWidthFull();
        binder.forField(codeField).bind(CSSData::getCode, CSSData::setCode);
        nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.addValueChangeListener(event -> templateName.setHtmlContent("<h2>" + event.getValue() + "</h2>"));
        binder.forField(nameField).bind(CSSData::getName, CSSData::setName);
        contentField = new TextArea("Content");
        contentField.setWidthFull();
        contentField.setHeight("500px");
        binder.forField(contentField).bind(CSSData::getContent, CSSData::setContent);


        VerticalLayout tab1Content = new VerticalLayout(
                codeField, nameField
        );
        tab1Content.setSizeFull();
        tab1Content.setPadding(true);
        tab1Content.setSpacing(true);
        HorizontalLayout tab2Content = new HorizontalLayout(contentField);
        tab2Content.setVisible(false);
        tab2Content.setSizeFull();


        final Tab tab1 = new Tab();
        final Tab tab2 = new Tab();
        tab1.setLabel("General");
        tab2.setLabel("Content");
        final Tabs tabs = new Tabs(tab1, tab2);

        tabs.addSelectedChangeListener(event -> {
            final Tab tab = event.getSelectedTab();
            tab1Content.setVisible(false);
            tab2Content.setVisible(false);
            if (tab == tab1) {
                tab1Content.setVisible(true);
            } else if (tab == tab2) {
                tab2Content.setVisible(true);
            }
        });

        final VerticalLayout tabsContent = new VerticalLayout(tab1Content, tab2Content);
        tabsContent.setPadding(false);
        tabsContent.setSpacing(false);

        return new VerticalLayout(tabs, tabsContent);
    }
}

