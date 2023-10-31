/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesetaxinvoiceocctests.controllers

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
class TaxInvoiceControllerTest extends AbstractCartTest {

	static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_MANAGER_USER = ["id": "asagent@nakano.com", "password": "1234"]
	static final String PRODUCT_CODE = "1225694"
	static final Integer ADD_PRODUCT_QUANTITY = 1

	def "Should get all tax invoice recipient types"() {
		when: "request to get all tax invoice recipient types"
		def response = restClient.get(
				path: getBasePathWithSite() + "/taxinvoice/recipienttypes",
				contentType: JSON,
				requestContentType: JSON)

		then: "a list of tax invoice recipient types are returned"
		with(response) {
			status == SC_OK
			data.size() == 2
			data[0].type == 'InvoiceRecipientType'
		}
	}

	def "#role should be able to set tax invoice for a given cart"() {
		given: "a registered and logged in #role"
		authorizationMethod(restClient, MARK_RIVERS)

		and: "he requests to create a cart"
		def cartId = createNewCart(MARK_RIVERS.id, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to set tax invoice for the given cart"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/carts/" + cartId + "/taxinvoice",
				body: [
						"recipientType": "INDIVIDUAL",
						"recipient"    : "Mark"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is successfully set"
		with(response) {
			status == SC_OK
		}

		where:
		role               | authorizationMethod            | userData
		"Customer"         | this.&authorizeCustomer        | MARK_RIVERS
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER
	}

	def "#role should be able to set tax invoice for a given cart"() {
		given: "a #role"
		authorizationMethod(restClient)
		def anonymous = ['id': 'anonymous']
		def userGuid = getGuestUid()

		and: "he requests to create a cart"
		def cart = createAnonymousCart(restClient, JSON, getBasePathWithIntegrationSite())
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_CODE, ADD_PRODUCT_QUANTITY, JSON, getBasePathWithIntegrationSite())
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, JSON, getBasePathWithIntegrationSite()) //setting email address on cart makes it recognized as guest cart
		setAddressForAnonymousCart(restClient, GOOD_ADDRESS_DE_JSON, cart.guid, JSON, getBasePathWithIntegrationSite())

		when: "he requests to set tax invoice for a give cart"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/anonymous/carts/" + cart.guid + "/taxinvoice",
				body: [
						"recipientType": "INDIVIDUAL",
						"recipient"    : "Mark"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is successfully set"
		with(response) {
			status == SC_OK
		}

		where:
		role             | authorizationMethod
		"Guest"          | this.&authorizeClient
		"Trusted client" | this.&authorizeTrustedClient
	}

	def "Customer should not be able to set tax invoice for a non-existing #object"() {
		given: "a registered and logged in customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to create a cart"
		def cartId = createNewCart(MARK_RIVERS.id, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to set tax invoice for a non-existing #object"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + userId + "/carts/" + cart + "/taxinvoice",
				body: [
						"recipientType": "INDIVIDUAL",
						"recipient"    : "Mark"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is failed to be set"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		object | cart           | userId              | errorStatus    | errorType        | errorMessage
		"cart" | "non-existing" | MARK_RIVERS.id      | SC_BAD_REQUEST | "CartError"      | "Cart not found."
		"user" | this.&cartId   | "non-existing-user" | SC_FORBIDDEN   | "ForbiddenError" | "Access is denied"
	}

	def "An anonymous user without any authorization token should not be able to set tax invoice for a cart"() {
		given: "an anonymous user"
		removeAuthorization(restClient)

		when: "he requests to set tax invoice for a cart"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/current/carts/non-existing-cart/taxinvoice",
				body: [
						"recipientType": "INDIVIDUAL",
						"recipient"    : "Mark"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is failed to be set"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].message == "Full authentication is required to access this resource"
			data.errors[0].type == "UnauthorizedError"
		}
	}

	def "#role should be able to delete tax invoice for a given cart"() {
		given: "a registered and logged in #role"
		authorizationMethod(restClient, MARK_RIVERS)

		and: "he requests to create a cart"
		def cartId = createNewCart(MARK_RIVERS.id, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to delete tax invoice for a give cart"
		def response = restClient.delete(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/carts/" + cartId + "/taxinvoice",
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is successfully deleted"
		with(response) {
			status == SC_OK
		}

		where:
		role               | authorizationMethod            | userData
		"Customer"         | this.&authorizeCustomer        | MARK_RIVERS
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER
	}

	def "#role should be able to delete tax invoice for a given cart"() {
		given: "a #role"
		authorizationMethod(restClient)
		def anonymous = ['id': 'anonymous']
		def userGuid = getGuestUid()

		and: "he requests to create a cart"
		def cart = createAnonymousCart(restClient, JSON, getBasePathWithIntegrationSite())
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_CODE, ADD_PRODUCT_QUANTITY, JSON, getBasePathWithIntegrationSite())
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, JSON, getBasePathWithIntegrationSite()) //setting email address on cart makes it recognized as guest cart
		setAddressForAnonymousCart(restClient, GOOD_ADDRESS_DE_JSON, cart.guid, JSON, getBasePathWithIntegrationSite())

		when: "he requests to delete tax invoice for a give cart"
		def response = restClient.delete(
				path: getBasePathWithSite() + "/users/anonymous/carts/" + cart.guid + "/taxinvoice",
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is successfully deleted"
		with(response) {
			status == SC_OK
		}

		where:
		role             | authorizationMethod
		"Guest"          | this.&authorizeClient
		"Trusted client" | this.&authorizeTrustedClient
	}

	def "Customer should not be able to delete tax invoice for a non-existing #object"() {
		given: "a registered and logged in customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to create a cart"
		def cartId = createNewCart(MARK_RIVERS.id, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to delete tax invoice for a non-existing #object"
		def response = restClient.delete(
				path: getBasePathWithSite() + "/users/" + userId + "/carts/" + cart + "/taxinvoice",
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is failed to be deleted"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		object | cart           | userId              | errorStatus    | errorType        | errorMessage
		"cart" | "non-existing" | MARK_RIVERS.id      | SC_BAD_REQUEST | "CartError"      | "Cart not found."
		"user" | this.&cartId   | "non-existing-user" | SC_FORBIDDEN   | "ForbiddenError" | "Access is denied"
	}

	def "An anonymous user without any authorization token should not be able to delete tax invoice for cart"() {
		given: "a anonymous user"
		removeAuthorization(restClient)

		when: "he requests to delete tax invoice for a cart"
		def response = restClient.delete(
				path: getBasePathWithSite() + "/users/current/carts/non-existing-cart/taxinvoice",
				contentType: JSON,
				requestContentType: JSON)

		then: "the tax invoice is failed to be deleted"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].message == "Full authentication is required to access this resource"
			data.errors[0].type == "UnauthorizedError"
		}
	}

	protected String createNewCart(String userId) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/carts",
				query: [
						'fields': 'DEFAULT'
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			status == SC_CREATED
			data.type == "cartWsDTO"
		}
		return response.data.code
	}

	protected String createNewCart(String userId, String productCode, int quantity) {
		def cartId = createNewCart(userId)
		addProductToCart(userId, cartId, productCode, quantity)
		return cartId
	}

	protected void addProductToCart(String userId, String cartId, String productCode, Integer quantity) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/carts/" + cartId + "/entries",
				body: [
						"product" : ["code": productCode],
						"quantity": quantity
				],
				contentType: JSON,
				requestContentType: JSON
		)
		with(response) {
			status == SC_OK
			data.quantity == quantity
			data.entry.product.code == productCode
		}
	}

	private String getGuestUid() {
		def randomUID = System.currentTimeMillis()
		def guestUid = "${randomUID}@test.com"
		return guestUid
	}
}
