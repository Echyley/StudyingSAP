/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerinterestsocctests.controllers


import static groovyx.net.http.ContentType.JSON

import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import com.fasterxml.jackson.databind.ObjectMapper
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.users.AbstractUserTest
import spock.lang.Unroll

@ManualTest
@Unroll
class CustomerInterestsOccControllerTests extends AbstractUserTest {

    static final DUMMY_CUSTOMER = ["id": "dummyuserocctests@dummy.com", "password": "1234"]
    static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
    static final AS_AGENT = ["id": "asagent", "password": "1234"]


    private final ObjectMapper objectMapper = new ObjectMapper();

    def "#user should be able to get product interests  details withId: #withId and para: #queryParameters"()
    {
        given: "a registered and logged in #user"
        if (credential == null)
        {
            authorizationMethod(restClient)
        }
        else
        {
            authorizationMethod(restClient, credential)
        }

        when: "he requests to get product interests"
        def response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/productinterests" ,
                query: queryParameters,
                contentType: JSON,
                requestContentType: JSON)

        then: "return product interests"
        if (needTotal)
        {
            print(needTotal);
        }
        with(response) {
            status == SC_OK
            data.results.size == expectedResult
            data.results[0].productInterestEntry[0].keySet().size() == 3
            data.pagination.count == pageSize
        }

        where:
        user                     | withId            | credential     | authorizationMethod            | queryParameters                      | expectedResult | productCode | needTotal | pageSize
        "dummy"                  | "current"         | DUMMY_CUSTOMER | this.&authorizeCustomer        | null                                 | 8              | "product5"  | false     | 10
        "dummy"                  | "current"         | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['notificationtype': "NOTIFICATION"] | 8              | "product5"  | false     | 10
        "dummy"                  | "current"         | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['productCode': "product1"]          | 1              | "product1"  | false     | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['productCode': "product1"]          | 1              | "product1"  | false     | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['pageSize': "5"]                    | 5              | "product5"  | false     | 5
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['pageSize': "-1"]                   | 8              | "product5"  | false     | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['pageSize': "invalid"]              | 8              | "product5"  | false     | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['pageSize': "5", "currentPage": 1]  | 3              | "product2"  | false     | 5
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['needsTotal': "true"]               | 8              | "product5"  | true      | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['needsTotal': "false"]              | 8              | "product5"  | false     | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['sort': "true"]                     | 8              | "product5"  | false     | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | ['sort': "false"]                    | 8              | "product5"  | false     | 10
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer        | null                                 | 8              | "product5"  | false     | 10
        "agent"                  | DUMMY_CUSTOMER.id | AS_AGENT       | this.&authorizeCustomerManager | ['productCode': "product1"]          | 1              | "product1"  | false     | 10
        "authorizeTrustedClient" | DUMMY_CUSTOMER.id | null           | this.&authorizeTrustedClient   | null                                 | 8              | "product5"  | false     | 10
    }


    def "#user should not be able to get dummy's product interests details with #withId for #reason"()
    {
        given: "a registered and logged in #user"
        if (credential == null)
        {
            authorizationMethod(restClient)
        }
        else
        {
            authorizationMethod(restClient, credential)
        }

        when: "he requests to get product interests"
        def response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/productinterests" ,
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return error"
        with(response) {
            status == statusCode
            data.errors[0].type == errType
        }

        where:
        user                     | withId            | authorizationMethod            | credential  | statusCode      | errType                  | reason
        "mark"                   | DUMMY_CUSTOMER.id | this.&authorizeCustomer        | MARK_RIVERS | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "agent"                  | "current"         | this.&authorizeCustomerManager | AS_AGENT    | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "anonymous"              | DUMMY_CUSTOMER.id | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "anonymous"              | "current"         | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "authorizeTrustedClient" | "current"         | this.&authorizeTrustedClient   | null        | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "authorizeClient"        | DUMMY_CUSTOMER.id | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "authorizeClient"        | "current"         | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
    }

    def "#user should be able to modify dummy's product interests details with #withId for"()
    {
        ensureProductIsNotSubscribed("1225694")
        given: "a registered and logged in #user"
        if (credential == null)
        {
            authorizationMethod(restClient)
        }
        else
        {
            authorizationMethod(restClient, credential)
        }
        when: "he requests to get product interests"
        def response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/productinterests" ,
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return product interests"
        with(response) {
            status == SC_OK
        }
        def originalResult = response.data.results.size
        def addedResult = originalResult + 1;

        when: "he requests to add product interests"
        response = restClient.post(path: getBasePathWithSite() + "/users/" + withId + "/productinterests"  ,
                query: [
                        'notificationType': "BACK_IN_STOCK",
                        'productCode'     : '1225694'
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "return OK with info"
        with(response) {
            status == SC_OK
            data.product.code == "1225694"
            data.productInterestEntry[0].interestType == "BACK_IN_STOCK"
        }

        when: "he requests to get product interests"
        response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/productinterests" ,
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return product interests"
        with(response) {
            status == SC_OK
            data.results.size == addedResult
            checkIfProductInside(data.results, "1225694", "BACK_IN_STOCK")
        }

        when: "he requests to remove product interests"
        response = restClient.delete(path: getBasePathWithSite() + "/users/" + withId + "/productinterests"  ,
                query:  [
                        'notificationType': "BACK_IN_STOCK",
                        'productCode'     : '1225694'
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "return OK"
        with(response) {
            status == SC_OK
        }

        when: "he requests to get product interests"
        response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/productinterests",
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return product interests"
        with(response) {
            status == SC_OK
            data.results.size == originalResult
            checkIfProductInside(data.results, "1225694", "BACK_IN_STOCK") == false
        }

        where:
        user                     | withId            | credential     | authorizationMethod
        "dummy"                  | "current"         | DUMMY_CUSTOMER | this.&authorizeCustomer
        "dummy"                  | "current"         | DUMMY_CUSTOMER | this.&authorizeCustomer
        "dummy"                  | "current"         | DUMMY_CUSTOMER | this.&authorizeCustomer
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer
        "dummy"                  | DUMMY_CUSTOMER.id | DUMMY_CUSTOMER | this.&authorizeCustomer
        "agent"                  | DUMMY_CUSTOMER.id | AS_AGENT       | this.&authorizeCustomerManager
        "authorizeTrustedClient" | DUMMY_CUSTOMER.id | null           | this.&authorizeTrustedClient
    }

    def "#user should not be able to add dummy's product interests details with #withId for #reason"()
    {
        given: "a registered and logged in #user"
        if (credential == null)
        {
            authorizationMethod(restClient)
        }
        else
        {
            authorizationMethod(restClient, credential)
        }

        when: "he requests to add product interests"
        def response = restClient.post(path: getBasePathWithSite() + "/users/" + withId + "/productinterests"  ,
                query: [
                        'notificationType': "BACK_IN_STOCK",
                        'productCode'     : '1225694'
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "return error"
        with(response) {
            status == statusCode
            data.errors[0].type == errType
        }

        where:
        user                     | withId            | authorizationMethod            | credential  | statusCode      | errType                  | reason
        "mark"                   | DUMMY_CUSTOMER.id | this.&authorizeCustomer        | MARK_RIVERS | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "agent"                  | "current"         | this.&authorizeCustomerManager | AS_AGENT    | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "anonymous"              | DUMMY_CUSTOMER.id | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "anonymous"              | "current"         | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "authorizeTrustedClient" | "current"         | this.&authorizeTrustedClient   | null        | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "authorizeClient"        | DUMMY_CUSTOMER.id | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "authorizeClient"        | "current"         | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
    }

    def "#user should not be able to delete dummy's product interests details with #withId for #reason"()
    {
        given: "a registered and logged in #user"
        if (credential == null)
        {
            authorizationMethod(restClient)
        }
        else
        {
            authorizationMethod(restClient, credential)
        }

        when: "he requests to add product interests"
        def response = restClient.delete(path: getBasePathWithSite() + "/users/" + withId + "/productinterests",
                query: [
                        'notificationType': "BACK_IN_STOCK",
                        'productCode'     : '1225694'
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "return error"
        with(response) {
            status == statusCode
            data.errors[0].type == errType
        }

        where:
        user                     | withId            | authorizationMethod            | credential  | statusCode      | errType                  | reason
        "mark"                   | DUMMY_CUSTOMER.id | this.&authorizeCustomer        | MARK_RIVERS | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "agent"                  | "current"         | this.&authorizeCustomerManager | AS_AGENT    | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "anonymous"              | DUMMY_CUSTOMER.id | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "anonymous"              | "current"         | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "authorizeTrustedClient" | "current"         | this.&authorizeTrustedClient   | null        | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "authorizeClient"        | DUMMY_CUSTOMER.id | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "authorizeClient"        | "current"         | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
    }

    def checkIfProductInside(results, productCode, type)
    {
        for (item in results)
        {
            if (item.product.code == productCode && item.productInterestEntry[0].interestType == type)
            {
                return true
            }
        }
        return false
    }

    def ensureProductIsNotSubscribed(productCode)
    {
        authorizeTrustedClient(restClient);
        def response = restClient.get(path: getBasePathWithSite() + "/users/" + DUMMY_CUSTOMER.id + "/productinterests",
                query: null,
                contentType: JSON,
                requestContentType: JSON)
        if (checkIfProductInside(response.data.results, productCode, "BACK_IN_STOCK"))
        {
            response = restClient.delete(path: getBasePathWithSite() + "/users/" + DUMMY_CUSTOMER.id + "/productinterests",
                    query: [
                            'notificationType': "BACK_IN_STOCK",
                            'productCode'     : '1225694'
                    ],
                    contentType: JSON,
                    requestContentType: JSON)
            with(response) {
                status == SC_OK
            }
        }

    }

}
