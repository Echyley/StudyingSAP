/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodelfacades.populators;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.stream.Collectors;


/**
 * SAP Product Type Populator
 */
public class DefaultSapProductTypePopulator implements Populator<ProductModel, ProductData>
{

	@Override
	public void populate(final ProductModel source, final ProductData target)
	{
		if (source.getSapProductTypes() != null)
		{
			target.setSapProductTypes(source.getSapProductTypes().stream().map(String::valueOf).collect(Collectors.joining(",")));
		}
		else
		{
			target.setSapProductTypes("");
		}
	}

}
