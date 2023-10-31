/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

/**
 * Sets the {@link AbstractIntegrationObjectItemAttributeModel#setReturnIntegrationObjectItem(IntegrationObjectItemModel)} for a {@link IntegrationObjectModel}.
 */
public interface ReturnIntegrationObjectItemService
{
	/**
	 * This method aims to set ReturnIntegrationObjectItem for each {@link ListItemAttributeDTO} and {@link ListItemClassificationAttributeDTO}.
	 * From an IntegrationObject it will iterate over its {@link IntegrationObjectItemModel} and {@link IntegrationObjectItemAttributeModel}.
	 * When iterating IOIA the method will make sure it is represented by a selected DTO and find the {@link ComposedTypeModel} of it then
	 * find the correct IOI by matching the DTO's typeAlias and type with keys in {@link IntegrationObjectDefinition}.
	 *
	 * @param integrationObject           The root {@link IntegrationObjectModel}
	 * @param integrationObjectDefinition the definition of the root IO
	 */
	void setReturnIntegrationObjectItems(IntegrationObjectModel integrationObject,
	                                     IntegrationObjectDefinition integrationObjectDefinition);
}
