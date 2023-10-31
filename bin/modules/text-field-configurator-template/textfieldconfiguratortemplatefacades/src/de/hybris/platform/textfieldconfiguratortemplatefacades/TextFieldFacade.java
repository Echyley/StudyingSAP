/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplatefacades;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;


/**
 * Utility methods for JSP and OCC controllers
 */
public interface TextFieldFacade
{


	/**
	 * Returns and order entry specified by its entry number
	 *
	 * @param entryNumber
	 *           Entry number, starting from 0
	 * @param abstractOrder
	 *           Cart, order or quote document the entry belongs to
	 * @return Order entry
	 * @throws CommerceCartModificationException
	 *            Thrown if entry cannot be found
	 */
	OrderEntryData getAbstractOrderEntry(int entryNumber, AbstractOrderData abstractOrder)
			throws CommerceCartModificationException;

}
