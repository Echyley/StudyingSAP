/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.impl.DefaultDescriptorFactory;

/**
 * Indicates any problem with an attribute definition for an integration object item or any invalid usage of an attribute.
 */
public abstract class IntegrationAttributeException extends RuntimeException
{
	private static final long serialVersionUID = 1090222127418771019L;
	private final transient TypeAttributeDescriptor attributeDescriptor;

	/**
	 * Instantiates this exception.
	 * @param message a message to be used for this exception. If the message contains {@link String#format(String, Object...)}} parameter
	 * placeholders, then they will be replaced in this order: attributeName, integration object item code.
	 * @param attr an invalid attribute
	 * @deprecated use {@link #IntegrationAttributeException(String, TypeAttributeDescriptor)} instead to avoid conversion of the
	 * {@code IntegrationObjectItemAttributeModel} to {@code TypeAttributeDescriptor}
	 */
	@Deprecated(since = "2205", forRemoval = true)
	protected IntegrationAttributeException(final String message, final IntegrationObjectItemAttributeModel attr)
	{
		this(message, DefaultDescriptorFactory.getContextFactory().createTypeAttributeDescriptor(attr));
	}

	/**
	 * Instantiates this exception
	 * @param message a message to be used for this exception. If this message contains format parameter placeholders, e.g. {@code "%s"},
	 * then the message will be used as a template and the placeholders will be replaced in this order: {@code attributeName}, {@code ioItemCode}
	 * @param descriptor descriptor of the attribute, for which the exception is thrown.
	 */
	protected IntegrationAttributeException(final String message, final TypeAttributeDescriptor descriptor)
	{
		super(String.format(message, descriptor.getAttributeName(), descriptor.getTypeDescriptor().getItemCode()));
		attributeDescriptor = descriptor;
	}

	/**
	 * Instantiates this exception
	 * @param message a message to be used for this exception. If this message contains format parameter placeholders, e.g. {@code "%s"},
	 * then the message will be used as a template and the placeholders will be replaced in this order: {@code attributeName}, {@code ioItemCode}
	 * @param descriptor descriptor of the attribute, for which the exception is thrown.
	 * @param cause an intercepted exception that caused this exception to be thrown
	 */
	protected IntegrationAttributeException(final String message, final TypeAttributeDescriptor descriptor, final Throwable cause)
	{
		super(String.format(message, descriptor.getAttributeName(), descriptor.getTypeDescriptor().getItemCode()), cause);
		attributeDescriptor = descriptor;
	}

	public String getAttributeName()
	{
		return attributeDescriptor.getAttributeName();
	}

	public String getIntegrationItemCode()
	{
		return attributeDescriptor.getTypeDescriptor().getItemCode();
	}

	/**
	 * Retrieves descriptor of the attribute, for which this exception was thrown.
	 * @return descriptor of the failed attribute
	 */
	public TypeAttributeDescriptor getAttributeDescriptor()
	{
		return attributeDescriptor;
	}
}
