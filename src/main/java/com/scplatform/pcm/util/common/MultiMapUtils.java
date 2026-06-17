/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.common;

import java.util.*;
import java.util.Map.Entry;

/**
 * Utility to organize Lists into the Structure Map<K,List<V>>
 * 
 * @author sgupta
 */
public class MultiMapUtils {

    /**
     * 
     * Basic functor to represent the key function on an object. Used by organize to organize a list into a multi map
     * 
     * @author sgupta
     */
    public interface MapKeyFunctor<K, V> {

        /**
         * Generates a key from the value. If the key is null then the value is not added to the multivalued Map
         * 
         * @param ase
         * @return
         */
        public abstract K getKey(V ase);
    }

    /**
     * Organizes a list into a MultiValuedMap based on the Key Functor. The values of the map and the map itself are
     * unmodifiable
     * 
     * @param values
     * @param keyFunctor
     *            The Key function to apply on the objects in the list
     * @return A multivalued map
     */
    public static <K, V> Map<K, List<V>> organizeIntoMultiValueMap(Collection<V> values, MapKeyFunctor<K, V> keyFunctor) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<K, List<V>> retval = new HashMap<K, List<V>>();
        for (V val : values) {
            addValueToMultiMap(keyFunctor, retval, val);
        }
        for (Entry<K, List<V>> entry : retval.entrySet()) {
            List<V> val = entry.getValue();
            entry.setValue(Collections.unmodifiableList(val));
        }
        return Collections.unmodifiableMap(retval);
    }

    /**
     * @param keyFunctor
     * @param retval
     * @param val
     */
    private static <K, V> void addValueToMultiMap(MapKeyFunctor<K, V> keyFunctor, Map<K, List<V>> retval, V val) {
        K key = keyFunctor.getKey(val);
        if (key != null) {
            List<V> valList = retval.get(key);
            if (valList == null) {
                valList = new LinkedList<V>();
                retval.put(key, valList);
            }
            valList.add(val);
        }
    }

}
