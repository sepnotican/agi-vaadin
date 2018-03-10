package com.sepnotican.springjpaformautocreator.generator.form;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.stream.Stream;

public class AbstractListForm<T> extends VerticalLayout {

    public AbstractListForm(Class aClass, JpaRepository<T, ? extends Serializable> repository) {

        Grid<T> grid = new Grid<T>(aClass);

        grid.setDataProvider(new AbstractBackEndDataProvider<T, Object>() {
            @Override
            protected Stream<T> fetchFromBackEnd(Query<T, Object> query) {
                return repository.findAll().stream();
            }

            @Override
            protected int sizeInBackEnd(Query<T, Object> query) {
                return repository.findAll().size();
            }
        });

        grid.removeAllColumns();
        for (Field field : aClass.getDeclaredFields()) {
            grid.addColumn(field.getName());
        }

        addComponent(grid);

    }

}
