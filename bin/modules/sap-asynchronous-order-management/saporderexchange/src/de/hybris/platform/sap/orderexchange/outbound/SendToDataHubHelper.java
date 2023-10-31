/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound;

import de.hybris.platform.servicelayer.model.AbstractItemModel;


/**
 * Helper for creating a raw items from a hybris item and sending it to the Data Hub
 * 
 * @param <T>
 *           The type of the item model for which the raw item shall be built and sent
 */
public interface SendToDataHubHelper<T extends AbstractItemModel>
{

	/**
	 * @param model
	 *           The item model for which the raw item shall be built and sent
	 * @return result of sending raw item to Data Hub
	 */
	SendToDataHubResult createAndSendRawItem(T model);

}
