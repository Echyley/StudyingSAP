/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.c2l.CountryModel
import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.core.model.user.AddressModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.UserGroupModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.service.ItemTypeDescriptorService
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.outboundsync.job.InfoExpressionGenerator
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import org.junit.Test
import org.springframework.expression.common.TemplateParserContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import spock.lang.AutoCleanup

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject

@IntegrationTest
class InfoExpressionGeneratorIntegrationTest extends ServicelayerSpockSpecification {
    private static final def TEST_NAME = 'InfoExpressionGenerator'
    private static final def IO_CODE = "${TEST_NAME}_IO"
    private static final def SPEL_EXPRESSION_PARSER = new SpelExpressionParser()
    private static final def PARSER_CONTEXT = new TemplateParserContext()

    @AutoCleanup('cleanup')
    def io = integrationObject().withCode(IO_CODE)

    @Resource(name = 'itemTypeDescriptorService')
    ItemTypeDescriptorService typeDescriptorService
    @Resource(name = 'outboundSyncInfoExpressionGenerator')
    InfoExpressionGenerator generator

    @Test
    def 'info for root item with simple key contains key, type and root type'() {
        given:
        io.withItem(integrationObjectItem().withCode('Customer').root()
                .withAttribute(integrationObjectItemAttribute('uid').unique()))
                .build()
        and:
        def item = new CustomerModel(uid: 'jsmith')

        when:
        def json = evaluateExpession item

        then:
        with(json) {
            getString('type') == item.itemtype
            getString('key') == item.uid
            getString('rootType') == item.itemtype
        }
    }

    @Test
    def 'info for root item with optional simple key contains key even if the key attribute is not set'() {
        given:
        io.withItem(integrationObjectItem().withCode('Customer').root()
                .withAttribute(integrationObjectItemAttribute('uid').unique()))
                .build()
        and:
        def item = new CustomerModel()

        when:
        def json = evaluateExpession item

        then:
        with(json) {
            getString('key') == ''
            getString('type') == item.itemtype
            getString('rootType') == item.itemtype
        }
    }

    @Test
    def 'info for root item with complex key contains key, type and root type when all items are present'() {
        given:
        io
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute('key').withQualifier('publicKey').unique()))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('paymentAddress')
                                .withQualifier('defaultPaymentAddress')
                                .withReturnItem('Address')
                                .unique()))
                .build()
        and:
        def address = new AddressModel(publicKey: 'addr1')
        def customer = new CustomerModel(uid: 'jsmith', defaultPaymentAddress: address)

        when:
        def json = evaluateExpession customer

        then:
        with(json) {
            getString('key') == "$address.publicKey|$customer.uid"
            getString('type') == customer.itemtype
            getString('rootType') == customer.itemtype
        }
    }

    @Test
    def 'info for root item with complex key contains key even when nested key item is absent'() {
        given:
        io
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute('key').withQualifier('publicKey').unique()))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('paymentAddress')
                                .withQualifier('defaultPaymentAddress')
                                .withReturnItem('Address')
                                .unique()))
                .build()
        and:
        def customer = new CustomerModel(uid: 'jsmith')

        when:
        def json = evaluateExpession customer

        then:
        with(json) {
            getString('key') == "|$customer.uid"
            getString('type') == customer.itemtype
            getString('rootType') == customer.itemtype
        }
    }

    @Test
    def 'info for non-root item without a path to root type has no root type'() {
        given: 'Country does not have reference to the root item in this IO'
        io
                .withItem(integrationObjectItem().withCode('Country')
                        .withAttribute(integrationObjectItemAttribute('code').withQualifier('isocode').unique()))
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute('key').withQualifier('publicKey').unique())
                        .withAttribute(integrationObjectItemAttribute('customer').withQualifier('owner').withReturnItem('Customer').unique())
                        .withAttribute(integrationObjectItemAttribute('country').withReturnItem('Country')))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('addresses').withReturnItem('Address')))
                .build()
        and:
        def country = new CountryModel(isocode: 'a value')

        when:
        def json = evaluateExpession country

        then:
        with(json) {
            getString('type') == country.itemtype
            getString('key') == country.isocode
            !exists('rootType')
        }
    }

    @Test
    def 'info for non-root item with a path to root item has root type, and key, and type'() {
        given: 'non-root type Address in this IO has path to the root type via its "customer" attribute'
        io
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute('key').withQualifier('publicKey').unique())
                        .withAttribute(integrationObjectItemAttribute('customer').withQualifier('owner').withReturnItem('Customer').unique()))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('defaultPaymentAddress').withReturnItem('Address')))
                .build()
        and:
        def customer = new CustomerModel(uid: 'jsmith')
        def address = new AddressModel(publicKey: 'addr1', owner: customer)
        customer.defaultPaymentAddress = address

        when:
        def json = evaluateExpession address

        then:
        with(json) {
            getString('type') == address.itemtype
            getString('key') == "$address.publicKey|$customer.uid"
            getString('rootType') == customer.itemtype
        }
    }

    @Test
    def 'info for non-root item with optional key attribute of root type has integration key even when root item is not assigned'() {
        given:
        io
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute('key').withQualifier('publicKey').unique())
                        .withAttribute(integrationObjectItemAttribute('customer').withQualifier('owner').withReturnItem('Customer').unique()))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('addresses').withReturnItem('Address')))
                .build()
        and: 'Address does not have key attribute pointing to the root type set'
        def address = new AddressModel(publicKey: 'addr1')

        when:
        def json = evaluateExpession address

        then:
        with(json) {
            getString('key') == "$address.publicKey|"
            getString('type') == address.itemtype
            getString('rootType') == ''
        }
    }

    @Test
    def 'info for non-root item with many-to-many relation to the root item has root type when a root item is present'() {
        given:
        io
                .withItem(integrationObjectItem().withCode('UserGroup')
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('members').withReturnItem('Customer')))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('groups').withReturnItem('UserGroup')))
                .build()
        and:
        def group = new UserGroupModel(uid: 'privileged')
        def customerOne = new CustomerModel(uid: 'jsmith', groups: [group])
        def customerTwo = new CustomerModel(uid: 'jdoe', groups: [group])
        group.members = [customerOne, customerTwo]

        when:
        def json = evaluateExpession group

        then:
        with(json) {
            getString('key') == group.uid
            getString('type') == group.itemtype
            getString('rootType') == customerOne.itemtype
        }
    }

    @Test
    def 'info for non-root item with many-to-many relation to the root item has no root type when the root item is absent'() {
        given:
        io
                .withItem(integrationObjectItem().withCode('UserGroup')
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('members').withReturnItem('Customer')))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('groups').withReturnItem('UserGroup')))
                .build()
        and:
        def group = new UserGroupModel(uid: 'empty', members: [])

        when:
        def json = evaluateExpession group

        then:
        with(json) {
            getString('key') == group.uid
            getString('type') == group.itemtype
            getString('rootType') == ''
        }
    }

    @Test
    def 'info has no key if key attributes in the integration object form a loop'() {
        given: 'there is a loop between Customer and Address items key attributes'
        io
                .withItem(integrationObjectItem().withCode('Address')
                        .withAttribute(integrationObjectItemAttribute('key').withQualifier('publicKey').unique())
                        .withAttribute(integrationObjectItemAttribute('customer').withQualifier('owner').withReturnItem('Customer').unique()))
                .withItem(integrationObjectItem().withCode('Customer').root()
                        .withAttribute(integrationObjectItemAttribute('uid').unique())
                        .withAttribute(integrationObjectItemAttribute('paymentAddress')
                                .withQualifier('defaultPaymentAddress')
                                .withReturnItem('Address')
                                .unique()))
                .build()
        and:
        def customer = new CustomerModel(uid: 'jsmith')
        def address = new AddressModel(publicKey: 'addr1', owner: customer)
        customer.defaultPaymentAddress = address

        when:
        def json = evaluateExpession customer

        then:
        with(json) {
            !exists('key')
            getString('type') == customer.itemtype
            getString('rootType') == customer.itemtype
        }
    }

    @Test
    def 'info contains epoch time for date key attributes'() {
        given:
        io
                .withItem(integrationObjectItem().withCode('Order')
                        .withAttribute(integrationObjectItemAttribute().withName('code').unique())
                        .withAttribute(integrationObjectItemAttribute().withName('date').unique()))
                .build()
        and:
        def order = new OrderModel(code: 'ord1', date: new Date())

        when:
        def json = evaluateExpession order

        then:
        json.getString('key') == "$order.code|$order.date.time"
    }

    JsonObject evaluateExpession(ItemModel item) {
        def expr = generator.generateInfoExpression typeDescriptor(item.itemtype)
        String result = SPEL_EXPRESSION_PARSER.parseExpression(expr, PARSER_CONTEXT).getValue(item)
        JsonObject.createFrom result
    }

    TypeDescriptor typeDescriptor(String itemCode) {
        typeDescriptorService.getTypeDescriptor(IO_CODE, itemCode)
                .orElseThrow { new IllegalStateException('IO is not created') }
    }
}
