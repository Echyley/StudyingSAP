/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

/**
 * Indicates that an integration object item has a key defined so, that at least one of the key attribute references forms a
 * loop. For example, type A key depends on type B, which has a key attribute referring type A.
 */
public class CircularKeyReferenceException extends InvalidKeyDefinitionException
{
	private static final long serialVersionUID = -5764336421659188807L;
	private static final String MESSAGE_TEMPLATE = "Key attribute '%s.%s' forms a circular reference back to '%s'";
	private final String referencedType;

	/**
	 * Instantiates this exception.
	 *
	 * @param attr an invalid key attribute model
	 * @deprecated use {@link #CircularKeyReferenceException(TypeAttributeDescriptor)} to avoid the attribute model conversion to
	 * {@code TypeAttributeDescriptor}
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public CircularKeyReferenceException(final IntegrationObjectItemAttributeModel attr)
	{
		super(message(attr), attr);
		referencedType = attr.getReturnIntegrationObjectItem().getCode();
	}

	/**
	 * Instantiates this exception.
	 *
	 * @param attr attribute descriptor for the invalid key attribute
	 */
	public CircularKeyReferenceException(final TypeAttributeDescriptor attr)
	{
		super(message(attr), attr);
		referencedType = attr.getAttributeType().getItemCode();
	}

	/**
	 * Retrieves integration item code, to which the loop is formed.
	 *
	 * @return code of the integration item, to which the invalid attribute references.
	 */
	public String getReferencedType()
	{
		return referencedType;
	}

	private static String message(final IntegrationObjectItemAttributeModel attr)
	{
		return String.format(MESSAGE_TEMPLATE, attr.getIntegrationObjectItem().getCode(), attr.getAttributeName(),
				attr.getReturnIntegrationObjectItem().getCode());
	}

	private static String message(final TypeAttributeDescriptor attr)
	{
		return String.format(MESSAGE_TEMPLATE, attr.getTypeDescriptor().getItemCode(), attr.getAttributeName(),
				attr.getAttributeType().getItemCode());
	}
}
