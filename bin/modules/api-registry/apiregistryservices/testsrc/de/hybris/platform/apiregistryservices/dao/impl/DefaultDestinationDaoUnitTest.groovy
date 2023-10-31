/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.dao.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class DefaultDestinationDaoUnitTest extends JUnitPlatformSpecification {

    def flexibleSearchService = Stub FlexibleSearchService
    def destinationDao = new DefaultDestinationDao(flexibleSearchService: flexibleSearchService)

    @Test
    def "the number of expected destinations found is #result"() {
        given:
        flexibleSearchService.search(_ as FlexibleSearchQuery) >> Stub(SearchResult) {
            getResult() >> searchResult
        }

        expect:
        result == destinationDao.findAllDestinationsByCredentialId("credentialId").size()

        where:
        result | searchResult
        2      | [Stub(AbstractDestinationModel), Stub(AbstractDestinationModel)]
        0      | []
    }
}
