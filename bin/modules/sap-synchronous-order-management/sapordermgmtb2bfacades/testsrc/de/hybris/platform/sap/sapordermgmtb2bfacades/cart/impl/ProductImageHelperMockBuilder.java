/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtb2bfacades.cart.impl;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;

import org.easymock.EasyMock;


/**
 * 
 */
public class ProductImageHelperMockBuilder
{

	ProductImageHelper productImageHelperMock = EasyMock.createNiceMock(ProductImageHelper.class);

	public static ProductImageHelperMockBuilder create()
	{
		return new ProductImageHelperMockBuilder();
	}

	public ProductImageHelperMockBuilder withEnrichWithProductImages(final AbstractOrderData orderEntryDataMock)
	{
		productImageHelperMock.enrichWithProductImages(orderEntryDataMock);
		return this;
	}

	public ProductImageHelperMockBuilder withEnrichWithProductImages(final OrderEntryData orderEntryDataMock)
	{
		productImageHelperMock.enrichWithProductImages(orderEntryDataMock);
		return this;
	}

	public ProductImageHelper build()
	{
		EasyMock.replay(productImageHelperMock);
		return productImageHelperMock;
	}


}
