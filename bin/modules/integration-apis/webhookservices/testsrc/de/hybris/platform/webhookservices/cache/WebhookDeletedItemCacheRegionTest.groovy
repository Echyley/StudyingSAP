/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.regioncache.CacheValueLoadException
import de.hybris.platform.regioncache.CacheValueLoader
import de.hybris.platform.regioncache.key.CacheKey
import de.hybris.platform.servicelayer.tenant.MockTenant
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class WebhookDeletedItemCacheRegionTest extends JUnitPlatformSpecification {

	private static final def JUNIT_TENANT = "junit"
	private static final def TTL_SECONDS = 5L

	private def cacheKey = webhookCacheKey()
	private def cacheRegion = new WebhookDeletedItemCacheRegion(
			"cacheRegion",
			1000,
			"LRU",
			false,
			true,
			Long.valueOf(TTL_SECONDS))

	@Test
	def "the region cache is not emptied when clear cache method is called"() {
		given: "load the cache with key and value"
		cacheRegion.init()
		cacheRegion.getWithLoader(cacheKey, cacheValueLoader(new ItemModel()))

		when: "clear the cache"
		cacheRegion.clearCache()

		then: "the cache was not emptied"
		cacheRegion.get(cacheKey) != null
	}

	private CacheValueLoader cacheValueLoader(final ItemModel itemModel) {
		new CacheValueLoader() {
			@Override
			Object load(final CacheKey cacheKey) throws CacheValueLoadException {
				return itemModel
			}
		}
	}

	private static WebhookDeletedItemCacheKey webhookCacheKey() {
		WebhookTenantUtil.setTenant(new MockTenant(JUNIT_TENANT))
		WebhookDeletedItemCacheKey.from(PK.fromLong(1L))
	}
}
