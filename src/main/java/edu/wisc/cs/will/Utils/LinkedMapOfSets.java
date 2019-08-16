package edu.wisc.cs.will.Utils;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author twalker
 */
public class LinkedMapOfSets<Key, Value> extends MapOfSets<Key, Value> {

    public LinkedMapOfSets() {}

    @Override
    protected Map<Key, Set<Value>> createMap() {
        return new LinkedHashMap<>();
    }

    @Override
    protected Set<Value> createValueSet() {
        return new LinkedHashSet<>();
    }
}
