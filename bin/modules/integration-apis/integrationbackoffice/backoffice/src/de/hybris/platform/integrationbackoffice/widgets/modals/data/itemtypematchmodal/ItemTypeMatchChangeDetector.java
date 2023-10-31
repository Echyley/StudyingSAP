/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.data.itemtypematchmodal;

import de.hybris.platform.integrationservices.enums.ItemTypeMatchEnum;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.List;

/**
 * Represents one of the row of the ItemTypeMatchModal after the save button is clicked. It is used to
 * check if the ItemTypeMatch value is changed for the collection of IntegrationObjectItemModels and needs
 * to be saved to the database.
 */
public class ItemTypeMatchChangeDetector
{
	private final List<IntegrationObjectItemModel> integrationObjectItemModels;
	private boolean isModified = false;

	public ItemTypeMatchChangeDetector(final List<IntegrationObjectItemModel> integrationObjectItemModels,
	                                   final String selectedItemTypeMatchCode)
	{
		this.integrationObjectItemModels = integrationObjectItemModels;
		setItemTypeMatchEnumByCode(selectedItemTypeMatchCode);
	}

	private void setItemTypeMatchEnumByCode(final String selectedItemTypeMatchCode)
	{
		final ItemTypeMatchEnum itemTypeMatchEnum = ItemTypeMatchEnum.valueOf(selectedItemTypeMatchCode);
		final ItemTypeMatchEnum itemTypeMatchEnumIOI = integrationObjectItemModels.get(0).getItemTypeMatch();
		if (itemTypeMatchEnumIOI == null || !itemTypeMatchEnumIOI.equals(itemTypeMatchEnum))
		{
			isModified = true;
			integrationObjectItemModels.forEach(ioi -> ioi.setItemTypeMatch(itemTypeMatchEnum));
		}
	}

	public List<IntegrationObjectItemModel> getIntegrationObjectItemModels()
	{
		return integrationObjectItemModels;
	}

	public boolean isModified()
	{
		return isModified;
	}
}
