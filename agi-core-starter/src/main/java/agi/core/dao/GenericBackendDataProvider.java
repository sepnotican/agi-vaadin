package agi.core.dao;

import com.google.common.collect.ImmutableSet;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class GenericBackendDataProvider<T> extends AbstractBackEndDataProvider {

    Class<T> aClass;
    GenericDao genericDao;

    public GenericBackendDataProvider(Class<T> aClass, GenericDao genericDao) {
        this.aClass = aClass;
        this.genericDao = genericDao;
    }

    @Override
    protected Stream fetchFromBackEnd(Query query) {
        return fetchFromBackEndList(query).stream();
    }

    protected List<T> fetchFromBackEndList(Query query) {
        return genericDao.getEntitiesByCriteriaFilterSet(
                (Set<CriteriaFilter>) query.getFilter().orElse(ImmutableSet.of()),
                query.getSortOrders(),
                query.getOffset(),
                query.getLimit());
    }

    @Override
    protected int sizeInBackEnd(Query query) {
        return Math.toIntExact(genericDao.getCountByCriteriaFilterSet(
                (Set<CriteriaFilter>) query.getFilter().orElse(ImmutableSet.of())));
    }
}
