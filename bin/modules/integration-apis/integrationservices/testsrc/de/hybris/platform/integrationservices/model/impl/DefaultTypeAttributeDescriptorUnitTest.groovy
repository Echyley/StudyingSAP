/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.HybrisEnumValue
import de.hybris.platform.core.enums.TypeOfCollectionEnum
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.type.AtomicTypeModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.CollectionTypeModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.type.MapTypeModel
import de.hybris.platform.core.model.type.TypeModel
import de.hybris.platform.integrationservices.model.AttributeSettableChecker
import de.hybris.platform.integrationservices.model.AttributeSettableCheckerFactory
import de.hybris.platform.integrationservices.model.AttributeValueAccessor
import de.hybris.platform.integrationservices.model.AttributeValueAccessorFactory
import de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.MockRelationDescriptorModelBuilder
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

import static de.hybris.platform.integrationservices.IntegrationObjectItemBuilder.item
import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor
import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.collectionDescriptor
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.collectionAttributeBuilder
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.complexRelationAttributeBuilder
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.oneToOneRelationAttributeBuilder
import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder
import static de.hybris.platform.integrationservices.model.MockMapAttributeDescriptorModelBuilder.mapAttributeDescriptor
import static de.hybris.platform.integrationservices.model.MockRelationAttributeDescriptorModelBuilder.relationAttribute
import static de.hybris.platform.integrationservices.model.MockRelationDescriptorModelBuilder.oneToOneRelation

@UnitTest
class DefaultTypeAttributeDescriptorUnitTest extends JUnitPlatformSpecification {
    @Shared
    def ITEM_DESCRIPTOR = Stub(TypeDescriptor) {
        getItemCode() >> 'Non-empty item code'
        getIntegrationObjectCode() >> 'Non-empty IO code'
    }

    def attributeModel = Stub IntegrationObjectItemAttributeModel
    def descriptor = new DefaultTypeAttributeDescriptor(ITEM_DESCRIPTOR, attributeModel, null)

    @Test
    def 'getAttributeName() is the attribute name in the model'() {
        given:
        attributeModel.getAttributeName() >> 'some name'

        expect:
        descriptor.attributeName == attributeModel.attributeName
    }

    @Test
    def "isCollection() is #res when the attribute is a #type attribute"() {
        given:
        attributeModel.getAttributeDescriptor() >> attr.build()

        expect:
        descriptor.collection == res

        where:
        attr                        | type                            | res
        attributeDescriptor()       | 'simple'                        | false
        oneToOneRelation()          | 'one-to-one relation'           | false
        oneToManyRelation()         | 'one-to-many relation'          | true
        reversedOneToManyRelation() | 'reversed one-to-many relation' | true
        collectionDescriptor()      | 'collection'                    | true
    }

    @Test
    def "getCollectionDescriptor() returns a collection descriptor"() {
        given:
        attributeModel.getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
            getAttributeType() >> Stub(CollectionTypeModel) {
                getTypeOfCollection() >> TypeOfCollectionEnum.COLLECTION
            }
        }

        expect:
        descriptor.getCollectionDescriptor() != null
    }

    @Test
    def "getAttributeType() correctly determines referenced item type for a #desc attribute"() {
        given:
        def descriptor = typeAttributeDescriptor(attr)

        expect:
        descriptor.attributeType?.itemCode == attrType

        where:
        attrType   | desc         | attr
        'SomeItem' | 'reference'  | simpleAttributeBuilder().withReturnIntegrationObject(attrType)
        'Integer'  | 'collection' | collectionAttributeBuilder().withAttributeDescriptor(collectionDescriptor().withPrimitiveTarget(attrType))
        'Child'    | 'relation'   | complexRelationAttributeBuilder().withReturnIntegrationObject(attrType)
    }

    @Test
    def "getAttributeType() uses container item integration object code for the primitive attribute type"() {
        given:
        attributeModel.getAttributeDescriptor() >> attributeDescriptor().withPrimitiveTypeCode('String').build()
        attributeModel.getReturnIntegrationObjectItem() >> null

        expect:
        with(descriptor) {
            attributeType instanceof PrimitiveTypeDescriptor
            attributeType.integrationObjectCode == ITEM_DESCRIPTOR.integrationObjectCode
        }
    }

    @Test
    def "getAttributeType() uses container item integration object code for the map attribute type"() {
        given:
        def descriptor = typeAttributeDescriptor(simpleAttributeBuilder()
                .withIntegrationObjectItem(item().withIntegrationObjectCode('IntegrationObject'))
                .withAttributeDescriptor(attributeDescriptor().withType(MapTypeModel.class)))

        expect:
        with(descriptor) {
            attributeType instanceof MapTypeDescriptor
            attributeType.integrationObjectCode == 'IntegrationObject'
        }
    }

    @Test
    def "getAttributeType() reports illegal state when referenced type is not Atomic and returnIntegrationObject is not modeled"() {
        given:
        def descriptor = typeAttributeDescriptor(collectionAttributeBuilder()
                .withAttributeDescriptor(collectionDescriptor().withTarget("Product")))

        when:
        descriptor.getAttributeType()

        then:
        def e = thrown(IllegalStateException)
        e.message == "Modeling error: $descriptor is not a primitive attribute"
    }

    @Test
    def "getAttributeType() is cached"() {
        given:
        def typeFromFirstCall = descriptor.getAttributeType()
        def typeFromSecondCall = descriptor.getAttributeType()

        expect:
        typeFromFirstCall.is typeFromSecondCall
    }

    @Test
    def "getTypeDescriptor() returns types of the integration object item containing the attribute"() {
        expect:
        descriptor.typeDescriptor?.itemCode == ITEM_DESCRIPTOR.itemCode
    }

    @Test
    def "getTypeDescriptor() is cached"() {
        given:
        def typeFromFirstCall = descriptor.getTypeDescriptor()
        def typeFromSecondCall = descriptor.getTypeDescriptor()

        expect:
        typeFromFirstCall.is typeFromSecondCall
    }

    @Test
    def "reverse() is not applicable for #attrType attribute"() {
        given:
        attributeModel.getAttributeDescriptor() >> attrDescriptor.build()

        expect:
        !descriptor.reverse().isPresent()

        where:
        attrType     | attrDescriptor
        'composite'  | attributeDescriptor()
        'collection' | collectionDescriptor()
    }

    @Test
    def "reverse() is applicable for #relation relation model"() {
        given:
        def itemModel = itemModelBuilder().withAttribute(simpleAttributeBuilder().withName("reverse"))
        def attr = typeAttributeDescriptor(oneToOneRelationAttributeBuilder()
                .withReturnIntegrationObjectItem(itemModel)
                .withAttributeDescriptor(attrDescriptor))

        expect:
        attr.reverse().isPresent()

        where:
        relation | attrDescriptor
        'target' | oneToOneRelation().withTargetAttribute(relationAttribute().withQualifier("reverse"))
        'source' | oneToOneRelation().withSourceAttribute(relationAttribute().withQualifier("reverse"))
    }

    @Test
    def "isNullable() is #res when attribute is #condition"() {
        given:
        def attr = typeAttributeDescriptor(attribute)

        expect:
        attr.isNullable() == res

        where:
        condition                                                    | res   | attribute
        'unique in integration metadata and optional in type system' | true  | simpleAttributeBuilder().unique().withAttributeDescriptor(attributeDescriptor().optional())
        'unique and optional in type system'                         | true  | simpleAttributeBuilder().withAttributeDescriptor(attributeDescriptor().unique().optional())
        'optional in type system'                                    | true  | simpleAttributeBuilder().withAttributeDescriptor(attributeDescriptor().optional())
        'not optional in type system'                                | false | simpleAttributeBuilder().withAttributeDescriptor(attributeDescriptor().withOptional(false))
        'defined with default value'                                 | true  | simpleAttributeBuilder().withAttributeDescriptor(attributeDescriptor().withOptional(false).withDefaultValue("some value"))
        'not defined in type system'                                 | true  | simpleAttributeBuilder()
    }

    @Test
    def "isPartOf() is #res partOf in attribute model is #value"() {
        given:
        attributeModel.getPartOf() >> value

        expect:
        descriptor.partOf == res

        where:
        value | res
        true  | true
        false | false
        null  | false
    }

    @Test
    def "isPartOf() is false when attribute model has autoCreate set to true and attribute descriptor has partOf set to false"() {
        given:
        attributeModel.getAttributeDescriptor() >> collectionDescriptor().withPartOf(false).build()
        attributeModel.getAutoCreate() >> true

        expect:
        !descriptor.partOf
    }

    @Test
    def "isAutoCreate() is #res when attribute model has autoCreate=#autoCreate and partOf=partOf"() {
        given:
        attributeModel.getAutoCreate() >> autoCreate
        attributeModel.getPartOf() >> partOf

        expect:
        descriptor.autoCreate == res

        where:
        autoCreate | partOf | res
        true       | null   | true
        false      | null   | false
        null       | null   | false
        true       | false  | true
        false      | false  | false
        null       | false  | false
        true       | true   | true
        false      | true   | true
        null       | true   | true
    }

    @Test
    def "isLocalized() is #res when attribute defined with localized=#value in type system"() {
        given:
        attributeModel.getAttributeDescriptor() >> attributeDescriptor().withLocalized(value).build()

        expect:
        descriptor.isLocalized() == res

        where:
        value | res
        true  | true
        false | false
        null  | false
    }

    @Test
    def "isPrimitive() is #res when attribute is #attr"() {
        expect:
        attr.isPrimitive() == res

        where:
        attr                          | res
        primitiveAttribute()          | true
        collectionAttribute()         | false
        localizedPrimitiveAttribute() | true
    }

    @Test
    def "isMap is #res for #desc attributes"() {
        expect:
        attr.isMap() == res

        where:
        attr                                    | res   | desc
        primitiveAttribute()                    | false | 'primitive'
        collectionAttribute()                   | false | 'collection'
        localizedMapAttribute()                 | true  | 'localized'
        typeAttributeDescriptor(mapAttribute()) | true  | 'map'
    }

    @Test
    def "getMapDescriptor().present() is #res for #desc attributes"() {
        expect:
        attr.getMapDescriptor().present == res

        where:
        attr                                    | res   | desc
        primitiveAttribute()                    | false | 'primitive'
        collectionAttribute()                   | false | 'collection'
        localizedMapAttribute()                 | false | 'localized'
        typeAttributeDescriptor(mapAttribute()) | true  | 'map'
    }

    @Test
    def 'getMapDescriptor() is empty when exception is thrown during map descriptor creation'() {
        given: 'map has unsupported value type'
        def attribute = typeAttributeDescriptor mapAttribute(Stub(ComposedTypeModel))

        expect:
        attribute.getMapDescriptor().empty
    }

    @Test
    def "isKeyAttribute is #value when model getUnique() is #model and attribute descriptor getUnique() is #desc"() {
        given:
        attributeModel.getUnique() >> model
        attributeModel.getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
            getUnique() >> desc
        }

        expect:
        descriptor.isKeyAttribute() == value

        where:
        model | desc  | value
        true  | true  | true
        true  | false | true
        false | true  | true
        true  | null  | true
        null  | true  | true
        null  | null  | false
        false | false | false
    }

    @Unroll
    def "isReadable is #result when attribute descriptor.getReadable is #readable"() {
        given:
        attributeModel.getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
            getReadable() >> readable
        }

        expect:
        descriptor.isReadable() == result

        where:
        readable | result
        true     | true
        null     | true
        false    | false
    }

    @Test
    def "equals() is #res when compared to #condition"() {
        given:
        def attr = typeAttributeDescriptor("Sample", "id")

        expect:
        (attr == other) == res

        where:
        condition                                       | other                                                   | res
        'null'                                          | null                                                    | false
        'another instance'                              | Stub(TypeAttributeDescriptor)                           | false
        'another instance with different attribute'     | typeAttributeDescriptor("Sample", "differentAttribute") | false
        'another instance with different type'          | typeAttributeDescriptor("DifferentType", "id")          | false
        'another instance with same type and attribute' | typeAttributeDescriptor("Sample", "id")                 | true
    }

    @Test
    @Unroll
    def "hashCode() are #resDesc when the other instance #condition"() {
        given:
        def attr = typeAttributeDescriptor("Sample", "id")

        expect:
        (attr.hashCode() == other.hashCode()) == res

        where:
        condition                     | other                                                   | res   | resDesc
        'has null type and attribute' | Stub(TypeAttributeDescriptor)                           | false | 'not equal'
        'has different attribute'     | typeAttributeDescriptor("Sample", "differentAttribute") | false | 'not equal'
        'has different type'          | typeAttributeDescriptor("DifferentType", "id")          | false | 'not equal'
        'has same type and attribute' | typeAttributeDescriptor("Sample", "id")                 | true  | 'equal'
    }

    @Test
    def 'toString contains the class name, attribute name and code of the item type containing the attribute'() {
        given:
        attributeModel.getAttributeName() >> 'some non-empty name'

        expect:
        with(descriptor.toString()) {
            contains descriptor.class.simpleName
            contains attributeModel.attributeName
            contains ITEM_DESCRIPTOR.itemCode
        }
    }

    @Test
    def 'accessor() is created and returned even when the factory is not provided'() {
        expect: 'accessor is still created and not null'
        descriptor.accessor()
    }

    @Test
    def 'accessor() is created by the provided factory'() {
        given: 'factory that creates an accessor'
        def accessorCreatedByFactory = Stub AttributeValueAccessor
        def factory = Stub(DescriptorFactory) {
            getAttributeValueAccessorFactory() >> Stub(AttributeValueAccessorFactory) {
                create(_ as TypeAttributeDescriptor) >> accessorCreatedByFactory
            }
        }
        and: 'the factory is passed to the constructor'
        def descriptor = new DefaultTypeAttributeDescriptor(ITEM_DESCRIPTOR, attributeModel, factory)

        expect:
        descriptor.accessor().is accessorCreatedByFactory
    }

    @Test
    def "isSettable returns #settable when settable checker returns #settable"() {
        given:
        def item = Stub ItemModel
        and:
        def settableChecker = Mock(AttributeSettableChecker) {
            isSettable(item, _ as TypeAttributeDescriptor) >> settable
        }
        and:
        def factory = factoryWithSettableChecker(settableChecker)
        and:
        def descriptor = new DefaultTypeAttributeDescriptor(ITEM_DESCRIPTOR, attributeModel, factory)

        expect:
        descriptor.isSettable(item) == settable

        where:
        settable << [true, false]
    }

    @Test
    def "isSettable is not delegated to settable checker when item is not of type ItemModel and settable=false"() {
        given:
        def settableChecker = Mock AttributeSettableChecker
        and:
        def factory = factoryWithSettableChecker(settableChecker)
        and:
        def descriptor = new DefaultTypeAttributeDescriptor(ITEM_DESCRIPTOR, attributeModel, factory)
        and:
        def item = Stub HybrisEnumValue

        when:
        def settable = descriptor.isSettable(item)

        then:
        !settable
        and:
        0 * settableChecker.isSettable(item, descriptor)
    }

    private DefaultTypeAttributeDescriptor typeAttributeDescriptor(String containerType, String name) {
        def itemType = Stub(TypeDescriptor) {
            getItemCode() >> containerType
        }
        def attribute = Stub(IntegrationObjectItemAttributeModel) {
            getAttributeName() >> name
        }
        new DefaultTypeAttributeDescriptor(itemType, attribute, null)
    }

    private DefaultTypeAttributeDescriptor typeAttributeDescriptor(BaseMockItemAttributeModelBuilder builder) {
        typeAttributeDescriptor(builder.build())
    }

    private DefaultTypeAttributeDescriptor typeAttributeDescriptor(IntegrationObjectItemAttributeModel attr) {
        new DefaultTypeAttributeDescriptor(ITEM_DESCRIPTOR, attr, null)
    }

    private static MockRelationDescriptorModelBuilder oneToManyRelation() {
        MockRelationDescriptorModelBuilder.oneToManyRelation()
                .withIsSource(true)
    }

    private static MockRelationDescriptorModelBuilder reversedOneToManyRelation() {
        MockRelationDescriptorModelBuilder.manyToOneRelation()
                .withIsSource(false)
    }

    private DefaultTypeAttributeDescriptor primitiveAttribute() {
        typeAttributeDescriptor(simpleAttributeBuilder()
                .withAttributeDescriptor(attributeDescriptor()
                        .withPrimitive(true)
                        .withType(AtomicTypeModel)))
    }

    private DefaultTypeAttributeDescriptor collectionAttribute() {
        typeAttributeDescriptor(simpleAttributeBuilder()
                .withAttributeDescriptor(attributeDescriptor()
                        .withType(CollectionTypeModel))
                .withReturnIntegrationObject("testObj"))
    }

    private DefaultTypeAttributeDescriptor localizedPrimitiveAttribute() {
        typeAttributeDescriptor(simpleAttributeBuilder()
                .withAttributeDescriptor(attributeDescriptor()
                        .withPrimitive(true)
                        .withLocalized(true)
                        .withType(AtomicTypeModel)))
    }

    private DefaultTypeAttributeDescriptor localizedMapAttribute() {
        typeAttributeDescriptor(simpleAttributeBuilder()
                .withAttributeDescriptor(mapAttributeDescriptor()
                        .withPrimitive(true)
                        .withLocalized(true)
                        .withReturnType(AtomicTypeModel)))
    }

    private IntegrationObjectItemAttributeModel mapAttribute() {
        mapAttribute Stub(AtomicTypeModel)
    }

    private IntegrationObjectItemAttributeModel mapAttribute(TypeModel mapValueType) {
        Stub(IntegrationObjectItemAttributeModel) {
            getAttributeDescriptor() >> Stub(AttributeDescriptorModel) {
                getAttributeType() >> Stub(MapTypeModel) {
                    getArgumentType() >> Stub(AtomicTypeModel)
                    getReturntype() >> mapValueType
                }
            }
            getIntegrationObjectItem() >> Stub(IntegrationObjectItemModel) {
                getIntegrationObject() >> Stub(IntegrationObjectModel) {
                    getCode() >> 'SomeIO'
                }
            }
        }
    }

    private DescriptorFactory factoryWithSettableChecker(settableChecker) {
        Stub(DescriptorFactory) {
            getAttributeSettableCheckerFactory() >> Stub(AttributeSettableCheckerFactory) {
                create(_ as DefaultTypeAttributeDescriptor) >> settableChecker
            }
        }
    }
}