/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.europe1.jalo.GeneratedPriceRow.MINQTD;


/**
 * Populator for product volume prices.
 */
public class ProductVolumePricesPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
		extends AbstractProductPopulator<SOURCE, TARGET>
{
	private static final int MULTIPLE_PRICE_INFO_MIN_SIZE = 2;

	private PriceService priceService;
	private PriceDataFactory priceDataFactory;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData)
	{
		if (productData != null)
		{
			final List<PriceInformation> pricesInfos = getPriceService().getPriceInformationsForProduct(productModel);
			if (pricesInfos == null || pricesInfos.size() < MULTIPLE_PRICE_INFO_MIN_SIZE)
			{
				productData.setVolumePrices(Collections.emptyList());
			}
			else
			{
				final List<PriceData> volPrices = createPrices(productModel, pricesInfos);

				// Sort the list into quantity order
				Collections.sort(volPrices, VolumePriceComparator.INSTANCE);

				// Set the max quantities
				for (int i = 0; i < volPrices.size() - 1; i++)
				{
					volPrices.get(i).setMaxQuantity(volPrices.get(i + 1).getMinQuantity() - 1);
				}

				productData.setVolumePrices(volPrices);
			}
		}
	}

	protected List<PriceData> createPrices(final SOURCE productModel, final List<PriceInformation> pricesInfos)
	{
		final List<PriceData> volPrices = new ArrayList<>();

		final PriceDataType priceType = getPriceType(productModel);//not necessary

		for (final PriceInformation priceInfo : pricesInfos)
		{
			final Long minQuantity = getMinQuantity(priceInfo);
			if (minQuantity != null)
			{
				final PriceData volPrice = createPriceData(priceType, priceInfo);
				if (volPrice != null)
				{
					volPrice.setMinQuantity(minQuantity);
					volPrices.add(volPrice);
				}
			}
		}
		return volPrices;
	}

	protected PriceDataType getPriceType(final ProductModel productModel)
	{
		if (CollectionUtils.isEmpty(productModel.getVariants()))
		{
			return PriceDataType.BUY;
		}
		else
		{
			return PriceDataType.FROM;
		}
	}

	protected Long getMinQuantity(final PriceInformation priceInfo)
	{
		final Map qualifiers = priceInfo.getQualifiers();
		final Object minQtdObj = qualifiers.get(MINQTD);
		if (minQtdObj instanceof Long)
		{
			return (Long) minQtdObj;
		}
		return null;
	}

	protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo)
	{
		return getPriceDataFactory().create(priceType, BigDecimal.valueOf(priceInfo.getPriceValue().getValue()),
				priceInfo.getPriceValue().getCurrencyIso());
	}

	protected PriceService getPriceService()
	{
		return priceService;
	}

	@Required
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	public static class VolumePriceComparator extends AbstractComparator<PriceData> implements Serializable
	{
		public static final VolumePriceComparator INSTANCE = new VolumePriceComparator();
		private static final long serialVersionUID = 1L;

		@Override
		protected int compareInstances(final PriceData price1, final PriceData price2)
		{
			if (price1 == null || price1.getMinQuantity() == null)
			{
				return BEFORE;
			}
			if (price2 == null || price2.getMinQuantity() == null)
			{
				return AFTER;
			}

			return compareValues(price1.getMinQuantity().longValue(), price2.getMinQuantity().longValue());
		}
	}
}
