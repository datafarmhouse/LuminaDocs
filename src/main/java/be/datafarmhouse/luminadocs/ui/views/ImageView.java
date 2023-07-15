package be.datafarmhouse.luminadocs.ui.views;

import be.datafarmhouse.luminadocs.template.data.ImageData;
import be.datafarmhouse.luminadocs.template.data.ImageRepository;
import be.datafarmhouse.luminadocs.ui.Layout;
import be.datafarmhouse.luminadocs.ui.components.ImageField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@Log4j2
@PageTitle("LuminaDocs | Images")
@Route(value = "images", layout = Layout.class)
public class ImageView extends Div {

    private final ImageRepository imageRepository;

    private ImageData selection;
    private Grid<ImageData> grid;
    private TextField codeField;
    private ImageField contentField;
    private VerticalLayout rightLayout;
    private Binder<ImageData> binder;

    private Button createButton;
    private Button saveButton;
    private DataProvider<ImageData, ?> gridData;

    @Autowired
    public ImageView(final ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
        initView();
        loadData();
    }

    private void initView() {
        binder = new Binder<>(ImageData.class);
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        grid = new Grid<>(ImageData.class);
        grid.setHeight(75, Unit.VH);
        grid.setColumns("code");
        grid.asSingleSelect().addValueChangeListener(event -> setSelection(event.getValue()));

        createButton = new Button("ADD", VaadinIcon.FILE_ADD.create());
        createButton.addClickListener(event -> setSelection(new ImageData()));

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
                query -> imageRepository.findAll(
                        PageRequest.of(query.getOffset() / query.getLimit(), query.getLimit())
                ).stream(),
                // Second callback fetches the total count of items
                query -> Math.toIntExact(imageRepository.count())
        );
        grid.setDataProvider(gridData);
    }

    private void setSelection(final ImageData image) {
        if (image != null) {
            rightLayout.setVisible(true);
            selection = image;
            binder.setBean(image);
        } else {
            rightLayout.setVisible(false);
            binder.setBean(null);
            selection = null;
            grid.deselectAll();
        }
    }

    private Component createButtonPanel() {
        saveButton = new Button("SAVE", VaadinIcon.FILE.create());
        saveButton.addClickListener(event -> {
            if (selection.getId() == null) {
                imageRepository.save(selection);
                setSelection(null);
                gridData.refreshAll();
            } else {
                setSelection(imageRepository.save(selection));
            }
        });

        final Button deleteButton = new Button("DELETE", VaadinIcon.FILE_REMOVE.create());
        deleteButton.addClickListener(event -> {
            imageRepository.delete(selection);
            setSelection(null);
            gridData.refreshAll();
        });

        return new VerticalLayout(new HorizontalLayout(saveButton, deleteButton));
    }

    @SneakyThrows
    private VerticalLayout createForm() {
        codeField = new TextField("Code");
        codeField.setWidthFull();
        binder.forField(codeField).bind(ImageData::getCode, ImageData::setCode);
        contentField = new ImageField(null);
        binder.forField(contentField).bind(ImageData::getContent, ImageData::setContent);

        return new VerticalLayout(
                codeField, contentField
        );
    }
}

