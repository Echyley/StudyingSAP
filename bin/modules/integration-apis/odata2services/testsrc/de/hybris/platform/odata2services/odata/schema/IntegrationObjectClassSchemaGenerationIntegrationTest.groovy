/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel
import de.hybris.platform.catalog.enums.ArticleApprovalStatus
import de.hybris.platform.catalog.model.CompanyModel
import de.hybris.platform.core.model.order.OrderEntryModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.product.UnitModel
import de.hybris.platform.core.model.test.TestItemModel
import de.hybris.platform.core.model.user.AddressModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.TestPojo
import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.IntegrationObjectDescriptorService
import de.hybris.platform.odata2services.odata.ODataSchema
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyException
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.util.TaxValue
import org.junit.Test
import spock.lang.AutoCleanup

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectClassAttributeModelBuilder.integrationObjectClassAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectClassModelBuilder.integrationObjectClass
import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem

@IntegrationTest
class IntegrationObjectClassSchemaGenerationIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_IO = 'IntegrationObjectClassSchemaGeneration_IO'
    private static final String NULLABLE = 'Nullable'
    private static final String IS_UNIQUE = 's:IsUnique'

    @AutoCleanup('cleanup')
    def ioBuilder = IntegrationObjectModelBuilder.integrationObject().withCode(TEST_IO)

    @Resource(name = 'oDataSchemaGenerator')
    SchemaGenerator generator

    @Resource(name = "integrationObjectDescriptorService")
    IntegrationObjectDescriptorService descriptorService

    @Test
    void 'schema contains entity types'() {
        given: 'integration object contains integration object class "Unit" '
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('Unit')
                        .withType(UnitModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(true)))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then:
        with(schema) {
            entityTypeNames == ['Unit']
            defaultEntityContainer.entitySetTypes == ['Unit']
        }
        and: 'schema contains no integration key'
        def unitType = schema.getEntityType 'Unit'
        unitType.keyProperties.contains('integrationKey')
        unitType.propertyNames.contains('integrationKey')
    }

    @Test
    void 'schema contains platform primitive type properties'() {
        given: 'integration object contains common primitive type properties'
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('TestItem')
                        .withType(TestItemModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('integer')
                                .withUnique(true))
                        .withAttribute(integrationObjectClassAttribute('double'))
                        .withAttribute(integrationObjectClassAttribute('character'))
                        .withAttribute(integrationObjectClassAttribute('string'))
                        .withAttribute(integrationObjectClassAttribute('boolean'))
                        .withAttribute(integrationObjectClassAttribute('date')))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then:
        with(schema) {
            entityTypeNames.containsAll(['TestItem'])
            getEntityType('TestItem').propertyNames.containsAll(['integer',
                                                                 'double',
                                                                 'character',
                                                                 'string',
                                                                 'boolean',
                                                                 'date'])
        }
    }

    @Test
    void 'schema contains java primitive type properties'() {
        given: 'integration object contains common primitive type properties'
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('Pojo')
                        .withType(TestPojo)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('long').unique())
                        .withAttribute(integrationObjectClassAttribute('int'))
                        .withAttribute(integrationObjectClassAttribute('double'))
                        .withAttribute(integrationObjectClassAttribute('char'))
                        .withAttribute(integrationObjectClassAttribute('byte'))
                        .withAttribute(integrationObjectClassAttribute('short'))
                        .withAttribute(integrationObjectClassAttribute('boolean'))
                        .withAttribute(integrationObjectClassAttribute('float')))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then:
        with(schema) {
            entityTypeNames.containsAll(['Pojo'])
            getEntityType('Pojo').propertyNames.containsAll(
                    ['long', 'int', 'double', 'char', 'byte', 'boolean', 'short', 'float'])
        }
    }

    @Test
    void 'schema contains enum properties'() {
        given: 'integration object contains an enum attribute "approvalStatus" '
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('Product')
                        .withType(ProductModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(true))
                        .withAttribute(integrationObjectClassAttribute('approvalStatus')
                                .withReadMethod('getApprovalStatus')
                                .withReturnClassCode('ArticleApprovalStatus')))
                .withClass(integrationObjectClass()
                        .withCode('ArticleApprovalStatus')
                        .withType(ArticleApprovalStatus)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(true)))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then:
        with(schema) {
            entityTypeNames.containsAll(['Product', 'ArticleApprovalStatus'])
            containsAssociationBetween('Product', 'ArticleApprovalStatus')
            defaultEntityContainer.entitySetTypes.containsAll(['Product', 'ArticleApprovalStatus'])
            defaultEntityContainer.associationSetNames.contains 'Product_ArticleApprovalStatuses'
            associations.collect { it.name }.contains 'FK_Product_approvalStatus'
        }
    }

    @Test
    void 'schema contains composed type properties'() {
        given: 'integration object contains integration object class "Product" with a composed type "unit" '
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('Product')
                        .withType(ProductModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(true))
                        .withAttribute(integrationObjectClassAttribute('unit')
                                .withReadMethod('getUnit')
                                .withReturnClassCode('Unit')))
                .withClass(integrationObjectClass()
                        .withCode('Unit')
                        .withType(UnitModel)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(true)))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then:
        with(schema) {
            entityTypeNames.containsAll(['Product', 'Unit'])
            containsAssociationBetween('Product', 'Unit')
            getEntityType('Product').navigationPropertyNames == ['unit']
            defaultEntityContainer.containsAssociationSetBetween('Product', 'Unit')
        }
    }

    @Test
    void 'schema contains primitive type collection properties'() {
        given: 'integration object contains a primitive type collection attribute "entryGroupNumbers" '
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('OrderEntry')
                        .withType(OrderEntryModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('entryNumber')
                                .withReadMethod('getEntryNumber')
                                .withUnique(true))
                        .withAttribute(integrationObjectClassAttribute('entryGroupNumbers')
                                .withReadMethod('getEntryGroupNumbers')))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then:
        with(schema) {
            entityTypeNames.containsAll(['OrderEntry', 'Integer'])
            containsAssociationBetween 'OrderEntry', 'Integer'
            defaultEntityContainer.entitySetTypes.contains 'Integer'
            defaultEntityContainer.associationSetNames.contains 'OrderEntry_Integers'
            associations.collect { it.name }.contains 'FK_OrderEntry_entryGroupNumbers'
        }
    }

    @Test
    void 'schema contains composed type collection properties'() {
        given: 'integration object contains a composed type collection attribute "addresses" '
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('Company')
                        .withType(CompanyModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('Id')
                                .withReadMethod('getId')
                                .withUnique(true))
                        .withAttribute(integrationObjectClassAttribute('addresses')
                                .withReadMethod('getAddresses')
                                .withReturnClassCode('Address')))
                .withClass(integrationObjectClass()
                        .withCode('Address')
                        .withType(AddressModel)
                        .withAttribute(integrationObjectClassAttribute('email')
                                .withReadMethod('getEmail')
                                .withUnique(true)))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then:
        with(schema) {
            entityTypeNames.containsAll(['Company', 'Address'])
            containsAssociationBetween 'Company', 'Address'
            defaultEntityContainer.entitySetTypes.contains 'Address'
            defaultEntityContainer.associationSetNames.contains 'Company_Addresses'
            associations.collect { it.name }.contains 'FK_Company_addresses'
        }
    }

    @Test
    void 'schema contains map type properties'() {
        given: 'integration object contains a Map attribute "additionalProperties" with primitive key/value'
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('ConsumedDestination')
                        .withType(ConsumedDestinationModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('id')
                                .withReadMethod('getId')
                                .withUnique(true))
                        .withAttribute(integrationObjectClassAttribute('additionalProperties')
                                .withReadMethod('getAdditionalProperties')))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then: 'schema contains the map type for the ConsumedDestination type'
        schema.entityTypeNames.containsAll(['String2StringMap', 'ConsumedDestination'])
        def mapType = schema.getEntityType('String2StringMap')
        def consumedDestinationType = schema.getEntityType('ConsumedDestination')

        and: 'the Map type contains all expected properties'
        mapType.propertyNames.containsAll(['key', 'value'])
        with(mapType.getAnnotatableProperty('key')) {
            annotationNames.containsAll(NULLABLE, IS_UNIQUE)
            getAnnotation(NULLABLE).get().text == 'false'
            getAnnotation(IS_UNIQUE).get().text == 'true'
        }
        with(mapType.getAnnotatableProperty('value')) {
            annotationNames.contains(NULLABLE)
            getAnnotation(NULLABLE).get().text == 'true'
        }

        and: 'the Map type key consists of the key property'
        mapType.getKeyProperties() == ['key']

        and: 'type ConsumedDestination contains a navigation property referring to its Map attributes'
        consumedDestinationType.navigationPropertyNames == ['additionalProperties']

        and: 'the schema contains association between the ConsumedDestination type and additionalProperties'
        schema.containsAssociationBetween('ConsumedDestination', 'String2StringMap')
        schema.associations.name.containsAll(['FK_ConsumedDestination_additionalProperties'])
        schema.defaultEntityContainer.containsAssociationSetBetween('ConsumedDestination', 'String2StringMap')
    }

    @Test
    void 'schema cannot be generated when no unique attributes are present for an IntegrationObjectClass'() {
        given: 'integration object contains integration object class "Unit" '
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('Unit')
                        .withType(UnitModel.class)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(false)))
                .build()

        when:
        generator.generate(integrationObjectTypes())

        then:
        def e = thrown(MissingKeyException)
        e.message.contains('There is no valid integration key defined for the current entityType [Unit]')
    }

    @Test
    void 'schema cannot be generated when an IO has been modeled with integration object items and integration object classes'() {
        given: 'integration object contains integration object class "Unit" and integration object item "Catalog"'
        ioBuilder
                .withClass(integrationObjectClass()
                        .withExcludedValidators(['defaultIntegrationObjectClassAndItemNotAllowedInterceptor'])
                        .withCode('Unit')
                        .withType(UnitModel.class)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withUnique(false)))
                .withItem(integrationObjectItem()
                        .withExcludedValidators(['defaultIntegrationObjectItemAndClassNotAllowedInterceptor'])
                        .withCode('Catalog')
                        .withRoot(true)
                        .withAttribute(integrationObjectItemAttribute('id').withQualifier('id')))
                .build()

        when:
        generator.generate(integrationObjectTypes())

        then:
        def e = thrown(IntegrationObjectItemAndClassConflictException)
        e.message.contains("Integration Object '$TEST_IO' has both IntegrationObjectItem(s) and " +
                "IntegrationObjectClass(es) associated. This combination is incompatible and one of the " +
                "types must be removed.")
    }

    @Test
    void 'Schema can be generated with an atomic type IntegrationObjectClass that is not the root'() {
        ioBuilder
                .withClass(integrationObjectClass()
                        .withCode('Order')
                        .withType(OrderModel)
                        .withRoot(true)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(true))
                        .withAttribute(integrationObjectClassAttribute('totalTaxValues')
                                .withReadMethod('getTotalTaxValues')
                                .withReturnClassCode('TaxValue')))
                .withClass(integrationObjectClass()
                        .withCode('TaxValue')
                        .withType(TaxValue.class)
                        .withRoot(false)
                        .withAttribute(integrationObjectClassAttribute('code')
                                .withReadMethod('getCode')
                                .withUnique(true)))
                .build()

        when:
        def schema = ODataSchema.from generator.generate(integrationObjectTypes())

        then: 'schema contains the expected entity types'
        schema.entityTypeNames.containsAll(['TaxValue', 'Order'])
    }

    private Collection<TypeDescriptor> integrationObjectTypes() {
        descriptorService.retrieve(TEST_IO)?.itemTypeDescriptors
    }
}
