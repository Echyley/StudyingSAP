/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.ItemPersistenceService
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.search.ItemNotFoundException
import de.hybris.platform.integrationservices.search.ItemSearchResult
import de.hybris.platform.integrationservices.search.ItemSearchService
import de.hybris.platform.integrationservices.search.validation.ItemSearchRequestValidator
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.odata2services.odata.persistence.ConversionOptions
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService
import de.hybris.platform.odata2services.odata.persistence.StorageRequest
import de.hybris.platform.odata2services.odata.processor.RetrievalErrorRuntimeException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.junit.Rule
import org.junit.Test

@UnitTest
class DefaultPersistenceServiceUnitTest extends JUnitPlatformSpecification {
    private static final def INTEGRATION_KEY = 'integrationKey|Value'
    private static final def ENTITY_TYPE = 'SomeType'
    private static final def ITEM = new ItemModel()

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    def modelService = Mock ModelService
    def itemPersistenceService = Mock ItemPersistenceService
    def modelEntityService = Stub ModelEntityService
    def itemSearchService = Stub ItemSearchService
    private ItemSearchRequestValidator deleteItemPermissionValidator = Mock(ItemSearchRequestValidator)
    def persistenceService = new DefaultPersistenceService(
            modelService: modelService,
            modelEntityService: modelEntityService,
            persistenceService: itemPersistenceService,
            itemSearchService: itemSearchService,
            deleteItemValidators: [deleteItemPermissionValidator]
    )

    @Test
    def 'createEntityData invokes persist on the default persistence service when persistence service is not injected'() {
        given: 'persistence service is not injected'
        persistenceService.persistenceService = null

        and: 'descriptor factory is present in the application context'
        def contextItemPersistenceService = Mock ItemPersistenceService
        applicationContext.addBean 'inboundServicesItemPersistenceService', contextItemPersistenceService

        when:
        persistenceService.createEntityData(Stub(StorageRequest))

        then:
        1 * contextItemPersistenceService.persist(_)
    }

    @Test
    def 'setDeleteItemValidators ignores null values'() {
        when:
        def service = new DefaultPersistenceService(deleteItemValidators: null)

        then:
        service.getDeleteItemValidators().isEmpty()
    }

    @Test
    def 'createEntityData invokes persist on the persistence service'() {
        given:
        def request = Stub StorageRequest

        when:
        persistenceService.createEntityData(request)

        then:
        1 * itemPersistenceService.persist(request)
    }

    @Test
    def 'getEntityData() returns entry for the found item'() {
        given: 'item is found'
        def oDataEntry = Stub ODataEntry
        def request = lookupRequest()
        itemSearchService.findUniqueItem(request) >> Optional.of(ITEM)

        and: 'the found item converts into the ODataEntry'
        def conversionRequest = Stub ItemConversionRequest
        def options = Stub ConversionOptions
        request.toConversionRequest(ITEM, options) >> conversionRequest
        modelEntityService.getODataEntry(conversionRequest) >> oDataEntry

        when:
        def entityData = persistenceService.getEntityData request, options

        then:
        entityData.is oDataEntry
    }

    @Test
    def 'getEntityData() throws exception if item is not found'() {
        given: 'item not found'
        def lookupRequest = lookupRequest()
        itemSearchService.findUniqueItem(lookupRequest) >> Optional.empty()

        when:
        persistenceService.getEntityData lookupRequest, Stub(ConversionOptions)

        then:
        def e = thrown ItemNotFoundException
        e.message.contains ENTITY_TYPE
        e.message.contains INTEGRATION_KEY
    }

    @Test
    def 'deleteItem() removes the item when it is found'() {
        given: 'the item is found'
        def request = lookupRequest()
        itemSearchService.findUniqueItem(request) >> Optional.of(ITEM)

        when:
        persistenceService.deleteItem request

        then:
        1 * modelService.remove(ITEM)
        1 * deleteItemPermissionValidator.validate(request)
    }

    @Test
    def 'deleteItem() throws exception when the item is not found'() {
        given: 'item not found'
        def lookupRequest = lookupRequest()
        itemSearchService.findUniqueItem(lookupRequest) >> Optional.empty()

        when:
        persistenceService.deleteItem lookupRequest

        then:
        0 * modelService._
        def e = thrown ItemNotFoundException
        e.message.contains ENTITY_TYPE
        e.message.contains INTEGRATION_KEY
    }

    @Test
    def 'deleteItem() throws exception when deleteItemValidator throws an exception'() {
        given: 'the item is found'
        def request = lookupRequest()
        itemSearchService.findUniqueItem(request) >> Optional.of(ITEM)
        and: "deleteItemPermissionValidator throws a RuntimeException"
        def e = new RuntimeException()
        deleteItemPermissionValidator.validate(request) >> { throw e }

        when:
        persistenceService.deleteItem request

        then:
        def actualE = thrown(RuntimeException)
        actualE.is e
        0 * modelService.remove(ITEM)
    }

    @Test
    def 'getEntities() returns entries for the found items'() {
        def totalCount = 20
        given: 'items found'
        def lookupRequest = lookupRequest()
        itemSearchService.findItems(lookupRequest) >> searchResult(totalCount, [Stub(ItemModel), Stub(ItemModel)])

        when:
        def result = persistenceService.getEntities lookupRequest, Stub(ConversionOptions)

        then:
        result.entries.size() == 2
        result.totalCount == totalCount
    }

    @Test
    def 'getEntities() returns empty result when items not found'() {
        given: 'items not found'
        def lookupRequest = lookupRequest()
        itemSearchService.findItems(lookupRequest) >> notFoundResult()

        when:
        def result = persistenceService.getEntities lookupRequest, Stub(ConversionOptions)

        then:
        result.entries.empty
    }

    @Test
    def 'getEntities() handles EdmException'() {
        def itemType = 'TestItem'

        given: 'items found'
        def lookupRequest = lookupRequest()
        itemSearchService.findItems(lookupRequest) >> searchResult([item(itemType), item(itemType)])
        and: 'item conversion fails'
        modelEntityService.getODataEntry(_ as ItemConversionRequest) >> { throw Stub(EdmException) }

        when:
        persistenceService.getEntities lookupRequest, Stub(ConversionOptions)

        then:
        def e = thrown RetrievalErrorRuntimeException
        e.code == 'runtime_error'
        e.entityType == itemType
        e.message.contains itemType
    }

    private ItemLookupRequest lookupRequest() {
        def itemType = Stub(TypeDescriptor) {
            getItemCode() >> ENTITY_TYPE
        }
        Stub(ItemLookupRequest) {
            getTypeDescriptor() >> itemType
            getAcceptLocale() >> Locale.ENGLISH
            getIntegrationItem() >> Stub(IntegrationItem) {
                getIntegrationKey() >> INTEGRATION_KEY
                getItemType() >> itemType
            }
        }
    }

    private ItemModel item(String type = ENTITY_TYPE) {
        Stub(ItemModel) {
            getItemtype() >> type
        }
    }

    private static ItemSearchResult<ItemModel> notFoundResult() {
        searchResult(0)
    }

    private static ItemSearchResult<ItemModel> searchResult(List<ItemModel> items = []) {
        searchResult(items.size(), items)
    }

    private static ItemSearchResult<ItemModel> searchResult(int count, List<ItemModel> items = []) {
        ItemSearchResult.createWith(items, count)
    }
}
