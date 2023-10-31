/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;

import java.util.List;

/**
 * Service to update the values of attribute DTOs used in IntegrationObject modeling
 */
public interface ListItemAttributeDTOUpdater
{
	/**
	 * Updates attributes of a list of DTOs by getting the attributes of another list of DTOs
	 *
	 * @param oldDTOs a list of DTOs with attributes to update
	 * @param newDTOs a list of DTOs containing updated attributes
	 * @return a list of DTOs with updated attributes
	 */
	List<AbstractListItemDTO> updateDTOs(final List<AbstractListItemDTO> oldDTOs,
	                                     final List<AbstractListItemDTO> newDTOs);
}
