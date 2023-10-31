/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.ContextItemModelService
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyValueGenerator
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.search.ItemSearchRequest
import de.hybris.platform.integrationservices.search.ItemSearchService
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService
import de.hybris.platform.odata2services.converter.IntegrationObjectItemNotFoundException
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.StorageRequest
import de.hybris.platform.odata2services.odata.persistence.populator.EntityModelPopulator
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.junit.Test

@UnitTest
class DefaultModelEntityServiceUnitTest extends JUnitPlatformSpecification {
    private static final def INTERNAL_INTEGRATION_KEY = 'myKey'
    private static final String EXISTING_OBJECT_CODE = 'TestObject'
    private static final String EXISTING_ITEM_TYPE_CODE = 'TestItem'

    def entityModelPopulator = Mock EntityModelPopulator
    def itemSearchService = Stub ItemSearchService
    def integrationKeyValueGenerator = Stub(IntegrationKeyValueGenerator) {
        generate(_ as TypeDescriptor, _ as ODataEntry) >> INTERNAL_INTEGRATION_KEY
    }
    def itemType = Stub(TypeDescriptor)
    def typeDescriptorService = Stub(ItemTypeDescriptorService)
    def modelEntityService = new DefaultModelEntityService()

    def setup() {
        modelEntityService.entityModelPopulator = entityModelPopulator
        modelEntityService.searchService = itemSearchService
        modelEntityService.keyValueGenerator = integrationKeyValueGenerator
        modelEntityService.itemTypeDescriptorService = typeDescriptorService
    }

    @Test
    def 'delegates count lookup to the item search service'() {
        given:
        def expectedCount = 5
        itemSearchService.countItems(_ as ItemSearchRequest) >> expectedCount

        expect:
        modelEntityService.count(Stub(ItemLookupRequest)) == expectedCount
    }

    @Test
    def 'findOrCreateItem delegates to the ContextItemModelService when it is set'() {
        given:
        def itemModelService = Mock ContextItemModelService
        def request = storageRequest()
        modelEntityService.setContextItemModelService(itemModelService)

        when:
        modelEntityService.findOrCreateItem request

        then:
        1 * itemModelService.findOrCreateItem(request)
    }

    @Test
    def 'getODataEntry populates entity with integrationKey attribute'() {
        given:
        def request = Stub(ItemConversionRequest) {
            getIntegrationObjectCode() >> EXISTING_OBJECT_CODE
            getEntityType() >> Stub(EdmEntityType) {
                getName() >> EXISTING_ITEM_TYPE_CODE
            }
        }

        and:
        typeDescriptorService.getTypeDescriptor(EXISTING_OBJECT_CODE, EXISTING_ITEM_TYPE_CODE) >> Optional.of(itemType)

        when:
        def entry = modelEntityService.getODataEntry request

        then:
        1 * entityModelPopulator.populateEntity(_, request)
        entry.properties.get('integrationKey') == INTERNAL_INTEGRATION_KEY
    }

    @Test
    def "IntegrationObjectItemNotFoundException is thrown when typeDescriptor is not found"() {
        given:
        def request = Stub(ItemConversionRequest) {
            getIntegrationObjectCode() >> EXISTING_OBJECT_CODE
            getEntityType() >> Stub(EdmEntityType) {
                getName() >> EXISTING_ITEM_TYPE_CODE
            }
        }

        and:
        typeDescriptorService.getTypeDescriptor(EXISTING_OBJECT_CODE, EXISTING_ITEM_TYPE_CODE) >> Optional.empty()

        when:
        modelEntityService.getODataEntry request

        then:
        def e = thrown IntegrationObjectItemNotFoundException
        with(e) {
            integrationObjectCode == EXISTING_OBJECT_CODE
            integrationItemType == EXISTING_ITEM_TYPE_CODE
            message == "Integration object $EXISTING_OBJECT_CODE does not contain item type $EXISTING_ITEM_TYPE_CODE"
        }
    }

    private StorageRequest storageRequest(final ItemModel itemModel = null) {
        Stub(StorageRequest) {
            getContextItem() >> Optional.ofNullable(itemModel)
            isItemCanBeCreated() >> true
        }
    }
}
