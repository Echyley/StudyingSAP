/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Map;

/**
 * Interface that handles assigning the item type match restriction to elements of an {@link IntegrationObjectModel}
 */
public interface IntegrationObjectItemTypeMatchService
{
	/**
	 * Set the item type match an {@link IntegrationObjectModel}
	 *
	 * @param integrationObjectModel {@link IntegrationObjectItemModel} who's items will have their match restrictions set for
	 * @param itemTypeMatchMap       map containing relations of {@link ComposedTypeModel} to their {@link ItemTypeMatchEnum}
	 */
	void assignItemTypeMatchForIntegrationObject(IntegrationObjectModel integrationObjectModel,
	                                             Map<ComposedTypeModel, ItemTypeMatchEnum> itemTypeMatchMap);

	/**
	 * Retrieves the map of {@link ItemTypeMatchEnum} item type match restrictions grouped by type for a given {@link IntegrationObjectModel}
	 *
	 * @param integrationObjectModel {@link IntegrationObjectModel} to gather item type matches for
	 * @return map containing the {@link ItemTypeMatchEnum} for each {@link ComposedTypeModel}
	 */
	Map<ComposedTypeModel, ItemTypeMatchEnum> groupItemTypeMatchForIntegrationObject(
			IntegrationObjectModel integrationObjectModel);
}
