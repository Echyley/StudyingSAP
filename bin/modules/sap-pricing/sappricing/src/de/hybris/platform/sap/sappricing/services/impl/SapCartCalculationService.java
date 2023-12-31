/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricing.services.impl;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sappricing.services.SapPricingCartService;
import de.hybris.platform.sap.sappricing.services.SapPricingEnablementService;
import de.hybris.platform.sap.sappricingbol.constants.SappricingbolConstants;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;


/**
 * SapCartCalculationService
 */
public class SapCartCalculationService extends DefaultCalculationService
{

	@SuppressWarnings("squid:S1948")
	private SapPricingCartService sapPricingCartService;
	
	@SuppressWarnings("squid:S1948")
	private SapPricingEnablementService sapPricingEnablementService;
	
	@SuppressWarnings("squid:S1948")
	private ModuleConfigurationAccess moduleConfigurationAccess;
	
	private static final Logger LOG = Logger.getLogger(SapCartCalculationService.class.getName());
	
	

	public SapPricingCartService getSapPricingCartService()
	{
		return sapPricingCartService;
	}

	@Required
	public void setSapPricingCartService(final SapPricingCartService sapPricingCartService)
	{
		this.sapPricingCartService = sapPricingCartService;
	}



	public SapPricingEnablementService getSapPricingEnablementService()
	{
		return sapPricingEnablementService;
	}

	@Required
	public void setSapPricingEnablementService(final SapPricingEnablementService sapPricingEnablementService)
	{
		this.sapPricingEnablementService = sapPricingEnablementService;
	}
	
	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
		
		if (sapPricingEnablementService.isCartPricingEnabled())
		{
			getSapPricingCartService().getPriceInformationForCart(order);
				
		}
		
		super.calculate(order);
	}
	
	@Override
	public void recalculate(final AbstractOrderModel order) throws CalculationException
	{
		
		if (sapPricingEnablementService.isCartPricingEnabled())
		{
			getSapPricingCartService().getPriceInformationForCart(order);
				
		}
		
		super.recalculate(order);
	}
	
	@Override
	public void calculateTotals(final AbstractOrderModel order, final boolean recalculate) throws CalculationException
	{
		if (sapPricingEnablementService.isCartPricingEnabled())
		{
			getSapPricingCartService().getPriceInformationForCart(order);
				
		}
		
		super.calculateTotals(order, recalculate);
	}
	
	/*
	 * These are the different combinations of pricing and order management that are handled:
	 * -AOM with synchronous pricing for cart enabled - need not access the price rows in hybris side, need not call resetAllValues()
	 * -AOM with synchronous pricing for cart and catalogue enabled - need not access the price rows in hybris side
	 * -AOM with asynchronous pricing - need to access the price rows replicated from ERP, hence need to call resetALLValues()
	 * -SOM with synchronous pricing for catalogue enabled - SOM Rfc takes care of pricing, need not call resetAllValues()
	 * -SOM with synchronous pricing for cart and catalogue enabled - Not supported since SOM Rfc takes care of pricing after add to cart,
	 * 																  hence need to disable pricing for cart in backoffice
	 * -SOM with asynchronous pricing - SOM Rfc takes care of pricing after add to cart is done, need not call resetAllValues()
	 * 
	 */
	@Override
	protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (sapPricingEnablementService.isCartPricingEnabled() || isSyncOrdermgmtEnabled())
		{
			return;	
		}
		super.resetAllValues(entry);
	}
	
	@Override
	protected Map resetAllValues(AbstractOrderModel order)
			throws CalculationException {
		
		if (sapPricingEnablementService.isCartPricingEnabled() || isSyncOrdermgmtEnabled())
		{
			// -----------------------------
			// set subtotal and get tax value map
			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = calculateSubtotal(
					order, false);
			/*
			 * filter just relative tax values - payment and delivery prices might
			 * require conversion using taxes -> absolute taxes do not apply here
			 * Ask someone for absolute taxes and how they apply to delivery
			 * cost etc. - this implementation might be wrong now
			 */
			final Collection<TaxValue> relativeTaxValues = new LinkedList<TaxValue>();
			for (final Map.Entry<TaxValue, ?> e : taxValueMap.entrySet()) {
				final TaxValue taxValue = e.getKey();
				if (!taxValue.isAbsolute()) {
					relativeTaxValues.add(taxValue);
				}
			}

			return taxValueMap;
		}

		return super.resetAllValues(order);
		

	}
	
	@Override
	protected void resetAdditionalCosts(AbstractOrderModel order,
			Collection<TaxValue> relativeTaxValues) {
		
		if (!sapPricingEnablementService.isCartPricingEnabled())
		{
			super.resetAdditionalCosts(order, relativeTaxValues);
				
		}
		
	}
	
	@Override
	protected List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (!sapPricingEnablementService.isCartPricingEnabled())
		{
			return super.findDiscountValues(entry);
			
		}
		return entry.getDiscountValues();
	}

	protected boolean isSyncOrdermgmtEnabled() {

		Boolean isSynchronousOrderManagementEnabled = false; 
		if(moduleConfigurationAccess!=null)
		{
			try{
				isSynchronousOrderManagementEnabled = moduleConfigurationAccess.getProperty(SappricingbolConstants.CONF_PROP_IS_ACTIVE_SYNCHRONOUS_ORDER_MANAGEMENT);
			}catch(ConfigurationRuntimeException ex) {
				LOG.warn(" No SAP Base Store Configuration assigned to active Base Store!");
			}
		}
		if(isSynchronousOrderManagementEnabled==null)
		{
			isSynchronousOrderManagementEnabled = false;
		}
		return isSynchronousOrderManagementEnabled;
	}

	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}
	
	@Required
	public void setModuleConfigurationAccess(ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

}
