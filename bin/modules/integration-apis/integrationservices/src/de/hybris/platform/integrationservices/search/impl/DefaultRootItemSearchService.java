/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.search.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.search.RootItemSearchResult;
import de.hybris.platform.integrationservices.search.RootItemSearchService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link RootItemSearchService}
 */
public class DefaultRootItemSearchService implements RootItemSearchService
{
	private final DescriptorFactory descriptorFactory;

	/**
	 * Instantiate the {@link DefaultItemSearchService}
	 *
	 * @param descriptorFactory the descriptor factory to create type descriptor
	 */
	public DefaultRootItemSearchService(@NotNull final DescriptorFactory descriptorFactory)
	{
		Preconditions.checkArgument(descriptorFactory != null, "descriptorFactory cannot be null");
		this.descriptorFactory = descriptorFactory;
	}

	@Override
	@NotNull
	public RootItemSearchResult findRoots(final ItemModel item, final IntegrationObjectModel io)
	{
		if (item != null && io != null)
		{
			final IntegrationObjectDescriptor descriptor = descriptorFactory.createIntegrationObjectDescriptor(io);
			if (descriptor != null)
			{
				final Optional<TypeDescriptor> optionalItemTypeDescriptor = descriptor.getTypeDescriptor(item);
				return optionalItemTypeDescriptor.isPresent() ?
						findRoots(item, optionalItemTypeDescriptor.get()) :
						RootItemSearchResult.EMPTY_RESULT;
			}
		}
		return RootItemSearchResult.EMPTY_RESULT;
	}

	@Override
	@NotNull
	public RootItemSearchResult findRoots(final ItemModel item, final TypeDescriptor typeDescriptor)
	{
		if (item != null && typeDescriptor != null)
		{
			final Collection<Object> referencePathToRoot = typeDescriptor.getPathsToRoot()
			                                                             .stream()
			                                                             .map(p -> p.execute(item))
			                                                             .flatMap(Collection::stream)
			                                                             .collect(Collectors.toList());
			return RootItemSearchResult.createFrom(referencePathToRoot);
		}
		return RootItemSearchResult.EMPTY_RESULT;
	}
}
