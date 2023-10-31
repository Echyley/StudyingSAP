/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.dto

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.model.WebhookPayloadModel
import org.junit.Test

@UnitTest
class WebhookItemPayloadUnitTest extends JUnitPlatformSpecification {

	private static final def DATA_ONE = [key1: 'value1', key2: 'value2']
	private static final def DATA_ONE_IN_DIFFERENT_ORDER = [key2: 'value2', key1: 'value1']
	private static final def DATA_TWO = [key1: 'value1']
	private static final def PK_ONE = PK.fromLong(1L)
	private static final def PK_TWO = PK.fromLong(2L)


	@Test
	def "instantiation fails because itemConversions cannot be null"() {
		when:
		new WebhookItemPayload(null)

		then:
		def e = thrown IllegalArgumentException
		e.message == "Set<WebhookItemConversion> cannot be null"

	}

	@Test
	def "two WebhookItemPayload instances are equal when conversions are equal, webhookConfigurationPk #pkOne and #pkTwo and webhookPayload.data #dataOne and #dataTwo"() {
		expect:
		firstPayload == secondPayload
		firstPayload.hashCode() == secondPayload.hashCode()

		where:
		pkOne  | pkTwo  | dataOne  | dataTwo                     | firstPayload                       | secondPayload
		PK_ONE | PK_ONE | DATA_ONE | DATA_ONE                    | webhookItemPayload(pkOne, dataOne) | webhookItemPayload(pkTwo, dataTwo)
		PK_ONE | PK_ONE | DATA_ONE | DATA_ONE_IN_DIFFERENT_ORDER | webhookItemPayload(pkOne, dataOne) | webhookItemPayload(pkTwo, dataTwo)
	}

	@Test
	def "two WebhookItemPayload instances are not equal when conversions are not equal, webhookConfigurationPk #pkOne and #pkTwo and webhookPayload.data #dataOne and #dataTwo"() {
		expect:
		firstPayload != secondPayload
		firstPayload.hashCode() != secondPayload.hashCode()

		where:
		pkOne  | pkTwo  | dataOne  | dataTwo  | firstPayload                       | secondPayload
		PK_ONE | PK_TWO | DATA_ONE | DATA_ONE | webhookItemPayload(pkOne, dataOne) | webhookItemPayload(pkTwo, dataTwo)
		PK_ONE | PK_ONE | DATA_ONE | DATA_TWO | webhookItemPayload(pkOne, dataOne) | webhookItemPayload(pkTwo, dataTwo)
	}

	private WebhookItemPayload webhookItemPayload(final PK pk, final Map<String, Object> data) {
		def payload = new WebhookPayloadModel()
		payload.setData(data)
		def conversion = new WebhookItemConversion(pk, payload)
		new WebhookItemPayload([conversion] as Set<WebhookItemConversion>)
	}

}
