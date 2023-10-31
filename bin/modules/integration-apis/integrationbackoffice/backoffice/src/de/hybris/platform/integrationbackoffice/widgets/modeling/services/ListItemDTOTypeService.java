/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;

/**
 * Determines the {@link TypeModel} of a {@link AbstractListItemDTO}.
 */
public interface ListItemDTOTypeService
{
	/**
	 * Gets an {@link AbstractListItemDTO}'s type.
	 *
	 * @param dto a given {@link AbstractListItemDTO}.
	 * @return a {@link TypeModel} representing the type of the DTO.
	 */
	TypeModel getDTOType(AbstractListItemDTO dto);
}
