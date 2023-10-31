/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.service.ItemModelSearchService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.cache.WebhookCacheService
import org.junit.Test

@UnitTest
class DefaultWebhookSearchServiceUnitTest extends JUnitPlatformSpecification {

	private static final PK TEST_PK = PK.fromLong(1L)
	private static final ItemModel DB_ITEM = new ItemModel()
	private static final ItemModel CACHE_ITEM = new ItemModel()

	def itemModelSearchService = Stub(ItemModelSearchService)
	def webhookCacheService = Stub(WebhookCacheService)
	def webhookSearchService = new DefaultWebhookSearchService(webhookCacheService, itemModelSearchService)

	@Test
	def 'nonCachingFindByPk returns the found item in the database and does not query the cache'() {
		given:
		itemModelSearchService.nonCachingFindByPk(TEST_PK) >> Optional.of(DB_ITEM)
		webhookCacheService.findItem(TEST_PK) >> Optional.of(CACHE_ITEM)

		expect:
		webhookSearchService.nonCachingFindByPk(TEST_PK) == Optional.of(DB_ITEM)
	}

	@Test
	def 'nonCachingFindByPk returns the cached item when the item is not found in the database'() {
		given:
		itemModelSearchService.nonCachingFindByPk(TEST_PK) >> Optional.empty()
		webhookCacheService.findItem(TEST_PK) >> Optional.of(CACHE_ITEM)

		expect:
		webhookSearchService.nonCachingFindByPk(TEST_PK) == Optional.of(CACHE_ITEM)
	}

	@Test
	def 'nonCachingFindByPk returns empty optional when the item is not found in the database nor in the cache'() {
		given:
		itemModelSearchService.nonCachingFindByPk(TEST_PK) >> Optional.empty()
		webhookCacheService.findItem(TEST_PK) >> Optional.empty()

		expect:
		webhookSearchService.nonCachingFindByPk(TEST_PK) == Optional.empty()
	}

	@Test
	def 'nonCachingFindByPk returns empty optional when the PK argument is null'() {
		given:
		webhookCacheService.findItem(null) >> Optional.empty()
		itemModelSearchService.nonCachingFindByPk(null) >> Optional.empty()

		expect:
		webhookSearchService.nonCachingFindByPk(null) == Optional.empty()
	}

	@Test
	def "constructor fails when #param is null"() {
		when:
		new DefaultWebhookSearchService(cacheService, searchService)

		then:
		def e = thrown IllegalArgumentException
		e.message == "$param cannot be null"

		where:
		searchService                | cacheService              | param
		null                         | Stub(WebhookCacheService) | "ItemModelSearchService"
		Stub(ItemModelSearchService) | null                      | "WebhookCacheService"
	}

}
