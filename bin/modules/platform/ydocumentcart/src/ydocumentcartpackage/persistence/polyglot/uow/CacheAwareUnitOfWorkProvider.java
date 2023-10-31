/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package ydocumentcartpackage.persistence.polyglot.uow;

import de.hybris.platform.persistence.polyglot.uow.UnitOfWork;
import de.hybris.platform.persistence.polyglot.uow.UnitOfWorkProvider;

import java.util.Optional;

import ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.cache.StorageCache;

public class CacheAwareUnitOfWorkProvider implements UnitOfWorkProvider
{
	private final UnitOfWorkProvider target;
	private final StorageCache storageCache;

	public CacheAwareUnitOfWorkProvider(final UnitOfWorkProvider target,
	                                    final StorageCache storageCache)
	{
		this.target = target;
		this.storageCache = storageCache;
	}

	@Override
	public Optional<UnitOfWork> getUnitOfWork()
	{
		return storageCache.isCacheActive() ? Optional.empty() : target.getUnitOfWork();
	}
}
