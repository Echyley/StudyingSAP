/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ListItemAttributeDTOUpdater;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Default implementation of {@link ListItemAttributeDTOUpdater}
 */
public class DefaultListItemAttributeDTOUpdater implements ListItemAttributeDTOUpdater
{
	@Override
	public List<AbstractListItemDTO> updateDTOs(@NotNull final List<AbstractListItemDTO> oldDTOs,
	                                            @NotNull final List<AbstractListItemDTO> newDTOs)
	{
		final List<AbstractListItemDTO> updatedList = new ArrayList<>(oldDTOs);
		newDTOs.forEach(newDTO -> {
			if (newDTO instanceof ListItemAttributeDTO)
			{
				oldDTOs.stream().filter(abstractDTO -> abstractDTO instanceof ListItemAttributeDTO)
				       .map(abstractDTO -> (ListItemAttributeDTO) abstractDTO)
				       .forEach(oldDTO -> updateOldDTO((ListItemAttributeDTO) newDTO, oldDTO));
			}
			else
			{
				updatedList.add(newDTO);
			}
		});
		return updatedList;
	}

	private void updateOldDTO(final ListItemAttributeDTO newAttributeDTO, final ListItemAttributeDTO oldDTO)
	{
		if (oldDTO.getAttributeDescriptor().getQualifier().equals(newAttributeDTO.getAttributeDescriptor().getQualifier()))
		{
			oldDTO.setAlias(newAttributeDTO.getAlias());
			oldDTO.setSelected(newAttributeDTO.isSelected());
			oldDTO.setCustomUnique(newAttributeDTO.isCustomUnique());
			oldDTO.setAutocreate(newAttributeDTO.isAutocreate());
			oldDTO.setTypeAlias(newAttributeDTO.getTypeAlias());
			oldDTO.setType(newAttributeDTO.getType());
			oldDTO.createDescription();
		}
	}
}
