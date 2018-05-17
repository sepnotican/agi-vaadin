package com.sepnotican.agi.core.form.generic;

import com.google.common.collect.ImmutableSet;
import com.sepnotican.agi.core.annotations.RepresentationResolver;
import com.sepnotican.agi.core.form.IFormHandler;
import com.sepnotican.agi.core.utils.CompareType;
import com.sepnotican.agi.core.utils.CriteriaFilter;
import com.sepnotican.agi.core.utils.GenericCriteriaFetcherFactory;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


@Component
@Scope("prototype")
@Slf4j
public class AbstractListForm<T, R extends JpaRepository> extends VerticalLayout {

    @Value("${agi.forms.list.new}")
    protected String CREATE_TEXT;
    @Value("${agi.forms.list.open}")
    protected String OPEN_TEXT;
    @Value("${agi.forms.list.delete}")
    protected String DELETE_TEXT;
    protected IFormHandler formHandler;
    protected R repository;
    protected Grid<T> grid;
    protected Class<T> aClass;
    protected HorizontalLayout filterLayout;

    protected Set<CriteriaFilter> filterSet = new HashSet<>();

    @Autowired
    GenericFieldGenerator fieldGenerator;
    protected DataProvider<T, CriteriaFilter> gridDataProvider;
    protected ConfigurableFilterDataProvider<T, Void, CriteriaFilter> wrapper;
    @Autowired
    private GenericCriteriaFetcherFactory repositoryFactory;

    public AbstractListForm(IFormHandler formHandler, Class aClass, R repository) {
        this.formHandler = formHandler;
        this.repository = repository;
        this.aClass = aClass;
        this.setHeightUndefined();
    }

    @PostConstruct
    protected void init() {
        initializeGridDataProvider();
        createCommandPanel();
        createFilterLayout();
        createFilers();
        createGrid();
    }

    private void initializeGridDataProvider() {
        gridDataProvider = DataProvider.fromFilteringCallbacks(
                new CallbackDataProvider.FetchCallback<T, CriteriaFilter>() {
                    @Override
                    public Stream<T> fetch(Query<T, CriteriaFilter> query) {
                        CriteriaFilter criteriaFilter = query.getFilter().orElse(null);
                        if (criteriaFilter != null)
                            return repositoryFactory.createCriteriaRepository(aClass).getByCriteria(ImmutableSet.of(criteriaFilter)).stream();
                        else
                            return repositoryFactory.createCriteriaRepository(aClass).getByCriteria(ImmutableSet.of()).stream();
                    }
                },
                query -> {
                    CriteriaFilter criteriaFilter = query.getFilter().orElse(null);
                    if (criteriaFilter != null)
                        return repositoryFactory.createCriteriaRepository(aClass).getByCriteria(ImmutableSet.of(criteriaFilter)).size();
                    else
                        return repositoryFactory.createCriteriaRepository(aClass).getByCriteria(ImmutableSet.of()).size();
                }
        );
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

    protected void createFilers() {
        for (Field field : aClass.getDeclaredFields()) {
            com.vaadin.ui.Component componentByField = fieldGenerator.getComponentByField(field);

            if (componentByField == null) continue;

            ((HasValue) componentByField).addValueChangeListener(new HasValue.ValueChangeListener() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent event) {
                    CriteriaFilter criteriaFilter = new CriteriaFilter(field.getName(), event.getValue().toString(), CompareType.STARTS_WITH);
                    wrapper.setFilter(criteriaFilter);
                    log.info(criteriaFilter.toString());
                }
            });

            filterLayout.addComponent(componentByField);
        }
    }

    protected void createGridColumns() {
        grid.removeAllColumns();
        for (Field field : aClass.getDeclaredFields()) {
            if (isNotIgnoredType(field.getType())) {
                if (field.getType().isAnnotationPresent(RepresentationResolver.class)) {
                    buildFieldRepresentationResolver(field);
                } else {
                    grid.addColumn(field.getName());
                }
            }
        }
    }

    protected void buildFieldRepresentationResolver(Field field) {
        String methodQualifier = field.getType().getAnnotation(RepresentationResolver.class).value();
        for (Method method : field.getType().getDeclaredMethods()) {
            if (method.isAnnotationPresent(RepresentationResolver.class) &&
                    method.getAnnotation(RepresentationResolver.class).value().equals(methodQualifier)) {
                Grid.Column<T, String> column = grid.addColumn(new ValueProvider<T, String>() {
                    @Override
                    public String apply(T t) {
                        try {
                            Object classMember;
                            if (!field.isAccessible()) {
                                field.setAccessible(true);
                                classMember = field.get(t);
                                field.setAccessible(false);
                            } else classMember = field.get(t);
                            if (classMember != null) return (String) method.invoke(classMember);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.error(e.getMessage());
                        }
                        return null;
                    }
                });
                column.setCaption(field.getType().getSimpleName());
                column.setSortOrderProvider(direction -> Stream.of(new QuerySortOrder(field.getName(), direction)));
                column.setSortable(true);
                break;
            }
        }
    }

    protected boolean isNotIgnoredType(Class<?> type) {
        return !type.isAssignableFrom(Set.class)
                && !type.isAssignableFrom(Map.class);
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
                        selectedItems.forEach(repository::delete);
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
            formHandler.showAbstractElementForm(repository, item, isNewInstance);
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
