/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.hook;

import java.util.Collections;
import java.util.List;

import de.hybris.platform.commerceservices.order.hook.CartEarliestRetrievalDateHook;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.saps4omservices.constants.Saps4omservicesConstants;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 *  Implementation of {@link CartEarliestRetrievalDateHook} for cart earliest retrieval date for S4OM integration.
 */
public class DefaultSapS4OMCartEarliestRetrievalDateHook implements CartEarliestRetrievalDateHook {
	
	private SessionService sessionService;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private SapS4OrderUtil sapS4OrderUtil;

	@Override
	public List<String> getEarliestRetrievalDates(final CartModel cart)
	{
		String earliestRetrievalDate = null;
		if(containsSalesProductInCart(cart) && getSapS4OrderManagementConfigService().isCartPricingEnabled()) {
			earliestRetrievalDate = getSessionService().getCurrentSession().getAttribute(Saps4omservicesConstants.SAPS4OMCARTEARLIESTRETRIEVALDATE_PREFIX+cart.getCode());
		}
		
		return earliestRetrievalDate != null && !earliestRetrievalDate.isEmpty() ?  Collections.singletonList(earliestRetrievalDate) : Collections.emptyList();
	}
	
	protected boolean containsSalesProductInCart(CartModel cart)
	{
		Boolean containsSalesProducts = false;
		if (cart != null && cart.getEntries() != null)
		{
			containsSalesProducts = cart.getEntries().stream().anyMatch(sapS4OrderUtil::isSalesEntry);
		}
		return containsSalesProducts;
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

	public SapS4OrderUtil getSapS4OrderUtil() {
		return sapS4OrderUtil;
	}

	public void setSapS4OrderUtil(SapS4OrderUtil sapS4OrderUtil) {
		this.sapS4OrderUtil = sapS4OrderUtil;
	}

}