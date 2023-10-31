/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.integrationservices.exception.ClassAttributeExecutionException
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class ClassAttributeValueGetterUnitTest extends JUnitPlatformSpecification {

    private static final def DOES_NOT_EXIST = "doesNotExist"
    private static final def ATTR_NAME = "some name"
    private static final def SOME_VALUE = "some value"
    private static final def IO_CLASS = new IntegrationObjectClassModel(type: CatalogModel)

    @Test
    def 'non-localized getValue throws exception when readMethod does not exist'() {
        given:
        def readMethod = DOES_NOT_EXIST
        def attributeName = ATTR_NAME
        def getter = getter(attributeName, readMethod)

        when:
        getter.getValue(new CatalogModel())

        then:
        def e = thrown ClassAttributeExecutionException
        e.message == "ReadMethod $readMethod from attribute $attributeName does not exist."
    }

    @Test
    def 'non-localized getValue throws exception when attribute does not exist'() {
        given:
        def attrName = DOES_NOT_EXIST
        def getter = getter(attrName)
        def model = new CatalogModel()

        when:
        getter.getValue(model)

        then:
        def e = thrown ClassAttributeExecutionException
        e.message == "Attribute $attrName for class ${model.getClass().name} does not exist."
    }

    @Test
    def 'non-localized getValue returns value when readMethod is present'() {
        given:
        def getter = getter(ATTR_NAME, "getId")

        and:
        def pojo = new CatalogModel(id: "some id")

        when:
        def value = getter.getValue(pojo)

        then:
        value == pojo.id
    }

    @Test
    def 'non-localized getValue returns value when attribute is present'() {
        given:
        def getter = getter("defaultCatalog")

        and:
        def pojo = new CatalogModel(defaultCatalog: true)

        when:
        def value = getter.getValue(pojo)

        then:
        value == pojo.defaultCatalog
    }

    @Test
    def 'localized getValue throws exception when readMethod does not exist'() {
        given:
        def readMethod = DOES_NOT_EXIST
        def attrName = ATTR_NAME
        def getter = getter(attrName, readMethod)

        when:
        getter.getValue(new CatalogModel(), Locale.FRENCH)

        then:
        def e = thrown ClassAttributeExecutionException
        e.message == "ReadMethod $readMethod from attribute $attrName does not exist."
    }

    @Test
    def 'localized getValue throws exception when attribute does not exist'() {
        given:
        def attrName = DOES_NOT_EXIST
        def getter = getter(attrName)
        def model = new CatalogModel()

        when:
        getter.getValue(model, Locale.FRENCH)

        then:
        def e = thrown ClassAttributeExecutionException
        e.message == "Attribute $attrName for class ${model.getClass().name} does not exist."
    }

    @Test
    def 'localized getValue returns value when readMethod with locale parameter is present'() {
        given:
        def getter = getter("something", "getName")

        and:
        def pojo = new CatalogModel()
        pojo.setName(SOME_VALUE, Locale.FRENCH)

        when:
        def value = getter.getValue(pojo, Locale.FRENCH)

        then:
        value == pojo.getName(Locale.FRENCH)
    }

    @Test
    def 'localized getValue returns value when attribute locale parameter is present'() {
        given:
        def getter = getter("name")

        and:
        def pojo = new CatalogModel()
        pojo.setName(SOME_VALUE, Locale.FRENCH)

        when:
        def value = getter.getValue(pojo, Locale.FRENCH)

        then:
        value == pojo.getName(Locale.FRENCH)
    }

    @Test
    def 'getValues throws exception when readMethod does not exist'() {
        given:
        def readMethod = DOES_NOT_EXIST
        def attrName = ATTR_NAME
        def getter = getter(attrName, readMethod)

        when:
        getter.getValues(new CatalogModel(), Locale.FRENCH)

        then:
        def e = thrown ClassAttributeExecutionException
        e.message == "ReadMethod $readMethod from attribute $attrName does not exist."
    }

    @Test
    def 'getValues throws exception when attribute does not exist'() {
        given:
        def attrName = DOES_NOT_EXIST
        def getter = getter(attrName)
        def model = new CatalogModel()

        when:
        getter.getValues(model, Locale.FRENCH)

        then:
        def e = thrown ClassAttributeExecutionException
        e.message == "Attribute $attrName for class ${model.getClass().name} does not exist."
    }

    @Test
    def 'getValues returns localized values when readMethod with multiple present locales parameter exists'() {
        given:
        def getter = getter(ATTR_NAME, "getName")

        and:
        def pojo = new CatalogModel()
        pojo.setName("Ni hao", Locale.CHINESE)
        pojo.setName("Hello", Locale.ENGLISH)
        pojo.setName("Hallo", Locale.GERMAN)

        when:
        def value = getter.getValues(pojo, Locale.ENGLISH, Locale.GERMAN, Locale.CHINESE)

        then:
        value == [(Locale.CHINESE): 'Ni hao', (Locale.ENGLISH): "Hello", (Locale.GERMAN): "Hallo"]
    }

    @Test
    def 'getValues returns localized values when attribute with multiple present locales parameter exists'() {
        given:
        def getter = getter("name")

        and:
        def pojo = new CatalogModel()
        pojo.setName("Ni hao", Locale.CHINESE)
        pojo.setName("Hello", Locale.ENGLISH)
        pojo.setName("Hallo", Locale.GERMAN)

        when:
        def value = getter.getValues(pojo, Locale.ENGLISH, Locale.GERMAN, Locale.CHINESE)

        then:
        value == [(Locale.CHINESE): 'Ni hao', (Locale.ENGLISH): "Hello", (Locale.GERMAN): "Hallo"]
    }

    @Test
    def 'getValues returns empty map when locale parameters are not provided'() {
        given:
        def getter = getter(ATTR_NAME, "getName")

        when:
        def value = getter.getValues(new CatalogModel())

        then:
        value.isEmpty()
    }

    private ClassAttributeValueGetter getter(String attrName, String readMethod = '') {
        def attr = new IntegrationObjectClassAttributeModel(
                integrationObjectClass: IO_CLASS, attributeName: attrName, readMethod: readMethod)
        new ClassAttributeValueGetter(attr)
    }
}
