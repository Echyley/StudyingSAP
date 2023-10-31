/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.order.events.SubmitOrderEvent
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

import java.beans.Introspector

@UnitTest
class PojoIntrospectorUnitTest extends JUnitPlatformSpecification {

    private static final def COMPOSED_ATTRIBUTE = "order"
    private static final def COLLECTION_ATTRIBUTE = "collectionType"
    private static final def PRIMITIVE_ATTRIBUTE = "stringType"
    private static final def NOT_FOUND_ATTRIBUTE = "NotFound"
    private static final def MAP_ATTRIBUTE_NAME = "mapValue"
    private static final def LIST_ATTRIBUTE = "listType"
    private static final def SET_ATTRIBUTE = "setType"
    private static final def NON_GENERIC_MAP_ATTRIBUTE = "mapNonGenericValue"

    private static final def READ_METHOD_NAME = "getOrder"
    private static final def MAP_READ_METHOD_NAME = "getMapValue"
    private static final def COLLECTION_READ_METHOD = "getCollectionType"
    private static final def PRIMITIVE_READ_METHOD = "getStringType"
    private static final def NOT_FOUND_METHOD = "NotFound"
    private static final def LIST_READ_METHOD = "getListType"
    private static final def SET_READ_METHOD = "getSetType"
    private static final def NO_MATTER_ATTRIBUTE = "NoMatter"
    private static final def LOCALE_READ_METHOD_NAME = "getLocaleOrder"
    private static final def WRITE_METHOD_NAME = "setOrder"
    private static final def NON_GENERIC_MAP_METHOD = "getMapNonGenericValue"
    private static final def EVENT_CLASS = TestPojo
    private static final def IO = new IntegrationObjectModel(code: 'TEST_IO')
    private static final def CLASS_1 = new IntegrationObjectClassModel(
            type: EVENT_CLASS, code: 'TEST_IO_ITEM', integrationObject: IO)

    private static final def BEAN_INFO = Introspector.getBeanInfo(EVENT_CLASS)
    private static final def NO_PARAMS = []
    private static final def ORDER_MODEL_PARAM = [OrderModel.class]

    @Test
    def "null method fails precondition"() {
        when:
        pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).getActualMethodReturnType(null)

        then:
        thrown IllegalArgumentException
    }

    @Test
    def "null property descriptor fails precondition"() {
        when:
        pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).getActualPropertyType(null)
        then:
        thrown IllegalArgumentException
    }

    @Test
    def "getAllProperties returns all the declared properties"() {
        expect:
        pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).getAllProperties()
                .containsAll(BEAN_INFO.getPropertyDescriptors())
    }

    @Test
    def "findProperty returns the optional of property descriptor for the given name"() {
        when:
        def optionalProp = pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).findProperty()

        then:
        optionalProp.present
        optionalProp.get().name == COMPOSED_ATTRIBUTE
    }

    @Test
    def "findProperty returns empty optional when given property name is not found"() {
        expect:
        pojoIntrospector([attributeName: "randomProperty"]).findProperty().empty
    }

    @Test
    def "findProperty returns the optional property descriptor of the attribute model in the context class"() {
        when:
        def optionalProp = pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).findProperty()

        then:
        optionalProp.present
        optionalProp.get().name == COMPOSED_ATTRIBUTE
    }

    @Test
    def "findProperty returns empty optional when property for the attribute model cannot be found"() {
        given:
        def introspector = pojoIntrospector([attributeName:"randomName"])

        expect:
        introspector.findProperty().empty
    }

    @Test
    def "findMethod(String,Object[]) returns the optional of method when the method name is #methodName and param types are #paramTypes"() {
        when:
        def optMethod = pojoIntrospector([readMethod: methodName]).findMethod(paramTypes)

        then:
        optMethod.present
        def method = optMethod.get()
        method.name == methodName
        method.parameterTypes == paramTypes

        where:
        methodName | paramTypes
        "getOrder" | NO_PARAMS as Object[]
        "setOrder" | ORDER_MODEL_PARAM as Object[]
    }

    @Test
    def "findMethod(String,Object[]) returns empty optional when #condition"() {
        expect:
        pojoIntrospector([readMethod: methodName]).findMethod(NO_PARAMS).empty

        where:
        methodName   | condition
        "randomName" | "method name not found"
        "setOrder"   | "method name found but param types doesn't match"
    }

    @Test
    def "findMethod(String) returns optional of method when method with given name found"() {
        when:
        def optMethod = pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).findMethod(READ_METHOD_NAME)

        then:
        optMethod.present
        optMethod.get().name == READ_METHOD_NAME
    }

    @Test
    def "findMethod(String) returns empty optional when name not found"() {
        expect:
        pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).findMethod("randomName").empty
    }

    @Test
    def "getReadMethod will look for attribute model read method name first"() {
        given:
        def introspector = pojoIntrospector([readMethod: READ_METHOD_NAME])

        when:
        def optMethod = introspector.getReadMethod()

        then:
        optMethod.present
        optMethod.get().name == READ_METHOD_NAME
    }

    @Test
    def "getReadMethod will use attribute model name to find read method if read method name is not provided"() {
        when:
        def optMethod = pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).getReadMethod()

        then:
        optMethod.present
        optMethod.get().name == READ_METHOD_NAME
    }

    @Test
    def "getReturnType returns the type of the read method correctly if it is #type"() {
        given:
        def introspector = pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE, readMethod: name])

        when:
        def optReturnType = introspector.getReturnType()

        then:
        optReturnType.present
        optReturnType.get() == returnType

        where:
        name              | returnType       | type
        READ_METHOD_NAME  | OrderModel.class | "not void"
        WRITE_METHOD_NAME | void             | "void"
    }

    @Test
    def "isLocalized is #returnValue when attribute is #type"() {
        given:
        def introspector = pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE, readMethod: name])

        expect:
        introspector.isLocalized() == returnValue

        where:
        name                    | returnValue | type
        READ_METHOD_NAME        | false       | "not localized"
        LOCALE_READ_METHOD_NAME | true        | "localized"
    }

    @Test
    def "getMapKeyType expects return #typeValue for #type"() {
        given:
        def introspector = pojoIntrospector([attributeName: attrName, readMethod: method])

        expect:
        introspector.getMapKeyType() == typeValue

        where:
        method                 | attrName                  | typeValue | type
        MAP_READ_METHOD_NAME   | NO_MATTER_ATTRIBUTE       | String    | "Map type"
        NON_GENERIC_MAP_METHOD | MAP_ATTRIBUTE_NAME        | Map       | "Map type"
        null                   | MAP_ATTRIBUTE_NAME        | String    | "Map type"
        null                   | NON_GENERIC_MAP_ATTRIBUTE | Map       | "Map type"
        READ_METHOD_NAME       | NO_MATTER_ATTRIBUTE       | null      | "Non Map Type"
        NOT_FOUND_METHOD       | NO_MATTER_ATTRIBUTE       | null      | "Not found Method"
        null                   | COMPOSED_ATTRIBUTE        | null      | "composed"
        null                   | SET_ATTRIBUTE             | null      | "set"
        null                   | LIST_ATTRIBUTE            | null      | "list"
        null                   | NOT_FOUND_ATTRIBUTE       | null      | "not found attribute"
    }

    @Test
    def "getMapValueType expects return #typeValue for #type"() {
        given:
        def introspector = pojoIntrospector([attributeName: attrName, readMethod: method])

        expect:
        introspector.getMapValueType() == typeValue

        where:
        method                 | attrName                  | typeValue | type
        MAP_READ_METHOD_NAME   | NO_MATTER_ATTRIBUTE       | Integer   | "Map type"
        null                   | MAP_ATTRIBUTE_NAME        | Integer   | "Map type"
        NON_GENERIC_MAP_METHOD | MAP_ATTRIBUTE_NAME        | Map       | "Map type"
        null                   | NON_GENERIC_MAP_ATTRIBUTE | Map       | "Map type"
        READ_METHOD_NAME       | NO_MATTER_ATTRIBUTE       | null      | "Non Map Type"
        NOT_FOUND_METHOD       | NO_MATTER_ATTRIBUTE       | null      | "Not found Method"
        null                   | COMPOSED_ATTRIBUTE        | null      | "composed"
        null                   | SET_ATTRIBUTE             | null      | "set"
        null                   | LIST_ATTRIBUTE            | null      | "list"
        null                   | NOT_FOUND_ATTRIBUTE       | null      | "not found attribute"
    }

    @Test
    def "getIntegrationObjectCode returns io code for the attribute model in the context of the introspector"() {
        expect:
        pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).getIntegrationObjectCode() == IO.code
    }

    @Test
    def "getIntegrationObjectItemCode returns io item code for the attribute model in the context of the introspector"() {
        expect:
        pojoIntrospector([attributeName: COMPOSED_ATTRIBUTE]).getIntegrationObjectItemCode() == CLASS_1.code
    }

    @Test
    def "isMap is #returnValue when method return type is #type"() {
        given:
        def introspector = pojoIntrospector([attributeName: attrName, readMethod: method])

        expect:
        introspector.isMap() == returnValue

        where:
        method                  | attrName                  | returnValue | type
        READ_METHOD_NAME        | NO_MATTER_ATTRIBUTE       | false       | "composed read method"
        NOT_FOUND_METHOD        | NO_MATTER_ATTRIBUTE       | false       | "not found read method"
        PRIMITIVE_READ_METHOD   | NO_MATTER_ATTRIBUTE       | false       | "primitive read method"
        COLLECTION_READ_METHOD  | NO_MATTER_ATTRIBUTE       | false       | "collection read method"
        LIST_READ_METHOD        | NO_MATTER_ATTRIBUTE       | false       | "list read method"
        SET_READ_METHOD         | NO_MATTER_ATTRIBUTE       | false       | "set read method"
        null                    | COMPOSED_ATTRIBUTE        | false       | "composed attribute"
        null                    | SET_ATTRIBUTE             | false       | "set attribute"
        null                    | LIST_ATTRIBUTE            | false       | "list attribute"
        null                    | NOT_FOUND_ATTRIBUTE       | false       | "not found attribute"
        null                    | PRIMITIVE_ATTRIBUTE       | false       | "primitive attribute"
        null                    | COLLECTION_ATTRIBUTE      | false       | "collection attribute"
        MAP_READ_METHOD_NAME    | NO_MATTER_ATTRIBUTE       | true        | "Map method"
        null                    | MAP_ATTRIBUTE_NAME        | true        | "Map attribute"
        NON_GENERIC_MAP_METHOD  | NO_MATTER_ATTRIBUTE       | true        | "Map method"
        null                    | NON_GENERIC_MAP_ATTRIBUTE | true        | "Map attribute"
    }

    @Test
    def "isCollection is #returnValue when method return type is #type"() {
        given:
        def introspector = pojoIntrospector([attributeName: attrName, readMethod: method])

        expect:
        introspector.isCollection() == returnValue

        where:
        method                 | attrName                  | returnValue | type
        READ_METHOD_NAME       | NO_MATTER_ATTRIBUTE       | false       | "composed"
        NOT_FOUND_METHOD       | NO_MATTER_ATTRIBUTE       | false       | "not found"
        PRIMITIVE_READ_METHOD  | NO_MATTER_ATTRIBUTE       | false       | "primitive"
        MAP_READ_METHOD_NAME   | NO_MATTER_ATTRIBUTE       | false       | "Map"
        null                   | COMPOSED_ATTRIBUTE        | false       | "composed"
        null                   | NOT_FOUND_ATTRIBUTE       | false       | "not found"
        null                   | PRIMITIVE_ATTRIBUTE       | false       | "primitive"
        null                   | SET_ATTRIBUTE             | true        | "set"
        COLLECTION_READ_METHOD | NO_MATTER_ATTRIBUTE       | true        | "collection"
        LIST_READ_METHOD       | NO_MATTER_ATTRIBUTE       | true        | "list"
        SET_READ_METHOD        | NO_MATTER_ATTRIBUTE       | true        | "set"
        null                   | COLLECTION_ATTRIBUTE      | true        | "collection"
    }

    private PojoIntrospector pojoIntrospector(final Map<String, String> params = [:]) {
        def classAttr = new IntegrationObjectClassAttributeModel(integrationObjectClass: CLASS_1, attributeName: params["attributeName"], readMethod: params["readMethod"])
        new PojoIntrospector(classAttr)
    }

    private static class TestPojo extends SubmitOrderEvent {
        private OrderModel localeOrder
        private Map<String, Integer> mapValue
        private Map<String, Integer> mapNonGenericValue
        private Collection<String> collectionType
        private List<String> listType
        private Set<OrderModel> setType
        private String stringType

        OrderModel getLocaleOrder(Locale l) { localeOrder }

        Map<String, Integer> getMapValue() { mapValue }

        Map getMapNonGenericValue() { mapNonGenericValue }

        Collection<String> getCollectionType() { collectionType }

        String getStringType() { stringType }

        List<String> getListType() { listType }

        Set<OrderModel> getSetType() { setType }
    }
}
