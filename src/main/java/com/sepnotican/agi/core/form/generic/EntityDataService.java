package com.sepnotican.agi.core.form.generic;

import com.vaadin.data.provider.QuerySortOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;

public class EntityDataService<T> {

    JpaRepository repository;

    public EntityDataService(JpaRepository repository) {
        this.repository = repository;
    }

    public Stream<T> getItems(int offset, int limit, List<QuerySortOrder> sortOrders, String filter) {
        return null;
    }

    public int count() {
        return 0;
    }
}
