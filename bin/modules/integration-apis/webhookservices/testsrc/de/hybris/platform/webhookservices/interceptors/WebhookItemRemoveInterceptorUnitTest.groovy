/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.interceptors

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.servicelayer.interceptor.InterceptorContext
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.interceptor.WebhookItemRemoveInterceptor
import org.junit.Test
import de.hybris.platform.webhookservices.cache.WebhookCacheService

@UnitTest
class WebhookItemRemoveInterceptorUnitTest extends JUnitPlatformSpecification {

	@Test
	def "instantiation fails because WebhookCacheService cannot be null"() {
		when:
		new WebhookItemRemoveInterceptor(null)

		then:
		def e = thrown IllegalArgumentException
		e.message == "WebhookCacheService cannot be null"
	}

	@Test
	def "cache the item when it is removed" () {
		given:
		def cacheService = Mock WebhookCacheService
		def interceptor = new WebhookItemRemoveInterceptor(cacheService)

		and:
		def deletedItem = new ItemModel()
		def ctx = Stub InterceptorContext

		when:
		interceptor.onRemove deletedItem, ctx

		then:
		1 * cacheService.cacheDeletedItem(deletedItem, ctx)
	}
}
