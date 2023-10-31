/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.inboundservices.persistence.populator

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.HybrisEnumValue
import de.hybris.platform.core.PK
import de.hybris.platform.core.enums.TestEnum
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.integrationservices.item.IntegrationItem
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared


@UnitTest
class EnumAttributePopulatorUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM = new ItemModel()

    @Shared
    def REFERENCED_CONTEXT = Stub PersistenceContext


    def attributeValueAccessor = Mock AttributeValueAccessor
    def modelService = Mock ModelService
    def itemModelService = Stub ContextReferencedItemModelService

    def populator = new EnumAttributePopulator(itemModelService, modelService)

    @Test
    def "fails to instantiate with null #paramType"() {
        when:
        new EnumAttributePopulator(referenceService, modelService)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "$paramType cannot be null"

        where:
        referenceService                        | modelService       | paramType
        null                                    | Stub(ModelService) | ContextReferencedItemModelService.simpleName
        Stub(ContextReferencedItemModelService) | null               | ModelService.simpleName
    }

    @Test
    def 'attribute is not populated when attribute is not an Enum'() {
        given:
        def attribute = nonEnumAttribute()
        and:
        attributeValueFound attribute, TestEnum.TESTVALUE1

        when:
        populator.populate ITEM, persistenceContextForItemWith(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'attribute is not populated when Enum attribute is not settable'() {
        given:
        def attribute = notSettableEnumAttribute()
        and:
        attributeValueFound attribute, TestEnum.TESTVALUE4

        when:
        populator.populate ITEM, persistenceContextForItemWith(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'attribute is not populated when Enum attribute is collection'() {
        given:
        def attribute = collectionEnumAttribute()
        and:
        attributeValueFound attribute, TestEnum.TESTVALUE3

        when:
        populator.populate ITEM, persistenceContextForItemWith(attribute)

        then:
        0 * attributeValueAccessor.setValue(ITEM, _)
    }

    @Test
    def 'attribute is populated when the attribute is an Enum'() {
        given:
        def attribute = enumAttribute()
        and:
        final foundValue = attributeValueFound attribute, TestEnum.TESTVALUE1

        when:
        populator.populate ITEM, persistenceContextForItemWith(attribute)

        then:
        1 * attributeValueAccessor.setValue(ITEM, TestEnum.TESTVALUE1)
        and:
        0 * modelService.save(foundValue)
    }

    @Test
    def 'attribute is populated with pre-saved Enum when the Enum value is created'() {
        given:
        def attribute = enumAttribute()
        and:
        def createdValue = attributeValueCreated attribute, TestEnum.TESTVALUE1

        when:
        populator.populate ITEM, persistenceContextForItemWith(attribute)

        then:
        1 * modelService.save(createdValue)
        and:
        1 * attributeValueAccessor.setValue(ITEM, TestEnum.TESTVALUE1)
    }

    @Test
    def 'populate throws exception when setting an attribute'() {
        given:
        def attribute = enumAttribute()
        def context = persistenceContextForItemWith(attribute)
        and:
        attributeValueFound attribute, TestEnum.TESTVALUE3
        and: 'setting the attribute throws an exception'
        attributeValueAccessor.setValue(ITEM, TestEnum.TESTVALUE3) >> {
            throw new AttributeNotSupportedException('IGNORE - testing exception', 'attribute name - does not matter')
        }

        when:
        populator.populate ITEM, context

        then:
        def e = thrown UnmodifiableAttributeException
        e.attributeDescriptor.is attribute
        e.persistenceContext.is context
    }

    @Test
    def 'populates all applicable attributes in an item'() {
        given: 'multiple applicable attributes'
        def attributeOne = enumAttribute()
        def attributeTwo = enumAttribute()
        and:
        attributeValueFound(attributeOne, TestEnum.TESTVALUE1)
        attributeValueFound(attributeTwo, TestEnum.TESTVALUE2)

        when:
        populator.populate ITEM, persistenceContextForItemWith([attributeOne, attributeTwo])

        then:
        1 * attributeValueAccessor.setValue(ITEM, TestEnum.TESTVALUE1)
        1 * attributeValueAccessor.setValue(ITEM, TestEnum.TESTVALUE2)
    }

    private ItemModel itemModel(long pk) {
        Stub(ItemModel) {
            getPk() >> PK.fromLong(pk)
        }
    }

    private TypeAttributeDescriptor nonEnumAttribute() {
        Stub(TypeAttributeDescriptor) {
            accessor() >> attributeValueAccessor
            isSettable(_) >> true
            getAttributeType() >> Stub(TypeDescriptor) {
                isEnumeration() >> false
            }
        }
    }

    private TypeAttributeDescriptor enumAttribute() {
        Stub(TypeAttributeDescriptor) {
            accessor() >> attributeValueAccessor
            isSettable(_) >> true
            getAttributeType() >> Stub(TypeDescriptor) {
                isEnumeration() >> true
            }
        }
    }

    private TypeAttributeDescriptor notSettableEnumAttribute() {
        Stub(TypeAttributeDescriptor) {
            accessor() >> attributeValueAccessor
            isSettable(_) >> false
            getAttributeType() >> Stub(TypeDescriptor) {
                isEnumeration() >> true
            }
        }
    }

    private TypeAttributeDescriptor collectionEnumAttribute() {
        Stub(TypeAttributeDescriptor) {
            accessor() >> attributeValueAccessor
            isSettable(_) >> true
            isCollection() >> true
            getAttributeType() >> Stub(TypeDescriptor) {
                isEnumeration() >> true
            }
        }
    }

    private PersistenceContext persistenceContextForItemWith(TypeAttributeDescriptor attribute) {
        persistenceContextForItemWith([attribute])
    }

    private PersistenceContext persistenceContextForItemWith(List<TypeAttributeDescriptor> attributes) {
        def ctx = Stub(PersistenceContext) {
            getIntegrationItem() >> Stub(IntegrationItem) {
                getAttributes() >> attributes
            }
        }
        attributes.forEach {
            ctx.getReferencedContext(it) >> REFERENCED_CONTEXT
        }
        ctx
    }

    private ItemModel attributeValueFound(TypeAttributeDescriptor attribute, HybrisEnumValue value) {
        def item = itemModel toPk(value)
        itemModelService.deriveReferencedItemModel(attribute, REFERENCED_CONTEXT) >> item
        modelService.isNew(item) >> false
        modelService.get(item.pk) >> value
        item
    }

    private ItemModel attributeValueCreated(TypeAttributeDescriptor attribute, HybrisEnumValue value) {
        def item = itemModel toPk(value)
        itemModelService.deriveReferencedItemModel(attribute, REFERENCED_CONTEXT) >> item
        modelService.isNew(item) >> true
        modelService.get(item.pk) >> value
        item
    }

    private long toPk(HybrisEnumValue value) {
        def pkStr = value.code.substring(value.code.length() - 1)
        Long.parseLong pkStr
    }
}
