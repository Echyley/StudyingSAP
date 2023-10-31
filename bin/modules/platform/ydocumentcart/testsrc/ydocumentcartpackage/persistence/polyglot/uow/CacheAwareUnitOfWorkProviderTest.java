/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package ydocumentcartpackage.persistence.polyglot.uow;

import static de.hybris.platform.servicelayer.internal.polyglot.PolyglotPersistenceServiceLayerSupport.getServiceLayerPersistenceInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import javax.annotation.Resource;

import org.junit.Test;

import ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.CachedDocumentStorage;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.cache.StorageCache;

@IntegrationTest
public class CacheAwareUnitOfWorkProviderTest extends ServicelayerBaseTest
{
	@Resource
	CachedDocumentStorage cachedDocumentStorage;

	@Resource
	CacheAwareUnitOfWorkProvider documentCartCacheAwareUnitOfWorkProvider;


	@Test
	public void shouldNotUseUnitOfWorkWhenCacheIsActive()
	{

		try (final StorageCache.CacheContext ignored = cachedDocumentStorage.initCacheContext())
		{
			getServiceLayerPersistenceInterceptor().createFromServiceLayer(() -> {
				assertThat(documentCartCacheAwareUnitOfWorkProvider.getUnitOfWork()).isEmpty();
				return null;
			});
		}
	}

	@Test
	public void shouldUseUnitOfWorkWhenCacheIsInactive()
	{
		getServiceLayerPersistenceInterceptor().createFromServiceLayer(() -> {
			assertThat(documentCartCacheAwareUnitOfWorkProvider.getUnitOfWork()).isPresent();
			return null;
		});
	}
}
