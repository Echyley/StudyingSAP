/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.service

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.model.ModelService
import org.junit.Test
import spock.lang.Issue

import javax.annotation.Resource

@Issue('https://cxjira.sap.com/browse/IAPI-3466')
@IntegrationTest
class ItemModelSearchServiceIntegrationTest extends ServicelayerSpockSpecification {

    @Resource
    ModelService modelService
    @Resource
    ItemModelSearchService itemModelSearchService

    @Test
    def 'nonCachingFindByPk returns found item'() {
        given:
        UnitModel item = modelService.create UnitModel
        item.setCode("${this.class.simpleName}Unit")
        item.setUnitType("${this.class.simpleName}UnitType")
        modelService.save(item)

        when:
        def foundItem = itemModelSearchService.nonCachingFindByPk item.getPk()

        then:
        foundItem.present
        foundItem.get() == item

        cleanup:
        modelService.remove item
    }

    @Test
    def 'nonCachingFindByPk returns empty Optional when item is not found'() {
        expect:
        def ioTypeCode = 8400
        itemModelSearchService.nonCachingFindByPk(PK.createCounterPK(ioTypeCode)).empty
    }
}
