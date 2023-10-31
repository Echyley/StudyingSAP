/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata;

import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

public class InvalidNavigationPropertyException extends InvalidDataException
{
	private static final long serialVersionUID = 1724229061981520425L;
	private static final String CODE = "invalid_property_definition";
	private final String propertyName;

	private InvalidNavigationPropertyException(final String message, final String entityType, final String propertyName)
	{
		super(String.format(message, entityType, propertyName), CODE, entityType);
		this.propertyName = propertyName;
	}

	public InvalidNavigationPropertyException(final String message, final TypeAttributeDescriptor descriptor)
	{
		this(message, descriptor.getTypeDescriptor().getTypeCode(), descriptor.getAttributeName());
	}
	
	public String getPropertyName()
	{
		return propertyName;
	}
}
