/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.config.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Specification

@UnitTest
class StreamContainerIdentifierGeneratorUnitTest extends JUnitPlatformSpecification {

	def identifierGenerator = new StreamContainerIdentifierGenerator()

	@Test
	def "generate"() {
		given:
		def channelCode = "testChannelCode"
		def channel = Stub(OutboundChannelConfigurationModel)
				{
					getCode() >> channelCode
				}

		expect:
		identifierGenerator.generate(channel) == channelCode + "Container"
	}
}
