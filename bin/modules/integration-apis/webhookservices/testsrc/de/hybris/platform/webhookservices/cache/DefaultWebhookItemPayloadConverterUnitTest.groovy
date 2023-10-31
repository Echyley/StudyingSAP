/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.cache

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import de.hybris.platform.webhookservices.dto.WebhookItemConversion
import de.hybris.platform.webhookservices.event.ItemDeletedEvent
import de.hybris.platform.webhookservices.filter.WebhookFilterService
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel
import de.hybris.platform.webhookservices.model.WebhookPayloadModel
import de.hybris.platform.webhookservices.service.WebhookConfigurationService
import org.junit.Test
import spock.lang.Shared

@UnitTest
class DefaultWebhookItemPayloadConverterUnitTest extends JUnitPlatformSpecification {

	private static final def IO_CODE_1 = "IO"
	private static final def IO_CODE_2 = "IO2"
	private static final def IO_CODE_3 = "IO3"
	private static final def ITEM_PK = PK.fromLong(1L)
	private static final def CONFIG_PK_1 = PK.fromLong(2L)
	private static final def CONFIG_PK_2 = PK.fromLong(3L)
	private static final def CONFIG_PK_3 = PK.fromLong(4L)

	private static final def FILTER_PATH = "model://PATH"
	private static final def FILTER_PATH_2 = "model://changeItemValue"
	private static final def PAYLOAD_1 = [key1: 'value1', key12: 'value12']
	private static final def PAYLOAD_2 = [key2: 'value2']
	private static final def PAYLOAD_3 = [key1: 'value3']

	@Shared
	def item = Stub(ItemModel) { getPk() >> ITEM_PK }
	@Shared
	def filteredItem = Stub(ItemModel) { getPk() >> ITEM_PK }
	@Shared
	def config1 = webhookConfigurationModel(IO_CODE_1, CONFIG_PK_1, FILTER_PATH)
	@Shared
	def config2 = webhookConfigurationModel(IO_CODE_2, CONFIG_PK_2, FILTER_PATH)
	@Shared
	def config3 = webhookConfigurationModel(IO_CODE_3, CONFIG_PK_3, FILTER_PATH_2)

	def webhookConfigurationService = Stub WebhookConfigurationService
	def webhookFilterService = Stub WebhookFilterService
	def itemPayloadConverter = new DefaultWebhookItemPayloadConverter(
			webhookConfigurationService,
			stubIntegrationObjectConversionService(),
			webhookFilterService)

	@Test
	def "verify the deleted item is converted to #expectResult"() {
		given:
		webhookConfigurationService.getWebhookConfigurationsByEventAndItemModel(_ as ItemDeletedEvent, item) >> configuration
		and:
		webhookFilterService.filter(item, _ as String) >> filteredItem

		when:
		def converterResults = itemPayloadConverter.convert(item)

		then:
		expectResult as Set == converterResults

		where:
		configuration | filteredItem              | expectResult
		[config1]     | Optional.of(item)         | [itemPayload(CONFIG_PK_1, PAYLOAD_1)]
		[config3]     | Optional.of(filteredItem) | [itemPayload(CONFIG_PK_3, PAYLOAD_3)]
		[config1]     | Optional.empty()          | []
		[]            | []                        | []
	}

	@Test
	def "convert deleted item to multiple webhook item payloads when multiple configurations found"() {
		given:
		webhookConfigurationService.getWebhookConfigurationsByEventAndItemModel(_ as ItemDeletedEvent, item) >> [config1, config2]
		and:
		webhookFilterService.filter(item, _ as String) >> Optional.of(item)
		def expectResult =
				[itemPayload(CONFIG_PK_1, PAYLOAD_1),
				 itemPayload(CONFIG_PK_2, PAYLOAD_2)] as Set

		expect:
		expectResult == itemPayloadConverter.convert(item)
	}

	@Test
	def "instantiation fails because #param cannot be null"() {
		when:
		new DefaultWebhookItemPayloadConverter(webhookConfigService, conversion, filterService)

		then:
		def e = thrown IllegalArgumentException
		e.message == "$param cannot be null"

		where:
		webhookConfigService              | conversion                               | filterService              | param
		null                              | Stub(IntegrationObjectConversionService) | Stub(WebhookFilterService) | "WebhookConfigurationService"
		Stub(WebhookConfigurationService) | null                                     | Stub(WebhookFilterService) | "IntegrationObjectConversionService"
		Stub(WebhookConfigurationService) | Stub(IntegrationObjectConversionService) | null                       | "WebhookFilterService"
	}

	private IntegrationObjectConversionService stubIntegrationObjectConversionService() {
		Stub(IntegrationObjectConversionService) {
			convert(item, config1.getIntegrationObject().getCode()) >> PAYLOAD_1
			convert(item, config2.getIntegrationObject().getCode()) >> PAYLOAD_2
			convert(filteredItem, config3.getIntegrationObject().getCode()) >> PAYLOAD_3
		}
	}

	private WebhookConfigurationModel webhookConfigurationModel(String ioCode, PK pk, String filterPath) {
		Stub(WebhookConfigurationModel) {
			getIntegrationObject() >> Stub(IntegrationObjectModel) {
				getCode() >> ioCode
			}
			getPk() >> pk
			getFilterLocation() >> filterPath
		}
	}

	private WebhookItemConversion itemPayload(PK pk, Map<String, String> data) {
		return new WebhookItemConversion(pk, new WebhookPayloadModel(data: data))
	}

}
