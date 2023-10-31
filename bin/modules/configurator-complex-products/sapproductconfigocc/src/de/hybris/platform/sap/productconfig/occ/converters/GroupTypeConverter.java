/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.converters;

import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link GroupType} and String
 */
@WsDTOMapping
public class GroupTypeConverter extends BidirectionalConverter<GroupType, String>
{
	@Override
	public GroupType convertFrom(final String source, final Type<GroupType> destinationType, final MappingContext mappingContext)
	{
		return GroupType.valueOf(source);
	}

	@Override
	public String convertTo(final GroupType source, final Type<String> destinationType, final MappingContext mappingContext)
	{
		return source.toString();
	}
}
