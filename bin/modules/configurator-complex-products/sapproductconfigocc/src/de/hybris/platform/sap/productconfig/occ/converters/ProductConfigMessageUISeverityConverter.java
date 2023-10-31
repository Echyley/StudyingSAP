/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.converters;

import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link ProductConfigMessageUISeverity} and String
 */
@WsDTOMapping
public class ProductConfigMessageUISeverityConverter extends BidirectionalConverter<ProductConfigMessageUISeverity, String>
{
	@Override
	public ProductConfigMessageUISeverity convertFrom(final String source,
			final Type<ProductConfigMessageUISeverity> destinationType, final MappingContext mappingContext)
	{
		return ProductConfigMessageUISeverity.valueOf(source);
	}

	@Override
	public String convertTo(final ProductConfigMessageUISeverity source, final Type<String> destinationType,
			final MappingContext mappingContext)
	{
		return source.toString();
	}
}
