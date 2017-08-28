package org.trading.exchange.interfaces;

import java.util.Collection;
import java.util.Map;

/**
 * Created by GArlington.
 */
public interface DataSource {
	default <T> int getCount(Class<T> clazz) {
		return getCount((T) null);
	}

	<T> int getCount(T filter);

	default <T> T getObject(T filter) {
		return getObject(getKeys(filter), filter);
	}

	<K, T> T getObject(Collection<K> key, T filter);

	default <T> T putObject(T object) {
		return putObject(getKeys(object), object);
	}

	<K, T> T putObject(Collection<K> key, T object);

	<T> T deleteObject(T object);

	<T> Collection<T> getResults(T filter);

	<K> Map<String, Collection<K>> getKeysMap();

	default <K, T> Collection<K> getKeys(T object) {
		@SuppressWarnings("unchecked") Collection<K> result = (Collection<K>) getKeysMap().get(object.getClass().getName());
		return result;
	}
}
