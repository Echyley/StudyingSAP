/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.kymaintegrationservices.strategies.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel
import de.hybris.platform.apiregistryservices.services.DestinationService
import de.hybris.platform.apiregistryservices.services.DestinationTargetService
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import org.junit.Test
import spock.lang.Issue
import javax.annotation.Resource

@IntegrationTest
@Issue('https://sapjira.wdf.sap.corp/browse/CXEC-5276')
class KymaDestinationTargetCloningStrategyIntegrationTest extends ServicelayerSpockSpecification {

	private static final String TEMPLATE_DESTINATION_TARGET_ID = "template_kyma"
	private static final String TEST_DESTINATION_TARGET_ID = "testDestinationTarget"
	private static final String TEST_DESTINATION_ID = "template_consumed_destination"
	private static final int EXPECT_CONSUMED_DESTINATION_COUNT = 1

	@Resource
	KymaDestinationTargetCloningStrategy kymaDestinationTargetCloningStrategy

	@Resource
	FlexibleSearchService flexibleSearchService

	@Resource
	DestinationService<AbstractDestinationModel> destinationService

	@Resource
	private DestinationTargetService destinationTargetService

	@Resource
	EventConfigurationDao eventConfigurationDao

	@Test
	def 'a cloned consumed destination cannot be assigned an Exposed OAuth Credential when cloning a Destination Target' () {
		given:'a destination target with an assigned consumed destination'
		importCsv("/test/kymaDestinationTargetCloningStrategyIntegrationTest.impex", "UTF-8")

		and: "get test destination target"
		def templateDestinationTarget = destinationTargetService.getDestinationTargetById(TEMPLATE_DESTINATION_TARGET_ID)

		and:
		def testDestinationTarget = kymaDestinationTargetCloningStrategy
				.createDestinationTarget(templateDestinationTarget, TEST_DESTINATION_TARGET_ID)

		when:
		kymaDestinationTargetCloningStrategy
				.createDestinations(templateDestinationTarget, testDestinationTarget, Collections.emptyList())
		then:
		noExceptionThrown()

		when:
		def clonedDestinations =  destinationService.getDestinationsByDestinationTargetId(testDestinationTarget.getId())

		then:
		EXPECT_CONSUMED_DESTINATION_COUNT == clonedDestinations.size()

		and:
		clonedDestinations.get(0).getId() == TEST_DESTINATION_ID
	}
}
