/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.populator.ContextReferencedItemModelService
import de.hybris.platform.inboundservices.persistence.populator.UnmodifiableAttributeException
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Rule
import org.junit.Test
import spock.lang.Shared


@UnitTest
class ItemModelAttributePopulatorUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM = new ItemModel()
    @Shared
    def REFERENCED_CONTEXT = Stub PersistenceContext

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    def attributeValueAccessor = Mock AttributeValueAccessor
    def itemModelService = Stub ContextReferencedItemModelService
    def populator = new ItemModelAttributePopulator(contextReferencedItemModelService: itemModelService)

    @Test
    def 'uses context item model service when it was not explicitly injected'() {
        given:
        def contextService = Stub ContextReferencedItemModelService
        applicationContext.addBean 'contextReferenceItemModelService', contextService
        and:
        populator = new ItemModelAttributePopulator()

        expect:
        populator.contextReferencedItemModelService.is contextService
    }

    @Test
    def 'attribute is not populated when attribute is primitive'() {
        given:
        def attribute = primitiveAttribute()
        and:
        referencedItemFoundFor(attribute)

        when:
        populator.populate ITEM, persistenceContext(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'attribute is not populated when attribute is a map'() {
        given:
        def attribute = mapAttribute()
        and:
        referencedItemFoundFor(attribute)

        when:
        populator.populate ITEM, persistenceContext(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'attribute is not populated when attribute is a collection'() {
        given:
        def attribute = collectionAttribute()
        and:
        referencedItemFoundFor(attribute)

        when:
        populator.populate ITEM, persistenceContext(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'attribute is not populated when attribute is an enum'() {
        given:
        def attribute = enumAttribute()
        and:
        referencedItemFoundFor(attribute)

        when:
        populator.populate ITEM, persistenceContext(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'attribute is not populated when the attribute is read-only'() {
        given:
        def attribute = readOnlyComposedTypeAttribute()
        and:
        referencedItemFoundFor(attribute)

        when:
        populator.populate ITEM, persistenceContext(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'populate throws exception when setting an attribute'() {
        given:
        def attribute = composedTypeAttribute()
        def context = persistenceContext(attribute)
        and: 'attribute reference resolves to attributeValue'
        def attributeValue = Stub ItemModel
        referencedItemFoundFor(attribute, attributeValue)
        and: 'setting the attribute throws an exception'
        attributeValueAccessor.setValue(ITEM, attributeValue) >> {
            throw new AttributeNotSupportedException('IGNORE - testing exception', 'attribute name - does not matter')
        }

        when:
        populator.populate ITEM, context

        then:
        def e = thrown UnmodifiableAttributeException
        e.attributeDescriptor == attribute
        e.persistenceContext == context
    }

    @Test
    def 'populates all applicable attributes in an item'() {
        given: 'multiple applicable attributes'
        def attributeOne = composedTypeAttribute()
        def attributeTwo = composedTypeAttribute()
        and: 'values for the attributes exist'
        def attributeOneValue = Stub ItemModel
        def attributeTwoValue = Stub ItemModel
        referencedItemFoundFor(attributeOne, attributeOneValue)
        referencedItemFoundFor(attributeTwo, attributeTwoValue)

        when:
        populator.populate ITEM, persistenceContext([attributeOne, attributeTwo])

        then:
        1 * attributeValueAccessor.setValue(ITEM, attributeOneValue)
        1 * attributeValueAccessor.setValue(ITEM, attributeTwoValue)
    }

    private TypeAttributeDescriptor mapAttribute() {
        Stub(TypeAttributeDescriptor) {
            isMap() >> true
            isSettable(_) >> true
        }
    }

    private TypeAttributeDescriptor primitiveAttribute() {
        Stub(TypeAttributeDescriptor) {
            isPrimitive() >> true
            isSettable(_) >> true
            accessor() >> attributeValueAccessor
        }
    }

    private TypeAttributeDescriptor collectionAttribute() {
        Stub(TypeAttributeDescriptor) {
            isCollection() >> true
            isSettable(_) >> true
            accessor() >> attributeValueAccessor
        }
    }

    private TypeAttributeDescriptor enumAttribute() {
        Stub(TypeAttributeDescriptor) {
            getAttributeType() >> Stub(TypeDescriptor) {
                isEnumeration() >> true
            }
            isSettable(_) >> true
            accessor() >> attributeValueAccessor
        }
    }

    private TypeAttributeDescriptor readOnlyComposedTypeAttribute() {
        Stub(TypeAttributeDescriptor) {
            accessor() >> attributeValueAccessor
            isSettable(_) >> false
        }
    }

    private TypeAttributeDescriptor composedTypeAttribute() {
        Stub(TypeAttributeDescriptor) {
            accessor() >> attributeValueAccessor
            isSettable(_) >> true
        }
    }

    private PersistenceContext persistenceContext(TypeAttributeDescriptor attribute) {
        persistenceContext([attribute])
    }

    private PersistenceContext persistenceContext(List<TypeAttributeDescriptor> attributes) {
        def ctx = Stub(PersistenceContext) {
            getIntegrationItem() >> Stub(IntegrationItem) {
                getAttributes() >> attributes
            }
        }
        attributes.each {
            ctx.getIntegrationItem().getReferencedItem(it) >> Stub(IntegrationItem)
            ctx.getReferencedContext(it) >> REFERENCED_CONTEXT
        }
        ctx
    }

    private void referencedItemFoundFor(TypeAttributeDescriptor attribute, ItemModel item = new ItemModel()) {
        itemModelService.deriveReferencedItemModel(attribute, REFERENCED_CONTEXT) >> item
    }
}
