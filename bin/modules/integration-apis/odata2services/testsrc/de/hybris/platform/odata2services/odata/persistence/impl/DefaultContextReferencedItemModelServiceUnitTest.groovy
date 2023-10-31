/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.CannotCreateReferencedItemException
import de.hybris.platform.inboundservices.persistence.ContextItemModelService
import de.hybris.platform.inboundservices.persistence.populator.ContextReferencedItemModelService
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.odata2services.odata.persistence.StorageRequest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class DefaultContextReferencedItemModelServiceUnitTest extends JUnitPlatformSpecification {
    private static final def CONTEXT_ITEM = new ItemModel()
    private static final def REFERENCED_ITEM = new ItemModel()
    @Shared
    private def REQUEST = Stub StorageRequest
    @Shared
    private def ATTRIBUTE = Stub TypeAttributeDescriptor

    def referencedItemModelService = Stub(ContextReferencedItemModelService) {
        deriveReferencedItemModel(ATTRIBUTE, REQUEST) >> REFERENCED_ITEM
        deriveItemsReferencedInAttributeValue(REQUEST, ATTRIBUTE) >> [REFERENCED_ITEM]
    }
    def contextItemModelService = Stub(ContextItemModelService) {
        findOrCreateItem(REQUEST) >> CONTEXT_ITEM
    }
    def modelEntityService = new DefaultModelEntityService(contextItemModelService: contextItemModelService)

    @Test
    def "deriveReferencedItemModel delegates to the referencedItemModelService when it is set"() {
        given: "the referencedItemModelService is set with the odata independent service"
        modelEntityService.setContextReferencedItemModelService(referencedItemModelService)

        when:
        def item = modelEntityService.deriveReferencedItemModel(ATTRIBUTE, REQUEST)

        then:
        item.is REFERENCED_ITEM
    }

    @Test
    def "deriveReferencedItemModel delegates to the contextItemModelService when referencedItemModelService is not set"() {
        when:
        def item = modelEntityService.deriveReferencedItemModel(ATTRIBUTE, REQUEST)

        then:
        item.is CONTEXT_ITEM
    }

    @Test
    def "deriveReferencedItemModel throws exception when item cannot be created and no existing item found"() {
        given: 'item was not created'
        modelEntityService.contextItemModelService = Stub(ContextItemModelService) {
            findOrCreateItem(REQUEST) >> null
        }

        when:
        modelEntityService.deriveReferencedItemModel(ATTRIBUTE, REQUEST)

        then:
        def e = thrown CannotCreateReferencedItemException
        e.attributeDescriptor == ATTRIBUTE
        e.persistenceContext == REQUEST
    }

    @Test
    def "deriveItemsReferencedInAttributeValue delegates to the referencedItemModelService when it is set"() {
        given: "the referencedItemModelService is set"
        modelEntityService.setContextReferencedItemModelService(referencedItemModelService)

        when:
        def items = modelEntityService.deriveItemsReferencedInAttributeValue(REQUEST, ATTRIBUTE)

        then:
        items == [REFERENCED_ITEM]
    }

    @Test
    def 'deriveItemsReferencedInAttributeValue returns empty collection when the attribute value in the payload is empty'() {
        given: 'the persistence context has empty attribute value'
        def context = Stub(StorageRequest) {
            getReferencedContexts(ATTRIBUTE) >> []
        }

        expect:
        modelEntityService.deriveItemsReferencedInAttributeValue(context, ATTRIBUTE).empty
    }

    @Test
    def "deriveItemsReferencedInAttributeValue throws exception when no existing item can be found nor created"() {
        given: 'item was not found nor create'
        modelEntityService.contextItemModelService = Stub(ContextItemModelService) {
            findOrCreateItem(REQUEST) >> null
        }
        and: 'request contains a collection referring to an item that does not exist in the system'
        def context = Stub(StorageRequest) {
            getReferencedContexts(ATTRIBUTE) >> [REQUEST]
        }

        when:
        modelEntityService.deriveItemsReferencedInAttributeValue context, ATTRIBUTE

        then:
        def e = thrown CannotCreateReferencedItemException
        e.attributeDescriptor == ATTRIBUTE
        e.persistenceContext == REQUEST
    }

    @Test
    def "deriveItemsReferencedInAttributeValue returns items found by contextItemModelService when referencedItemModelService is not set"() {
        given: 'request contains a collection referring to an item that does not exist in the system'
        def context = Stub(StorageRequest) {
            getReferencedContexts(ATTRIBUTE) >> [REQUEST]
        }

        when:
        def items = modelEntityService.deriveItemsReferencedInAttributeValue context, ATTRIBUTE

        then:
        items == [CONTEXT_ITEM]
    }
}
