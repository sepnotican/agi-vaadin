package com.sepnotican.springjpaformautocreator.generator.form;

import com.sepnotican.springjpaformautocreator.PageableDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AbstractListForm<T> extends VerticalLayout {

    public AbstractListForm(Class aClass, JpaRepository<T, ? extends Serializable> repository) {

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

        addComponent(grid);

    }

}
