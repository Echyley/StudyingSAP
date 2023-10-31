/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMPricingService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;


/**
 * SapCartCalculationService
 */
public class SapCartCalculationService extends DefaultCalculationService
{
	private transient SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService ;
	private transient SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private transient SapS4OMPricingService sapS4OMPricingService;

	private static final Logger LOG = LoggerFactory.getLogger(SapCartCalculationService.class);

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}


	public void setSapS4OrderManagementConfigService(
			SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
		calculateOrder(order);
		LOG.debug("Calculate the cart");
		super.calculate(order);
	}

	protected void calculateOrder(final AbstractOrderModel order) {
		if(isCalculationRequired(order)) {
			LOG.debug("Prepare cart by making backend call");
				getSapS4SalesOrderSimulationService().setCartDetails( order);
		}
	}

	private boolean isCalculationRequired(final AbstractOrderModel order) {
		return !order.getEntries().isEmpty() && getSapS4OrderManagementConfigService().isCartPricingEnabled() ;
	}
	
	@Override
	public void recalculate(final AbstractOrderModel order) throws CalculationException
	{
		calculateOrder(order);
		LOG.debug("Recalculate the cart");
		super.recalculate(order);
	}
	
	@Override
	public void calculateTotals(final AbstractOrderModel order, final boolean recalculate) throws CalculationException
	{
		calculateOrder(order);
		super.calculateTotals(order, recalculate);
	}
	
	@Override
	protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (getSapS4OrderManagementConfigService().isCartPricingEnabled())
		{
			LOG.debug("resetAllValues disabled as it is already taken care by backend call");
			return;	
		}
		LOG.debug("Synchronous cart pricing is disabled so falling back to out of the box implementation to resetAllValues");
		super.resetAllValues(entry);
	}
	
	@Override
	protected Map resetAllValues(AbstractOrderModel order)
			throws CalculationException {
		
		if (getSapS4OrderManagementConfigService().isCartPricingEnabled())
		{

			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = calculateSubtotal(
					order, false);

			final Collection<TaxValue> relativeTaxValues = new LinkedList<>();
			for (final Map.Entry<TaxValue, ?> e : taxValueMap.entrySet()) {
				final TaxValue taxValue = e.getKey();
				if (!taxValue.isAbsolute()) {
					relativeTaxValues.add(taxValue);
				}
			}
			LOG.debug("Synchronous cart pricing is enabled so fetch taxValueMap.");
			return taxValueMap;
		}
		LOG.debug("Synchronous cart pricing is disabled so falling back to out of the box implementation to resetAllValues.");
		return super.resetAllValues(order);
		

	}
	
	@Override
	protected void resetAdditionalCosts(AbstractOrderModel order,
			Collection<TaxValue> relativeTaxValues) {
		
		if (!(getSapS4OrderManagementConfigService().isCartPricingEnabled()))
		{
			LOG.debug("Synchronous cart pricing is disabled so falling back to out of the box implementation for resetAdditionalCosts");
			super.resetAdditionalCosts(order, relativeTaxValues);
				
		}
		
	}
	
	@Override
	protected List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (!(getSapS4OrderManagementConfigService().isCartPricingEnabled()) || ( entry instanceof OrderEntryModel && ((OrderModel)entry.getOrder()).getQuoteReference() != null) )
		{
			LOG.debug("Synchronous cart pricing is disabled so falling back to out of the box implementation to getDiscountValues");
			return super.findDiscountValues(entry);
			
		}
		LOG.debug("Synchronous cart pricing is enabled so findDiscountValues from OrderEntryModel.");
		return entry.getDiscountValues();
	}

	@Override
	protected PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
	{
		LOG.debug("findBasePrice for synchronous order managment");
		if(getSapS4OrderManagementConfigService().isCatalogPricingEnabled() && entry.getProduct().getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
			List<PriceInformation> priceInformations = null;
			try {
				LOG.debug("Synchronous catalog pricing is enabled findBasePrice from backend.");
				priceInformations = getSapS4OMPricingService().getPriceForProduct(entry.getProduct());
			} catch (OutboundServiceException e) {
				LOG.error("SapCartCalculationService {}"+ e.getMessage());
			}
			
			PriceInformation priceInfo = null;
			if (priceInformations !=null && !priceInformations.isEmpty()) {
				priceInfo =	priceInformations.iterator().next();
			}
			return (priceInfo!= null)? priceInfo.getPriceValue():null;
			
			
		} else {
			LOG.debug("Synchronous catalog pricing is {}  so falling back to out of the box implementation to findBasePrice", getSapS4OrderManagementConfigService().isCatalogPricingEnabled());
			return super.findBasePrice(entry);
		}
	}

	protected SapS4SalesOrderSimulationService getSapS4SalesOrderSimulationService() {
		return sapS4SalesOrderSimulationService;
	}

	public void setSapS4SalesOrderSimulationService(SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService) {
		this.sapS4SalesOrderSimulationService = sapS4SalesOrderSimulationService;
	}


	public SapS4OMPricingService getSapS4OMPricingService() {
		return sapS4OMPricingService;
	}


	public void setSapS4OMPricingService(SapS4OMPricingService sapS4OMPricingService) {
		this.sapS4OMPricingService = sapS4OMPricingService;
	}
	
	
	

}
