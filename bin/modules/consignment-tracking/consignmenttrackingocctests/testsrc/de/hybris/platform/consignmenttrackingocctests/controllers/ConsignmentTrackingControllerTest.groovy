/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.consignmenttrackingocctests.controllers

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC

import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

import com.fasterxml.jackson.databind.ObjectMapper
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.users.AbstractUserTest
import spock.lang.Unroll

@ManualTest
@Unroll
class ConsignmentTrackingControllerTest extends AbstractUserTest {

    static final WILLIAM_HUNTER = ["id": "william.hunter@rustic-hw.com", "password": "1234"]
    static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
    static final AS_AGENT = ["id": "asagent", "password": "1234"]

    static final ORDER_NUMBER = "consignmentOrder";
    static final CONSIGNMENT_NUMBER = "consignment1";

    private final ObjectMapper objectMapper = new ObjectMapper();

    def "#user should be able to get consignment details with #withId"()
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

        when: "he requests to get consignment"
        def response2 = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/orders/" + ORDER_NUMBER + "/consignments/" + CONSIGNMENT_NUMBER + "/tracking",
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return consignment"
        with(response2) {
            status == SC_OK
            isEmpty(data.trackingEvents)
            data.statusDisplay == "shipped"
            data.trackingUrl == "about:blank"
        }
        where:
        user                     | withId            | credential     | authorizationMethod
        "customer"               | "current"         | WILLIAM_HUNTER | this.&authorizeCustomer
        "customer"               | WILLIAM_HUNTER.id | WILLIAM_HUNTER | this.&authorizeCustomer
        "agent"                  | WILLIAM_HUNTER.id | AS_AGENT       | this.&authorizeCustomerManager
        "authorizeTrustedClient" | WILLIAM_HUNTER.id | null           | this.&authorizeTrustedClient
    }

    def "#user should not be able to get william's consignment details with #withId for #reason"()
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

        when: "he requests to get consignment"
        def response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/orders/" + ORDER_NUMBER + "/consignments/" + CONSIGNMENT_NUMBER + "/tracking",
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return error"
        with(response) {
            status == statusCode
            response.data.errors[0].type == errType
        }
        where:
        user                     | withId            | authorizationMethod            | credential  | statusCode      | errType                  | reason
        "mark"                   | "current"         | this.&authorizeCustomer        | MARK_RIVERS | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong user"
        "mark"                   | WILLIAM_HUNTER.id | this.&authorizeCustomer        | MARK_RIVERS | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "agent"                  | MARK_RIVERS.id    | this.&authorizeCustomerManager | AS_AGENT    | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "agent"                  | "current"         | this.&authorizeCustomerManager | AS_AGENT    | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "anonymous"              | WILLIAM_HUNTER.id | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "anonymous"              | "current"         | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "authorizeTrustedClient" | "current"         | this.&authorizeTrustedClient   | null        | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "authorizeTrustedClient" | MARK_RIVERS.id    | this.&authorizeTrustedClient   | null        | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "authorizeClient"        | WILLIAM_HUNTER.id | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "authorizeClient"        | "current"         | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
    }
}
