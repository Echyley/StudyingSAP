/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocctests.test.groovy.webservicetests.v2.controllers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercefacades.order.CartFacade
import de.hybris.platform.commerceservices.order.CommerceCartModificationException
import de.hybris.platform.core.Registry
import de.hybris.platform.util.Config
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED
import static org.mockito.Mockito.doCallRealMethod
import static org.mockito.Mockito.doThrow

@ManualTest
@Unroll
class B2BOrdersControllerTest extends AbstractSpockFlowTest {
    static final String B2BADMIN_USERNAME = "mark.rivers.approval@rustic-retail-hw.com"
    static final String B2BADMIN_PASSWORD = "1234"

    static final String B2BCUSTOMER_USERNAME = "mark.rivers@rustic-hw.com"
    static final String B2BCUSTOMER_PASSWORD = "1234"

    static final String B2BCUSTOMER_C_USERNAME = "marie.dubois@rustic-hw.com"
    static final String B2BCUSTOMER_C_NAME = "Maria Dubois"
    static final String B2BCUSTOMER_C_PASSWORD = "1234"

    static final String B2B_UNIT_NAME = "Rustic Retail"

    static final String OCC_OVERLAPPING_PATHS_FLAG = "occ.rewrite.overlapping.paths.enabled"
    static final ENABLED_CONTROLLER_PATH = Config.getBoolean(OCC_OVERLAPPING_PATHS_FLAG, false) ? COMPATIBLE_CONTROLLER_PATH : CONTROLLER_PATH
    static final String CONTROLLER_PATH = "/users"
    static final String COMPATIBLE_CONTROLLER_PATH = "/orgUsers"
    static final ORDER_PATH = "/orders"
    static final UNIT_LEVEL_ORDER_PATH = "/orgUnits/orders"
    static final ORDER_ID = "0001"

    static final String SPY_ON_CART_FACADE = "spyOnCartFacade"

    def setup() {
        authorizeTrustedClient(restClient)
    }

    def cleanup() {
        final CartFacade cartFacade = Registry.getApplicationContext().getBean(SPY_ON_CART_FACADE, CartFacade.class);
        // it should be an spy of CartFacade
        doCallRealMethod().when(cartFacade).validateCartData()
    }
    def "B2B Customer tries to access unit-level orders"() {
        given: "a registered and logged in B2B customer without permission to view unit-level orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BCUSTOMER_C_USERNAME,
                'password': B2BCUSTOMER_C_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user requests to get unit-level orders"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'currentPage' : 0,
                        'pageSize'    : 20,
                        'fields'      : FIELD_SET_LEVEL_DEFAULT,
                ])

        then: "user gets an unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_UNAUTHORIZED
        }
    }

    def "B2B Customer tries to access unit-level order details"() {
        given: "a registered and logged in B2B customer without permission to view unit-level order details"
        def b2bUnitOrderViewer = [
                'id'      : B2BCUSTOMER_C_USERNAME,
                'password': B2BCUSTOMER_C_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user requests to get unit-level order details"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH + "/" + ORDER_ID,
                query: [
                        'currentPage' : 0,
                        'pageSize'    : 20,
                        'fields'      : FIELD_SET_LEVEL_DEFAULT,
                ])

        then: "user gets an unauthorized error"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_UNAUTHORIZED
        }
    }

    def "Should not be able to create cart from order from a B2C store"() {
        given: "a registered and logged in customer with B2B Admin role"
        def b2bAdminCustomer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bAdminCustomer)

        when: "A request is made to create a new cart from a given order"
        String path = getBasePath() + "/wsTestB2C" + ENABLED_CONTROLLER_PATH + '/' + B2BADMIN_USERNAME + '/cartFromOrder'
        HttpResponseDecorator response = restClient.post(
                path: path,
                query: [
                        "orderCode": "testOrder1",
                        "fields"   : "FULL"
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "request fails because a restricted B2B API endpoint is used from a B2C base-site"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_UNAUTHORIZED
        }
    }

    def "Should create a new cart from a given order"() {
        given: "a registered and logged in customer with B2B Admin role"
        def b2bAdminCustomer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bAdminCustomer)

        when: "A request is made to create a new cart from an existing order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + '/' + B2BADMIN_USERNAME + '/cartFromOrder',
                query: [
                        "orderCode": "testOrder1",
                        "fields"   : "FULL"
                ],
                contentType: JSON,
                requestContentType: URLENC
        )
        then: "An empty list of modifications is retrieved"
        with(response) {
            status == SC_CREATED
            data != ""
        }

        where:
        descriptor << [ENABLED_CONTROLLER_PATH]
    }

    def "B2B Customer creates a new order with payment type CARD"() {
        given: "a registered and logged in customer"
        def b2bCustomer = [
                'id'      : B2BCUSTOMER_USERNAME,
                'password': B2BCUSTOMER_PASSWORD
        ]
        authorizeCustomer(restClient, b2bCustomer)

        when: "he requests to create a new order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + '/' + b2bCustomer.id + ORDER_PATH,
                query: [
                        'cartId'      : "xyz",
                        'termsChecked': true,
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets the newly created order"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
        }
    }

    def "Returned orders contain purchase order number and cost center"() {
        given: "a registered and logged in customer"
        def b2bCustomer = [
                'id'      : B2BCUSTOMER_USERNAME,
                'password': B2BCUSTOMER_PASSWORD
        ]
        authorizeCustomer(restClient, b2bCustomer)

        when: "he requests to create new order"
        HttpResponseDecorator postResult = restClient.post(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + '/' + b2bCustomer.id + ORDER_PATH,
                query: [
                        'cartId'      : "ccpo",
                        'termsChecked': true,
                        'fields'      : FIELD_SET_LEVEL_FULL
                ])
        then: "expect an order"
        with(postResult) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
        }
        def orderCode = postResult.data.code

        when: "customer retrieves his orders"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/users/' + b2bCustomer.id + "/orders",
                contentType: JSON,
                requestContentType: URLENC)

        then: "all orders contain purchase order number and cost center"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            def order = data.orders.find { it.code == orderCode }
            order != null
            order.purchaseOrderNumber == "purchaseCCPO"
            order.costCenter.code == "Rustic_US"
        }
    }

    def "Returned unit-level orders contain uid of the customer and its unit and parent-unit uid"() {
        given: "a registered and logged in customer"
        def b2bCustomer = [
                'id'      : B2BCUSTOMER_C_USERNAME,
                'password': B2BCUSTOMER_C_PASSWORD
        ]
        authorizeCustomer(restClient, b2bCustomer)

        when: "customer requests to create new order"
        HttpResponseDecorator postResult = restClient.post(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + '/' + b2bCustomer.id + ORDER_PATH,
                query: [
                        'cartId'      : "tsd",
                        'termsChecked': true,
                        'fields'      : FIELD_SET_LEVEL_FULL
                ])
        then: "the order is created and returned as part of the response"
        with(postResult) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
        }
        def orderCode = postResult.data.code

        def b2bAdmin = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bAdmin)

        when: "customer retrieves the Unit-Level orders"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bAdmin.id + UNIT_LEVEL_ORDER_PATH,
                contentType: JSON,
                requestContentType: URLENC)

        then: "previously created order is present on the retrieved order list and contains uid of the correct customer and its unit and parent-unit uid"

        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            def order = data.orders.find { it.code == orderCode }
            order != null
            order.orgCustomer.uid == "marie.dubois@rustic-hw.com"
            order.orgUnit.uid == "Rustic_Retail"
            order.orgUnit.parentOrgUnit.uid == "Rustic"
        }
    }

    def "Returned unit-level order details contain uid of the customer and its unit and parent-unit uid"() {
        given: "a registered and logged in customer"
        def b2bCustomer = [
                'id'      : B2BCUSTOMER_C_USERNAME,
                'password': B2BCUSTOMER_C_PASSWORD
        ]
        authorizeCustomer(restClient, b2bCustomer)

        when: "customer requests to create new order"
        HttpResponseDecorator postResult = restClient.post(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + '/' + b2bCustomer.id + ORDER_PATH,
                query: [
                        'cartId'      : "ssd",
                        'termsChecked': true,
                        'fields'      : FIELD_SET_LEVEL_FULL
                ])
        then: "the order is created and returned as part of the response"
        with(postResult) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
        }
        def orderCode = postResult.data.code

        def b2bAdmin = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bAdmin)

        when: "customer retrieves the Unit-Level orders"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bAdmin.id + UNIT_LEVEL_ORDER_PATH + "/" + orderCode,
                contentType: JSON,
                requestContentType: URLENC)

        then: "previously created order is present on the retrieved order list and contains uid of the correct customer and its unit and parent-unit uid"

        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            def order = data
            order != null
            order.orgCustomer.uid == "marie.dubois@rustic-hw.com"
            order.orgUnit.uid == "Rustic_Retail"
            order.orgUnit.parentOrgUnit.uid == "Rustic"
        }
    }

    def "B2B Customer should fail to create a new order when card contains product, which is out of stock"() {
        given: "a registered and logged in customer"
        def b2bCustomer = [
                'id'      : B2BCUSTOMER_USERNAME,
                'password': B2BCUSTOMER_PASSWORD
        ]
        authorizeCustomer(restClient, b2bCustomer)

        when: "he requests to create a new order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + '/' + b2bCustomer.id + ORDER_PATH,
                query: [
                        'cartId'      : "bbb",
                        'termsChecked': true,
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets the newly created order"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_BAD_REQUEST
            data.errors[0].message == "Product [HW1210-9346] is currently out of stock"
            data.errors[0].type == "InsufficientStockError"
        }
    }

    def "InvalidCartException should be issued when cart validation encountered CommerceCartModificationException"() {
        given: "a registered and logged in customer"
        def b2bCustomer = [
                'id'      : B2BCUSTOMER_USERNAME,
                'password': B2BCUSTOMER_PASSWORD
        ]
        authorizeCustomer(restClient, b2bCustomer)

        and: "validateCartData is configured to fail"
        final CartFacade cartFacade = Registry.getApplicationContext().getBean(SPY_ON_CART_FACADE, CartFacade.class); // it should be an spy of defaultCartFacade
        doThrow(new CommerceCartModificationException("Error when validating a cart")).when(cartFacade).validateCartData()

        when: "he requests to create a new order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + '/' + b2bCustomer.id + ORDER_PATH,
                query: [
                        'cartId'      : "bbb",
                        'termsChecked': true,
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "he gets the newly created order"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_BAD_REQUEST
            data.errors[0].message == "The application has encountered an error"
            data.errors[0].type == "InvalidCartError"
        }
    }

    def "Return unit-level orders sorted by the buyer name if sorting byBuyer is applied"() {
        given: "a registered and logged in user with permission to view unit-orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user retrieves unit-level order history sorted by the buyer"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'currentPage' : 0,
                        'pageSize'    : 20,
                        'fields'      : FIELD_SET_LEVEL_DEFAULT,
                        'sort'        : "byBuyer",
                ])

        then: "orders of the retrieved order list are sorted by the buyers name"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.orders != null
            def orders_sorted_buyer = data.orders.sort(false){a,b ->
                a.orgCustomer.name <=> b.orgCustomer.name ?: b.placed <=> a.placed}
            data.orders == orders_sorted_buyer
        }
    }

    def "Return unit-level orders sorted by the buyer name descending if sorting byBuyerDesc is applied"() {
        given: "a registered and logged in user with permission to view unit-orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user retrieves unit-level order history sorted by the buyer descending"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'currentPage' : 0,
                        'pageSize'    : 20,
                        'fields'      : FIELD_SET_LEVEL_DEFAULT,
                        'sort'        : "byBuyerDesc",
                ])

        then: "orders of the retrieved order list are sorted by the buyer descending"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.orders != null
            def orders_sorted_buyer_desc = data.orders.sort(false){a,b ->
                b.orgCustomer.name <=> a.orgCustomer.name ?: b.placed <=> a.placed}
            data.orders == orders_sorted_buyer_desc
        }
    }

    def "Return unit-level orders sorted by the unit name if sorting byOrgUnit is applied"() {
        given: "a registered and logged in user with permission to view unit-orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user retrieves unit-level order history sorted by the unit"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'currentPage' : 0,
                        'pageSize'    : 20,
                        'fields'      : FIELD_SET_LEVEL_DEFAULT,
                        'sort'        : "byOrgUnit",
                ])

        then: "orders of the retrieved order list are sorted by the unit"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.orders != null
            def orders_sorted_unit = data.orders.sort(false){a,b ->
                a.orgUnit.name <=> b.orgUnit.name ?: b.placed <=> a.placed}
            data.orders == orders_sorted_unit
        }
    }

    def "Return unit-level orders sorted by the unit name descending if sorting byOrgUnitDesc is applied"() {
        given: "a registered and logged in user with permission to view unit-orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user retrieves unit-level order history sorted by the unit descending"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'currentPage' : 0,
                        'pageSize'    : 20,
                        'fields'      : FIELD_SET_LEVEL_DEFAULT,
                        'sort'        : "byOrgUnitDesc",
                ])

        then: "orders of the retrieved order list are sorted by the unit descending"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.orders != null
            def orders_sorted_unit_desc = data.orders.sort(false){a,b ->
                b.orgUnit.name <=> a.orgUnit.name ?: b.placed <=> a.placed}
            data.orders == orders_sorted_unit_desc
        }
    }

    def "Return unit-level orders sorted by the order number if sorting byOrderNumber is applied"() {
        given: "a registered and logged in user with permission to view unit-orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user retrieves unit-level order history sorted by the order number"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'currentPage' : 0,
                        'pageSize'    : 20,
                        'fields'      : FIELD_SET_LEVEL_DEFAULT,
                        'sort'        : "byOrderNumber",
                ],
                contentType: JSON,
                requestContentType: URLENC)

        then: "orders of the retrieved order list are sorted by the order number "
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.orders != null
            def orders_sorted_number = data.orders.sort(false){a,b ->
                a.code <=> b.code ?: b.placed <=> a.placed }
            data.orders == orders_sorted_number
        }
    }

    def "Return unit-level orders of specific user when the filter for the username is applied"() {
        given: "a registered and logged in user with permission to view unit-orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user retrieves unit-level order history for specific customer"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'filters'     : "::user:" + B2BCUSTOMER_C_NAME,
                ],
                contentType: JSON,
                requestContentType: URLENC)

        then: "only orders placed by specified customer are present in the retrieved order list"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.orders != null
            def orders_by_customer = data.orders.findAll { it.orgCustomer.name.contains(B2BCUSTOMER_C_NAME) }
            data.orders.size == orders_by_customer.size
        }
    }

    def "Return unit-level orders of specific unit when the filter for the unitname is applied"() {
        given: "a registered and logged in user with permission to view unit-orders"
        def b2bUnitOrderViewer = [
                'id'      : B2BADMIN_USERNAME,
                'password': B2BADMIN_PASSWORD
        ]
        authorizeCustomer(restClient, b2bUnitOrderViewer)

        when: "user retrieves unit-level order history for specific unit"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + ENABLED_CONTROLLER_PATH + "/" + b2bUnitOrderViewer.id + UNIT_LEVEL_ORDER_PATH,
                query: [
                        'filters'     : "::unit:" + B2B_UNIT_NAME,
                ],
                contentType: JSON,
                requestContentType: URLENC)

        then: "only orders of specified unit are present in the retrieved order list"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) {
                println(data)
            }
            status == SC_OK
            data.orders != null
            def orders_by_unit = data.orders.findAll{it.orgUnit.name.contains(B2B_UNIT_NAME)}
            data.orders.size == orders_by_unit.size
        }
    }
}
