/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.CollectionDescriptor;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import javax.validation.constraints.NotNull;

/**
 * {@inheritDoc}
 * <p>Implementation of {@code TypeAttributeDescriptor} based on {@code IntegrationObjectItemVirtualAttributeModel}</p>
 * <p>This implementation is effectively immutable and therefore is thread safe.</p>
 * <p>Reuse this class through composition, not inheritance.</p>
 */
public class VirtualTypeAttributeDescriptor extends AbstractAttributeDescriptor<IntegrationObjectItemVirtualAttributeModel>
{
	VirtualTypeAttributeDescriptor(@NotNull final TypeDescriptor itemType,
	                               @NotNull final IntegrationObjectItemVirtualAttributeModel attribute,
	                               final DescriptorFactory descriptorFactory)
	{
		super(itemType, attribute, descriptorFactory);
	}

	@Override
	public String getQualifier()
	{
		return getAttributeName();
	}

	@Override
	public boolean isCollection()
	{
		return false;
	}

	@Override
	public boolean isNullable()
	{
		return true;
	}

	@Override
	public boolean isAutoCreate()
	{
		return false;
	}

	@Override
	public boolean isLocalized()
	{
		return false;
	}

	@Override
	public boolean isPrimitive()
	{
		return true;
	}

	@Override
	public boolean isSettable(final Object item)
	{
		return false;
	}

	@Override
	public CollectionDescriptor getCollectionDescriptor()
	{
		return null;
	}

	public String getLogicLocation()
	{
		return getAttributeModel().getRetrievalDescriptor().getLogicLocation();
	}

	@Override
	TypeDescriptor deriveAttributeType()
	{
		return new PrimitiveTypeDescriptor(getTypeDescriptor().getIntegrationObjectCode(), derivePrimitiveType());
	}

	private AtomicTypeModel derivePrimitiveType()
	{
		final TypeModel type = getAttributeModel().getRetrievalDescriptor().getType();
		if (type instanceof AtomicTypeModel)
		{
			return (AtomicTypeModel) type;
		}
		throw new IllegalArgumentException("Only virtual attributes with primitive type can be used");
	}
}
