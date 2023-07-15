package be.datafarmhouse.luminadocs.ui;

import be.datafarmhouse.luminadocs.ui.views.CSSView;
import be.datafarmhouse.luminadocs.ui.views.ImageView;
import be.datafarmhouse.luminadocs.ui.views.TemplatesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

public class Layout extends AppLayout {

    public Layout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeader());
        addToDrawer(createDrawer());
    }

    private Component createHeader() {
        final HorizontalLayout layout = new HorizontalLayout();

        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.add(new DrawerToggle());
        layout.add(new H3("LuminaDocs"));

        return layout;
    }

    private Component createDrawer() {
        final VerticalLayout layout = new VerticalLayout();

        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        layout.add(createMenu());

        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.add(
                createTab(VaadinIcon.FILE_TEXT.create(), "Templates", TemplatesView.class),
                createTab(VaadinIcon.CSS.create(), "CSS", CSSView.class),
                createTab(VaadinIcon.PICTURE.create(), "Images", ImageView.class)
        );
        return tabs;
    }

    private static Tab createTab(
            final Icon icon,
            final String text,
            final Class<? extends Component> navigationTarget
    ) {
        final Tab tab = new Tab();
        tab.add(icon, new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);
        return tab;
    }
}
