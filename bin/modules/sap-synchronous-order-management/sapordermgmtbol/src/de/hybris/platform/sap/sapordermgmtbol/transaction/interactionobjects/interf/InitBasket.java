/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;


/**
 * This interaction object is used to initialize a basket.
 * 
 */
public interface InitBasket
{

	/**
	 * Initialize the basket. This method is used to initialize a new created basket.
	 * 
	 * @param basket
	 *           basket to initialize
	 * @param soldToId
	 *           the id of the sold to party
	 * @param contactId
	 *           the id of the contact
	 * @throws BusinessObjectException
	 *            Is thrown if there is an exception in the bo layer
	 */
	void init(Basket basket, String soldToId, String contactId) throws BusinessObjectException;


	/**
	 * Initialize the partner list.
	 * 
	 * @param partnerList
	 *           list of partner to initialize
	 * @param soldToId
	 *           the id of the sold to party
	 * @param contactId
	 *           the id of the contact
	 */
	void initPartnerList(final PartnerList partnerList, String soldToId, String contactId);

}
