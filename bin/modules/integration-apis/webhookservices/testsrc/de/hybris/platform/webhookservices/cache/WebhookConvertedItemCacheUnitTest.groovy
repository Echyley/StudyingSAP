/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.cache

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.regioncache.CacheValueLoader
import de.hybris.platform.regioncache.key.CacheKey
import de.hybris.platform.regioncache.region.CacheRegion
import de.hybris.platform.servicelayer.tenant.MockTenant
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.dto.WebhookItemPayload
import org.junit.Test
import spock.lang.Shared

@UnitTest
class WebhookConvertedItemCacheUnitTest extends JUnitPlatformSpecification {

	private static final def JUNIT_TENANT = "junit"

	@Shared
	def key = webhookCacheKey()
	@Shared
	def webhookItemPayloads = Stub(WebhookItemPayload)

	def cacheRegion = Mock(CacheRegion)
	def cache = new WebhookConvertedItemCache(cacheRegion: cacheRegion)

	@Test
	def 'get method returns a value when the value is present in the cache region'() {
		given:
		def value = Stub(WebhookItemPayload)
		cacheRegion.get(key) >> value

		expect:
		cache.get(key) == value
	}

	@Test
	def "get method returns null when #condition"() {
		given:
		cacheRegion.get(_ as CacheKey) >> cachedValue

		expect:
		cache.get(cacheKey) == null

		where:
		condition                                  | cacheKey | cachedValue
		"there is no cached value for the key"     | key      | null
		"cached value is not a WebhookItemPayload" | key      | new Object()
		"key is null"                              | null     | null
	}

	@Test
	def "put method #result the WebhookItemPayloads when the provided key is #condition"() {
		when:
		cache.put(cacheKey, webhookItemPayloads)

		then:
		times * cacheRegion.getWithLoader(cacheKey, _ as CacheValueLoader)

		where:
		result           | condition  | cacheKey | times
		'caches'         | 'not null' | key      | 1
		'does not cache' | 'is null'  | null     | 0
	}

	@Test
	def "remove method returns #result when #condition"() {
		given:
		cacheRegion.invalidate(cacheKey, false) >> evictedValue

		expect:
		cache.remove(cacheKey) == result

		where:
		condition                                  | cacheKey | evictedValue        | result
		'there is a cached value for the key'      | key      | webhookItemPayloads | webhookItemPayloads
		'there is no cached value for the key'     | key      | null                | null
		'cached value is not a WebhookItemPayload' | key      | new Object()        | null
		'key is null'                              | null     | null                | null
	}

	@Test
	def "contains method returns #result when the key #condition"() {
		given:
		cacheRegion.containsKey(cacheKey) >> cacheRegionResult

		expect:
		cache.contains(cacheKey) == result

		where:
		condition        | cacheKey | cacheRegionResult | result
		"exists"         | key      | true              | true
		"does not exist" | key      | false             | false
		"is null"        | null     | true              | false
	}

	private static WebhookDeletedItemCacheKey webhookCacheKey() {
		WebhookTenantUtil.setTenant(new MockTenant(JUNIT_TENANT))
		WebhookDeletedItemCacheKey.from(PK.fromLong(1))
	}

}
