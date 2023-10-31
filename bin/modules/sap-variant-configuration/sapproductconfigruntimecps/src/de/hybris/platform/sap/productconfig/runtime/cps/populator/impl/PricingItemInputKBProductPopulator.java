/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Populates the pricing item input data for querying static pricing information, such as value prices, based on the
 * configuration master data.<br>
 * <br>
 * We are caching the value prices per product. This of course assumes that a product is part of a KB only in <b> one
 * </b> unit, the base unit. This assumption is correct for IPC/SSC in general, even if it is possible to create BOM's
 * with different UOMs for the same product: You can achieve this by assigning different unit of issues on plant level.
 * Such situations however lead to errors during KB generation. *
 */
public class PricingItemInputKBProductPopulator extends AbstractPricingItemInputPopulator
		implements ContextualPopulator<CPSMasterDataProductContainer, PricingItemInput, MasterDataContext>
{
	private static final Logger LOG = Logger.getLogger(PricingItemInputKBProductPopulator.class);

	@Override
	public void populate(final CPSMasterDataProductContainer source, final PricingItemInput target,
			final MasterDataContext context)
	{
		fillCoreAttributes(source.getId(), createQty(BigDecimal.ONE, getIsoUOM(source)), target);
		fillPricingAttributes(retrievePricingProduct(source, context), target);
		fillAccessDates(target, null);
		fillVariantConditions(source, target, context);
	}

	protected String retrievePricingProduct(final CPSMasterDataProductContainer source, final MasterDataContext context)
	{
		// specifying a different pricing product (e.g for changeable product variants) is only supported for singlelevel / root products
		// stetting this also for non-root procuts would break multilevel pricing!
		// we rely on that the pricing product will  only be specified for singlelebel, as we can not check this constraint easily here
		final String pricingProduct;
		if (context != null && StringUtils.isNotEmpty(context.getPricingProduct()))
		{
			pricingProduct = context.getPricingProduct();
		}
		else
		{
			pricingProduct = source.getId();
		}
		return pricingProduct;

	}

	protected void fillVariantConditions(final CPSMasterDataProductContainer source, final PricingItemInput target,
			final MasterDataContext context)
	{
		target.setVariantConditions(context.getRequestedPricesByProductMap().get(source.getId()).stream()
				.map(this::createVariantCondition).collect(Collectors.toList()));
	}



	protected CPSVariantCondition createVariantCondition(final CPSMasterDataVariantPriceKey pricingKey)
	{
		final CPSVariantCondition variantCondition = new CPSVariantCondition();
		variantCondition.setFactor(String.valueOf(1));
		variantCondition.setKey(pricingKey.getVariantConditionKey());
		return variantCondition;
	}

	protected String getIsoUOM(final CPSMasterDataProductContainer product)
	{
		final String productId = product.getId();
		try
		{
			final ProductModel productModel = getProductService().getProductForCode(productId);
			final UnitModel unitModel = productModel.getUnit();
			return getPricingConfigurationParameter().retrieveUnitIsoCode(unitModel);
		}
		catch (final UnknownIdentifierException ex)
		{
			//In this case we fall back to the unit of measure of the root item
			final String unitOfMeasure = product.getUnitOfMeasure();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Use root unit of measure " + unitOfMeasure + " for product " + productId);
			}
			return unitOfMeasure;
		}
	}
}
