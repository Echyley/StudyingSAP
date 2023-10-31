/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.hook;

import java.util.List;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.hooks.CartValidationHook;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import com.sap.retail.oaa.commerce.services.sourcing.strategy.SourcingStrategy;

/**
 * Cart validation hook to do order source during checkout process.
 */
public class DefaultSapOaaCartValidationHook implements CartValidationHook{
	private static final String SOURCING_INVALID = "ordersourcinginvalid";
	private SourcingStrategy sourcingStrategy;
	

	@Override
	public void beforeValidateCart(CommerceCartParameter parameter, List<CommerceCartModification> modifications) 
	{
		//Nothing to do	
	}

	/**
	 * Do sourcing for cart
	 *
	 * @param parameter
	 *           the information for validation
	 * @param modifications
	 *           list containing the validation results
	 */
	@Override
	public void afterValidateCart(CommerceCartParameter parameter, List<CommerceCartModification> modifications) 
	{
		if (parameter == null)
		{
			return;
		}
		
		ServicesUtil.validateParameterNotNullStandardMessage("cart", parameter.getCart());
		if(!getSourcingStrategy().doSourcing(parameter.getCart())) {
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setStatusCode(SOURCING_INVALID);
			modifications.add(modification);
		}
		
	}
	
	public SourcingStrategy getSourcingStrategy() {
		return sourcingStrategy;
	}
	
	public void setSourcingStrategy(final SourcingStrategy sourcingStrategy)
	{
		this.sourcingStrategy = sourcingStrategy;
	}

}
