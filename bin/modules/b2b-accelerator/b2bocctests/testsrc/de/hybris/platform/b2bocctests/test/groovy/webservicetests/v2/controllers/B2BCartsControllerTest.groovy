/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2bocctests.test.groovy.webservicetests.v2.controllers

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.util.Config
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest

import spock.lang.Unroll

@ManualTest
@Unroll
class B2BCartsControllerTest extends AbstractCartTest {
	static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
	static final String CURRENT_USER = "current"
	static final String ANONYMOUS_USER = "anonymous"
	static final String PRODUCT_CODE = "1225694"
	static final String PRODUCT_CODE_1 = "3225694"
	static final String OCC_OVERLAPPING_PATHS_FLAG = "occ.rewrite.overlapping.paths.enabled"
	static final ENABLED_CONTROLLER_PATH = Config.getBoolean(OCC_OVERLAPPING_PATHS_FLAG, false) ? COMPATIBLE_CONTROLLER_PATH : CONTROLLER_PATH
	static final String CONTROLLER_PATH = "/users"
	static final String COMPATIBLE_CONTROLLER_PATH = "/orgUsers"

	def "B2B Customer should be able to add multiple entries to his cart"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER)

		and: "he requests to add multiple entries to his cart"
		def response = restClient.post(
				path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/current/carts/" + cartId + "/entries/",

				body: ["orderEntries":[
					["product": ["code": PRODUCT_CODE], "quantity": 1],
					["product": ["code": PRODUCT_CODE_1], "quantity": 1]]],
				contentType: JSON,
				requestContentType: JSON)

		then: "A cart entry is created"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.cartModifications.size() == 2
			data.cartModifications[0].entry.product.code == PRODUCT_CODE
			data.cartModifications[0].entry.quantity == 1
			data.cartModifications[1].entry.product.code == PRODUCT_CODE_1
			data.cartModifications[1].entry.quantity == 1
		}
	}

	def "B2B Customer should be able to add multiple entries to his cart, only valid ones are added"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER)

		and: "he requests to add multiple entries to his cart"
		def response = restClient.post(
			path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/current/carts/" + cartId + "/entries/",
			body: ["orderEntries":[
				["product": ["code": PRODUCT_CODE], "quantity": 0],
				["product": ["code": PRODUCT_CODE_1], "quantity": 1]]],
			contentType: JSON,
			requestContentType: JSON)

		then: "A cart entry is created"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.cartModifications.size() == 1
			data.cartModifications[0].entry.product.code == PRODUCT_CODE_1
			data.cartModifications[0].entry.quantity == 1
		}
	}

	def "B2B Customer should be OT able to add multiple entries to his cart, when quantities are invalid"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER)

		and: "he requests to add multiple entries to his cart, providing wrong quantities"
		def response = restClient.post(
			path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/current/carts/" + cartId + "/entries/",
			body: ["orderEntries":[
				["product": ["code": PRODUCT_CODE], "quantity": -1],
				["product": ["code": PRODUCT_CODE_1], "quantity": -1]]],
			contentType: JSON,
			requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_BAD_REQUEST
			data.errors[0].type == "DomainError"
			data.errors[0].message == "The application has encountered an error"
		}
	}

	def "B2B Customer should NOT be able to add entries to his cart not providing product code"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER)

		and: "he requests to add multiple entries to his cart, not providing product code"
		def response = restClient.post(
			path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/current/carts/" + cartId + "/entries/",
			body: ["orderEntries":[
				["quantity": 1],
				["quantity": 1]]],
			contentType: JSON,
			requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_BAD_REQUEST
			data.errors[0].message == "This field is required."
			data.errors[0].reason == "missing"
			data.errors[0].type == "ValidationError"
			data.errors[0].subject == "orderEntries[0].product.code"
			data.errors[1].message == "This field is required."
			data.errors[1].reason == "missing"
			data.errors[1].type == "ValidationError"
			data.errors[1].subject == "orderEntries[1].product.code"
		}
	}

	def "B2B Customer should NOT be able to add entries to his cart not providing quantity"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER)

		and: "he requests to add multiple entries to his cart, not providing quantity"
		def response = restClient.post(
			path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/current/carts/" + cartId + "/entries/",
			body: ["orderEntries":[
				["product": ["code": PRODUCT_CODE]],
				["product": ["code": PRODUCT_CODE_1]]]],
			contentType: JSON,
			requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_BAD_REQUEST
			data.errors[0].message == "This field is required."
			data.errors[0].reason == "missing"
			data.errors[0].type == "ValidationError"
			data.errors[0].subject == "orderEntries[0].quantity"
			data.errors[1].message == "This field is required."
			data.errors[1].reason == "missing"
			data.errors[1].type == "ValidationError"
			data.errors[1].subject == "orderEntries[1].quantity"
		}
	}

	def "B2B Customer should NOT be able to add entries to his cart not providing orderEntries"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER)

		and: "he requests to add multiple entries to his cart, not providing orderEntries"
		def response = restClient.post(
			path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/current/carts/" + cartId + "/entries/",
			body: '{}',
			contentType: JSON,
			requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_BAD_REQUEST
			data.errors[0].message == "This field is required."
			data.errors[0].reason == "missing"
			data.errors[0].type == "ValidationError"
			data.errors[0].subject == "orderEntries"
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
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
			data.type == "cartWsDTO"
		}
		return response.data.code
	}
}
