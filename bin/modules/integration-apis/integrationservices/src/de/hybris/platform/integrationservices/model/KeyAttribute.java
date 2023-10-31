/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model;

import de.hybris.platform.integrationservices.exception.LocalizedKeyAttributeException;
import de.hybris.platform.integrationservices.model.impl.DefaultDescriptorFactory;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

/**
 * An attribute declared as a key attribute in integration object item definition.
 */
public class KeyAttribute
{
	private final TypeAttributeDescriptor attributeDescriptor;

	/**
	 * Instantiates this key attribute
	 *
	 * @param model a model
	 * @deprecated use {@link #KeyAttribute(TypeAttributeDescriptor)} to avoid conversion of the attribute model to
	 * {@code TypeAttributeDescriptor}
	 */
	@Deprecated(since = "2205", forRemoval = true)
	public KeyAttribute(final IntegrationObjectItemAttributeModel model)
	{
		this(DefaultDescriptorFactory.getContextFactory().createTypeAttributeDescriptor(model));
	}

	public KeyAttribute(final TypeAttributeDescriptor desc)
	{
		Preconditions.checkArgument(desc != null, "TypeAttributeDescriptor is required to instantiate a KeyAttribute");
		if (desc.isLocalized())
		{
			throw new LocalizedKeyAttributeException(desc);
		}
		attributeDescriptor = desc;
	}

	private String getObjectCode()
	{
		return attributeDescriptor.getTypeDescriptor().getIntegrationObjectCode();
	}

	/**
	 * Retrieves integration object item code this attribute belongs to.
	 *
	 * @return value of the corresponding {@link IntegrationObjectItemModel#getCode()}
	 */
	public String getItemCode()
	{
		return attributeDescriptor.getTypeDescriptor().getItemCode();
	}

	/**
	 * Retrieves name of the integration object item attribute.
	 *
	 * @return name of the attribute given in the integration object
	 */
	public String getName()
	{
		return attributeDescriptor.getAttributeName();
	}

	/**
	 * Retrieves attribute descriptor for this key attribute
	 *
	 * @return attribute descriptor for this attribute.
	 */
	public @NotNull TypeAttributeDescriptor getAttributeDescriptor()
	{
		return attributeDescriptor;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o != null && getClass() == o.getClass())
		{
			final KeyAttribute that = (KeyAttribute) o;
			return getName().equals(that.getName())
					&& getItemCode().equals(that.getItemCode())
					&& getObjectCode().equals(that.getObjectCode());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder()
				.append(getName())
				.append(getItemCode())
				.append(getObjectCode())
				.build();
	}

	@Override
	public String toString()
	{
		return "KeyAttribute{" +
				getObjectCode() + ":" +
				getItemCode() + ":" +
				getName() +
				'}';
	}
}
