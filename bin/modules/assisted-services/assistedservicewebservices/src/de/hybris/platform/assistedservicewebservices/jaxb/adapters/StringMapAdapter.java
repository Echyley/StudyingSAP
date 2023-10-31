/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.jaxb.adapters;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringMapAdapter extends XmlAdapter<StringMapAdapter.KeyValueAdaptedMap, Map<String, String>>
{
    /**
     * This class represents a key value map. It contains {@link KeyValueAdaptedEntry} entries.
     */
    public static class KeyValueAdaptedMap
    {
        @XmlVariableNode("key")
        List<KeyValueAdaptedEntry> entries = new ArrayList<>();
    }

    /**
     * This class represents a simple key-value entry in a map. It holds a key and a value, both
     * as strings.
     */
    public static class KeyValueAdaptedEntry
    {
        @XmlTransient
        public String key;

        @XmlValue
        public String value;
    }

    @Override
    public KeyValueAdaptedMap marshal(final Map<String, String> map)
    {
        if (map == null)
        {
            return null;
        }
        final KeyValueAdaptedMap adaptedMap = new KeyValueAdaptedMap();
        map.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry ->
        {
            final KeyValueAdaptedEntry adaptedEntry = new KeyValueAdaptedEntry();
            adaptedEntry.key = entry.getKey();
            adaptedEntry.value = entry.getValue();
            adaptedMap.entries.add(adaptedEntry);
        });
        return adaptedMap;
    }

    @Override
    public Map<String, String> unmarshal(final KeyValueAdaptedMap adaptedMap)
    {
        if (adaptedMap == null)
        {
            return null;
        }
        final List<KeyValueAdaptedEntry> adaptedEntries = adaptedMap.entries;
        final Map<String, String> map = new HashMap<>(adaptedEntries.size());
        for (final KeyValueAdaptedEntry adaptedEntry : adaptedEntries)
        {
            map.put(adaptedEntry.key, adaptedEntry.value);
        }
        return map;
    }
}
