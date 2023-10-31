/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.HybrisEnumValue
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Issue

@Issue('https://cxjira.sap.com/browse/IAPI-3466')

@UnitTest
class DefaultItemModelSearchServiceUnitTest extends JUnitPlatformSpecification {

    private static final PK TEST_PK = PK.fromLong(1L)

    def modelService = Stub ModelService
    def searchService = new DefaultItemModelSearchService(modelService)

    @Test
    def 'finder requires a ModelService when instantiating'() {
        when:
        new DefaultItemModelSearchService(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains 'ModelService cannot be null'
    }

    @Test
    def 'nonCachingFindByPk returns the item when found'() {
        given:
        def item = Stub ItemModel
        modelService.get(TEST_PK) >> item

        when:
        def foundItem = searchService.nonCachingFindByPk(TEST_PK)

        then:
        foundItem.present
        foundItem.get() == item
    }

    @Test
    def 'nonCachingFindByPk filters out non item when non-item is found'() {
        given:
        def item = Stub HybrisEnumValue
        modelService.get(TEST_PK) >> item

        when:
        def foundItem = searchService.nonCachingFindByPk(TEST_PK)

        then:
        foundItem.empty
    }

    @Test
    def 'nonCachingFindByPk returns empty Optional when the PK argument is null'() {
        expect:
        searchService.nonCachingFindByPk(null).empty
    }

    @Test
    def 'nonCachingFindByPk returns empty Optional when the item is not found'() {
        given:
        modelService.get(TEST_PK) >> { throw new ModelLoadingException("TEST IGNORE - Can't find item") }

        expect:
        searchService.nonCachingFindByPk(TEST_PK).empty
    }
}
