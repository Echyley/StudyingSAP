/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.XmlObject
import de.hybris.platform.odata2services.util.Odata2ServicesEssentialData
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.ClassRule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject

@IntegrationTest
class ODataFacadeEntityKeySchemaGenerationIntegrationTest extends ServicelayerSpockSpecification {
    static def TEST_NAME = 'ODataFacadeEntityKeySchemaGeneration'
    static def IO = "${TEST_NAME}_TestIO"

    @Shared
    @ClassRule
    Odata2ServicesEssentialData essentialData = Odata2ServicesEssentialData.odata2ServicesEssentialData()
    @AutoCleanup('cleanup')
    IntegrationObjectModelBuilder integrationObject = integrationObject().withCode(IO)

    @Resource(name = "defaultODataFacade")
    ODataFacade facade

    @Test
    def 'reports an error when a type system key attribute is missing in the item definition'() {
        given: 'IO item Product is defined without attributes for its keys in the type system, e.g. code, catalogVersion, etc'
        integrationObject.withItem(integrationObjectItem().withCode('Product')
                .withAttribute(integrationObjectItemAttribute().withName('name')))
                .build()

        when: 'metadata is requested'
        def response = facade.handleGetSchema request()

        then: 'an error is reported'
        response.status == HttpStatusCodes.BAD_REQUEST
        def xml = XmlObject.createFrom response.entityAsStream
        xml.get('/error/code') == 'invalid_key_definition'
        xml.get('/error/message').contains 'Product'
    }

    @Test
    def 'reports an error when key attribute reference forms a loop'() {
        given: 'a key attribute reference path forms a loop back to the item'
        integrationObject
                .withItem(integrationObjectItem().withCode('Address'))
                .withItem(integrationObjectItem().withCode('Customer')
                        .withAttribute(integrationObjectItemAttribute('uid'))
                        .withAttribute(integrationObjectItemAttribute('defaultPaymentAddress').unique().withReturnItem('Address')))
                .build()
        IntegrationTestUtil.importImpEx(
                '$item=integrationObjectItem(integrationObject(code), code)',
                '$descriptor=attributeDescriptor(enclosingType(code), qualifier)',
                '$references=returnIntegrationObjectItem(integrationObject(code), code)',
                'INSERT_UPDATE IntegrationObjectItemAttribute; $item[unique = true]; attributeName[unique = true]; $descriptor  ; $references ; unique',
                "                                            ; $IO:Address         ; owner                       ; Address:owner; $IO:Customer; true")

        when: 'metadata is requested'
        def response = facade.handleGetSchema request()

        then: 'an error is reported'
        response.status == HttpStatusCodes.BAD_REQUEST
        def xml = XmlObject.createFrom response.entityAsStream
        xml.get('/error/code') == 'misconfigured_attribute'
        xml.get('/error/message').contains 'circular'
    }

    @Test
    def 'generates simple key for an entity type'() {
        given: 'an item has only a simple key defined'
        integrationObject
                .withItem(integrationObjectItem().withCode('Unit')
                        .withAttribute(integrationObjectItemAttribute().withName('code')))
                .build()

        when: 'metadata is requested'
        def response = facade.handleGetSchema request()

        then: 'the key is generated'
        response.status == HttpStatusCodes.OK
        def xml = XmlObject.createFrom response.entityAsStream
        xml.get('//EntityType/Key/PropertyRef/@Name') == 'integrationKey'
        xml.get('//Property[@Name="code"]/@Nullable') == 'false'
        xml.get('//Property[@Name="code"]/@IsUnique') == 'true'
        xml.get('//Property[@Name="integrationKey"]/@Nullable') == 'false'
        xml.exists('//Property[@Name="integrationKey"]/@Alias')
        !xml.exists('//Property[@Name="integrationKey"]/@IsUnique')
    }

    /**
     * In Simple Terms this test attempts to create the Key a AttributeReferencePath with the attributeChain including the following IntegrationObjectItem codes:
     *
     * <ol>
     *     <li>  D -> B  </li>
     *     <li>  C -> B  </li>
     * </ol>
     * where, B is Address, D is Customer.defaultPaymentAddress and C is Customer.defaultShipmentAddress
     * See STOUT-2493 and/or STOUT-2528 for details
     */
    @Test
    def "generates metadata when item has key with same entityType in independent attribute reference paths"() {
        given:
        integrationObject
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute().withName('email').unique()))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid'))
                        .withAttribute(integrationObjectItemAttribute('defaultPaymentAddress').unique().withReturnItem('Address'))
                        .withAttribute(integrationObjectItemAttribute('defaultShipmentAddress').unique().withReturnItem('Address')))
                .build()

        when:
        def response = facade.handleGetSchema request()

        then:
        response.status == HttpStatusCodes.OK
        def xml = XmlObject.createFrom response.entityAsStream
        xml.get('//EntityType/Key/PropertyRef/@Name') == 'integrationKey'
    }

    def request() {
        ODataFacadeTestUtils.createContext ODataRequestBuilder.oDataGetRequest()
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(IO)
                        .withRequestPath('$metadata'))
    }
}
