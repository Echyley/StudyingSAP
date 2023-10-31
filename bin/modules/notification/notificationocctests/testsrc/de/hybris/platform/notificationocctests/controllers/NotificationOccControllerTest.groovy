/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.notificationocctests.controllers

import java.lang.reflect.WildcardType

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
class NotificationOccControllerTest extends AbstractUserTest {

    static final WILLIAM_HUNTER = ["id": "william.hunter@rustic-hw.com", "password": "1234"]
    static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
    static final AS_AGENT = ["id": "asagent", "password": "1234"]

    def "#user should be able to get William's notification preferences with #withId"()
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

        when: "he requests to get notification preferences"
        def response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return notification preferences"
        with(response) {
            status == SC_OK
            data.preferences.size >= 2
            data.preferences[0].channel == "EMAIL"
            data.preferences[0].value == WILLIAM_HUNTER.id
            data.preferences[0].enabled == false
            data.preferences[0].visible == true
        }
        where:
        user                     | withId            | credential     | authorizationMethod
        "customer"               | "current"         | WILLIAM_HUNTER | this.&authorizeCustomer
        "customer"               | WILLIAM_HUNTER.id | WILLIAM_HUNTER | this.&authorizeCustomer
        "agent"                  | WILLIAM_HUNTER.id | AS_AGENT       | this.&authorizeCustomerManager
        "authorizeTrustedClient" | WILLIAM_HUNTER.id | null           | this.&authorizeTrustedClient
    }



    def "#user should not be able to get william's notification preferences details with #withId for #reason"()
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

        when: "he requests to get notification preferences"
        def response = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
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
        "mark"                   | WILLIAM_HUNTER.id | this.&authorizeCustomer        | MARK_RIVERS | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "agent"                  | "current"         | this.&authorizeCustomerManager | AS_AGENT    | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "anonymous"              | WILLIAM_HUNTER.id | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "anonymous"              | "current"         | this.&authorizeGuest           | null        | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "authorizeTrustedClient" | "current"         | this.&authorizeTrustedClient   | null        | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "authorizeClient"        | WILLIAM_HUNTER.id | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user with Id"
        "authorizeClient"        | "current"         | this.&authorizeClient          | null        | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user withId"
    }

    def "#user should be able to update Mark's notification preferences with #withId"()
    {
        resetPreference(MARK_RIVERS.id)
        given: "a registered and logged in #user"
        if (credential == null)
        {
            authorizationMethod(restClient)
        }
        else
        {
            authorizationMethod(restClient, credential)
        }

        when: "he requests to change notification preferences"
        def response = restClient.patch(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
                query: null,
                body: [
                        "preferences":
                                [
                                        "channel": "EMAIL",
                                        "value"  : "newEmail@test.com",
                                        "enabled": true
                                ]
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "succeed"
        with(response) {
            status == SC_OK
        }
        when: "he requests to read notification preferences"
        def response2 = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        then: "return notification preferences"
        with(response2) {
            status == SC_OK
            data.preferences.size >= 2
            data.preferences[0].channel == "EMAIL"
            // ensure value is not be changed even we have input
            data.preferences[0].value == MARK_RIVERS.id
            data.preferences[0].enabled == true
        }

        where:
        user                     | withId         | credential  | authorizationMethod
        "customer"               | "current"      | MARK_RIVERS | this.&authorizeCustomer
        "customer"               | MARK_RIVERS.id | MARK_RIVERS | this.&authorizeCustomer
        "agent"                  | MARK_RIVERS.id | AS_AGENT    | this.&authorizeCustomerManager
        "authorizeTrustedClient" | MARK_RIVERS.id | null        | this.&authorizeTrustedClient
    }

    def "#user should not able to update mark's notification preferences if channel is invalid #withId"()
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

        when: "he requests to change notification preferences"
        def response = restClient.patch(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
                query: null,
                body: [
                        "preferences":
                                [
                                        "channel": "non-existing-channel",
                                        "value"  : "newEmail@test.com",
                                        "enabled": true
                                ]
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "return error"
        with(response) {
            status == SC_BAD_REQUEST
            data.errors[0].type == "ValidationError"
            data.errors[0].reason == "invalid"
        }

        where:
        user                     | withId         | credential  | authorizationMethod
        "customer"               | "current"      | MARK_RIVERS | this.&authorizeCustomer
        "customer"               | MARK_RIVERS.id | MARK_RIVERS | this.&authorizeCustomer
        "agent"                  | MARK_RIVERS.id | AS_AGENT    | this.&authorizeCustomerManager
        "authorizeTrustedClient" | MARK_RIVERS.id | null        | this.&authorizeTrustedClient
    }

    def "#user should not able to update mark's notification preferences if permission is not allowed"()
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

        when: "he requests to change notification preferences"
        def response = restClient.patch(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
                query: null,
                body: [
                        "preferences":
                                [
                                        "channel": "EMAIL",
                                        "enabled": true
                                ]
                ],
                contentType: JSON,
                requestContentType: JSON)

        then: "return error"
        with(response) {
            status == statusCode
            data.errors[0].type == errType
        }

        where:
        user                     | withId         | authorizationMethod            | credential     | statusCode      | errType                  | reason
        "William"                | MARK_RIVERS.id | this.&authorizeCustomer        | WILLIAM_HUNTER | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user"
        "agent"                  | "current"      | this.&authorizeCustomerManager | AS_AGENT       | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "anonymous"              | MARK_RIVERS.id | this.&authorizeGuest           | null           | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "anonymous"              | "current"      | this.&authorizeGuest           | null           | SC_UNAUTHORIZED | "UnauthorizedError"      | "wrong user"
        "authorizeTrustedClient" | "current"      | this.&authorizeTrustedClient   | null           | SC_BAD_REQUEST  | "UnknownIdentifierError" | "wrong withId"
        "authorizeClient"        | MARK_RIVERS.id | this.&authorizeClient          | null           | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user with Id"
        "authorizeClient"        | "current"      | this.&authorizeClient          | null           | SC_FORBIDDEN    | "ForbiddenError"         | "wrong user withId"
    }

    // Ensure the notification preference email channel is set to false
    def resetPreference(String withId)
    {
        authorizeCustomerManager(restClient, AS_AGENT)
        def response = restClient.patch(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
                query: null,
                body: [
                        "preferences":
                                [
                                        "channel": "EMAIL",
                                        "enabled": false
                                ]
                ],
                contentType: JSON,
                requestContentType: JSON)

        with(response) {
            status == SC_OK
        }

        def response2 = restClient.get(path: getBasePathWithSite() + "/users/" + withId + "/notificationpreferences",
                query: null,
                contentType: JSON,
                requestContentType: JSON)

        with(response2) {
            status == SC_OK
            data.preferences.size >= 2
            data.preferences[0].channel == "EMAIL"
            data.preferences[0].enabled == false
        }
        removeAuthorization(restClient)
    }
}
