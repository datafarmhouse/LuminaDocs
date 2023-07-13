package be.datafarmhouse.luminadocs.ui.views;

import be.datafarmhouse.luminadocs.template.data.CSSData;
import be.datafarmhouse.luminadocs.template.data.CSSRepository;
import be.datafarmhouse.luminadocs.ui.Layout;
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

    private CSSData selection;
    private Grid<CSSData> grid;
    private TextField codeField;
    private TextField nameField;
    private TextArea contentField;
    private VerticalLayout rightLayout;
    private Binder<CSSData> binder;

    private Button newButton;
    private Button saveButton;
    private Button cancelButton;

    @Autowired
    public CSSView(final CSSRepository cssRepository) {
        this.cssRepository = cssRepository;
        initView();
        loadData();
    }

    private void initView() {
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        grid = new Grid<>(CSSData.class);
        grid.setColumns("code");
        grid.asSingleSelect().addValueChangeListener(event -> setSelection(event.getValue()));

        newButton = new Button(VaadinIcon.PLUS_SQUARE_O.create());
        newButton.addClickListener(event -> setSelection(new CSSData()));

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setSizeFull();
        leftLayout.add(new HorizontalLayout(newButton));
        leftLayout.add(grid);

        rightLayout = new VerticalLayout();
        rightLayout.setVisible(false);
        rightLayout.setSizeFull();
        rightLayout.add(createButtonPanel(), createForm());

        splitLayout.addToPrimary(leftLayout);
        splitLayout.addToSecondary(rightLayout);
        splitLayout.setSplitterPosition(20);

        add(splitLayout);
    }

    private void loadData() {
        grid.setDataProvider(DataProvider.fromCallbacks(
                // First callback fetches items based on the requested range
                query -> cssRepository.findAll(
                        PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit())
                ).stream(),
                // Second callback fetches the total count of items
                query -> Math.toIntExact(cssRepository.count())
        ));
    }

    private void setSelection(final CSSData template) {
        if (template != null) {
            rightLayout.setVisible(true);
            selection = template;
            binder.setBean(template);
        } else {
            rightLayout.setVisible(false);
            binder.setBean(null);
            selection = null;
            grid.deselectAll();
        }
    }

    private HorizontalLayout createButtonPanel() {
        saveButton = new Button(VaadinIcon.FILE.create());
        saveButton.addClickListener(event -> {
            cssRepository.save(selection);
            setSelection(null);
        });
        cancelButton = new Button(VaadinIcon.ARROW_LONG_LEFT.create());
        cancelButton.addClickListener(event -> {
            setSelection(null);
        });
        final HorizontalLayout layout = new HorizontalLayout(saveButton, cancelButton);
        layout.setWidthFull();
        return layout;
    }

    private VerticalLayout createForm() {
        binder = new Binder<>(CSSData.class);

        codeField = new TextField("Code");
        codeField.setWidthFull();
        binder.forField(codeField).bind(CSSData::getCode, CSSData::setCode);
        nameField = new TextField("Name");
        nameField.setWidthFull();
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

