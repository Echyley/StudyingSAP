/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;

import org.zkoss.zul.Treeitem;

/**
 * Holds information about a tree node to be retyped.
 */
public class RetypeTreeData
{

	final IntegrationMapKeyDTO parentKey;
	final ComposedTypeModel newComposedType;
	final Treeitem currentTreeitem;
	final ListItemAttributeDTO dto;

	/**
	 * Data class that holds information about a tree node to be retyped.
	 *
	 * @param parentKey the key of the parent tree node.
	 * @param newComposedType the new type.
	 * @param currentTreeitem the currently selected {@link Treeitem}.
	 * @param dto the {@link ListItemAttributeDTO} of the attribute to retype.
	 */
	public RetypeTreeData(final IntegrationMapKeyDTO parentKey, final ComposedTypeModel newComposedType,
	                      final Treeitem currentTreeitem, final ListItemAttributeDTO dto)
	{
		this.parentKey = parentKey;
		this.newComposedType = newComposedType;
		this.currentTreeitem = currentTreeitem;
		this.dto = dto;
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
	 * Gets the new type.
	 *
	 * @return the new type.
	 */
	public ComposedTypeModel getNewComposedType()
	{
		return newComposedType;
	}

	/**
	 * Gets the currently selected {@link Treeitem}.
	 *
	 * @return the currently selected {@link Treeitem}.
	 */
	public Treeitem getCurrentTreeitem()
	{
		return currentTreeitem;
	}

	/**
	 * Gets the {@link ListItemAttributeDTO} of the attribute to retype.
	 *
	 * @return the {@link ListItemAttributeDTO} of the attribute to retype.
	 */
	public ListItemAttributeDTO getDto()
	{
		return dto;
	}
}
