/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.cxml.ItemOut;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;


/**
 * Converts an {@link ItemOut} into an {@link AbstractOrderEntryModel}.
 */
public class DefaultItemOutConverter implements Converter<ItemOut, AbstractOrderEntryModel>
{
	private CartService cartService;
	private ProductService productService;


	@Override
	public AbstractOrderEntryModel convert(final ItemOut source)
	{
		final String supplierPartID = source.getItemID().getSupplierPartID().getvalue();
		final Long quantity = Double.valueOf(source.getQuantity()).longValue();

		try
		{
			final ProductModel productModel = getProductService().getProductForCode(supplierPartID);
			final UnitModel unit = productModel.getUnit();

			return getCartService().addNewEntry(getCartService().getSessionCart(), productModel, quantity.longValue(),
					unit);
		}
		catch (final UnknownIdentifierException e)
		{
			throw new PunchOutException(HttpStatus.BAD_REQUEST, "Product with code " + supplierPartID + " is not found.", e);
		}
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

}
