/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.converters;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link ProductConfigMessagePromoType} and String
 */
@WsDTOMapping
public class ProductConfigMessagePromoTypeConverter extends BidirectionalConverter<ProductConfigMessagePromoType, String>
{
	@Override
	public ProductConfigMessagePromoType convertFrom(final String source,
			final Type<ProductConfigMessagePromoType> destinationType, final MappingContext mappingContext)
	{
		return ProductConfigMessagePromoType.valueOf(source);
	}

	@Override
	public String convertTo(final ProductConfigMessagePromoType source, final Type<String> destinationType,
			final MappingContext mappingContext)
	{
		return source.toString();
	}
}
