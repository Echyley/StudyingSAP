/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.servicelayer.tenant.MockTenant
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class WebhookDeletedItemCacheKeyUnitTest extends JUnitPlatformSpecification {

	private static final def JUNIT_TENANT = "junit"
	private static final def PK1 = PK.fromLong(1234)
	private static final def PK2 = PK.fromLong(4321)

	@Shared
	def cacheKey1 = webhookDeletedItemCacheKey(PK1)

	@Test
	def "hashcode method is #result when second cache key is #description"() {
		expect:
		(cacheKey1.hashCode() == otherCacheKey.hashCode()) == result

		where:
		description                          | otherCacheKey                   | result
		'same object'                        | cacheKey1                       | true
		'different object with same pk'      | webhookDeletedItemCacheKey(PK1) | true
		'different object with different pk' | webhookDeletedItemCacheKey(PK2) | false
		'different object'                   | new Integer(1)                  | false
	}

	@Test
	def "equals method is #result when compared to #description"() {
		expect:
		(cacheKey1 == otherCacheKey) == result

		where:
		description                          | otherCacheKey                   | result
		'same object'                        | cacheKey1                       | true
		'different object with same pk'      | webhookDeletedItemCacheKey(PK1) | true
		'different object with different pk' | webhookDeletedItemCacheKey(PK2) | false
		'different object'                   | new Integer(1)                  | false
		'null'                               | null                            | false
	}

	@Test
	def "IllegalArgumentException when instantiated with null pk"() {
		when:
		webhookDeletedItemCacheKey(null)

		then:
		thrown IllegalArgumentException
	}

	@Test
	def "getTypeCode method returns __DELETED_ITEMS_CACHE__"() {
		expect:
		cacheKey1.getTypeCode() == "__DELETED_ITEMS_CACHE__"
	}

	@Test
	def "getTenantId method returns ID of the current tenant in the execution context for which the key is created"() {
		expect:
		cacheKey1.getTenantId() == JUNIT_TENANT
	}

	@Test
	def "getId method returns pk"() {
		expect:
		cacheKey1.getId() == PK1
	}

	private static WebhookDeletedItemCacheKey webhookDeletedItemCacheKey(PK pk) {
		WebhookTenantUtil.setTenant(new MockTenant(JUNIT_TENANT))
		return WebhookDeletedItemCacheKey.from(pk)
	}

}
