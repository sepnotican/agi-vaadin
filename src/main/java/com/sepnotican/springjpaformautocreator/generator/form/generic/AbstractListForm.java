package com.sepnotican.springjpaformautocreator.generator.form.generic;

import com.sepnotican.springjpaformautocreator.PageableDataProvider;
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

public class AbstractListForm<T, R extends JpaRepository> extends VerticalLayout {

    private static final String OPEN_TEXT = "Open element";

    public AbstractListForm(Class aClass, R repository) {

        Grid<T> grid = new Grid<T>(aClass);

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
        commandPanel.addItem(OPEN_TEXT, VaadinIcons.FOLDER_OPEN,
                event -> {
                    //todo
                });
    }

}
