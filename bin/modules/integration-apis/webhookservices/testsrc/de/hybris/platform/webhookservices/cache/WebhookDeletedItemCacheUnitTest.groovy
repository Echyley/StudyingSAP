/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.regioncache.CacheValueLoader
import de.hybris.platform.regioncache.key.CacheKey
import de.hybris.platform.regioncache.region.CacheRegion
import de.hybris.platform.servicelayer.tenant.MockTenant
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

@UnitTest
class WebhookDeletedItemCacheUnitTest extends JUnitPlatformSpecification {

	private static final def JUNIT_TENANT = "junit"
	private static final def KEY = webhookCacheKey()
	private static final def VALUE = new ItemModel()

	def cacheRegion = Mock(CacheRegion)
	def cache = new WebhookDeletedItemCache(cacheRegion: cacheRegion)

	@Test
	def 'get method returns the cached value from the cache region'() {
		given:
		cacheRegion.get(KEY) >> VALUE

		expect:
		cache.get(KEY) == VALUE
	}

	@Test
	def "get method returns null when #condition"() {
		given:
		cacheRegion.get(_ as CacheKey) >> cachedValue

		expect:
		cache.get(cacheKey) == null

		where:
		condition                              | cacheKey | cachedValue
		"there is no cached value for the key" | KEY      | null
		"cached value is not an ItemModel"     | KEY      | new Object()
		"key is null"                          | null     | null
	}

	@Test
	def "put method #result the ItemModel when the provided key is #condition"() {
		when:
		cache.put(cacheKey, VALUE)

		then:
		times * cacheRegion.getWithLoader(cacheKey, _ as CacheValueLoader) >> { List args ->
			assertThat((args[1] as CacheValueLoader).load(args[0] as CacheKey)).isEqualTo(VALUE)
		}

		where:
		result           | condition  | cacheKey | times
		'caches'         | 'not null' | KEY      | 1
		'does not cache' | 'is null'  | null     | 0
	}

	@Test
	def "remove method returns #result when #condition"() {
		given:
		cacheRegion.invalidate(cacheKey, false) >> evictedValue

		expect:
		cache.remove(cacheKey) == result

		where:
		condition                              | cacheKey | evictedValue | result
		'there is a cached value for the key'  | KEY      | VALUE        | VALUE
		'there is no cached value for the key' | KEY      | null         | null
		'cached value is not a ItemModel'      | KEY      | new Object() | null
		'key is null'                          | null     | null         | null
	}

	@Test
	def "contains method returns #result when the key #condition"() {
		given:
		cacheRegion.containsKey(cacheKey) >> cacheRegionResult

		expect:
		cache.contains(cacheKey) == result

		where:
		condition        | cacheKey | cacheRegionResult | result
		"exists"         | KEY      | true              | true
		"does not exist" | KEY      | false             | false
		"is null"        | null     | true              | false
	}

	private static WebhookDeletedItemCacheKey webhookCacheKey() {
		WebhookTenantUtil.setTenant(new MockTenant(JUNIT_TENANT))
		WebhookDeletedItemCacheKey.from(PK.fromLong(1))
	}

}
