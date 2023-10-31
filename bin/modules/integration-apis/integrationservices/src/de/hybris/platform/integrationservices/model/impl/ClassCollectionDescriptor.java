/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.CollectionDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import com.google.common.base.Preconditions;

/**
 * Implementation of the {@link CollectionDescriptor} for POJO attributes. If the underlying
 * attribute is not of type {@link Collection}, the new collection will be null.
 */
public final class ClassCollectionDescriptor implements CollectionDescriptor
{
	private final PojoIntrospector introspector;
	private final Supplier<Collection<Object>> supplier;

	private ClassCollectionDescriptor(final PojoIntrospector spector)
	{
		Preconditions.checkArgument(spector != null, "PojoIntrospector cannot be null");
		introspector = spector;

		final CollectionInfo info = deriveCollectionInfo();
		supplier = info.supplier;
	}

	/**
	 * Creates descriptor for {@link IntegrationObjectClassAttributeModel}.
	 *
	 * @param spector a {@link PojoIntrospector} used to retrieve method/attribute info
	 */
	public static CollectionDescriptor create(final PojoIntrospector spector)
	{
		return new ClassCollectionDescriptor(spector);
	}

	@Override
	public Collection<Object> newCollection()
	{
		return supplier.get();
	}

	private boolean isSet()
	{
		return introspector.getReturnType().map(Set.class::isAssignableFrom)
		                   .orElse(false);
	}

	private CollectionInfo deriveCollectionInfo()
	{
		if (introspector.isCollection())
		{
			return isSet() ? CollectionInfo.createSetCollectionInfo() : CollectionInfo.createListCollectionInfo();
		}
		return CollectionInfo.createNullCollectionInfo();
	}

	private static class CollectionInfo
	{
		private final Supplier<Collection<Object>> supplier;

		private CollectionInfo(final Supplier<Collection<Object>> s)
		{
			supplier = s;
		}

		private static CollectionInfo createSetCollectionInfo()
		{
			return new CollectionInfo(HashSet::new);
		}

		private static CollectionInfo createListCollectionInfo()
		{
			return new CollectionInfo(ArrayList::new);
		}

		private static CollectionInfo createNullCollectionInfo()
		{
			return new CollectionInfo(() -> null);
		}
	}
}

