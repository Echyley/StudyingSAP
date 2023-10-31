/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.c2l.C2LItemModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.core.model.user.AddressModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.jalo.order.Order
import de.hybris.platform.persistence.order.TestOrder
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared

@UnitTest
class DefaultIntegrationObjectDescriptorUnitTest extends JUnitPlatformSpecification {
    @Shared
    def descriptorFactory = Stub(DescriptorFactory) {
        createItemTypeDescriptor(_ as IntegrationObjectItemModel) >> { List args ->
            new ItemTypeDescriptor(args[0] as IntegrationObjectItemModel)
        }
        createClassTypeDescriptor(_ as IntegrationObjectDescriptor, _ as IntegrationObjectClassModel) >> { List args ->
            new ClassTypeDescriptor(args[0] as IntegrationObjectDescriptor, args[1] as IntegrationObjectClassModel, descriptorFactory)
        }
    }

    @Test
    def 'creates integration object descriptor from a model and factory'() {
        expect:
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel('MyObject'), descriptorFactory)
        ioDescriptor.code == 'MyObject'
        ioDescriptor.factory.is descriptorFactory
    }

    @Test
    def "retrieves item type descriptor for an item when it has an IO item defined for its #condition"() {
        given: 'a product item model, which has ApparelProduct and ElectronicsProduct subtypes'
        def productModel = ioItemModel('Product', ['ApparelProduct', 'ElectronicsProduct'])
        and: 'an apparel item model, which has not subtypes in the type system'
        def apparelProductModel = ioItemModel('ApparelProduct')
        and: 'an IO with a Product and ApparelProduct items'
        def model = ioModel([productModel, apparelProductModel])
        and: 'an IO descriptor for the model'
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(model, descriptorFactory)

        expect:
        ioDescriptor.getTypeDescriptor(item).map({ it.itemCode }).orElse(null) == expectedType

        where:
        condition                                                 | item                            | expectedType
        'type'                                                    | itemModel('Product')            | 'Product'
        'type and its super type'                                 | itemModel('ApparelProduct')     | 'ApparelProduct'
        'super type'                                              | itemModel('ElectronicsProduct') | 'Product'
        'ComposedTypeModel corresponding type'                    | composedTypeModel('Product')    | 'Product'
        'ComposedTypeModel corresponding type and its super type' | itemModel('ApparelProduct')     | 'ApparelProduct'
        'ComposedTypeModel corresponding super type'              | itemModel('ElectronicsProduct') | 'Product'
    }

    @Test
    def "retrieves type descriptor for an item when it has an IO item defined for its #condition"() {
        given: 'a product item model, which has ApparelProduct and ElectronicsProduct subtypes'
        def productModel = ioItemModel('Product', ['ApparelProduct', 'ElectronicsProduct'])
        and: 'an apparel item model, which has not subtypes in the type system'
        def apparelProductModel = ioItemModel('ApparelProduct')


        and: 'an IO with a Product and ApparelProduct items'
        def model = ioModel([productModel, apparelProductModel])
        and: 'an IO descriptor for the model'
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(model, descriptorFactory)

        expect:
        ioDescriptor.getTypeDescriptor(item).map({ it.itemCode }).orElse(null) == expectedType

        where:
        condition                                                 | item                            | expectedType
        'type'                                                    | itemModel('Product')            | 'Product'
        'type and its super type'                                 | itemModel('ApparelProduct')     | 'ApparelProduct'
        'super type'                                              | itemModel('ElectronicsProduct') | 'Product'
        'ComposedTypeModel corresponding type'                    | composedTypeModel('Product')    | 'Product'
        'ComposedTypeModel corresponding type and its super type' | itemModel('ApparelProduct')     | 'ApparelProduct'
        'ComposedTypeModel corresponding super type'              | itemModel('ElectronicsProduct') | 'Product'
    }

    @Test
    def "retrieves item type descriptor for a class when it has an IO class defined for the #condition"() {
        given: 'an IO with class models for ItemModel and C2LItemModel, where the latter is a subclass of the ItemModel'
        def model = ioModel([], [ioClassModel(ItemModel), ioClassModel(C2LItemModel)])
        and: 'an IO descriptor for the model'
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(model, descriptorFactory)

        expect:
        ioDescriptor.getTypeDescriptor(item).map({ it.itemCode }).orElse(null) == expectedType

        where:
        condition                        | item                | expectedType
        'items class'                    | new ItemModel()     | 'ItemModel'
        'items class and its superclass' | new C2LItemModel()  | 'C2LItemModel'
        'superclass'                     | new CustomerModel() | 'ItemModel'
    }

    @Test
    def "retrieves type descriptor for a class when it has an IO class defined for the #condition"() {
        given: 'an IO with class models for ItemModel and C2LItemModel, where the latter is a subclass of the ItemModel'
        def model = ioModel([], [ioClassModel(ItemModel), ioClassModel(C2LItemModel), ioClassModel(Order), ioClassModel(TestOrder)])
        and: 'an IO descriptor for the model'
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(model, descriptorFactory)

        expect:
        ioDescriptor.getTypeDescriptor(item).map({ it.itemCode }).orElse(null) == expectedType

        where:
        condition                         | item                  | expectedType
        'items class'                     | new ItemModel()       | 'ItemModel'
        'items class and its superclass'  | new C2LItemModel()    | 'C2LItemModel'
        'superclass'                      | new CustomerModel()   | 'ItemModel'
        'Order class'                     | new Order()           | 'Order'
        'Order class and its superclass'  | new TestOrder()       | 'TestOrder'
        'superclass'                      | new GrandChildOrder() | 'Order'
    }

    @Test
    def "empty type descriptor is returned when itemModel is null"() {
        given: "integration object with a Product item"
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel([ioItemModel('Product')]), descriptorFactory)

        expect: 'empty type descriptor for a type that is not in the IO model'
        ioDescriptor.getTypeDescriptor(null).empty
    }

    @Test
    def "empty type descriptor is returned when POJO is null"() {
        given: "integration object with a Product item"
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel([ioItemModel('Product')]), descriptorFactory)

        expect: 'empty type descriptor for a type that is not in the IO model'
        ioDescriptor.getTypeDescriptor(null).empty
    }

    @Test
    def "retrieves empty type descriptor for an item that has no IO item defined for its type"() {
        given: "integration object with a Product item"
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel([ioItemModel('Product')]), descriptorFactory)

        expect: 'empty type descriptor for a type that is not in the IO model'
        ioDescriptor.getTypeDescriptor(itemModel('Category')).empty
    }

    @Test
    def "retrieves empty type descriptor for an item (passed as POJO) that has no IO item defined for its type"() {
        given: "integration object with a Product item"
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel([ioItemModel('Product')]), descriptorFactory)

        expect: 'empty type descriptor for a type that is not in the IO model'
        ioDescriptor.getTypeDescriptor(itemModel('Category')).empty
    }

    @Test
    def "retrieves empty type descriptor for a POJO that has no IO item defined for its type"() {
        given: "integration object with a Product item"
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel([], [ioClassModel(Order)]), descriptorFactory)

        expect: 'empty type descriptor for a type that is not in the IO model'
        ioDescriptor.getTypeDescriptor(pojoModel('Category')).empty
    }

    @Test
    def "retrieves root item type descriptor using getRootItemType when integration object has a root item defined"() {
        given:
        def ioModel = ioModel([ioRootItemModel('Product'), ioItemModel('Unit')])
        def io = new DefaultIntegrationObjectDescriptor(ioModel, descriptorFactory)

        when:
        def rootType = io.getRootItemType()

        then:
        rootType.map({ it.itemCode }).orElse(null) == 'Product'
    }

    @Test
    def "retrieves root item type descriptor when integration object has a root item defined"() {
        given:
        def ioModel = ioModel([ioRootItemModel('Product'), ioItemModel('Unit')])
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel, descriptorFactory)

        when:
        def rootType = ioDescriptor.getRootType()

        then:
        rootType.map({ it.itemCode }).orElse(null) == 'Product'
    }

    @Test
    def "retrieves root class type descriptor using getRootItemType when integration object has a root class defined"() {
        given:
        def ioModel = ioModel([], [ioRootClassModel(CustomerModel), ioClassModel(AddressModel)])
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel, descriptorFactory)

        when:
        def rootType = ioDescriptor.getRootItemType()

        then:
        rootType.map({ it.itemCode }).orElse(null) == CustomerModel.simpleName
    }

    @Test
    def "retrieves root class type descriptor when integration object has a root class defined"() {
        given:
        def ioModel = ioModel([], [ioRootClassModel(CustomerModel), ioClassModel(AddressModel)])
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel, descriptorFactory)

        when:
        def rootType = ioDescriptor.getRootType()

        then:
        rootType.map({ it.itemCode }).orElse(null) == CustomerModel.simpleName
    }

    @Test
    def "retrieves empty root item type descriptor using getRootItemType when integration object has no #condition"() {
        expect:
        !io.getRootItemType().present

        where:
        condition             | io
        'root item types'     | new DefaultIntegrationObjectDescriptor(ioModel([ioItemModel('NotRoot')], []), descriptorFactory)
        'item or class types' | new DefaultIntegrationObjectDescriptor(ioModel(), descriptorFactory)
        'root class types'    | new DefaultIntegrationObjectDescriptor(ioModel([], [ioClassModel(AddressModel)]), descriptorFactory)
    }

    @Test
    def "retrieves empty root item type descriptor when integration object has no #condition"() {
        expect:
        !io.getRootType().present

        where:
        condition             | io
        'root item types'     | new DefaultIntegrationObjectDescriptor(ioModel([ioItemModel('NotRoot')], []), descriptorFactory)
        'item or class types' | new DefaultIntegrationObjectDescriptor(ioModel(), descriptorFactory)
        'root class types'    | new DefaultIntegrationObjectDescriptor(ioModel([], [ioClassModel(AddressModel)]), descriptorFactory)
    }


    @Test
    def "getItemTypeDescriptors() returns #condition defined in the integration object"() {
        given:
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel(itemModels, classModels), descriptorFactory)

        expect:
        ioDescriptor.getItemTypeDescriptors().size() == itemModels.size() + classModels.size()

        where:
        condition                      | itemModels                                                            | classModels
        'all item types'               | [ioRootItemModel('Car'), ioItemModel('Wheel'), ioItemModel('Engine')] | []
        'all class types'              | []                                                                    | [ioRootClassModel(CustomerModel), ioClassModel(AddressModel)]
        'empty set when no item types' | []                                                                    | []
    }

    @Test
    def "exception is thrown when IntegrationObjectModel is defined with both items and classes"() {
        given:
        def model = ioModel([ioRootItemModel('Customer')], [ioClassModel(AddressModel)])

        when:
        new DefaultIntegrationObjectDescriptor(model, descriptorFactory)

        then:
        def e = thrown IntegrationObjectItemAndClassConflictException
        e.message == "Integration Object 'SomeIO' has both IntegrationObjectItem(s) and IntegrationObjectClass(es) " +
                "associated. This combination is incompatible and one of the types must be removed."
    }

    @Test
    def 'equal when other descriptor has same class and integration object model code'() {
        given:
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel('Object'), descriptorFactory)

        expect:
        ioDescriptor == new DefaultIntegrationObjectDescriptor(ioModel('Object'), descriptorFactory)
    }

    @Test
    def "not equal when other descriptor #condition"() {
        given:
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel('Object'), descriptorFactory)

        expect:
        ioDescriptor != other

        where:
        condition                                   | other
        'is null'                                   | null
        'has different class'                       | Stub(IntegrationObjectDescriptor)
        'has different IntegrationObjectModel code' | new DefaultIntegrationObjectDescriptor(ioModel('Other'), descriptorFactory)
    }

    @Test
    def 'hash codes equal when other descriptor has same class and integration object model code'() {
        given:
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel('Object'), descriptorFactory)

        expect:
        ioDescriptor.hashCode() == new DefaultIntegrationObjectDescriptor(ioModel('Object'), descriptorFactory).hashCode()
    }

    @Test
    def 'hash codes not equal when other descriptor has different IntegrationObjectModel code'() {
        given:
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel('Object'), descriptorFactory)

        expect:
        ioDescriptor.hashCode() != new DefaultIntegrationObjectDescriptor(ioModel('Other'), descriptorFactory).hashCode()
    }

    @Test
    def "getItemTypeDescriptors does not leak reference"() {
        given:
        def itemModels = [ioRootItemModel('Car'), ioItemModel('Wheel'), ioItemModel('Engine')]
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel(itemModels), descriptorFactory)

        expect:
        !ioDescriptor.itemTypeDescriptors.is(ioDescriptor.itemTypeDescriptors)
    }

    @Test
    def 'contains returns false if type descriptor is not present in the integration object'() {
        given: 'some simple integration object'
        def itemModels = [ioItemModel('DoesNotMatter')]
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel(itemModels), descriptorFactory)
        and: 'a type descriptor not from the integration object'
        def type = Stub TypeDescriptor

        expect:
        !ioDescriptor.contains(type)
    }

    @Test
    def 'contains returns true if type descriptor is present in the integration object'() {
        given: 'some simple integration object'
        def itemModel = ioItemModel('DoesNotMatter')
        def ioDescriptor = new DefaultIntegrationObjectDescriptor(ioModel([itemModel]), descriptorFactory)

        and: 'a type descriptor from the integration object'
        def type = ioDescriptor.getItemTypeDescriptors()[0]

        expect:
        ioDescriptor.contains type
    }

    private IntegrationObjectModel ioModel(String code) {
        Stub(IntegrationObjectModel) {
            getCode() >> code
        }
    }

    private IntegrationObjectModel ioModel(List<IntegrationObjectItemModel> items = [],
                                           List<IntegrationObjectClassModel> classes = []) {
        Stub(IntegrationObjectModel) {
            getCode() >> 'SomeIO'
            getItems() >> items
            getRootItem() >> { items.find { it.root } }
            getClasses() >> classes
            getRootClass() >> { classes.find { it.root } }
        }
    }

    private IntegrationObjectItemModel ioRootItemModel(String type, List<String> subtypes = []) {
        ioItemModel(type, subtypes, true)
    }

    private IntegrationObjectItemModel ioItemModel(String type, List<String> subtypes = [], boolean root = false) {
        Stub(IntegrationObjectItemModel) {
            getCode() >> type
            getType() >> Stub(ComposedTypeModel) {
                getCode() >> type
                getAllSubTypes() >> { subtypes.collect { composedType(it) } }
            }
            getRoot() >> root
        }
    }

    private IntegrationObjectClassModel ioRootClassModel(Class<?> type, List<String> subtypes = []) {
        ioClassModel(type, subtypes, true)
    }

    private IntegrationObjectClassModel ioClassModel(Class<?> type, List<String> subtypes = [], boolean root = false) {
        Stub(IntegrationObjectClassModel) {
            getCode() >> type.getSimpleName()
            getType() >> type
            getRoot() >> root
        }
    }

    ComposedTypeModel composedType(String code) {
        Stub(ComposedTypeModel) {
            getCode() >> code
        }
    }

    ItemModel itemModel(String type) {
        Stub(ItemModel) {
            getItemtype() >> type
        }
    }

    Object pojoModel(String type) {
        Stub(Object) {
            getClass().getName() >> type
        }
    }

    ComposedTypeModel composedTypeModel(String type) {
        Stub(ComposedTypeModel) {
            getCode() >> type
        }
    }

    private class ChildOrder extends Order{}
    private class GrandChildOrder extends ChildOrder{}
}
