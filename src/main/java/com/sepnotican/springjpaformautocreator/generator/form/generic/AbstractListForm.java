package com.sepnotican.springjpaformautocreator.generator.form.generic;

import com.sepnotican.springjpaformautocreator.PageableDataProvider;
import com.sepnotican.springjpaformautocreator.generator.form.IFormHandler;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbstractListForm<T, R extends JpaRepository> extends VerticalLayout {

    private static final String OPEN_TEXT = "Open";
    private static final String DELETE_TEXT = "Delete";
    private static final String CREATE_TEXT = "Create";
    private IFormHandler formHandler;
    private Class aClass;
    private R repository;
    private Grid<T> grid;

    public AbstractListForm(IFormHandler formHandler, Class aClass, R repository) {
        this.formHandler = formHandler;
        this.repository = repository;
        this.aClass = aClass;

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
            grid.addColumn(field.getName());
        }


        createCommandPanel();

        addComponent(grid);

    }

    protected void createCommandPanel() {
        MenuBar commandPanel = new MenuBar();
        MenuBar.MenuItem menuItemOpen = commandPanel.addItem(OPEN_TEXT, VaadinIcons.FOLDER_OPEN,
                event -> {
                    if (grid.getSelectedItems().isEmpty()) return;
                    Set<T> selectedItems = grid.getSelectedItems();
                    selectedItems.forEach(item -> formHandler.showAbstractElementForm(aClass, repository, item));
                });
        MenuBar.MenuItem menuItemCreate = commandPanel.addItem(CREATE_TEXT, VaadinIcons.FOLDER_OPEN,
                event -> {
                    //todo
                });
        MenuBar.MenuItem menuItemDelete = commandPanel.addItem(DELETE_TEXT, VaadinIcons.FOLDER_OPEN,
                event -> {
                    //todo
                });
        addComponent(commandPanel);
    }

}
