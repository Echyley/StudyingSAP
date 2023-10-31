/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileocctests.controllers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

@ManualTest
@Unroll
class MobileNumberControllerTest extends AbstractCartTest {

	static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_MANAGER_USER = ["id": "asagent@nakano.com", "password": "1234"]
	static final WILLIAM_HUNTER = ["id": "william.hunter@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_1 = ["id": "customer1@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_2 = ["id": "customer2@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_3 = ["id": "customer3@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_4 = ["id": "customer4@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_5 = ["id": "customer5@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_6 = ["id": "customer6@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_7 = ["id": "customer7@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_8 = ["id": "customer8@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_9 = ["id": "customer9@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_10 = ["id": "customer10@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_11 = ["id": "customer11@rustic-hw.com", "password": "1234"]
	static final MOBILE_NUMBER_1 = "+8618600000121"
	static final MOBILE_NUMBER_2 = "+8618600000122"
	static final MOBILE_NUMBER_3 = "+8618600000123"
	static final MOBILE_NUMBER_4 = "+8618600000124"
	static final MOBILE_NUMBER_5 = "+8618600000125"
	static final MOBILE_NUMBER_6 = "+8618600000126"
	static final MOBILE_NUMBER_7 = "+8618600000127"
	static final MOBILE_NUMBER_8 = "+8618600000128"
	static final MOBILE_NUMBER_9 = "+8618600000129"
	static final MOBILE_NUMBER_10 = "+8618600000130"
	static final MOBILE_NUMBER_11 = "+8618600000131"
	static final MOBILE_NUMBER_12 = "+8618600000132"
	static final MOBILE_NUMBER_13 = "+8618600000133"
	static final MOBILE_NUMBER_14 = "+8618600000134"

	def "#role should be able to send verification code for a given mobile number"() {
		given: "a registered and logged in #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		when: "he requests to send verification code"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber/verificationcode",
				query: ["mobileNumber": mobileNumber],
				contentType: JSON,
				requestContentType: JSON)

		then: "the verification code is successfully sent"
		with(response) {
			status == SC_OK
		}

		where:
		role               | authorizationMethod            | user                  | userId            | mobileNumber
		"Customer"         | this.&authorizeCustomer        | MARK_RIVERS           | "current"         | MOBILE_NUMBER_1
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | WILLIAM_HUNTER.id | MOBILE_NUMBER_2
		"Trusted client"   | this.&authorizeTrustedClient   | null                  | CUSTOMER_1.id     | MOBILE_NUMBER_3
	}

	def "#role should not be able to send verification code for a given mobile number #scenario"() {
		given: "a #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		when: "he requests to send verification code"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber/verificationcode",
				query: ["mobileNumber": mobileNumber],
				contentType: JSON,
				requestContentType: JSON)

		then: "the verification code is failed to be sent"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		role                                             | scenario                            | authorizationMethod            | user                  | userId                   | mobileNumber    | errorStatus     | errorType                | errorMessage
		"Guest"                                          | null                                | this.&authorizeClient          | null                  | "current"                | MOBILE_NUMBER_1 | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"
		"Anonymous user without any authorization token" | null                                | this.&removeAuthorization      | null                  | "current"                | MOBILE_NUMBER_1 | SC_UNAUTHORIZED | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"Mark"                                           | "when the user is not Mark"         | this.&authorizeCustomer        | MARK_RIVERS           | CUSTOMER_MANAGER_USER.id | MOBILE_NUMBER_1 | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"
		"Customer agent"                                 | null                                | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | "current"                | MOBILE_NUMBER_1 | SC_BAD_REQUEST  | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"Customer"                                       | "when the mobile number is invalid" | this.&authorizeCustomer        | MARK_RIVERS           | "current"                | "invalidNumber" | SC_BAD_REQUEST  | "ValidationError"        | "Invalid mobile number."
		"Customer"                                       | "when the mobile number is null"    | this.&authorizeCustomer        | MARK_RIVERS           | "current"                | null            | SC_BAD_REQUEST  | "ValidationError"        | "Invalid mobile number."
	}

	def "#role should be able to bind mobile number for a given customer"() {
		given: "a registered and logged in #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		and: "he requests to send verification code"
		sendVerificationCode(userId, mobileNumber)

		when: "he requests to bind mobile number"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber",
				query: [
						"mobileNumber"    : mobileNumber,
						"verificationCode": "1234"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the mobile number is successfully bound"
		with(response) {
			status == SC_CREATED
		}

		where:
		role               | authorizationMethod            | user                  | userId         | mobileNumber
		"Customer"         | this.&authorizeCustomer        | CUSTOMER_9            | "current"      | MOBILE_NUMBER_4
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | CUSTOMER_10.id | MOBILE_NUMBER_5
		"Trusted client"   | this.&authorizeTrustedClient   | null                  | CUSTOMER_11.id | MOBILE_NUMBER_6
	}

	def "#role should not be able to bind mobile number for a given customer #scenario"() {
		given: "a #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		if (shouldFirstBindMobileNumber) {
			and: "he requests to send verification code"
			sendVerificationCode("current", mobileNumber)

			and: "he requests to bind mobile number"
			bindMobileNumber("current", mobileNumber)
		}

		if (shouldSendVerificationCode) {
			and: "he requests to send verification code"
			sendVerificationCode("current", mobileNumber)
		}

		when: "he requests to bind mobile number"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber",
				query: [
						"mobileNumber"    : mobileNumber,
						"verificationCode": verificationCode
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the mobile number is failed to be bound"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		role                                             | scenario                            | authorizationMethod            | user                  | userId                   | mobileNumber     | verificationCode | errorStatus     | errorType                | errorMessage                                                       | shouldSendVerificationCode | shouldFirstBindMobileNumber
		"Guest"                                          | null                                | this.&authorizeClient          | null                  | "current"                | MOBILE_NUMBER_1  | "1234"           | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"                                                 | false                      | false
		"Anonymous user without any authorization token" | null                                | this.&removeAuthorization      | null                  | "current"                | MOBILE_NUMBER_1  | "1234"           | SC_UNAUTHORIZED | "UnauthorizedError"      | "Full authentication is required to access this resource"          | false                      | false
		"Mark"                                           | "when the user is not Mark"         | this.&authorizeCustomer        | MARK_RIVERS           | CUSTOMER_MANAGER_USER.id | MOBILE_NUMBER_1  | "1234"           | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"                                                 | false                      | false
		"Customer agent"                                 | null                                | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | "current"                | MOBILE_NUMBER_1  | "1234"           | SC_BAD_REQUEST  | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"                    | false                      | false
		"Customer"                                       | "when the mobile number is invalid" | this.&authorizeCustomer        | MARK_RIVERS           | "current"                | "invalidNumber"  | "1234"           | SC_BAD_REQUEST  | "ValidationError"        | "Invalid mobile number."                                           | false                      | false
		"Customer"                                       | "when the mobile number is null"    | this.&authorizeCustomer        | MARK_RIVERS           | "current"                | null             | "1234"           | SC_BAD_REQUEST  | "ValidationError"        | "Invalid mobile number."                                           | false                      | false
		"Customer"                                       | "when send twice"                   | this.&authorizeCustomer        | CUSTOMER_8            | "current"                | MOBILE_NUMBER_12 | "1234"           | SC_BAD_REQUEST  | "ValidationError"        | "A mobile number has already been bound for the current customer." | true                       | true
		"Customer"                                       | "when verification code is invalid" | this.&authorizeCustomer        | CUSTOMER_2            | "current"                | MOBILE_NUMBER_13 | "invalidCode"    | SC_BAD_REQUEST  | "ValidationError"        | "Invalid verification code."                                       | true                       | false
		"Customer"                                       | "when verification code is null"    | this.&authorizeCustomer        | CUSTOMER_2            | "current"                | MOBILE_NUMBER_14 | null             | SC_BAD_REQUEST  | "ValidationError"        | "Invalid verification code."                                       | true                       | false
	}

	def "#role should be able to unbind mobile number for a given customer"() {
		given: "a registered and logged in #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		and: "he requests to send verification code"
		sendVerificationCode(userId, mobileNumber)

		and: "he requests to bind mobile number"
		bindMobileNumber(userId, mobileNumber)

		and: "he requests to send verification code"
		sendVerificationCode(userId, mobileNumber)

		when: "he requests to unbind mobile number"
		def response = restClient.delete(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber",
				query: ["verificationCode": "1234"],
				contentType: JSON,
				requestContentType: JSON)

		then: "the mobile number is successfully unbound"
		with(response) {
			status == SC_OK
		}

		where:
		role               | authorizationMethod            | user                  | userId        | mobileNumber
		"Customer"         | this.&authorizeCustomer        | CUSTOMER_3            | "current"     | MOBILE_NUMBER_7
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | CUSTOMER_4.id | MOBILE_NUMBER_8
		"Trusted client"   | this.&authorizeTrustedClient   | null                  | CUSTOMER_5.id | MOBILE_NUMBER_9
	}

	def "#role should not be able to unbind mobile number for a given customer"() {
		given: "a #role"
		if (user == null) {
			authorizationMethod(restClient)
		} else {
			authorizationMethod(restClient, user)
		}

		when: "he requests to unbind mobile number"
		def response = restClient.delete(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber",
				query: ["verificationCode": "1234"],
				contentType: JSON,
				requestContentType: JSON)

		then: "the mobile number is failed to be unbound"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		role                                             | authorizationMethod            | user                  | userId                   | errorStatus     | errorType                | errorMessage
		"Guest"                                          | this.&authorizeClient          | null                  | "current"                | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"
		"Anonymous user without any authorization token" | this.&removeAuthorization      | null                  | "current"                | SC_UNAUTHORIZED | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"Mark"                                           | this.&authorizeCustomer        | MARK_RIVERS           | CUSTOMER_MANAGER_USER.id | SC_FORBIDDEN    | "ForbiddenError"         | "Access is denied"
		"Customer agent"                                 | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER | "current"                | SC_BAD_REQUEST  | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
	}

	def "Customer should not be able to unbind mobile number when #scenario"() {
		given: "a registered and logged in customer"
		authorizeCustomer(restClient, user)

		and: "he requests to send verification code"
		sendVerificationCode(userId, mobileNumber)

		and: "he requests to bind mobile number"
		bindMobileNumber(userId, mobileNumber)

		and: "he requests to send verification code"
		sendVerificationCode(userId, mobileNumber)

		when: "he requests to unbind mobile number"
		def response = restClient.delete(
				path: getBasePathWithSite() + "/users/current/mobilenumber",
				query: ["verificationCode": verificationCode],
				contentType: JSON,
				requestContentType: JSON)

		then: "the mobile number is failed to be unbound"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                       | user       | verificationCode | userId        | mobileNumber     | errorStatus    | errorType         | errorMessage
		"verification code is invalid" | CUSTOMER_6 | "invalidCode"    | CUSTOMER_6.id | MOBILE_NUMBER_10 | SC_BAD_REQUEST | "ValidationError" | "Invalid verification code."
		"verification code is null"    | CUSTOMER_7 | null             | CUSTOMER_7.id | MOBILE_NUMBER_11 | SC_BAD_REQUEST | "ValidationError" | "Invalid verification code."
	}

	protected void bindMobileNumber(String userId, String mobileNumber) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber",
				query: [
						"mobileNumber"    : mobileNumber,
						"verificationCode": "1234"
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_CREATED
		}
	}

	protected void sendVerificationCode(String userId, String mobileNumber) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/mobilenumber/verificationcode",
				query: ["mobileNumber": mobileNumber],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			status == SC_OK
		}
	}
}
