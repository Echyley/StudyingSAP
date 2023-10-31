/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiproductexchange.inbound.events;

import java.util.Optional;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;


public class SapCpiProductFeatureCleanHook implements PrePersistHook
{
	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		return Optional.empty();
	}
}
