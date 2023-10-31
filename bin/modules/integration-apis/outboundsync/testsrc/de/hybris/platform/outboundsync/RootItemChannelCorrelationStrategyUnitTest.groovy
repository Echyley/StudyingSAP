/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.outboundsync.dto.OutboundItemChange
import de.hybris.platform.outboundsync.dto.OutboundItemDTOBuilder
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class RootItemChannelCorrelationStrategyUnitTest extends JUnitPlatformSpecification {

	def correlationStrategy = new RootItemChannelCorrelationStrategy()

	@Test
	@Unroll
	def "creates a correlation key using the root item and channel configuration PKs when #condition"() {
		given:
		def dto = OutboundItemDTOBuilder.outboundItemDTO()
					.withRootItemPK(rootItemPk)
					.withChannelConfigurationPK(channelPk)
					.withItem(Stub(OutboundItemChange))
					.withIntegrationObjectPK(0)
					.withCronJobPK(PK.fromLong(1))
					.build()

		expect:
		correlationKey == correlationStrategy.correlationKey(dto)

		where:
		condition 				| rootItemPk 	| channelPk | correlationKey
		"root item PK is null"	| null			| 456		| 'null-456'
		'both PKs are provided'	| 123			| 456		| '123-456'
	}

	@Test
	def "Exception is thrown when dto is null"() {
		when:
		correlationStrategy.correlationKey(null)

		then:
		thrown IllegalArgumentException
	}
}
