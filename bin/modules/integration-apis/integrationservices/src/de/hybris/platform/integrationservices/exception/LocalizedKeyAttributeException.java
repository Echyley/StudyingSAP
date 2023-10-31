/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

/**
 * Indicates a problem with Integration Object item definition, when one of its key attributes is also localized. Localized
 * attributes cannot be declared as unique for an item.
 */
public class LocalizedKeyAttributeException extends InvalidKeyDefinitionException
{
	private static final String MESSAGE = "Cannot generate unique property for localized attribute [%s] set on item [%s].";
	private static final long serialVersionUID = 616255084574439071L;

	/**
	 * Constructor to create LocalizedKeyAttributeException
	 *
	 * @param attributeModel model of the invalid attribute definition
	 * @deprecated use {@link #LocalizedKeyAttributeException(TypeAttributeDescriptor)} to avoid conversion of the model to
	 * descriptor.
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public LocalizedKeyAttributeException(final IntegrationObjectItemAttributeModel attributeModel)
	{
		super(MESSAGE, attributeModel);
	}

	/**
	 * Instantiates this exception
	 *
	 * @param attribute descriptor for the localized key attribute
	 */
	public LocalizedKeyAttributeException(final TypeAttributeDescriptor attribute)
	{
		super(MESSAGE, attribute);
	}
}
