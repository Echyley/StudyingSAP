/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapdigitalpaymentocctests.test.groovy.webservicetests.v2.controllers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import de.hybris.platform.core.Registry
import de.hybris.platform.sapdigitalpaymentocc.facade.SapDpWebServicesPaymentFacade
import de.hybris.platform.sapdigitalpaymentocc.facade.impl.DefaultSapDpWebServicesPaymentFacade
import groovyx.net.http.HttpResponseDecorator
import org.junit.AfterClass
import org.junit.BeforeClass
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class DigitalPaymentsControllerTest extends AbstractCartTest {

    @BeforeClass
    static void initMockDigitalPayment() {
        final SapDpWebServicesPaymentFacade dpPaymentFacade = Registry.getApplicationContext().getBean("sapDpWebServicesPaymentFacade", SapDpWebServicesPaymentFacade.class)

        if (dpPaymentFacade instanceof DefaultSapDpWebServicesPaymentFacade) {
            // Set mock digital payments service
            final SapDigitalPaymentService mockSapDpService = Registry.getApplicationContext().getBean("mockSapDigitalPaymentService", SapDigitalPaymentService.class)
            def defaultDpPaymentFacade = (DefaultSapDpWebServicesPaymentFacade) dpPaymentFacade
            defaultDpPaymentFacade.setSapDpService(mockSapDpService)
        }
    }

    @AfterClass
    static void cleanMockDigitalPayment() {
        final SapDpWebServicesPaymentFacade dpPaymentFacade = Registry.getApplicationContext().getBean("sapDpWebServicesPaymentFacade", SapDpWebServicesPaymentFacade.class)

        if (dpPaymentFacade instanceof DefaultSapDpWebServicesPaymentFacade) {
            // Set default digital payments service
            final SapDigitalPaymentService defaultSapDpService = Registry.getApplicationContext().getBean("defaultSapDigitalPaymentService", SapDigitalPaymentService.class)
            def defaultDpPaymentFacade = (DefaultSapDpWebServicesPaymentFacade) dpPaymentFacade
            defaultDpPaymentFacade.setSapDpService(defaultSapDpService)
        }
    }

    def "Retrieve post url from digital payments"() {
        given: "a customer with cart ready for ordering"
        def (customer, cart) = createAndAuthorizeCustomerWithCart(restClient, format, getBasePathWithIntegrationSite())
        addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 1, format, getBasePathWithIntegrationSite())

        when: "customer sends requests to get details for digital-payments payment request"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/payment/digitalPayments/request',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                contentType: format,
                requestContentType: URLENC
        )

        then: "post url is successfully retrieved"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors))
                println(data)

            status == SC_OK
            isNotEmpty(data.postUrl)
            def parameter = data.parameters.entry
            parameter.find { it.key == 'session_id' }
            parameter.find { it.key == 'signature' }
        }

        where:
        format << [JSON]
    }

    def "Customer create payment info in extended digital payment flow : #format"() {
        given: "a customer with cart ready for ordering"
        def (customer, cart) = createAndAuthorizeCustomerWithCart(restClient, format, getBasePathWithIntegrationSite())
        addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480, 1, format, getBasePathWithIntegrationSite())
        def address = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
        addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480)
        setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, format)

        when: "customer sends requests to get details for digital-payments payment request"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/payment/digitalPayments/request',
                query: ['fields': FIELD_SET_LEVEL_FULL],
                contentType: format,
                requestContentType: URLENC
        )
        response.status == SC_OK
        def parameter = response.data.parameters.entry
        def sessionId = parameter.find { it.key == 'session_id' }.value
        def signature = parameter.find { it.key == 'signature' }.value

        and: "customer send create subscription request to digital payments"
        response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cart.code + '/payment/digitalPayments/response',
                query: ['fields': FIELD_SET_LEVEL_FULL, 'sid': sessionId, 'sign': signature],
                contentType: format,
                requestContentType: URLENC
        )

        then: "payment info should be created"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors))
                println(data)

            status == SC_OK
            data.saved == true
            data.defaultPayment == false
            data.accountHolderName == 'Some Name'
            data.cardNumber == '************7651'
            data.cardType.code == 'visa'
            data.expiryYear == '2026'
            Integer.parseInt(data.expiryMonth) == 05
            data.billingAddress.line1 == CUSTOMER_ADDRESS_LINE1
            data.billingAddress.line2 == CUSTOMER_ADDRESS_LINE2
            data.billingAddress.postalCode == CUSTOMER_ADDRESS_POSTAL_CODE
            data.billingAddress.town == CUSTOMER_ADDRESS_TOWN
            data.billingAddress.country.isocode == CUSTOMER_ADDRESS_COUNTRY_ISO_CODE
        }
        where:
        format << [JSON]
    }

}

