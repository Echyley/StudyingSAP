/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Class holding a map object with contents pertaining to the definition of a {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}.
 * Used in conjunction with the backoffice modeling tool.
 */
public class IntegrationObjectDefinition
{
	private Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> definitionMap;

	/**
	 * Instantiates with an empty definition map.
	 */
	public IntegrationObjectDefinition()
	{
		this.definitionMap = new HashMap<>();
	}

	/**
	 * Instantiates with the definition map of an existing integration object.
	 *
	 * @param definitionMap an integration object's definition map.
	 */
	public IntegrationObjectDefinition(final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> definitionMap)
	{
		this.definitionMap = copyDefinitionMap(definitionMap);
	}

	/**
	 * Gets a copy of the encapsulated definition map.
	 *
	 * @return a copy of the definition map.
	 */
	public Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> getDefinitionMap()
	{
		return copyDefinitionMap(definitionMap);
	}

	/**
	 * Sets a copy of the argument as the definition map.
	 *
	 * @param definitionMap the map to set as the definition map.
	 */
	public void setDefinitionMap(final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> definitionMap)
	{
		this.definitionMap = definitionMap != null ? copyDefinitionMap(definitionMap) : new HashMap<>();
	}

	/**
	 * Gets a copy of the list of attributes for a given definition map key.
	 *
	 * @param key the {@link IntegrationMapKeyDTO} that is a key in the definition map.
	 * @return a copy of the list of attributes for the given key.
	 */
	public List<AbstractListItemDTO> getAttributesByKey(final IntegrationMapKeyDTO key)
	{
		if (key != null)
		{
			final List<AbstractListItemDTO> attributes = definitionMap.get(key);
			return attributes != null
					? new ArrayList<>(attributes)
					: new ArrayList<>();
		}
		return new ArrayList<>();
	}

	/**
	 * Sets a copy of a given list of attributes as the value of a given definition map key.
	 *
	 * @param key        the {@link IntegrationMapKeyDTO} that is a key in the definition map.
	 * @param attributes the list of attributes that will be set as the value of the map entry.
	 */
	public void setAttributesByKey(final IntegrationMapKeyDTO key, final List<AbstractListItemDTO> attributes)
	{
		if (key != null && definitionMap != null)
		{
			definitionMap.put(key, new ArrayList<>(attributes));
		}
	}

	/**
	 * Gets a regular attribute by alias within a specified parent type instance.
	 *
	 * @param key   the parent type instance of the attribute.
	 * @param alias the attribute's alias.
	 * @return a {@link ListItemAttributeDTO} with the given alias belonging to the given type instance.
	 */
	public ListItemAttributeDTO getAttributeByAlias(final IntegrationMapKeyDTO key, final String alias)
	{
		return getAttributesByKey(key)
				.stream()
				.filter(ListItemAttributeDTO.class::isInstance)
				.map(ListItemAttributeDTO.class::cast)
				.filter(listItemDTO -> listItemDTO.getAlias().equals(alias))
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException(
						String.format("No ListItemAttribute was found for attribute with alias %s", alias)));
	}

	/**
	 * Clears the definition map
	 */
	public void clear()
	{
		definitionMap.clear();
	}

	/**
	 * Determines if map contains a given key
	 *
	 * @param key Key used in search
	 * @return If key is present
	 */
	public boolean containsKey(final IntegrationMapKeyDTO key)
	{
		return definitionMap.containsKey(key);
	}

	public void replace(final IntegrationMapKeyDTO key, final List<AbstractListItemDTO> listDTO)
	{
		definitionMap.replace(key, listDTO);
	}

	private Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> copyDefinitionMap(
			final Map<IntegrationMapKeyDTO, List<AbstractListItemDTO>> definitionMap)
	{
		return definitionMap.entrySet()
		                    .stream()
		                    .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
	}
}
