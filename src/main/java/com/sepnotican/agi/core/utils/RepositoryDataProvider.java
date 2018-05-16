package com.sepnotican.agi.core.utils;

import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public class RepositoryDataProvider<T> extends PageableDataProvider {

    private JpaRepository repository;

    public RepositoryDataProvider(JpaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Page<T> fetchFromBackEnd(Query query, Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        List<QuerySortOrder> list = new ArrayList<>();
        list.add(new QuerySortOrder("id", SortDirection.ASCENDING));
        return list;
    }

    @Override
    protected int sizeInBackEnd(Query query) {
        return (int) repository.count();
    }

}
