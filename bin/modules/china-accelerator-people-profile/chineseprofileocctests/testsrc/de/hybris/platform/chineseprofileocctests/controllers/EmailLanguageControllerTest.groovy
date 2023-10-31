/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileocctests.controllers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

@ManualTest
@Unroll
class EmailLanguageControllerTest extends AbstractCartTest {

	static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_MANAGER_USER = ["id": "asagent@nakano.com", "password": "1234"]

	def "#role should be able to set email language for a given customer"() {
		given: "a registered and logged in #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		when: "he requests to set his email language"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + userId + "/emaillanguage",
				query: ["languageIsoCode": "zh"],
				contentType: JSON,
				requestContentType: JSON)

		then: "the email language is successfully set"
		with(response) {
			status == SC_OK
		}

		where:
		role               | authorizationMethod            | user                  | userId
		"Customer"         | this.&authorizeCustomer        | MARK_RIVERS           | "current"
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | MARK_RIVERS.id
		"Trusted client"   | this.&authorizeTrustedClient   | null                  | MARK_RIVERS.id
	}

	def "#role should not be able to set email language for a given customer #scenario"() {
		given: "a #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		when: "he requests to set email language for a give customer"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + userId + "/emaillanguage",
				query: ["languageIsoCode": languageCode],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to set email language"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		role                                             | scenario                            | authorizationMethod            | user                  | userId                   | languageCode  | errorStatus     | errorType                | errorMessage
		"Guest"                                          | null                                | this.&authorizeClient          | null                  | "current"                | "en"          | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"
		"Anonymous user without any authorization token" | null                                | this.&removeAuthorization      | null                  | "current"                | "en"          | SC_UNAUTHORIZED | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"Mark"                                           | "when the customer is not Mark"     | this.&authorizeCustomer        | MARK_RIVERS           | CUSTOMER_MANAGER_USER.id | "en"          | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"
		"Customer agent"                                 | null                                | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | "current"                | "en"          | SC_BAD_REQUEST  | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"Customer"                                       | "when the language code is invalid" | this.&authorizeCustomer        | MARK_RIVERS           | "current"                | "invalidCode" | SC_BAD_REQUEST  | "ValidationError"        | "Invalid email language ISO code."
		"Customer"                                       | "when the language code is null"    | this.&authorizeCustomer        | MARK_RIVERS           | "current"                | null          | SC_BAD_REQUEST  | "ValidationError"        | "Invalid email language ISO code."
	}
}
