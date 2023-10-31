/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.data;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class RenameAttributeModalData
{
	private final List<AbstractListItemDTO> attributes;
	private final AbstractListItemDTO dto;
	private final IntegrationMapKeyDTO parent;

	/**
	 * Holds information about an attribute to be renamed. Updates as user inputs information in the rename modal.
	 *
	 * @param attributes the list of attributes for the parent item which the attribute to rename belongs to.
	 * @param dto        the {@link AbstractListItemDTO} to update for the attribute to rename.
	 * @param parent     the {@link IntegrationMapKeyDTO} of the parent item for the attribute to rename.
	 */
	public RenameAttributeModalData(final List<AbstractListItemDTO> attributes,
	                                final AbstractListItemDTO dto,
	                                final IntegrationMapKeyDTO parent)
	{
		this.attributes = CollectionUtils.isNotEmpty(attributes) ? new ArrayList<>(attributes) : new ArrayList<>();
		this.dto = dto;
		this.parent = parent;
	}

	/**
	 * Gets the list of attributes for the parent item which the attribute to rename belongs to.
	 *
	 * @return the list of attributes of the parent item of the attribute to rename.
	 */
	public List<AbstractListItemDTO> getAttributes()
	{
		return List.copyOf(attributes);
	}

	/**
	 * Gets the {@link AbstractListItemDTO} to update for the attribute to rename.
	 *
	 * @return the {@link AbstractListItemDTO} to update.
	 */
	public AbstractListItemDTO getDto()
	{
		return dto;
	}

	/**
	 * Gets the {@link IntegrationMapKeyDTO} of the parent item for the attribute to rename.
	 *
	 * @return the {@link IntegrationMapKeyDTO} of the parent item.
	 */
	public IntegrationMapKeyDTO getParent()
	{
		return parent;
	}
}
