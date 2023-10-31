/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.hook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hybris.platform.commerceservices.order.hook.CartEarliestRetrievalDateHook;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.sapserviceorder.constants.SapserviceorderConstants;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.Config;

/**
 *  Implementation of {@link CartEarliestRetrievalDateHook} for cart earliest retrieval date for service order integration.
 */
public class DefaultSapServiceCartEarliestRetrievalDataHook implements CartEarliestRetrievalDateHook {

	@Override
	public List<String> getEarliestRetrievalDates(CartModel cart) {
		
		String earliestRetrievalDate = null;
		if(containsServiceProductInCart(cart)) {
			earliestRetrievalDate = getServiceLeadDate(getLeadDaysForService(cart));
		}
		
		return earliestRetrievalDate != null && !earliestRetrievalDate.isEmpty() ?  Collections.singletonList(earliestRetrievalDate) : Collections.emptyList();
	}
	
	protected boolean containsServiceProductInCart(CartModel cart)
	{
		boolean containsServiceProducts = false;
		if (cart != null && cart.getEntries() != null && !cart.getEntries().isEmpty())
		{
			containsServiceProducts = cart.getEntries().stream().anyMatch(SapServiceOrderUtil::isServiceEntry);
		}
		return containsServiceProducts;
	}
	
	
	protected int getLeadDaysForService(CartModel cart)
	{

		final BaseStoreModel baseStore = cart.getStore();

		return baseStore.getSAPConfiguration() != null && baseStore.getSAPConfiguration().getLeadDays() != null
				? baseStore.getSAPConfiguration().getLeadDays()
				: Integer.parseInt(Config.getParameter(SapserviceorderConstants.DEFAULT_LEAD_DAYS));
	}
	
	protected String getServiceLeadDate(int leadDays)
	{
		final Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DATE, leadDays);
		
		
		final DateFormat formatter = new SimpleDateFormat(SapserviceorderConstants.DATE_FORMAT);
		return formatter.format(c.getTime());
	}

}
