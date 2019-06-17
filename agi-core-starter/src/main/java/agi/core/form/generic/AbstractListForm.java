package agi.core.form.generic;

import agi.core.annotations.AgiColumnValueProvider;
import agi.core.annotations.Filtered;
import agi.core.annotations.LinkedObject;
import agi.core.annotations.RepresentationResolver;
import agi.core.dao.CompareType;
import agi.core.dao.CriteriaFilter;
import agi.core.dao.GenericBackendDataProvider;
import agi.core.dao.GenericDaoFactory;
import agi.core.form.IFormHandler;
import agi.core.form.util.VaadinProvidersFactory;
import com.google.common.collect.ImmutableSet;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;


@Component
@Scope("prototype")
@Slf4j
public class AbstractListForm<T> extends VerticalLayout {

    @Value("${agi.forms.list.new}")
    protected String CREATE_TEXT;
    @Value("${agi.forms.list.open}")
    protected String OPEN_TEXT;
    @Value("${agi.forms.list.delete}")
    protected String DELETE_TEXT;
    protected IFormHandler formHandler;
    protected Grid<T> grid;
    protected Class<T> aClass;
    protected HorizontalLayout filterLayout;
    protected Set<CriteriaFilter> filterSet = new HashSet<>();
    protected DataProvider<T, Set<CriteriaFilter>> gridDataProvider;
    protected ConfigurableFilterDataProvider<T, Void, Set<CriteriaFilter>> wrapper;
    @Autowired
    GenericFieldGenerator genericFieldGenerator;
    @Autowired
    GenericDaoFactory genericDaoFactory;
    List<Class<?>> supportedFilters;

    public AbstractListForm(IFormHandler formHandler, Class aClass) {
        this.formHandler = formHandler;
        this.aClass = aClass;
        this.setHeightUndefined();
    }

    @PostConstruct
    protected void init() {
        fillSupportedFilters();
        initializeGridDataProvider();
        createCommandPanel();
        createFilterLayout();
        createFilters();
        createGrid();
    }

    private void fillSupportedFilters() {
        supportedFilters = new ArrayList<>();
        supportedFilters.add(Integer.class);
        supportedFilters.add(int.class);
        supportedFilters.add(Long.class);
        supportedFilters.add(long.class);
        supportedFilters.add(String.class);
        supportedFilters.add(Short.class);
        supportedFilters.add(short.class);
        supportedFilters.add(Double.class);
        supportedFilters.add(double.class);
        supportedFilters.add(Boolean.class);
        supportedFilters.add(boolean.class);
    }

    @SuppressWarnings("unchecked")
    private void initializeGridDataProvider() {
        gridDataProvider = new GenericBackendDataProvider<T>(aClass, genericDaoFactory.getGenericDaoForClass(aClass));

        wrapper = gridDataProvider.withConfigurableFilter();
    }

    private void createFilterLayout() {
        filterLayout = new HorizontalLayout();
        addComponent(filterLayout);
    }

    @SuppressWarnings("unchecked")
    protected void createGrid() {
        grid = new Grid<>(aClass);

        grid.setHeightUndefined();
        grid.setWidthUndefined();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setDataProvider(wrapper);

        createGridColumns();
        addComponentsAndExpand(grid);
    }

    @SuppressWarnings("unchecked")
    protected void createFilters() {
        for (Field field : aClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Filtered.class) ||
                    !(supportedFilters.contains(field.getType()) || field.isAnnotationPresent(LinkedObject.class)))
                continue;
            com.vaadin.ui.Component componentByField = genericFieldGenerator.getComponentByField(field);
            if (componentByField == null) continue;
            ((HasValue) componentByField).addValueChangeListener((HasValue.ValueChangeListener) event -> {
                CriteriaFilter criteriaFilter = new CriteriaFilter(field.getType(), field.getName(),
                        event.getValue(), getFieldCompareType(field));
                filterSet.remove(criteriaFilter);
                filterSet.add(criteriaFilter);
                wrapper.setFilter(filterSet);
            });
            filterLayout.addComponent(componentByField);
        }
    }

    private CompareType getFieldCompareType(Field field) {
        if (field.getType() == String.class) return CompareType.LIKE;
        return CompareType.EQUALS;
    }

    protected void createGridColumns() {
        grid.removeAllColumns();
        for (Field field : aClass.getDeclaredFields()) {
            if (!isIgnoredType(field.getType())) {
                Grid.Column<T, ?> tColumn;
                if (field.getType().isAnnotationPresent(RepresentationResolver.class)) {
                    tColumn = createColumnWithRepresentationResolver(field);
                } else {
                    tColumn = grid.addColumn(field.getName());
                }
                genericFieldGenerator.makeUpCaptionForField(field, tColumn);
            }
        }
        for (Method method : aClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AgiColumnValueProvider.class)) {
                Grid.Column<T, ?> tColumn = createColumnWithAgiValueProvider(method);
                genericFieldGenerator.makeUpCaptionForMethodProvidedColumn(method, tColumn);
            }
        }
    }

    private Grid.Column<T, ?> createColumnWithAgiValueProvider(Method method) {
        Grid.Column<T, String> column = grid.addColumn(VaadinProvidersFactory.getValueProvider(method));
        return column;
    }

    protected Grid.Column<T, ?> createColumnWithRepresentationResolver(Field field) {
        String methodQualifier = field.getType().getAnnotation(RepresentationResolver.class).value();
        Grid.Column<T, String> column = grid.addColumn(VaadinProvidersFactory.getValueProvider(field, methodQualifier));
        column.setSortOrderProvider(direction -> Stream.of(new QuerySortOrder(field.getName(), direction)));
        column.setSortable(true);
        return column;
    }

    protected boolean isIgnoredType(Class<?> type) {
        Set<Class> ignoredClasses = ImmutableSet.of(Map.class, List.class, Set.class);
        for (Class iclass : ignoredClasses) {
            if (type.isAssignableFrom(iclass)) {
                return true;
            }
        }
        return false;
    }

    protected void createCommandPanel() {
        MenuBar commandPanel = new MenuBar();
        createMenuButtonNew(commandPanel);
        createMenuButtonOpen(commandPanel);
        createMenuButtonRemove(commandPanel);
        addComponent(commandPanel);
    }

    protected void createMenuButtonRemove(MenuBar commandPanel) {
        commandPanel.addItem(DELETE_TEXT, VaadinIcons.FILE_REMOVE,
                event -> {
                    if (grid.getSelectedItems().isEmpty()) return;

                    Set<T> selectedItems = grid.getSelectedItems();
                    final Label label = new Label(String.format("Do you really want to delete %d elements?",
                            grid.getSelectedItems().size()));
                    Window dialog = new Window();
                    VerticalLayout verticalLayout = new VerticalLayout();
                    verticalLayout.addComponent(label);
                    verticalLayout.addComponent(new Button("Yes", event1 -> {
                        selectedItems.forEach(genericDaoFactory.getGenericDaoForClass(aClass)::delete);
                        this.grid.getDataProvider().refreshAll();
                        dialog.close();
                    }));
                    verticalLayout.addComponent(new Button("No", event1 -> {
                        dialog.close();
                    }));
                    dialog.setContent(verticalLayout);
                    dialog.setModal(true);
                    dialog.center();
                    getUI().addWindow(dialog);
                });
    }

    protected void createMenuButtonOpen(MenuBar commandPanel) {
        commandPanel.addItem(OPEN_TEXT, VaadinIcons.FOLDER_OPEN,
                event -> {
                    if (grid.getSelectedItems().isEmpty()) return;
                    Set<T> selectedItems = grid.getSelectedItems();
                    selectedItems.forEach(item -> openAbstractElementForm(item, false));
                });
    }

    protected void createMenuButtonNew(MenuBar commandPanel) {
        commandPanel.addItem(CREATE_TEXT, VaadinIcons.FILE_ADD,
                event -> {
                    try {
                        T newObject = aClass.newInstance();
                        openAbstractElementForm(newObject, true);
                    } catch (InstantiationException | IllegalAccessException e) {
                        handleError("Error is appeared while instantiating new object ", e);
                    }
                });
    }

    protected void openAbstractElementForm(T item, boolean isNewInstance) {
        try {
            formHandler.showAbstractElementForm(item, isNewInstance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            handleError("Error is appeared while creating abstract element form", e);
        }
    }

    protected void handleError(String userMessage, Exception e) {
        userMessage += " for class :\n" + aClass.getCanonicalName();
        Notification.show("Error", userMessage, Notification.Type.ERROR_MESSAGE);
        log.error(userMessage + '\n' + e.getMessage());
    }

}
