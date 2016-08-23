package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.publicInterfaces.Exchangeable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by GArlington.
 */
public class CollectionOfExchangeableMock implements Collection<Exchangeable> {
    Collection<Exchangeable> collection;

    public CollectionOfExchangeableMock(Exchangeable... exchangeables) {
        this.collection = new ArrayList<>(Arrays.asList(exchangeables));
    }

    public Collection<Exchangeable> getCollection() {
        return collection;
    }

    @Override
    public int size() {
        return collection.size();
    }

    @Override
    public boolean isEmpty() {
        return collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return collection.contains(o);
    }

    @Override
    public Iterator<Exchangeable> iterator() {
        return collection.iterator();
    }

    @Override
    public Object[] toArray() {
        return collection.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return collection.toArray(a);
    }

    @Override
    public boolean add(Exchangeable exchangeable) {
        return collection.add(exchangeable);
    }

    @Override
    public boolean remove(Object o) {
        return collection.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return collection.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Exchangeable> c) {
        return collection.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return collection.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return collection.retainAll(c);
    }

    @Override
    public void clear() {
        collection.clear();
    }

    @Override
    public String toString() {
        return "CollectionOfExchangeableMock{" +
                "collection=" + collection +
                '}';
    }
}
