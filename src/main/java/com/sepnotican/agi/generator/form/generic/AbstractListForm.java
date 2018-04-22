package com.sepnotican.agi.generator.form.generic;

import com.sepnotican.agi.PageableDataProvider;
import com.sepnotican.agi.generator.form.IFormHandler;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractListForm<T, R extends JpaRepository> extends VerticalLayout {

    private static final String OPEN_TEXT = "Open";
    private static final String DELETE_TEXT = "Delete";
    private static final String CREATE_TEXT = "Create";
    private IFormHandler formHandler;
    private final static Logger logger = Logger.getLogger(AbstractListForm.class);
    private R repository;
    private Grid<T> grid;
    private Class<T> aClass;

    public AbstractListForm(IFormHandler formHandler, Class aClass, R repository) {
        this.formHandler = formHandler;
        this.repository = repository;
        this.aClass = aClass;

        createCommandPanel();
        createGrid(aClass, repository);
    }

    protected void createGrid(Class aClass, R repository) {
        grid = new Grid<T>(aClass);

        grid.setHeightUndefined();
        grid.setWidthUndefined();
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);


        grid.setDataProvider(new PageableDataProvider<T, String>() {
            @Override
            protected Page<T> fetchFromBackEnd(Query<T, String> query, Pageable pageable) {
                return repository.findAll(pageable);
            }

            @Override
            protected List<QuerySortOrder> getDefaultSortOrders() {
                List<QuerySortOrder> list = new ArrayList<>();
                list.add(new QuerySortOrder("id", SortDirection.ASCENDING));
                return list;
            }

            @Override
            protected int sizeInBackEnd(Query<T, String> query) {
                return (int) repository.count();
            }
        });

        grid.removeAllColumns();
        for (Field field : aClass.getDeclaredFields()) {
            if (isNotIgnoredType(field.getType())) {
                grid.addColumn(field.getName());
            }
        }

        addComponent(grid);
    }

    protected boolean isNotIgnoredType(Class<?> type) {
        return !type.isAssignableFrom(Set.class)
                && !type.isAssignableFrom(Map.class);
    }

    protected void createCommandPanel() {
        MenuBar commandPanel = new MenuBar();
        //BUTTON NEW
        MenuBar.MenuItem menuItemCreate = commandPanel.addItem(CREATE_TEXT, VaadinIcons.FILE_ADD,
                event -> {
                    try {
                        T newObject = aClass.newInstance();
                        openAbstractElementForm(newObject, true);
                    } catch (InstantiationException | IllegalAccessException e) {
                        handleError("Error is appeared while instantiating new object ", e);
                    }
                });
        //BUTTON OPEN
        MenuBar.MenuItem menuItemOpen = commandPanel.addItem(OPEN_TEXT, VaadinIcons.FOLDER_OPEN,
                event -> {
                    if (grid.getSelectedItems().isEmpty()) return;
                    Set<T> selectedItems = grid.getSelectedItems();
                    selectedItems.forEach(item -> openAbstractElementForm(item, false));
                });
        //BUTTON REMOVE
        MenuBar.MenuItem menuItemDelete = commandPanel.addItem(DELETE_TEXT, VaadinIcons.FILE_REMOVE,
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

//                    selectedItems.forEach(item -> openAbstractElementForm(item, false));
                });
        addComponent(commandPanel);
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
        logger.error(userMessage + '\n' + e.getMessage());
    }

}
