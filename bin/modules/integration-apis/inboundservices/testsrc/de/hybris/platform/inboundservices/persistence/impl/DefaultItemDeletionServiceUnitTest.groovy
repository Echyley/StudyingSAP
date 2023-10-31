/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.search.ItemNotFoundException
import de.hybris.platform.integrationservices.search.ItemSearchRequest
import de.hybris.platform.integrationservices.search.ItemSearchService
import de.hybris.platform.integrationservices.search.validation.ItemSearchRequestValidator
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

@UnitTest
class DefaultItemDeletionServiceUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM = new ItemModel()

    @Shared
    def request = Stub(ItemSearchRequest) {
        getRequestedItem() >> Optional.of(Stub(IntegrationItem))
    }

    def searchService = Stub(ItemSearchService) {
        findUniqueItem(request) >> Optional.of(ITEM)
    }
    def modelService = Mock ModelService
    def itemDeletionService = new DefaultItemDeletionService(modelService, searchService)

    @Test
    @Unroll
    def "cannot be instantiated when #param is null"() {
        when:
        new DefaultItemDeletionService(modelService, searchService)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains param

        where:
        param               | modelService       | searchService
        'ModelService'      | null               | Stub(ItemSearchService)
        'ItemSearchService' | Stub(ModelService) | null
    }

    @Test
    def 'deleteItem() removes the item when it is found'() {
        when:
        itemDeletionService.deleteItem request

        then:
        1 * modelService.remove(ITEM)
    }

    @Test
    def 'deleteItem() throws exception when the item is not found'() {
        given: 'item not found'
        def itemSearchService = Stub(ItemSearchService) {
            findUniqueItem(request) >> Optional.empty()
        }
        def deletionService = new DefaultItemDeletionService(modelService, itemSearchService)

        when:
        deletionService.deleteItem request

        then:
        0 * modelService._

        and:
        def e = thrown ItemNotFoundException
        e.getRequestedItem() == request.requestedItem.get()
    }

    @Test
    def 'deleteItem() throws exception when deleteItemValidator throws an exception'() {
        given: "an item search request validator throws a RuntimeException"
        def validator = Stub(ItemSearchRequestValidator) {
            validate(request) >> { throw new RuntimeException('IGNORE-Testing Exception') }
        }
        itemDeletionService.deleteItemValidators = [validator]

        when:
        itemDeletionService.deleteItem request

        then: 'the validator exception is rethrown by the deleteItem() method'
        thrown(RuntimeException)

        and: 'the item was not deleted'
        0 * modelService.remove(ITEM)
    }

    @Test
    def 'all validators are called in the order they are configured before item is deleted'() {
        given: "there are several validators"
        def validator1 = Mock ItemSearchRequestValidator
        def validator2 = Mock ItemSearchRequestValidator
        itemDeletionService.deleteItemValidators = [validator1, validator2]

        when:
        itemDeletionService.deleteItem request

        then: 'the validators are called in the order they are present in the itemDeletionService'
        1 * validator1.validate(request)
        then:
        1 * validator2.validate(request)
        then: 'item was deleted'
        1 * modelService.remove(ITEM)
    }

    @Test
    def 'validators references are not escaped'() {
        given: 'a validator is present'
        def validator = Mock ItemSearchRequestValidator
        def validators = [validator]
        itemDeletionService.deleteItemValidators = validators

        when: 'validators are manipulated outside the itemDeletionService'
        validators.clear()
        and: 'the item is deleted'
        itemDeletionService.deleteItem(request)

        then: 'validator is still called and not affected by clear call'
        1 * validator.validate(request)
    }
    
    @Test
    def 'validators can be null'() {
        given: 'a null validator'
        itemDeletionService.deleteItemValidators = null

        when:
        itemDeletionService.deleteItem(request)

        then:
        noExceptionThrown()
    }
}
