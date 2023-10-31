/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.core.model.ItemModel;

import java.util.List;

/**
 * Service to handle backend operations of the export configuration editor.
 */
public interface ExportConfigurationEditorService
{
	/**
	 * Find all item model instances of a given integration object item's type's code.
	 *
	 * @param itemTypeCode the integration object item's type's code
	 * @return a list of {@link List<ItemModel>} which are instances of the integration object item's type
	 */
	List<ItemModel> findItemModelInstances(String itemTypeCode);

}
