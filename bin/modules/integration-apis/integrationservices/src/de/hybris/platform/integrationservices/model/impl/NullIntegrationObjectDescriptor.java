/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * This class uses the Null Object Design Pattern, in order to provide an object with details about the
 * {@link IntegrationObjectDescriptor} that could not be created due to a missing {@link IntegrationObjectModel}.
 */
public class NullIntegrationObjectDescriptor implements IntegrationObjectDescriptor
{
	private final String code;

	/**
	 * Constructor to create a NullIntegrationObjectDescriptor
	 *
	 * @param code integration object code
	 */
	public NullIntegrationObjectDescriptor(final String code)
	{
		this.code = StringUtils.isNotBlank(code) ? code : StringUtils.EMPTY;
	}

	@Override
	public String getCode()
	{
		return code;
	}

	@Override
	public Set<TypeDescriptor> getItemTypeDescriptors()
	{
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated use {@link #getTypeDescriptor(Object)}
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public Optional<TypeDescriptor> getItemTypeDescriptor(final ItemModel item)
	{
		return Optional.empty();
	}

	@Override
	public Optional<TypeDescriptor> getTypeDescriptor(final Object payloadObject)
	{
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated use {@link #getRootType()} instead.
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public Optional<TypeDescriptor> getRootItemType()
	{
		return getRootType();
	}

	@Override
	public Optional<TypeDescriptor> getRootType()
	{
		return Optional.empty();
	}

	@Override
	public boolean contains(final TypeDescriptor descriptor)
	{
		return false;
	}
}
