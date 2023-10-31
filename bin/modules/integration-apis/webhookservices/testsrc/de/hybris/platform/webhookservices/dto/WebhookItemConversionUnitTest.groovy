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
class WebhookItemConversionUnitTest extends JUnitPlatformSpecification {

	private static final def DATA_ONE = [key1: 'value1', key2: 'value2']
	private static final def DATA_ONE_IN_DIFFERENT_ORDER = [key2: 'value2', key1: 'value1']
	private static final def DATA_TWO = [key1: 'value1']
	private static final def PK_ONE = PK.fromLong(1L)
	private static final def PK_TWO = PK.fromLong(2L)

	@Test
	def "instantiation fails because #param cannot be null"() {
		when:
		new WebhookItemConversion(webhookConfigPk, webhookPayload)

		then:
		def e = thrown IllegalArgumentException
		e.message == "$param cannot be null"

		where:
		webhookConfigPk | webhookPayload            | param
		null            | Stub(WebhookPayloadModel) | 'PK'
		PK_ONE          | null                      | 'WebhookPayloadModel'
	}

	@Test
	def "two WebhookItemConversion instances are equal when PK=#pkOne and #pkTwo, and data=#payloadOne.data and #payloadTwo.data"() {
		given:
		def firstPayload = new WebhookItemConversion(pkOne, payloadOne)
		def secondPayload = new WebhookItemConversion(pkTwo, payloadTwo)

		expect:
		firstPayload == secondPayload
		firstPayload.hashCode() == secondPayload.hashCode()

		where:
		pkOne  | pkTwo  | payloadOne               | payloadTwo
		PK_ONE | PK_ONE | webhookPayload(DATA_ONE) | webhookPayload(DATA_ONE)
		PK_ONE | PK_ONE | webhookPayload(DATA_ONE) | webhookPayload(DATA_ONE_IN_DIFFERENT_ORDER)
	}

	@Test
	def "two WebhookItemConversion instances are not equal when PK=#pkOne and #pkTwo, and data=#payloadOne.data and #payloadTwo.data"() {
		given:
		def firstPayload = new WebhookItemConversion(pkOne, payloadOne)
		def secondPayload = new WebhookItemConversion(pkTwo, payloadTwo)

		expect:
		firstPayload != secondPayload
		firstPayload.hashCode() != secondPayload.hashCode()

		where:
		pkOne  | pkTwo  | payloadOne               | payloadTwo
		PK_ONE | PK_ONE | webhookPayload(DATA_ONE) | webhookPayload(DATA_TWO)
		PK_ONE | PK_TWO | webhookPayload(DATA_TWO) | webhookPayload(DATA_TWO)
	}

	private WebhookPayloadModel webhookPayload(final Map<String, Object> data) {
		def payload = new WebhookPayloadModel()
		payload.setData(data)
		return payload
	}

}
