/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chinesepaymentocctests.controllers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import de.hybris.platform.util.Config
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED

@ManualTest
@Unroll
class PaymentInfoControllerTest extends AbstractCartTest {

	static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
	static final CUSTOMER_MANAGER_USER = ["id": "asagent@nakano.com", "password": "1234"]
	static final String CURRENT_USER = "current"
	static final String PRODUCT_CODE = "1225694"
	static final Integer ADD_PRODUCT_QUANTITY = 1

	def "#role should be able to update chinese payment info for a cart"() {
		given: "a registered and logged in #role"
		authorizationMethod(restClient, userData)

		and: "he requests to create a cart for Mark"
		def cartId = createNewCart(MARK_RIVERS.id, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to update chinese payment info for a give cart"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/carts/" + cartId + "/chinesepaymentinfo",
				query: ["paymentModeCode": "alipay"],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese payment info is successfully updated"
		with(response) {
			status == SC_OK
		}

		where:
		role               | authorizationMethod            | userData
		"Customer"         | this.&authorizeCustomer        | MARK_RIVERS
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER
	}

	def "#role should be able to update chinese payment info for a given cart"() {
		given: "a #role"
		authorizationMethod(restClient)
		def anonymous = ['id': 'anonymous']
		def userGuid = getGuestUid()

		and: "he requests to create a cart"
		def cart = createAnonymousCart(restClient, JSON, getBasePathWithIntegrationSite())
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_CODE, ADD_PRODUCT_QUANTITY, JSON, getBasePathWithIntegrationSite())
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, JSON, getBasePathWithIntegrationSite()) //setting email address on cart makes it recognized as guest cart
		setAddressForAnonymousCart(restClient, GOOD_ADDRESS_DE_JSON, cart.guid, JSON, getBasePathWithIntegrationSite())

		when: "he requests to update chinese payment info for a give cart"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/anonymous/carts/" + cart.guid + "/chinesepaymentinfo",
				query: ["paymentModeCode": "alipay"],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese payment info is successfully updated"
		with(response) {
			status == SC_OK
		}

		where:
		role             | authorizationMethod
		"Guest"          | this.&authorizeClient
		"Trusted client" | this.&authorizeTrustedClient
	}

	def "Customer should not be able to update chinese payment info for a non-existing #object"() {
		given: "a registered and logged in customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to update chinese payment info for a non-existing #object"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + userId + "/carts/" + cart + "/chinesepaymentinfo",
				query: ["paymentModeCode": "alipay"],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese payment info is failed to be updated"
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

	def "An anonymous user without any authorization token should not be able to update chinese payment info for a cart"() {
		given: "a anonymous user"
		removeAuthorization(restClient)

		when: "he requests to update chinese payment info for a cart"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/current/carts/non-existing-cart/chinesepaymentinfo",
				query: ["paymentModeCode": "alipay"],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese payment info is failed to be updated"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].message == "Full authentication is required to access this resource"
			data.errors[0].type == "UnauthorizedError"
		}
	}

	def "#role should be able to create a chinese order"() {
		given: "a registered and logged in #role"
		authorizationMethod(restClient, userData)

		and: "he requests to create a cart for Mark"
		def cartId = createNewCart(MARK_RIVERS.id, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)
		setAddressForCart(restClient, GOOD_ADDRESS_DE_JSON, cartId, MARK_RIVERS.id, JSON, getBasePathWithIntegrationSite())
		setDeliveryModeForCart(restClient, MARK_RIVERS, cartId, DELIVERY_STANDARD, JSON, getBasePathWithIntegrationSite())
		setPaymentInfoForCart(MARK_RIVERS.id, cartId, 'alipay')

		when: "he requests to create chinese order"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/carts/" + cartId + "/createchineseorder",
				query: [
						'fields': 'DEFAULT'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese order is successfully created"
		with(response) {
			status == SC_OK
			isNotEmpty(data.code)
			data.type == "orderWsDTO"
			data.status == "CREATED"
		}

		where:
		role               | authorizationMethod            | userData
		"Customer"         | this.&authorizeCustomer        | MARK_RIVERS
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER
	}

	def "#role should be able to create a chinese order"() {
		given: "a #role"
		authorizationMethod(restClient)
		def anonymous = ['id': 'anonymous']
		def userGuid = getGuestUid()

		and: "he requests to create a cart and set delivery mode and payment info"
		def cart = createAnonymousCart(restClient, JSON, getBasePathWithIntegrationSite())
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_CODE, ADD_PRODUCT_QUANTITY, JSON, getBasePathWithIntegrationSite())
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, JSON, getBasePathWithIntegrationSite()) //setting email address on cart makes it recognized as guest cart
		setAddressForAnonymousCart(restClient, GOOD_ADDRESS_DE_JSON, cart.guid, JSON, getBasePathWithIntegrationSite())
		setDeliveryModeForCart(restClient, anonymous, cart.guid, DELIVERY_STANDARD, JSON, getBasePathWithIntegrationSite())
		setPaymentInfoForCart(anonymous.id, cart.guid, 'alipay')

		when: "he requests to create chinese order"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + anonymous.id + "/carts/" + cart.guid + "/createchineseorder",
				query: [
						'fields': 'DEFAULT'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese order is successfully created"
		with(response) {
			status == SC_OK
			isNotEmpty(data.code)
			data.type == "orderWsDTO"
			data.status == "CREATED"
		}

		where:
		role             | authorizationMethod
		"Guest"          | this.&authorizeClient
		"Trusted client" | this.&authorizeTrustedClient
	}

	def "Customer should not be able to create chinese order for a non-existing #object"() {
		given: "a registered and logged in customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to create chinese order for a non-existing #object"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/carts/" + cart + "/createchineseorder",
				query: [
						'fields': 'DEFAULT'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese order is failed to be created"
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

	def "An anonymous user without any authorization token should not be able to create chinese order"() {
		given: "a anonymous user"
		removeAuthorization(restClient)

		when: "he requests to create chinese order for a cart"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/carts/non-existing-cart/createchineseorder",
				query: [
						'fields': 'DEFAULT'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese order is failed to be updated"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].message == "Full authentication is required to access this resource"
			data.errors[0].type == "UnauthorizedError"
		}
	}

	def "#role should be able to create Chinese payment request data for an order"() {
		given: "a registered and logged in #role"
		authorizationMethod(restClient, userData)

		and: "he requests to create a cart for Mark"
		def cartId = createNewCart(MARK_RIVERS.id, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to place an order"
		def order = placeOrder(MARK_RIVERS, cartId)

		when: "he requests to create Chinese payment request data for a give order"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/orders/" + order.code + "/payment/request",
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese payment request data is successfully created"
		with(response) {
			status == SC_OK
			isNotEmpty(data.order)
			data.order.code == order.code
			data.order.paymentStatus == "PAID"
		}

		where:
		role               | authorizationMethod            | userData
		"Customer"         | this.&authorizeCustomer        | MARK_RIVERS
		"Customer manager" | this.&authorizeCustomerManager | CUSTOMER_MANAGER_USER
	}

	def "#role should be able to create Chinese payment request data for an order"() {
		given: "a #role"
		authorizationMethod(restClient)
		def anonymous = ['id': 'anonymous']
		def userGuid = getGuestUid()

		and: "he requests to create a cart and set delivery mode and payment info"
		def cart = createAnonymousCart(restClient, JSON, getBasePathWithIntegrationSite())
		addProductToCartOnline(restClient, anonymous, cart.guid, PRODUCT_CODE, ADD_PRODUCT_QUANTITY, JSON, getBasePathWithIntegrationSite())
		addEmailToAnonymousCart(restClient, cart.guid, userGuid, JSON, getBasePathWithIntegrationSite()) //setting email address on cart makes it recognized as guest cart
		setAddressForAnonymousCart(restClient, GOOD_ADDRESS_DE_JSON, cart.guid, JSON, getBasePathWithIntegrationSite())
		setDeliveryModeForCart(restClient, anonymous, cart.guid, DELIVERY_STANDARD, JSON, getBasePathWithIntegrationSite())
		setPaymentInfoForCart(anonymous.id, cart.guid, 'alipay')

		and: "he requests to place a Chinese order"
		def order = createChineseOrder(anonymous.id, cart.guid)

		when: "he requests to create Chinese payment request data for a given order"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + anonymous.id + "/orders/" + order.guid + "/payment/request",
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese payment request data is successfully created"
		with(response) {
			status == SC_OK
			isNotEmpty(data.order)
			data.order.code == order.code
			data.order.paymentStatus == "PAID"
		}

		where:
		role             | authorizationMethod
		"Guest"          | this.&authorizeClient
		"Trusted client" | this.&authorizeTrustedClient
	}

	def "Customer should not be able to create Chinese payment request data for a non-existing #object"() {
		given: "a registered and logged in customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to place an order"
		def order = placeOrder(MARK_RIVERS, cartId)
		def orderCode = order.code

		when: "he requests to create chinese order for a non-existing #object"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/orders/" + orderId + "/payment/request",
				contentType: JSON,
				requestContentType: JSON)

		then: "the chinese order is failed to be created"
		with(response) {
			status == errorStatus
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		object  | orderId            | userId              | errorStatus    | errorType                | errorMessage
		"order" | "non-existing"     | MARK_RIVERS.id      | SC_BAD_REQUEST | "UnknownIdentifierError" | "Order with guid non-existing not found for current user in current BaseStore"
		"user"  | this.&orderCode | "non-existing-user" | SC_FORBIDDEN   | "ForbiddenError"         | "Access is denied"
	}

	def "An anonymous user without any authorization token should not be able to create Chinese payment request data"() {
		given: "a anonymous user"
		removeAuthorization(restClient)

		when: "he requests to create Chinese payment request data for an order"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/orders/non-existing-order/payment/request",
				contentType: JSON,
				requestContentType: JSON)

		then: "the Chinese payment request data is failed to be updated"
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

	protected void setAddressForCart(RESTClient client, address, cartGuid, userId, format, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.post(
				path: basePathWithSite + '/users/' + userId+ '/carts/' + cartGuid + "/addresses/delivery",
				body: address,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: JSON
		)
		with(response) { status == SC_CREATED }
	}

	protected void setPaymentInfoForCart(String userId, String cartId, String paymentCode) {
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + userId + "/carts/" + cartId + "/chinesepaymentinfo",
				query: ["paymentModeCode": paymentCode],
				contentType: JSON,
				requestContentType: JSON
		)
		with(response) {
			status == SC_OK
		}
	}

	protected placeOrder(Object user, String cartId) {
		setAddressForCart(restClient, GOOD_ADDRESS_DE_JSON, cartId, user.id, JSON, getBasePathWithIntegrationSite())
		setDeliveryModeForCart(restClient, user, cartId, DELIVERY_STANDARD, JSON, getBasePathWithIntegrationSite())
		setPaymentInfoForCart(user.id, cartId, 'alipay')
		return createChineseOrder(user.id, cartId)
	}

	protected createChineseOrder(String userId, String cartId) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/carts/" + cartId + "/createchineseorder",
				query: [
						'fields': 'DEFAULT'
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			status == SC_OK
			isNotEmpty(data.guid)
		}
		return response.data
	}

	private String getGuestUid() {
		def randomUID = System.currentTimeMillis()
		def guestUid = "${randomUID}@test.com"
		return guestUid
	}
}
