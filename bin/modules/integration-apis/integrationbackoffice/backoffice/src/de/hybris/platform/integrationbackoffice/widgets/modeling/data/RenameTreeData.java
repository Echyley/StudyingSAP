/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;

/**
 * Holds information about a tree node to be renamed.
 */
public class RenameTreeData
{
	private final IntegrationMapKeyDTO parentKey;
	private final AbstractListItemDTO matchedDTO;
	private final String qualifier;

	/**
	 * Data class holding information about a tree node to be renamed
	 *
	 * @param parentKey  Key identifying parent node
	 * @param matchedDTO {@link AbstractListItemDTO} that matches the info of the node to be updated
	 * @param qualifier  Qualifier label of the node to be renamed
	 */
	public RenameTreeData(final IntegrationMapKeyDTO parentKey,
	                      final AbstractListItemDTO matchedDTO, final String qualifier)
	{
		this.parentKey = parentKey;
		this.matchedDTO = matchedDTO;
		this.qualifier = qualifier;
	}

	/**
	 * Gets the parent tree node's key.
	 *
	 * @return the parent tree node's key.
	 */
	public IntegrationMapKeyDTO getParentKey()
	{
		return parentKey;
	}

	/**
	 * Gets the {@link AbstractListItemDTO} that holds the information of the tree node to be updated.
	 *
	 * @return the {@link AbstractListItemDTO} that holds the information of the tree node to be updated.
	 */
	public AbstractListItemDTO getMatchedDTO()
	{
		return matchedDTO;
	}

	/**
	 * Gets the qualifier label of the node to be renamed.
	 *
	 * @return the qualifier label of the node to be renamed.
	 */
	public String getQualifier()
	{
		return qualifier;
	}
}
