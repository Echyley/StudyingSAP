/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.AttributeSettableChecker;
import de.hybris.platform.integrationservices.model.AttributeValueAccessor;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * A base class for all attribute descriptor implementations.
 */
abstract class AbstractAttributeDescriptor<MODEL extends AbstractIntegrationObjectItemAttributeModel>
		extends AbstractDescriptor
		implements TypeAttributeDescriptor
{
	private final TypeDescriptor containerItemType;
	private final MODEL attributeModel;
	private TypeDescriptor attributeType;
	private AttributeValueAccessor attributeValueAccessor;
	private AttributeSettableChecker attributeSettableChecker;

	AbstractAttributeDescriptor(@NotNull final TypeDescriptor t, @NotNull final MODEL a, final DescriptorFactory f)
	{
		super(f);
		Preconditions.checkArgument(t != null, "TypeDescriptor cannot be null");
		Preconditions.checkArgument(a != null, "Non-null attribute model must be provided");
		containerItemType = t;
		attributeModel = a;
	}

	@Override
	public String getAttributeName()
	{
		return attributeModel.getAttributeName();
	}

	@Override
	public TypeDescriptor getAttributeType()
	{
		if (attributeType == null)
		{
			attributeType = deriveAttributeType();
		}
		return attributeType;
	}

	@Override
	@NotNull
	public TypeDescriptor getTypeDescriptor()
	{
		return containerItemType;
	}

	@Override
	public AttributeValueAccessor accessor()
	{
		if (attributeValueAccessor == null)
		{
			attributeValueAccessor = getFactory().getAttributeValueAccessorFactory().create(this);
		}
		return attributeValueAccessor;
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
			final AbstractAttributeDescriptor<?> that = (AbstractAttributeDescriptor<?>) o;
			return Objects.equals(getAttributeName(), that.getAttributeName()) &&
					Objects.equals(containerItemTypeName(), that.containerItemTypeName());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(containerItemTypeName(), getAttributeName());
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("itemType", containerItemTypeName())
				.add("attributeName", getAttributeName())
				.toString();
	}

	MODEL getAttributeModel()
	{
		return attributeModel;
	}

	AttributeSettableChecker getSettableChecker()
	{
		if (attributeSettableChecker == null)
		{
			attributeSettableChecker = getFactory().getAttributeSettableCheckerFactory().create(this);
		}
		return attributeSettableChecker;
	}

	abstract TypeDescriptor deriveAttributeType();

	String containerItemTypeName()
	{
		return getTypeDescriptor().getItemCode();
	}
}
