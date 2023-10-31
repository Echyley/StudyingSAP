/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class holding a map pertaining to duplicate attribute data. Primarily if two or more attributes have the same name/alias.
 * Used in conjunction with the backoffice modeling tool.
 */
public class IntegrationObjectDefinitionDuplicationMap
{
	private final Map<IntegrationMapKeyDTO, Map<String, List<AbstractListItemDTO>>> duplicationMap;

	/**
	 * Default constructor for a {@link IntegrationObjectDefinitionDuplicationMap}.
	 */
	public IntegrationObjectDefinitionDuplicationMap()
	{
		duplicationMap = new HashMap<>();
	}

	/**
	 * Gets a copy of the duplication map.
	 *
	 * @return a copy of the duplication map.
	 */
	public Map<IntegrationMapKeyDTO, Map<String, List<AbstractListItemDTO>>> getDuplicationMap()
	{
		return copyDuplicationOuterMap(duplicationMap);
	}

	/**
	 * Gets a copy of the duplicate attributes for a given duplication map key.
	 *
	 * @param key a {@link IntegrationMapKeyDTO} that is a key in the duplication map.
	 * @return a copy of the duplicate attributes for the given key.
	 */
	public Map<String, List<AbstractListItemDTO>> getDuplicateAttributesByKey(final IntegrationMapKeyDTO key)
	{
		return key != null && duplicationMap.get(key) != null
				? copyDuplicationInnerMap(duplicationMap.get(key))
				: new HashMap<>();
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 *
	 * @param key                 a {@link IntegrationMapKeyDTO} that is a key in the duplication map.
	 * @param duplicateAttributes a map that is to be set associated with the key.
	 */
	public void setAttributesByKey(final IntegrationMapKeyDTO key,
	                               final Map<String, List<AbstractListItemDTO>> duplicateAttributes)
	{
		if (key != null && duplicateAttributes != null)
		{
			duplicationMap.put(key, copyDuplicationInnerMap(duplicateAttributes));
		}
	}

	/**
	 * Associates the specified value with the specified key in a inner map that is associated with a MapKeyDTO.
	 *
	 * @param key       a {@link IntegrationMapKeyDTO} that is a key in the duplication map.
	 * @param alias     a key in the inner map.
	 * @param listOfDTO a list that is to be set associated with the two keys.
	 */
	public void setAttributesByKey(final IntegrationMapKeyDTO key, final String alias,
	                               final List<AbstractListItemDTO> listOfDTO)
	{
		duplicationMap.putIfAbsent(key, new HashMap<>());
		duplicationMap.get(key).put(alias, listOfDTO);
	}

	/**
	 * Clears the duplication map.
	 */
	public void clear()
	{
		duplicationMap.clear();
	}

	/**
	 * Determines if the outer map and inner map contains given keys.
	 *
	 * @param key   a {@link IntegrationMapKeyDTO} that is a key in the duplication map.
	 * @param alias a key in the inner map.
	 */
	public boolean containsKey(final IntegrationMapKeyDTO key, final String alias)
	{
		return duplicationMap.containsKey(key) && duplicationMap.get(key) != null
				&& duplicationMap.get(key).containsKey(alias) && duplicationMap.get(key).get(alias) != null;
	}

	/**
	 * Add dto to the list in the inner map that is associated with the key and alias.
	 *
	 * @param key   a {@link IntegrationMapKeyDTO} that is a key in the duplication map.
	 * @param alias a key in the inner map.
	 * @param dto   a value that is associated with key and alias.
	 */
	public void addDTO(final IntegrationMapKeyDTO key, final String alias, final AbstractListItemDTO dto)
	{
		if (containsKey(key, alias))
		{
			duplicationMap.get(key).get(alias).add(dto);
		}
	}

	/**
	 * Removes the mapping for a key from this map if it is present.
	 */
	public Map<String, List<AbstractListItemDTO>> remove(final IntegrationMapKeyDTO key)
	{
		return duplicationMap.remove(key);
	}

	/**
	 * Removes all entries that have no mentions of duplicates
	 */
	public void removeEntryWithEmptyList()
	{
		duplicationMap.entrySet().removeIf(type -> type.getValue().isEmpty());
	}

	private Map<IntegrationMapKeyDTO, Map<String, List<AbstractListItemDTO>>> copyDuplicationOuterMap(
			final Map<IntegrationMapKeyDTO, Map<String, List<AbstractListItemDTO>>> map)
	{
		return map.entrySet()
		          .stream()
		          .collect(Collectors.toMap(Map.Entry::getKey, e -> copyDuplicationInnerMap(e.getValue())));
	}

	private Map<String, List<AbstractListItemDTO>> copyDuplicationInnerMap(final Map<String, List<AbstractListItemDTO>> map)
	{
		return map.entrySet()
		          .stream()
		          .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
	}
}
