/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2bocctests.test.groovy.webservicetests.v2.controllers

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_FORBIDDEN
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR
import static org.apache.http.HttpStatus.SC_NOT_ACCEPTABLE
import static org.apache.http.HttpStatus.SC_NOT_FOUND
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED
import static org.apache.http.HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.basecommerce.enums.InStockStatus
import de.hybris.platform.commercefacades.order.CartFacade
import de.hybris.platform.commercefacades.order.QuoteFacade
import de.hybris.platform.commercefacades.voucher.VoucherFacade
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException
import de.hybris.platform.commerceservices.order.CommerceCartModificationException
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import de.hybris.platform.core.Registry
import de.hybris.platform.core.model.order.QuoteModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.order.QuoteService
import de.hybris.platform.ordersplitting.WarehouseService
import de.hybris.platform.product.ProductService
import de.hybris.platform.stock.StockService
import de.hybris.platform.util.Config
import groovyx.net.http.RESTClient
import javolution.testing.AssertionException
import org.joda.time.DateTime
import spock.lang.Unroll

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.concurrent.Callable

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.awaitility.Awaitility.await
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.doCallRealMethod
import static org.mockito.Mockito.doThrow

@ManualTest
@Unroll
class QuoteControllerTest extends AbstractCartTest {
	static final String SPY_ON_QUOTE_FACADE = "spyOnQuoteFacade"
	static final String SPY_ON_VOUCHER_FACADE = "spyOnVoucherFacade"
	static final String SPY_ON_CART_FACADE = "spyOnCartFacade"
	static final MARK_RIVERS = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
	static final WILLIAM_HUNTER = ['id': "william.hunter@rustic-hw.com", 'password': "1234"]
	static final GLEN_HOFER = ["id": "glen.hofer@acme.com", "password": "1234"]
	static final DARRIN_HESSER = ["id": "darrin.hesser@acme.com", "password": "1234"]
	static final String CURRENT_USER = "current"
	static final String ANONYMOUS_USER = "anonymous"
	static final String PRODUCT_CODE = "1225694"
	static final String PRODUCT_CODE_1 = "3225694"
	static final String PRODUCT_CODE_FOR_STOCK_MODIFICATIONS = "2225694"
	static final String EXPIRED_QUOTE_CODE = "testExpiredQuote"
	static final String NULL_EXPIRED_TIME_QUOTE_CODE = "testNullExpiredTimeQuote"
	static final Integer ADD_PRODUCT_QUANTITY = 50
	static final String OCC_OVERLAPPING_PATHS_FLAG = "occ.rewrite.overlapping.paths.enabled"
	static final ENABLED_CONTROLLER_PATH = Config.getBoolean(OCC_OVERLAPPING_PATHS_FLAG, false) ? COMPATIBLE_CONTROLLER_PATH : CONTROLLER_PATH
	static final String CONTROLLER_PATH = "/users"
	static final String COMPATIBLE_CONTROLLER_PATH = "/orgUsers"
	static final String QUOTE_NEW_NAME = "MY_QUOTE_NAME"
	static final String QUOTE_NEW_DESCRIPTION = "MY_QUOTE_DESCRIPTION"
	static final String TOO_LONG_STRING = "a".repeat(256)
	static final String EDGE_LENGTH_STRING = "a".repeat(255)
	static final String QUOTE_NEW_COMMENT = "MY_QUOTE_COMMENT"
	static final String VOUCHER_ID = "SUMMER69"
	static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ"
	static final String QUOTE_EXPIRATION_TIME_TOMORROW = new SimpleDateFormat(DATE_TIME_PATTERN).format(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
	static final String QUOTE_EXPIRATION_TIME_YESTERDAY = new SimpleDateFormat(DATE_TIME_PATTERN).format(new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000)))
	static final String TIME_NOW = new SimpleDateFormat(DATE_TIME_PATTERN).format(new Date())

	def cleanup() {
		final QuoteFacade quoteFacade = Registry.getApplicationContext().getBean(SPY_ON_QUOTE_FACADE, QuoteFacade.class) // it should be an spy of defaultQuoteFacade
		doCallRealMethod().when(quoteFacade).getAllowedActions(any())
		final VoucherFacade voucherFacade = Registry.getApplicationContext().getBean(SPY_ON_VOUCHER_FACADE, VoucherFacade.class) // it should be an spy of VoucherFacade
		doCallRealMethod().when(voucherFacade).releaseVoucher(any())
		final CartFacade cartFacade = Registry.getApplicationContext().getBean(SPY_ON_CART_FACADE, CartFacade.class) // it should be an spy of CartFacade
		doCallRealMethod().when(cartFacade).validateCartData()

		setProductStockLevel(PRODUCT_CODE_FOR_STOCK_MODIFICATIONS, InStockStatus.FORCEINSTOCK)
	}

	def "B2B Customer should be able to create a quote based on his cart"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "A quote is created and carId is included in the returned data"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
			data.state == "BUYER_DRAFT"
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY
			data.cartId != null
		}
	}

	def "When create a quote, a QuoteWsDTO is returned including the threshold value."() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "A quote is created"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
			data.threshold == 1500
		}
	}

	def "B2B Customer should be able to create a quote and return QuoteData according to fields parameter"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a new cart"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to create a quote providing a fields parameter"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				query: [
						'fields': 'state'
				],
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "A quote was created and QuoteData was returned according to fields parameter"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
			data.state == "BUYER_DRAFT"
			data.size() == 1
		}
	}

	def "B2B Customer should fail to create a quote with the wrong cartId"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a quote with a wrong cartId"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": "99999999"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "he can not create a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Cart not found."
			data.errors[0].type == "CartError"
		}
	}

	def "B2B Customer should fail to create a quote with an anonymous cartId"() {
		given: "An anonymous user"
		authorizeTrustedClient(restClient)

		and: "he requests to create an anonymous cartId"
		def cartId = createNewCart(ANONYMOUS_USER)

		when: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to create a quote with the anonymous cartId"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "A B2BCustomer can't create a quote with an anonymous cartId"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Cart not found."
			data.errors[0].type == "CartError"
		}
	}

	def "B2B Customer should fail to create a quote with a cartId not assigned to current user/store"() {
		given: "an anonymous user"
		authorizeTrustedClient(restClient)

		and: "he request to create an anonymous cartId"
		def cartId = createNewCart(ANONYMOUS_USER)

		when: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "A B2BCustomer can't create a quote with a cartId not assigned to current user/store"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Cart not found."
			data.errors[0].type == "CartError"
		}
	}

	def "Internal Server Error should be issued when validation of the cart encountered an error"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a new cart is created"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "validateCartData is configured to fail"
		final CartFacade cartFacade = Registry.getApplicationContext().getBean(SPY_ON_CART_FACADE, CartFacade.class) // it should be an spy of defaultCartFacade
		doThrow(new CommerceCartModificationException("======= This is an Exception =====")).when(cartFacade).validateCartData()

		when: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error message has been produced"
		with(response) {
			status == SC_INTERNAL_SERVER_ERROR
			data.errors[0].message == "The application has encountered an error"
			data.errors[0].type == "CommerceCartModificationError"
		}
	}

	def "B2B Customer should fail to create a quote when cart validation generates errors"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a new cart is created"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE_FOR_STOCK_MODIFICATIONS, ADD_PRODUCT_QUANTITY)

		and: "product becomes out of stock"
		setProductStockLevel(PRODUCT_CODE_FOR_STOCK_MODIFICATIONS, InStockStatus.FORCEOUTOFSTOCK)

		when: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error message has been produced"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].errorCode == "noStock"
			data.errors[0].type == "CartValidationError"
		}
	}

	def "B2B Customer should fail to act when no payload is provided"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a new cart"
		createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "no payload is provided when creating a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				headers: ["Content-Type": JSON])

		then: "Can not create or requote a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Invalid request body"
			data.errors[0].type == "HttpMessageNotReadableError"
		}
	}

	def "When neither cartId nor quoteCode is given, B2B Customer should fail to act"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a new cart"
		createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "Neither cartId nor quoteCode are provided"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: "{}",
				contentType: JSON,
				requestContentType: JSON)

		then: "Can not create or requote a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Either cartId or quoteCode must be provided"
			data.errors[0].type == "IllegalArgumentError"
		}
	}

	def "When cartId and quoteCode are provided at the same time, B2B Customer should fail to act"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "cartId and quoteCode are provided at the same time"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId"   : "00000001",
						"quoteCode": "00000001"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "Can not create or requote a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Either cartId or quoteCode must be provided"
			data.errors[0].type == "IllegalArgumentError"
		}
	}

	def "B2B Customer should fail to create a quote from an empty cart"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create an empty cart"
		def cartId = createNewCart(CURRENT_USER)

		and: "he requests to create a quote by providing a cartId"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId"   : cartId,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "Can not create a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Empty carts are not allowed"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should be able to requote a quote based on quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a comment is added to it"
		addCommentToQuote(CURRENT_USER, quote.code, QUOTE_NEW_COMMENT)

		and: "he requests to cancel that quote"
		cancelQuote(quote.code)

		when: "he requests to requote the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"quoteCode": quote.code
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "A quote is created and quoteCode is included in the returned data"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
			data.state == "BUYER_DRAFT"
			data.code != quote.code
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY
			data.cartId != null && data.cartId != quote.cartId
		}
		def newQuoteCode = response.data.code

		when: "he requests to view the comments of the requoted quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + newQuoteCode,
				query: [
						'fields': 'comments'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment is gone in the requoted quote"
		with(response2) {
			isEmpty(data.comments)
		}
	}

	def "B2B Customer should fail to requote a quote which is not cancelled"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to requote the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"quoteCode": quote.code
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "A quote is created and quoteCode is included in the returned data"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [REQUOTE] is not allowed for quote code [" + quote.code + "] in quote state " +
					"[BUYER_DRAFT] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	def "B2B Customer should fail to requote a quote with an invalid quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to requote a quote with an invalid quoteCode"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"quoteCode": "99999999"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to requote a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "The allowedActions should be returned with the quote when B2B Customer tries to create a quote based on his cart"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a cart"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "A quote is created"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
			data.allowedActions.containsAll(["CANCEL", "EDIT", "SUBMIT"])
			data.allowedActions.size() == 3
		}
	}

	def "Internal Server Error should be issued after creating cart and when calling getAllowedActions"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a new cart is created"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "getAllowedActions is configured to fail"
		final QuoteFacade quoteFacade = Registry.getApplicationContext().getBean(SPY_ON_QUOTE_FACADE, QuoteFacade.class) // it should be an spy of defaultQuoteFacade
		doThrow(new RuntimeException("======= This is an Exception =====")).when(quoteFacade).getAllowedActions(any())

		when: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
					"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error message has been produced"
		with(response) {
			status == SC_INTERNAL_SERVER_ERROR
			data.errors[0].message == "The application has encountered an error"
			data.errors[0].type == "QuoteAssemblingError"
		}
	}

	def "An anonymous user (#scenario) should fail to create a quote for #quoteOwner user"() {
		given: "an anonymous user"
		authorizationMethod(restClient)

		when: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes",
				body: [
						"cartId": "99999999"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to create the quote"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	def "Internal Server Error should be issued when calling removeCoupons before creating quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a new cart is created"
		def cartId = createNewCart(CURRENT_USER)

		and: "a product is added to it"
		addProductToCart(CURRENT_USER, cartId, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a voucher is applied to it"
		applyVoucher(cartId, VOUCHER_ID)

		and: "releaseVoucher is configured to fail"
		final VoucherFacade voucherFacade = Registry.getApplicationContext().getBean(SPY_ON_VOUCHER_FACADE, VoucherFacade.class) // it should be an spy of VoucherFacade
		doThrow(new VoucherOperationException("======= This is an Exception =====")).when(voucherFacade).releaseVoucher(any())

		when: "he requests to create a quote"
		def response2 = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error message has been produced"
		with(response2) {
			status == SC_INTERNAL_SERVER_ERROR
			data.errors[0].message == "The application has encountered an error"
			data.errors[0].type == "VoucherOperationError"
		}
	}

	def "applied vouchers will be removed when initiating a quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a new cart is created"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a voucher is applied to it"
		applyVoucher(cartId, VOUCHER_ID)

		and: "he requests to create a quote based on the cart"
		def quote = createQuote(CURRENT_USER, cartId)

		when: "he requests to get the new cart created for the quote"
		def response3 = restClient.get(
				path: getBasePathWithSite() + "/users/current/carts/" + quote.cartId,
				contentType: JSON,
				requestContentType: JSON)

		then: "no vouchers are applied to the cart"
		with(response3) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			isEmpty(data.appliedVouchers)
		}
	}

	// for edit action
	def "B2B Customer should be able to edit a quote based on quoteCode before the submission"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit this quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote was edited"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view this quote"
		def response2 = getQuote(CURRENT_USER, quote.code)

		then: "the quote's cartId does not change"
		with(response2) {
			cartId == quote.cartId
		}
	}

	def "B2B Customer should fail to edit a quote with the wrong quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to submit a quote with wrong quoteCode"
		def WRONG_QUOTECODE = '9999999'
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + WRONG_QUOTECODE + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should fail to edit a previously submitted quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates and submits a quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit this quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state " +
					"[BUYER_SUBMITTED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	def "B2B Customer should fail to edit a previously cancelled quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he cancels this quote"
		cancelQuote(quote.code)

		when: "he requests to edit this quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state " +
					"[CANCELLED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	def "B2B Customer should fail to edit a quote of another b2b customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "another b2b customer requests to cancel the quote"
		authorizeCustomer(restClient, WILLIAM_HUNTER)
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to cancel the quote created by another b2b customer"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should be able to edit a quote without cartId"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote1 = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he creates another quote from the cart linked to the quote1"
		createQuote(CURRENT_USER, quote1.cartId)

		when: "he requests to get quote1"
		def responseData = getQuote(CURRENT_USER, quote1.code)
		then: "The quote does not have cartId"
		with(responseData) {
			cartId == null
		}

		when: "he requests to edit quote1, which is now without cartId"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote1.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote was edited"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view the edited quote"
		def responseData2 = getQuote(CURRENT_USER, quote1.code)
		then: "The quote with the cartId is returned"
		with(responseData2) {
			isNotEmpty(cartId)
		}

		when: "he requests to view the new cart linked to the quote"
		def responseData3 = getCart(CURRENT_USER, responseData2.cartId)
		then: "this cart contains the same elements as a quote"
		with(responseData3) {
			entries[0].product.code == PRODUCT_CODE
			entries[0].quantity == ADD_PRODUCT_QUANTITY
		}
	}

	def "B2B customer should not be able to edit a quote in DRAFT mode created by B2B seller on his behalf"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to create a quote using the customers cart"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		when: "B2B customer tries to edit the quote created by B2B seller"
		authorizeCustomer(restClient, MARK_RIVERS)
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B customer should fail to do so"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should be able to edit a quote after the approval from the customer manager, but all the discounts must be removed"() {
		given: "an approved quote with 50% discount"
		def quote = createQuoteAndApplyDiscount(MARK_RIVERS, DARRIN_HESSER, "PERCENT", 50);

		when:"when customer manager gets the quote"
		authorizeCustomerManager(restClient, DARRIN_HESSER)
		def response1 = getQuote(MARK_RIVERS.id, quote.code)
		then: "the content of quote is returned with included discounts"
		with(response1) {
			state == "SELLERAPPROVER_APPROVED"
			quoteDiscounts.value == 1250.0
		}

		when: "B2B customer edits the quote"
		authorizeCustomer(restClient, MARK_RIVERS)
		restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		and: "and B2B customer gets a quote"
		def response2 = getQuote(MARK_RIVERS.id, quote.code)
		then: "the quote was edited and all discounts were removed"
		with(response2) {
			state == "BUYER_DRAFT"
			quoteDiscounts.value == 0
		}
	}

	def "B2B Seller should be able to edit a quote if it has been submitted by a buyer in the first place"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "the customer creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "the seller requests to edit the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The quote was edited"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT is in the list of allowedActions"
		with(quoteData) {
			allowedActions.contains("EDIT")
		}
	}

	def "B2B Seller should be able to edit a quote in DRAFT mode created by himself/herself"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to create a quote using the customers cart"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		when: "the seller tries to edit the quote created by B2B seller"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The quote was edited"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT is in the list of allowedActions"
		with(quoteData) {
			allowedActions.contains("EDIT")
		}
	}

	def "B2B Seller should be able to edit a quote if it has been rejected by an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "the approver requests to reject the quote"
		rejectQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		await().pollInterval(Duration.ofMillis(500)).atMost(Duration.ofSeconds(5)).ignoreException(AssertionException.class).until(new Callable<Boolean>() {
			@Override
			Boolean call() throws Exception {
				when: "the seller requests to edit the quote"
				def response = restClient.post(
						path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
						body: [
								"action": "EDIT"
						],
						contentType: JSON,
						requestContentType: JSON)

				then: "The quote was edited"
				with(response) {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) {
						println(data)
					}
					assertTrue(status == SC_OK, "httpResponseStatus")
				}
				return true
			}
		})

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT is in the list of allowedActions"
		with(quoteData) {
			allowedActions.contains("EDIT")
		}
	}

	def "B2B Seller should fail to edit a quote if it hasn't been submitted by a buyer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "the customer creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "the seller requests to edit the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote can't be edited"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Seller should fail to edit a quote if it has been cancelled by a buyer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "the customer creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "the customer requests to cancel the quote"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "the seller requests to edit the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote can't be edited"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [CANCELLED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT isn't in the list of allowedActions"
		with(quoteData) {
			isEmpty(allowedActions)
		}
	}

	def "B2B Seller should fail to edit a quote if it has been approved by an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "the approver requests to reject the quote"
		approveQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "the seller requests to edit the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote can't be edited"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_APPROVED] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT isn't in the list of allowedActions"
		with(quoteData) {
			isEmpty(allowedActions)
		}
	}

	def "B2B Seller should fail to edit a quote if that quote is in SELLERAPPROVER_APPROVED state with auto approved"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "the customer creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "the seller submits the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		when: "the seller requests to edit the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote can't be edited"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_APPROVED] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT isn't in the list of allowedActions"
		with(quoteData) {
			isEmpty(allowedActions)
		}
	}

	def "B2B Seller should fail to edit a quote if the quote is pending for approver's approval"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "the seller requests to edit the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote can't be edited"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_PENDING] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT isn't in the list of allowedActions"
		with(quoteData) {
			isEmpty(allowedActions)
		}
	}

	def "B2B Seller should fail to edit a quote if the quote has been submitted by himself/herself"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "the customer creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "the seller submits the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		when: "the seller requests to edit the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "EDIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote can't be edited"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_APPROVED] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "the seller requests to get the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "action EDIT isn't in the list of allowedActions"
		with(quoteData) {
			isEmpty(allowedActions)
		}
	}

	// for submit
	def "B2B Customer should be able to submit a quote based on quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to submit that quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote was submitted"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
	}

	def "B2B Customer should fail to submit a quote when cart validation generates errors"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE_FOR_STOCK_MODIFICATIONS, ADD_PRODUCT_QUANTITY)

		and: "product becomes out of stock"
		setProductStockLevel(PRODUCT_CODE_FOR_STOCK_MODIFICATIONS, InStockStatus.FORCEOUTOFSTOCK)

		when: "he requests to submit that quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error message has been produced"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].errorCode == "noStock"
			data.errors[0].type == "CartValidationError"
		}
	}

	def "B2B Customer should fail to perform an unknown action on a quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to submit a quote with an unknown action"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "unknown_action"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Provided action not supported"
			data.errors[0].type == "IllegalArgumentError"
		}
	}

	def "B2B Customer should fail to submit a quote with the wrong quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to submit a quote with wrong quoteCode"
		def WRONG_QUOTECODE = '9999999'
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + WRONG_QUOTECODE + "/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should fail to submit a quote with the quote value is lower than the quote threshold"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, 1)

		when: "he requests to submit a quote with the quote value is lower than the quote threshold"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote with code [" + quote.code + "] and version [1] does not meet the threshold."
			data.errors[0].type == "QuoteUnderThresholdError"
		}
	}

	def "B2B Customer should fail to submit a quote when the quote was previously successfully submitted"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to submit the same quote again"
		def response2 = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response2) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [BUYER_SUBMITTED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	def "An anonymous user (#scenario) should fail to submit a quote for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "he requests to submit a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/12345678/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to submit the quote"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	def "Internal Server Error should be issued when calling removeCoupons before submitting quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a voucher is applied to the cart related to the quote just created"
		applyVoucher(quote.cartId, VOUCHER_ID)

		and: "releaseVoucher is configured to fail"
		final VoucherFacade voucherFacade = Registry.getApplicationContext().getBean(SPY_ON_VOUCHER_FACADE, VoucherFacade.class) // it should be an spy of VoucherFacade
		doThrow(new VoucherOperationException("======= This is an Exception =====")).when(voucherFacade).releaseVoucher(any())

		when: "he requests to submit the quote"
		def response3 = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error message has been produced"
		with(response3) {
			status == SC_INTERNAL_SERVER_ERROR
			data.errors[0].message == "The application has encountered an error"
			data.errors[0].type == "VoucherOperationError"
		}
	}

	def "applied vouchers will be removed when submitting a quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a voucher is applied to the cart"
		applyVoucher(quote.cartId, VOUCHER_ID)

		and: "he submits the quote"
		submitQuote(CURRENT_USER, quote.code)

		when: "the quote is checked"
		final QuoteService quoteService = Registry.getApplicationContext().getBean("quoteService", QuoteService.class)
		final QuoteModel quoteModel = quoteService.getCurrentQuoteForCode(quote.code)

		then: "The content of the specific quote was returned and without any vouchers applied to it"
		isEmpty(quoteModel.getAppliedCouponCodes())
	}

	def "B2B seller should be able to submit a quote back to the B2B customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to submit the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		when:"he wants to get the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "the content of quote is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "SELLERAPPROVER_APPROVED"
		}

		when:"the B2B customer wants to get the quote"
		authorizeCustomer(restClient, MARK_RIVERS)
		def response3 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "the content of quote is returned and the B2B customer can then proceed to checkout"
		with(response3) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "BUYER_OFFER"
			data.allowedActions.containsAll(["CANCEL","EDIT","CHECKOUT"])
		}
	}

	def "A seller submitted quote will be sent to the approver if the totalPrice of the quote is too high"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to submit the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when:"he requests to get the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "the content of quote is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "SELLERAPPROVER_PENDING"
		}
	}

	// for cancel
	def "B2B Customer should be able to cancel a quote based on quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to cancel that quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The quote was cancelled"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view the quote just cancelled"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The quote with the CANCELLED state is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "CANCELLED"
			data.cartId == null
		}

		when: "he requests to get the cart"
		def response3 = restClient.get(
				path: getBasePathWithSite() + "/users/current/carts/" + quote.cartId,
				contentType: JSON,
				requestContentType: JSON)

		then: "The cart has been removed"
		with(response3) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Cart not found."
			data.errors[0].type == "CartError"
		}
	}

	def "B2B #role should fail to cancel a quote before it has been submitted"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "#role tries to cancel the quote"
		authorizeCustomerManager(restClient, manager)
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}

		where:
		role       | manager
		"seller"   | DARRIN_HESSER
		"approver" | GLEN_HOFER
	}

	def "B2B Customer should fail to cancel a quote with the wrong quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to cancel a quote with wrong quoteCode"
		def WRONG_QUOTECODE = '9999999'
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + WRONG_QUOTECODE + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should fail to cancel a quote when the quote was previously successfully cancelled"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to cancel that quote"
		cancelQuote(quote.code)

		when: "he requests to cancel the cancelled quote"
		def response2 = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		with(response2) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [CANCEL] is not allowed for quote code [" + quote.code + "] in quote state " +
					"[CANCELLED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	def "B2B Customer should fail to cancel a quote of another b2b customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		authorizeCustomer(restClient, WILLIAM_HUNTER)
		when: "another b2b customer requests to cancel the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to cancel the quote created by another b2b customer"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B #role should fail to cancel a quote when it is already been submitted by customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates and submits a quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "#role tries to cancel the quote"
		authorizeCustomerManager(restClient, manager)
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "An error is thrown"
		def message = errorMessage.replaceAll("_QUOTE_CODE_", quote.code)
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == message
			data.errors[0].type == errorType
		}

		where:
		role       | manager       | errorType                | errorMessage
		"seller"   | DARRIN_HESSER | "IllegalQuoteStateError" | "Action [CANCEL] is not allowed for quote code [_QUOTE_CODE_] in quote state [SELLER_REQUEST] having version [2]."
		"approver" | GLEN_HOFER    | "QuoteError"             | "Quote not found"
	}

	// for getQuote
	def "B2B Customer should be able to see the content of a specific quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to view the quote just created"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				query: [
						'fields': 'state'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "BUYER_DRAFT"
			data.size() == 1
		}
	}

	def "B2B Customer should be able to see the content of a specific quote in a readonly state"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to view the quote just submitted"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned and it's allowedActions only contains VIEW"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "BUYER_SUBMITTED"
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY
			isEmpty(data.allowedActions)
		}
	}

	def "The threshold and cartId should be returned with the quote when B2B Customer try to see the content of a specific quote "() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to view the quote just created"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.threshold == 1500
			data.cartId != null
		}
	}

	def "The allowedActions should be returned with the quote when B2B Customer tries to see the content of a specific quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to view the quote just created"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.allowedActions.containsAll(["CANCEL", "EDIT", "SUBMIT"])
			data.allowedActions.size() == 3
		}
	}

	def "Internal Server Error should be issued when calling getAllowedActions"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "getAllowedActions is configured to fail"
		final QuoteFacade quoteFacade = Registry.getApplicationContext().getBean(SPY_ON_QUOTE_FACADE, QuoteFacade.class) // it should be an spy of defaultQuoteFacade
		doThrow(new RuntimeException("======= This is an Exception =====")).when(quoteFacade).getAllowedActions(any())

		when: "he requests to view the quote just created"
		def response = restClient.get(
			path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
			contentType: JSON,
			requestContentType: JSON)

		then: "An error message has been produced"
		with(response) {
			status == SC_INTERNAL_SERVER_ERROR
			data.errors[0].message == "The application has encountered an error"
			data.errors[0].type == "QuoteAssemblingError"
		}
	}

	def "B2B Customer should fail to see the content of a specific quote When the quote is not belonging to the user"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		authorizeCustomer(restClient, WILLIAM_HUNTER)
		when: "another b2b customer requests to view the quote"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to see the content of the specific quote"
		with(response) {
			status == SC_NOT_FOUND
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "NotFoundError"
		}
	}

	def "B2B Customer should fail to see the content of a specific quote When the quote is not belonging to the site"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "The b2b customer requests to view the quote in another base site"
		def response = restClient.get(
				path: getBasePath() + "/wsTestB2B/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to see the content of the specific quote"
		with(response) {
			status == SC_NOT_FOUND
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "NotFoundError"
		}
	}

	def "B2B Customer should fail to see the content of a specific quote with a quoteCode that doesn't exist"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to see the content of a specific quote"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/99999999",
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to see the content of the specific quote"
		with(response) {
			status == SC_NOT_FOUND
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "NotFoundError"
		}
	}

	def "An anonymous user (#scenario) should fail to see the content of a specific quote for #quoteOwner user"() {
		when: "he requests to view the specific quote"
		authorizationMethod(restClient)

		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/12345678",
				query: [
						'fields': 'state'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to see the content of the specific quote"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	def "B2B Customer should be able to see the name and description of the quote that has not been edited"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to view the name of the quote"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the quote is returned including name and description"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.name == "Quote " + quote.code
			data.description == null
		}
	}

	def "totals are honoured after adding different products to the related cart after quote has been created"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a new cart is created"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a quote is created for that cart"
		def quote = createQuote(CURRENT_USER, cartId)

		when: "he requests to view the quote just created"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.totalItems == 1
			data.totalPrice.value == 2500.0
			data.totalPriceWithTax.value == 2500.0
			data.updatedTime != null
		}

		Thread.sleep(1000)

		and: "different products are added to the related cart"
		addProductToCart(CURRENT_USER, response.data.cartId, PRODUCT_CODE_1, 2)

		when: "he requests to view the quote after adding a different product"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned including changes of totals and updatedTime"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.totalItems == 2
			data.totalPrice.value == 2510.0
			data.totalPriceWithTax.value == 2510.0
			data.updatedTime != quote.updatedTime
		}
	}

	def "B2B Customer should not be able to see seller's update to the quote in BUYER_SUBMITTED state"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates and submits a quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a seller tries to add a comment to the quote"
		authorizeCustomerManager(restClient, DARRIN_HESSER)
		addCommentToQuote(MARK_RIVERS.id, quote.code, QUOTE_NEW_COMMENT)

		when: "the customer requests to view the quote he submitted previously"
		authorizeCustomer(restClient, MARK_RIVERS)
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned without seller's changes"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "BUYER_SUBMITTED"
			data.cartId == null
			data.updatedTime == quote.updatedTime
			isEmpty(data.comments)
		}
	}

	def "when B2B Customer perform checkout to a quote in BUYER_OFFER state, seller and approver can't see updates to the quote"() {
		given: "a registered and logged in B2B customer creates and submits a quote"
		authorizeCustomer(restClient, MARK_RIVERS)
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a seller submits the quote back to the customer"
		authorizeCustomerManager(restClient, DARRIN_HESSER)
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "the customer requests to checkout the quote"
		authorizeCustomer(restClient, MARK_RIVERS)
		checkoutQuote(MARK_RIVERS.id, quote.code)

		when: "the customer requests to view the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "he can see an assigned cart"
		with(quoteData) {
			state == "BUYER_OFFER"
			cartId != null
			updatedTime != null
		}

		when: "the seller requests to view the quote"
		authorizeCustomerManager(restClient, DARRIN_HESSER)
		def response = getQuote(MARK_RIVERS.id, quote.code)
		then: "The content of the specific quote was returned without buyer's changes"
		with(response) {
			state == "SELLERAPPROVER_APPROVED"
			cartId == null
			updatedTime != quoteData.updatedTime
		}

		when: "an approver requests to view the quote"
		authorizeCustomerManager(restClient, GLEN_HOFER)
		def response2 = getQuote(MARK_RIVERS.id, quote.code)
		then: "The content of the specific quote was returned without buyer's changes"
		with(response2) {
			state == "SELLERAPPROVER_APPROVED"
			cartId == null
			updatedTime != quoteData.updatedTime
		}
	}

	def "when B2B Customer requests to get a quote in BUYER_OFFER state, quote content should be returned rather than cart content"() {
		given: "a registered and logged in B2B customer requests to create a quote and submit it"
		authorizeCustomer(restClient, MARK_RIVERS)
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller submit the quote back to the customer"
		authorizeCustomerManager(restClient, DARRIN_HESSER)
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "the customer requests to checkout the quote"
		authorizeCustomer(restClient, MARK_RIVERS)
		checkoutQuote(CURRENT_USER, quote.code)

		when: "the customer requests to view the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "he can see an assigned cart and the content of the specific quote"
		with(quoteData) {
			state == "BUYER_OFFER"
			cartId != null
			totalPrice == quoteData.totalPrice
			version == 4
		}
	}

	// get all quotes
	def "B2B Customer should be able to see all his quotes"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes",
				contentType: JSON,
				requestContentType: JSON)

		then: "a paginated list of the quotes is returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.pagination.currentPage == 0
			data.pagination.pageSize == 20
			data.pagination.sort == "byCode"
			data.quotes.size() > 0
		}
	}

	def "B2B Customer should be able to get an empty list of quotes when he has not created quote"() {
		given: "a user haven't created any quote"
		authorizeCustomer(restClient, WILLIAM_HUNTER)

		when: "he requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes",
				contentType: JSON,
				requestContentType: JSON)

		then: "an empty list of the quotes is returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			isEmpty(data.quotes)
			data.pagination.totalResults == 0
		}
	}

	def "An anonymous user (#scenario) should not be able to see any quotes for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "he tries to see his quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes",
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to see any quotes"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	def "B2B Customer should be able to only see the fields specific in the query parameter"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes",
				query: [
						'fields': 'quotes'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "only the fields specific in the query parameter are returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.pagination == null
			data.quotes.size() > 0
		}
	}

	def "B2B seller should NOT be able to get the list of quotes that have not been submitted yet"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should NOT be able to get the list of quotes that have not been submitted yet"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.state == 'BUYER_DRAFT' }.isEmpty()
		}
	}

	def "B2B seller should be able to get the list of quotes that have been submitted by the customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the list of quotes that have been submitted by the customer"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLER_REQUEST"
		}
	}

	def "B2B seller should be able to get the list of quotes that have been sent to an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the list of quotes that have been sent to an approver"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLERAPPROVER_PENDING"
		}
	}

	def "B2B seller should be able to get the list of APPROVED quotes"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "approver requests to APPROVED the quote"
		approveQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the list of APPROVED quotes"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLERAPPROVER_APPROVED"
		}
	}

	def "B2B seller should be able to get the list of REJECTED quotes"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "approver requests to REJECTED the quote"
		rejectQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		await().pollInterval(Duration.ofMillis(500)).atMost(Duration.ofSeconds(5)).ignoreException(AssertionException.class).until(new Callable<Boolean>() {
			@Override
			Boolean call() throws Exception {
				when: "B2B seller requests to get all quotes"
				def response = restClient.get(
						path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
						query: [
								'sort': 'byDate'
						],
						contentType: JSON,
						requestContentType: JSON)

				then: "B2B seller should be able to get the list of REJECTED quotes"
				with(response) {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) {
						println(data)
					}
					assertTrue(status == SC_OK, "httpResponseStatus")
					assertTrue(data.quotes.findAll { it.code == quote.code }.state[0] == "SELLER_REQUEST", "quoteStatus")
				}
				return true
			}
		})
	}


	private static void assertTrue(boolean passed, String identifier) {
		if (!passed) {
			throw new AssertionException(String.format("Assert failed: %s", identifier))
		}
	}

	def "B2B seller should be able to get the list of quotes that were submitted back to the customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "seller submit the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		when: "B2B seller requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the list of quotes that were submitted back to the customer"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLERAPPROVER_APPROVED"
		}
	}

	def "B2B seller should be able to get the list of cancelled quotes"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "customer cancel this quote"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the list of cancelled quotes"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "CANCELLED"
		}
	}

	def "B2B seller should be able to get the list quotes in DRAFT mode that were created by himself on behalf of the customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to create a quote using the customers cart"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		when: "B2B seller requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the quotes created by himself"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLER_DRAFT"
		}
	}

	def "When perform getQuotes(), a sales rep sees different states than a buyer or an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "the approver requests to REJECTED the quote"
		rejectQuote(MARK_RIVERS.id, quote.code)

		when: "the approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the B2B approver sees the quote in the state of SELLERAPPROVER_REJECTED"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLERAPPROVER_REJECTED"
		}

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "the B2B customer requests to get all quotes"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the B2B customer sees the quote in the state of BUYER_SUBMITTED"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "BUYER_SUBMITTED"
		}

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to get all quotes"
		def response3 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the B2B seller sees the quote in the state of SELLER_REQUEST"
		with(response3) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLER_REQUEST"
		}
	}

	// for edit a quote
	def "B2B Customer should be able to edit the quote's name using #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit the name of the quote"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				body: [
						"name": newName,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The name of the quote is updated"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view the quote just edited"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The new name of the quote is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.name == getNewName
		}
		where:
		scenario                    | newName            | getNewName
		"a new valid string"        | QUOTE_NEW_NAME     | QUOTE_NEW_NAME
		"a 255 chars length string" | EDGE_LENGTH_STRING | EDGE_LENGTH_STRING
	}

	def "B2B Customer should be able to edit the quote's description using #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit the description of the quote"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				body: [
						"description": newDescription
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The description of the quote is updated"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view the quote just edited"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The new description of the quote is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.description == getNewDescription
		}
		where:
		scenario                    | newDescription        | getNewDescription
		"a new valid string"        | QUOTE_NEW_DESCRIPTION | QUOTE_NEW_DESCRIPTION
		"a 255 chars length string" | EDGE_LENGTH_STRING    | EDGE_LENGTH_STRING
	}

	def "B2B Customer should be able to edit the quote's name and description #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit the name and description of the quote"
		def response = editQuote(restClient, "current", quote.code, ["name": QUOTE_NEW_NAME, "description": QUOTE_NEW_DESCRIPTION])

		then: "The name and description of the quote are updated"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view the quote just edited"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The new name and description of the quote are returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.name == QUOTE_NEW_NAME
			data.description == QUOTE_NEW_DESCRIPTION
		}

		where:
		scenario    | editQuote
		"via PATCH" | this.&updateQuote
		"via PUT"   | this.&replaceQuote
	}

	def "B2B Customer should be able to edit the cart entries after quote created, and the cart entries were synced to quote before submitted"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "one more piece of same product is added to it"
		addProductToCart(CURRENT_USER, quote.cartId, PRODUCT_CODE, 1)

		when: "he requests to view the quote to see if cart info synced"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The updated cart entry of the quote is returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.cartId == quote.cartId
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY + 1
		}
	}

	def "B2B Customer should fail to give the quote #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to give the quote #scenario"
		def response = editQuote(restClient, 'current', quote.code, ["name": newNameContent, "description":newDescriptionContent])

		then: "fail to edit the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == message
			data.errors[0].type == type
		}

		where:
		scenario                           | editQuote          | newNameContent  | newDescriptionContent | message                                                    | type
		"an empty name via PATCH"          | this.&updateQuote  | ""              | null                  | "This field is required."                                  | "ValidationError"
		"a blank name via PATCH"           | this.&updateQuote  | " "             | null                  | "This field is required."                                  | "ValidationError"
		"a too long name via PATCH"        | this.&updateQuote  | TOO_LONG_STRING | null                  | "This field must to be between 0 and 255 characters long." | "ValidationError"
		"a too long description via PATCH" | this.&updateQuote  | null            | TOO_LONG_STRING       | "This field must to be between 0 and 255 characters long." | "ValidationError"
		"an empty name via PUT"            | this.&replaceQuote | ""              | QUOTE_NEW_DESCRIPTION | "This field is required."                                  | "ValidationError"
		"a blank name via PUT"             | this.&replaceQuote | " "             | QUOTE_NEW_DESCRIPTION | "This field is required."                                  | "ValidationError"
		"a too long name via PUT"          | this.&replaceQuote | TOO_LONG_STRING | QUOTE_NEW_DESCRIPTION | "This field must to be between 0 and 255 characters long." | "ValidationError"
		"a too long description via PUT"   | this.&replaceQuote | QUOTE_NEW_NAME  | TOO_LONG_STRING       | "This field must to be between 0 and 255 characters long." | "ValidationError"
	}

	def "#role should fail to edit a quote when using a non-existent quoteCode"() {
		given: "a registered and logged in #role"
		authorizationMethod(restClient, user)

		when: "he requests to edit the quote"
		def response = editQuote(restClient, MARK_RIVERS.id, "99999999", payload)

		then: "fail to edit the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}

		where:
		role           | user          | authorizationMethod            | editQuote          | payload
		"B2B customer" | MARK_RIVERS   | this.&authorizeCustomer        | this.&replaceQuote | ["name": QUOTE_NEW_NAME, "description": QUOTE_NEW_DESCRIPTION]
		"B2B customer" | MARK_RIVERS   | this.&authorizeCustomer        | this.&updateQuote  | ["name": QUOTE_NEW_NAME, "description": QUOTE_NEW_DESCRIPTION]
		"B2B seller"   | DARRIN_HESSER | this.&authorizeCustomerManager | this.&updateQuote  | ["expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW]
		"B2B seller"   | DARRIN_HESSER | this.&authorizeCustomerManager | this.&replaceQuote | ["expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW]
	}

	def "B2B Customer should fail to edit a quote when using a non-existent baseSiteId"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)
		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)
		when: "he requests to edit the quote with a non-existent baseSiteId"
		def response = restClient.patch(
				path: getBasePath() + "/wrongBaseSiteId/users/current/quotes/" + quote.code,
				body: [
						"name": QUOTE_NEW_NAME
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "fail to edit the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Base site wrongBaseSiteId doesn't exist"
			data.errors[0].type == "InvalidResourceError"
		}
	}

	def "B2B Customer should fail to edit a quote with using a non-existent userId "() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit the quote with a non-existent userId"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/nonExistentUserId/quotes/" + quote.code,
				body: [
						"name": QUOTE_NEW_NAME
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to edit the quote"
		with(response) {
			status == SC_FORBIDDEN
			data.errors[0].message == "Access is denied"
			data.errors[0].type == "ForbiddenError"
		}
	}

	def "An anonymous user (#scenario) should not be able to edit a quote for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "he requests to edit the quote"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/12345678",
				body: [
						"name": QUOTE_NEW_NAME,
						"description": QUOTE_NEW_DESCRIPTION
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to edit the quote"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	def "B2B Customer should fail to edit a quote which is not editable"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit the quote"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				body: [
						"name"       : QUOTE_NEW_NAME,
						"description": QUOTE_NEW_DESCRIPTION
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to edit the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [BUYER_SUBMITTED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "the description and name are not changed"
		with(quoteData) {
			name == quote.name
			description == quote.description
		}
	}

	def "B2B seller should be able to edit a quote's expiration time using #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to edit the expiration time of the quote"
		def response = editQuote(restClient, MARK_RIVERS.id, quote.code, ["expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW])

		then: "The expiration time of the quote is updated"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "The new expiration time of the quote is returned"
		with(quoteData) {
			new DateTime(expirationTime) == new DateTime(QUOTE_EXPIRATION_TIME_TOMORROW)
		}

		where:
		scenario                                  | editQuote
		"a valid date time after today via PATCH" | this.&updateQuote
		"a valid date time after today via PUT"   | this.&replaceQuote
	}

	def "B2B seller should #action a quote's expiration time #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to edit the expiration time of the quote"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				body: [
						"expirationTime"       : QUOTE_EXPIRATION_TIME_TOMORROW
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The expiration time of the quote is updated"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to clear the expiration time of the quote"
		def response2 = editQuote(restClient, MARK_RIVERS.id, quote.code, payload)

		then: "The expiration time of the quote is updated"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "The expiration time of the quote is returned"
		with(quoteData) {
			if (expirationTime != null) {
				new DateTime(expirationTime) == newExpirationTime
			} else {
				expirationTime == newExpirationTime
			}
		}

		where:
		action                 | scenario                       | editQuote          | payload                  | newExpirationTime
		"not be able to clear" | "with null value via PATCH"    | this.&updateQuote  | ["expirationTime": null] | new DateTime(QUOTE_EXPIRATION_TIME_TOMORROW)
		"not be able to clear" | "with empty payload via PATCH" | this.&updateQuote  | [:]                      | new DateTime(QUOTE_EXPIRATION_TIME_TOMORROW)
		"be able to clear"     | "with null value via PUT"      | this.&replaceQuote | ["expirationTime": null] | null
		"be able to clear"     | "with empty payload via PUT"   | this.&replaceQuote | [:]                      | null
	}

	def "B2B Customer should #action the quote's #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit the name and description of the quote"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				body: [
						"name"       : QUOTE_NEW_NAME,
						"description": QUOTE_NEW_DESCRIPTION
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The name and description of the quote are updated"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "he requests to clear the #scenario"
		def response2 = editQuote(restClient, MARK_RIVERS.id, quote.code, payload)

		then: "The quote is updated"
		with(response2) {
			status == updateStatus
			if (data && data.errors && data.errors.size() > 0) {
				data.errors[0].message == "Name is required."
				data.errors[0].type == "IllegalArgumentError"
			}
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "The description and name of the quote is returned"
		with(quoteData) {
			name == QUOTE_NEW_NAME
			description == getNewDescription
		}

		where:
		action                 | scenario                                   | editQuote          | payload                                              | updateStatus   | getNewDescription
		"not be able to clear" | "description with null value via PATCH"    | this.&updateQuote  | ["description": null]                                | SC_OK          | QUOTE_NEW_DESCRIPTION
		"not be able to clear" | "description with empty payload via PATCH" | this.&updateQuote  | [:]                                                  | SC_OK          | QUOTE_NEW_DESCRIPTION
		"be able to clear"     | "description with null value via PUT"      | this.&replaceQuote | ["name": QUOTE_NEW_NAME, "description": null]        | SC_OK          | null
		"be able to clear"     | "description with only name via PUT"       | this.&replaceQuote | ["name": QUOTE_NEW_NAME]                             | SC_OK          | null
		"not be able to clear" | "name with only description via PUT"       | this.&replaceQuote | ["description": QUOTE_NEW_DESCRIPTION]               | SC_BAD_REQUEST | QUOTE_NEW_DESCRIPTION
		"not be able to clear" | "name with null value via PUT"             | this.&replaceQuote | ["name": null, "description": QUOTE_NEW_DESCRIPTION] | SC_BAD_REQUEST | QUOTE_NEW_DESCRIPTION
	}

	def "B2B Seller should fail to set expiration time for a quote when using a non-existent baseSiteId"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to set expiration time for the quote with a non-existent baseSiteId"
		def response = restClient.put(
				path: getBasePath() + "/wrongBaseSiteId/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				body: [
						"expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to set expiration time for the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Base site wrongBaseSiteId doesn't exist"
			data.errors[0].type == "InvalidResourceError"
		}
	}

	def "B2B Seller should fail to set expiration time for a quote using a non-existent userId"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to set expiration time for the quote with a non-existent userId"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/nonExistentUserId/quotes/" + quote.code,
				body: [
						"expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to set expiration time for the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Cannot find user with propertyValue 'nonExistentUserId'"
			data.errors[0].type == "UnknownIdentifierError"
		}
	}

	def "B2B Seller should fail to edit expiration time for a quote which is not editable"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he submits the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "he requests to get the quote"
		def quote2 = getQuote(MARK_RIVERS.id, quote.code)

		when: "he requests to edit expiration time for the quote"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				body: [
						"expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to edit expiration time for the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_APPROVED] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "the expiration time is not changed"
		with(quoteData) {
			new DateTime(expirationTime) == new DateTime(quote2.expirationTime)
		}
	}

	def "B2B Seller should fail to edit expiration time for a quote using #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to edit expiration time for the quote"
		def response = editQuote(restClient, MARK_RIVERS.id, quote.code, ["expirationTime": date])

		then: "fail to edit expiration time for the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "the expiration time is not changed"
		with(quoteData) {
			expirationTime == null
		}

		where:
		scenario                        | editQuote          | date                            | errorMessage                                                             | errorType
		"an invalid date via PUT"       | this.&replaceQuote | "223423566"                     | "Invalid request body"                                                   | "HttpMessageNotReadableError"
		"a date before today via PUT"   | this.&replaceQuote | QUOTE_EXPIRATION_TIME_YESTERDAY | getExpirationTimeValidationErrorMessage(QUOTE_EXPIRATION_TIME_YESTERDAY) | "IllegalArgumentError"
		"today via PUT"                 | this.&replaceQuote | TIME_NOW                        | getExpirationTimeValidationErrorMessage(TIME_NOW)                        | "IllegalArgumentError"
		"an invalid date via PATCH"     | this.&updateQuote  | "223423566"                     | "Invalid request body"                                                   | "HttpMessageNotReadableError"
		"a date before today via PATCH" | this.&updateQuote  | QUOTE_EXPIRATION_TIME_YESTERDAY | getExpirationTimeValidationErrorMessage(QUOTE_EXPIRATION_TIME_YESTERDAY) | "IllegalArgumentError"
		"today via PATCH"               | this.&updateQuote  | TIME_NOW                        | getExpirationTimeValidationErrorMessage(TIME_NOW)                        | "IllegalArgumentError"
	}

	def "B2B seller should not be able to edit name or description of a quote #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to edit name of the quote"
		def response = editQuote(restClient, MARK_RIVERS.id, quote.code, ["name": QUOTE_NEW_NAME])

		then: "fail to edit name of the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "User not allowed to change name or description"
			data.errors[0].type == "IllegalArgumentError"
		}

		when: "he requests to view the quote just edited"
		def response2 = getQuote(MARK_RIVERS.id, quote.code)

		then: "the name is not changed"
		with(response2) {
			name == quote.name
		}

		when: "he requests to edit description of the quote"
		def response3 = editQuote(restClient, MARK_RIVERS.id, quote.code, ["description": QUOTE_NEW_DESCRIPTION])

		then: "fail to edit description of the quote"
		with(response3) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "User not allowed to change name or description"
			data.errors[0].type == "IllegalArgumentError"
		}

		when: "he requests to view the quote just edited"
		def response4 = getQuote(MARK_RIVERS.id, quote.code)

		then: "the description is not changed"
		with(response4) {
			description == quote.description
		}

		where:
		scenario    | editQuote
		"via PUT"   | this.&replaceQuote
		"via PATCH" | this.&updateQuote
	}

	def "B2B customer should not be able to edit expiration time of a quote #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to edit expiration time for the quote"
		def response = editQuote(restClient, MARK_RIVERS.id, quote.code, ["expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW])

		then: "fail to edit expiration time for the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "User not allowed to change expiration date"
			data.errors[0].type == "IllegalArgumentError"
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "the expiration time is not changed"
		with(quoteData) {
			expirationTime == null
		}

		where:
		scenario    | editQuote
		"via PUT"   | this.&replaceQuote
		"via PATCH" | this.&updateQuote
	}

	def "B2B approver should not be able to edit expiration time of a quote #scenario"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "he requests to view the quote"
		def quote2 = getQuote(MARK_RIVERS.id, quote.code)

		when: "he requests to edit expiration time for the quote"
		def response = editQuote(restClient, MARK_RIVERS.id, quote.code, ["expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW])

		then: "fail to edit expiration time for the quote"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].message == "Access is denied"
			data.errors[0].type == "AccessDeniedError"
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "the expiration time is not changed"
		with(quoteData) {
			new DateTime(expirationTime) == new DateTime(quote2.expirationTime)
		}

		where:
		scenario    | editQuote
		"via PUT"   | this.&replaceQuote
		"via PATCH" | this.&updateQuote
	}

	def "An anonymous user (#scenario) should not be able to edit expiration time of a quote for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "he requests to edit the expiration time of a quote"
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/12345678",
				body: [
						"expirationTime": QUOTE_EXPIRATION_TIME_TOMORROW
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to edit the expiration time of the quote"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	//	 add a comment to quote
	def "B2B Customer should be able to add a comment to his quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment is added"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}

		when: "he requests to view the comments of the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				query: [
						'fields': 'comments'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment just added to the quote is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			response2.data.comments[0].text == QUOTE_NEW_COMMENT
		}
	}

	def "B2B Customer should be able to add a comment to his quote and get his comments according to fields parameter"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment is added"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}

		when: "he requests to view the comments of the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				query: [
						'fields': 'comments(author)'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment just added to the quote is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.comments[0].size() == 1
			data.comments[0].author.uid == "mark.rivers@rustic-hw.com"
		}
	}

	def "An anonymous (#scenario) user should not be able to add a comment to a quote for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "he tries to add a comment to a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/12345678/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to add a comment"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	def "B2B Customer should not be able to add a comment to a quote when the quote does not exist"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to add a comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/99999999/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to add a comment"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should not be able to add a comment to a quote when the comment is #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/comments",
				body: postbody,
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to add a comment"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == message
			data.errors[0].type == type
			data.errors[0].reason == reason
		}
		where:
		scenario         | postbody         | message                   | type              | reason
		"textIsNull"     | '{"text":null }' | "This field is required." | "ValidationError" | "missing"
		"textIsBlank"    | '{"text":" " }'  | "This field is required." | "ValidationError" | "invalid"
		"textIsEmpty"    | '{"text":"" }'   | "This field is required." | "ValidationError" | "invalid"
		"textNotPresent" | '{}'             | "This field is required." | "ValidationError" | "missing"
	}

	def "A B2B Seller should be able to add a comment to a quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates and submits a quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to add a comment to the customer submitted quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment is added"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}

		when: "he requests to view the comments of the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				query: [
						'fields': 'comments'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment just added to the quote is returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			response2.data.comments[0].text == QUOTE_NEW_COMMENT
			response2.data.comments[0].author.uid == "darrin.hesser@acme.com"
		}
	}

	def "A B2B Seller should not be able to add a comment to a quote when the quote hasn't been submitted"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates and submits a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to add a comment to the customer submitted quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment can not be added"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "A B2B Customer should not be able to add a comment to a quote when the quote has been submitted"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates and submits a quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a comment to the customer submitted quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comment can not be added"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [BUYER_SUBMITTED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	//line comments
	def "B2B User should be able to add a line comment to his quote with #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a quote with line comment"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)
		addLineCommentToQuote(CURRENT_USER, quote.code, commentText, "0")

		when: "he requests to view the line comments of the quote"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				query: [
						'fields': 'entries(entryNumber,comments)'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the line comment just added to the quote is returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			response.data.entries[0].comments.size == 1
			response.data.entries[0].comments[0].text == commentText
			response.data.entries[0].comments[0].author.uid == MARK_RIVERS.id
		}

		where:
		scenario         | commentText
		"min length"     | "c"
		"length between" | QUOTE_NEW_COMMENT
		"max length"     | EDGE_LENGTH_STRING

	}

	def "B2B Seller should be able to add a line comment to a quote with #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a submitted quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he adds a line comment and views the line comments of the quote"
		addLineCommentToQuote(MARK_RIVERS.id, quote.code, commentText, "0")
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				query: [
						'fields': 'entries(entryNumber,comments)'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the line comment just added to the quote is returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			response.data.entries[0].comments.size == 1
			response.data.entries[0].comments[0].text == commentText
			response.data.entries[0].comments[0].author.uid == DARRIN_HESSER.id
		}

		where:
		scenario         | commentText
		"min length"     | "c"
		"length between" | QUOTE_NEW_COMMENT
		"max length"     | EDGE_LENGTH_STRING

	}

	def "B2B #roleName should not get line comments when specifying #scenario as fields"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a quote with line comment"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)
		addLineCommentToQuote(CURRENT_USER, quote.code, QUOTE_NEW_COMMENT, "0")
		submitQuote(CURRENT_USER, quote.code)

		when: "a registered B2B User with role #roleName is logged in"
		authorizeClient(restClient, user)

		and: "he requests to view the entries of the quote"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				query: [
						'fields': requestedFields
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the comment just added to the quote is not returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			! response.data.entries[0].containsKey("comments")
		}

		where:
		scenario    | requestedFields    | user          | roleName | authorizeClient
		"entries"   | "entries"          | MARK_RIVERS   | "Buyer"  | this.&authorizeCustomer
		"BASIC"     | "entries(BASIC)"   | MARK_RIVERS   | "Buyer"  | this.&authorizeCustomer
		"DEFAULT"   | "entries(DEFAULT)" | MARK_RIVERS   | "Buyer"  | this.&authorizeCustomer
		"FULL"      | "entries(FULL)"    | MARK_RIVERS   | "Buyer"  | this.&authorizeCustomer
		"entries"   | "entries"          | DARRIN_HESSER | "Seller" | this.&authorizeCustomerManager
		"BASIC"     | "entries(BASIC)"   | DARRIN_HESSER | "Seller" | this.&authorizeCustomerManager
		"DEFAULT"   | "entries(DEFAULT)" | DARRIN_HESSER | "Seller" | this.&authorizeCustomerManager
		"FULL"      | "entries(FULL)"    | DARRIN_HESSER | "Seller" | this.&authorizeCustomerManager
	}

	def "B2B User should be able to add multiple line comment to his quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a quote having multipele line comments added"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)
		addLineCommentToQuote(CURRENT_USER, quote.code, QUOTE_NEW_COMMENT, "0")
		addLineCommentToQuote(CURRENT_USER, quote.code, QUOTE_NEW_COMMENT+"_2", "0")

		when: "he requests to view the line comments of the quote"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				query: [
						'fields': 'entries(entryNumber,comments)'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The comments just added to the quote are returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			response.data.entries[0].comments.size == 2
			response.data.entries[0].comments[0].text == QUOTE_NEW_COMMENT
			response.data.entries[0].comments[0].author.uid == MARK_RIVERS.id

			response.data.entries[0].comments[1].text == QUOTE_NEW_COMMENT+"_2"
			response.data.entries[0].comments[1].author.uid == MARK_RIVERS.id
		}
	}

	def "B2B User should not be able to add a line comment to submitted quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a submitted quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a line comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/entries/0/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "the comment is rejected"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "he requests to view the line comments of the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				query: [
						'fields': 'entries(entryNumber,comments)'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "no comments are returned"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			isEmpty(data.entries[0].comments)
		}
	}

	def "B2B User should not be able to add a line comment to a quote when the comment is #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/entries/0/comments",
				body: postbody,
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to add a comment"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == message
			data.errors[0].type == type
			data.errors[0].reason == reason
		}
		where:
		scenario           | postbody                            | message                                      | type                   | reason
		"textIsNull"       | '{"text":null }'                    | "This field is required."                    | "ValidationError"      | "missing"
		"textIsBlank"      | '{"text":" " }'                     | "This field is required."                    | "ValidationError"      | "invalid"
		"textIsEmpty"      | '{"text":"" }'                      | "This field is required."                    | "ValidationError"      | "invalid"
		"textNotPresent"   | '{}'                                | "This field is required."                    | "ValidationError"      | "missing"
		"textExceedsLimit" | """{ "text":"$TOO_LONG_STRING" }""" | "Parameter text cannot exceed length of 255" | "IllegalArgumentError" | null
	}

	def "An anonymous (#scenario) user should not be able to add a line comment to a quote for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "he tries to to add a comment to a quote"
		def response = restClient.post(
			path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/12345678/entries/43211/comments",
			body: [
				"text": QUOTE_NEW_COMMENT,
			],
			contentType: JSON,
			requestContentType: JSON)

		then: "fail to add a comment"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	def "B2B Customer should not be able to add a line comment to a quote when the line does not exist"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code + "/entries/99999999/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to add a comment"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote entry not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should not be able to add a line comment to a quote when the quote does not exist"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a quote"
		createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he requests to add a comment to his quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/99999999/entries/0/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to add a comment"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "When accept header set to xml, B2B Customer should get Not Acceptable error"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to get all quotes, with accept header set to XMl"
		def response = restClient.get(
			path: getBasePathWithSite() + "/users/current/quotes",
			headers: ["accept": XML]
		)

		then: "an error is returned"
		with(response) {
			status == SC_NOT_ACCEPTABLE
			contentType == XML.toString()
			data.errors[0].type == "HttpMediaTypeNotAcceptableError"
			data.errors[0].message == "Could not find acceptable representation"
		}
	}

	def "When content-type set to xml, B2B Customer should get Unsupported Media Type error"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to create a new quote with content-type set to XML"
		def response = restClient.post(
			path: getBasePathWithSite() + "/users/current/quotes",
			body: '<quoteStarter><cartId>1234567</cartId></quoteStarter>',
			contentType: JSON,
			requestContentType: XML)

		then: "HttpMediaTypeNotSupportedError should be issued"
		with(response) {
			status == SC_UNSUPPORTED_MEDIA_TYPE
			contentType == JSON.toString()
			data.errors[0].message == "Content type 'application/xml' not supported"
			data.errors[0].type == "HttpMediaTypeNotSupportedError"
		}
	}

	// apply discounts to a quote
	def "B2B seller should be able to apply a discount of a quote when discountType is #discountType"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to apply a discount to the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/discounts",
				body: [
						"discountRate": 10,
						"discountType": discountType
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount is applied"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}

		when: "he requests to view the content of the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned including the applied discounts"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quoteDiscounts.value == quoteDiscountValue
			data.totalPrice.value == 2500 - quoteDiscountValue
			data.orderDiscounts.value == 0
			!data.allowedActions.contains(["DISCOUNT"])
		}

		where:
		discountType | quoteDiscountValue
		"PERCENT"    | 250
		"ABSOLUTE"   | 10
		"TARGET"     | 2490
	}

	def "B2B seller should be able to overwrite the discount of a quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to apply a discount to the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/discounts",
				body: [
						"discountRate": 10,
						"discountType": "ABSOLUTE"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount is applied"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}

		when: "he requests to view the content of the quote"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "The content of the specific quote was returned including the applied discounts"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quoteDiscounts.value == 10
			data.orderDiscounts.value == 0
		}

		when: "he changes the discount applied to the quote"
		def response3 = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/discounts",
				body: [
						"discountRate": 50,
						"discountType": "ABSOLUTE"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "New discount has been applied "
		with(response3) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}

		when: "he requests to view the content of the quote"
		def response4 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "Applied discount has been changed"
		with(response4) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quoteDiscounts.value == 50
			data.orderDiscounts.value == 0
		}
	}

	def "B2B seller should fail to apply a discount with #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to apply a discount to the quote with invalid payload"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/discounts",
				body: postbody,
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount cannot be applied"
		with(response) {
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
			status == SC_BAD_REQUEST
		}

		where:
		scenario                                                     | postbody                                                          | errorMessage                                                                           | errorType
		"invalid discountType"                                       | '{"discountRate":10, "discountType": "invalidDiscountType"}'      | "[invalidDiscountType] is not valid value for [DiscountType]"                          | "ValidationError"
		"no discountRate and no discountType"                        | '{ }'                                                             | "[] is not valid value for [DiscountType]"                                             | "ValidationError"
		"no discountType"                                            | '{"discountRate":10 }'                                            | "[] is not valid value for [DiscountType]"                                             | "ValidationError"
		"no discountRate"                                            | '{"discountType": "ABSOLUTE" }'                                   | "DiscountRate cannot be null"                                                          | "IllegalArgumentError"
		"discountRate < 0 when discountType = ABSOLUTE"              | '{"discountRate":-1, "discountType": "ABSOLUTE"}'                 | "The discount rate is less then 0!"                                                    | "IllegalArgumentError"
		"discountRate < 0 when discountType = PERCENT"               | '{"discountRate":-1, "discountType": "PERCENT"}'                  | "The discount rate is less then 0!"                                                    | "IllegalArgumentError"
		"discountRate < 0 when discountType = TARGET"                | '{"discountRate":-1, "discountType": "TARGET"}'                   | "The discount rate is less then 0!"                                                    | "IllegalArgumentError"
		"discountRate > cartTotalValue when discountType = ABSOLUTE" | '{"discountRate":99999, "discountType": "ABSOLUTE"}'              | "Discount type is absolute, but the discont rate is greater than cart total [2500.0]!" | "IllegalArgumentError"
		"discountRate > 100 when discountType = PERCENT"             | '{"discountRate":101, "discountType": "PERCENT"}'                 | "Discount type is percent, but the discount rate is greater than 100!"                 | "IllegalArgumentError"
		"invalid discountRate"                                       | '{"discountRate":invalidDiscountRate, "discountType": "PERCENT"}' | "Invalid request body"                                                                 | "HttpMessageNotReadableError"
	}

	def "B2B seller should fail to apply a discount if quote is not editable"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "the quote is cancelled by the customer"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "he requests to apply a discount to the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/discounts",
				body: [
						"discountRate": 10,
						"discountType": "ABSOLUTE"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount cannot be applied"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [CANCELLED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	def "B2B seller should fail to apply a discount if quote does not exist"() {
		given: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		String NON_EXISTING_QUOTE_CODE = "12345678"
		when: "he requests to apply a discount to the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + NON_EXISTING_QUOTE_CODE + "/discounts",
				body: [
						"discountRate": 10,
						"discountType": "ABSOLUTE"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount cannot be applied"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}
	}

	def "B2B Customer should fail to apply a discount of a quote"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "He requests to apply a discount to the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/00000000/discounts",
				body: [
						"discountRate": 10,
						"discountType": "ABSOLUTE"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount cannot be applied"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].message == "Access is denied"
			data.errors[0].type == "AccessDeniedError"
		}
	}

	def "An anonymous user (#scenario) should fail to apply a discount of a quote for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "He requests to apply a discount to the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/00000000/discounts",
				body: [
						"discountRate": 10,
						"discountType": "ABSOLUTE"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount cannot be applied"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode       | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED  | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST   | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED  | "AccessDeniedError"      | "Access is denied"
	}

	//approve and reject quotes
	def "B2B approver can not #action a quote if it does not exist"() {
		given: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to #action the quote"
		String NON_EXISTING_QUOTE_CODE = "NON_EXISTed"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + NON_EXISTING_QUOTE_CODE + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "IllegalQuoteStateError should be issued"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}

		where:
		action << ["APPROVE", "REJECT"]
	}

	def "B2B approver can not #action a quote when it hasn't been submitted"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "the approver requests to #action the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "IllegalQuoteStateError should be issued"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "QuoteError"
		}

		where:
		action << ["REJECT", "APPROVE"]
	}

	def "B2B approver can not #action a quote when it was auto-approved"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B Seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "Seller also submitted, the quote should be auto approved"
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "the approver requests to approve the quote which has been auto-approved"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "IllegalQuoteStateError should be issued"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "IllegalQuoteStateError"
			data.errors[0].message == "Action [" + action + "] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_APPROVED] having version [3]."
		}

		where:
		action << ["REJECT", "APPROVE"]
	}

	def "B2B approver should not be able to #action a quote in status #quoteState"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "approver requests to approve the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "approver can apply #action to it"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		when: "approver requests to #action the same quote again"
		def response2 = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action2
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "approver can't apply #action2 to it"
		with(response2) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "IllegalQuoteStateError"
			data.errors[0].message == "Action [" + action2 + "] is not allowed for quote code [" + quote.code + "] in quote state [" + quoteState + "] having version [3]."
		}

		where:
		action    | action2   | quoteState
		"REJECT"  | "REJECT"  | "SELLERAPPROVER_REJECTED"
		"REJECT"  | "APPROVE" | "SELLERAPPROVER_REJECTED"
		"APPROVE" | "REJECT"  | "SELLERAPPROVER_APPROVED"
		"APPROVE" | "APPROVE" | "SELLERAPPROVER_APPROVED"
	}

	def "B2B approver should only have approve/reject action for quote"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to view the quote"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)
		then: "he can see allowed actions only have approve and reject"
		with(quoteData) {
			allowedActions.containsAll(["APPROVE", "REJECT"])
			allowedActions.size() == 2
		}
	}

	def "B2B approver cannot perform #action to quote in a waiting-for-approval state"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to #action the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "he can not apply #action to it"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == errorType
			data.errors[0].message == errorMessage.replaceAll("_QUOTE_CODE_", quote.code)
		}

		where:
		action     | errorType                | errorMessage
		"CANCEL"   | "IllegalQuoteStateError" | "Action [CANCEL] is not allowed for quote code [_QUOTE_CODE_] in quote state [SELLERAPPROVER_PENDING] having version [3]."
		"CHECKOUT" | "QuoteError"             | "Action [CHECKOUT] is not allowed for quote code [_QUOTE_CODE_]"
		"SUBMIT"   | "IllegalQuoteStateError" | "Action [EDIT] is not allowed for quote code [_QUOTE_CODE_] in quote state [SELLERAPPROVER_PENDING] having version [3]."
		"EDIT"     | "IllegalQuoteStateError" | "Action [EDIT] is not allowed for quote code [_QUOTE_CODE_] in quote state [SELLERAPPROVER_PENDING] having version [3]."
	}

	def "B2B approver can not add discount of type #discountType to quote in a waiting-for-approval state"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to apply a discount to the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/discounts",
				body: [
						"discountRate": discountRate,
						"discountType": discountType
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "The discount cannot be applied"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_PENDING] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		where:
		discountType | discountRate
		"PERCENT"    | 10
		"ABSOLUTE"   | 100
		"TARGET"     | 2000
	}

	def "B2B approver can not edit quote (update metadata) in a waiting-for-approval state"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to edit the name of the quote"
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				body: [
						"name": "newName",
						"description": "newDescription"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "The update operation is rejected"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].type == "AccessDeniedError"
			data.errors[0].message == "Access is denied"
		}

		when: "he requests to view the quote just edited"
		def quoteData = getQuote(MARK_RIVERS.id, quote.code)

		then: "the name of the quote is not changed"
		with(quoteData) {
			name == quote.name
		}
	}

	def "B2B approver can not add comment to a quote in a waiting-for-approval state"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to add a comment to customer's quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "the comment is rejected"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "IllegalQuoteStateError"
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_PENDING] having version [3]."
		}
	}

	def "B2B approver cannot add comment to an entry in quote in a waiting-for-approval state"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to add a line comment to customer's quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/entries/0/comments",
				body: [
						"text": QUOTE_NEW_COMMENT,
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "the comment is rejected"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "IllegalQuoteStateError"
			data.errors[0].message == "Action [EDIT] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_PENDING] having version [3]."
		}
	}

	def "B2B approver should not be able to requote a quote based on quoteCode"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "he requests to cancel that quote"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to requote the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				body: [
						"quoteCode": quote.code
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "requote is rejected"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].type == "AccessDeniedError"
			data.errors[0].message == "Access is denied"
		}

		when: "he requests the quote again"
		def oldQuote = getQuote(MARK_RIVERS.id, quote.code)

		then: "the quote is still there and cancelled"
		with(oldQuote) {
			oldQuote.code == quote.code
			oldQuote.state == "CANCELLED"
		}
	}

	def "B2B approver should not be able to create a quote based on customer's cartId"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "a new cart is created"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "he requests to create a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "request is rejected"
		with(response) {
			status == SC_UNAUTHORIZED
			data.errors[0].type == "AccessDeniedError"
			data.errors[0].message == "Access is denied"
		}

		when: "he checks the cart"
		def cart = getCart(MARK_RIVERS.id, cartId)

		then: "the cart is still there"
		with(cart) {
			cart.code == cartId
		}
	}

	def "B2B seller should not be able to #action a quote"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "seller tries to #action the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "seller can't apply #action to it"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "IllegalQuoteStateError"
			data.errors[0].message == "Action [" + action + "] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_PENDING] having version [3]."
		}

		where:
		action << ["REJECT", "APPROVE"]
	}

	def "B2B approver can #action a quote"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "approver requests to #action the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "approver can apply #action to it"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		where:
		action << ["REJECT", "APPROVE"]
	}

	def "B2B approver should NOT be able to get the list of quotes that #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "#action"
		def quote = this.invokeMethod(actionRoutineName, [CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY])

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "B2B approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B approver should NOT be able to get the list of quotes that #scenario"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			isEmpty(data.quotes) || data.quotes.findAll { it.code == quote.code }.isEmpty()
		}

		where:
		scenario                              | action                           | actionRoutineName
		"have not been submitted by customer" | "he creates a quote"             | "createQuote"
		"have been submitted by customer"     | "he creates and submits a quote" | "createAndSubmitQuote"
	}

	def "B2B approver should be able to get the list of quotes that have been sent to an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "B2B approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B approver should be able to get the list of quotes that have been sent to an approver"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLERAPPROVER_PENDING"
		}
	}

	def "B2B approver should be able to get the list of #quoteState"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "approver requests to #action the quote"
		this.invokeMethod(actionMethodName, [MARK_RIVERS.id, quote.code])

		when: "B2B approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B approver should be able to get the list of #quoteState"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == newState
		}

		where:
		action    | quoteState        | actionMethodName | newState
		"approve" | "approved quotes" | "approveQuote"   | "SELLERAPPROVER_APPROVED"
		"reject"  | "rejected quotes" | "rejectQuote"    | "SELLERAPPROVER_REJECTED"
	}

	def "B2B approver should be able to get the list of quotes that were approved automatically"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "seller submit the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "B2B approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B approver should be able to get the list of quotes that were approved automatically "
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLERAPPROVER_APPROVED"
		}
	}

	def "B2B approver should be able to get the list of cancelled quotes"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "customer cancels this quote"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "B2B approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B approver should be able to get the list of cancelled quotes"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "CANCELLED"
		}
	}

	def "B2B approver should NOT be able to get the list of quotes that have not been submitted by the seller"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to create a quote"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "B2B approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B aprover should NOT able to get the list of quotes that have not been submitted by the seller"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			isEmpty(data.quotes) || data.quotes.findAll { it.code == quote.code }.isEmpty()
		}
	}

	def "B2B approver should be able to get the list of quotes that have been submitted by the seller"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to create a quote"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		and: "he submits quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		when: "B2B approver requests to get all quotes"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				query: [
						'sort': 'byDate'
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B aprover should able to get the list of quotes that have been submitted by the seller"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quotes.findAll { it.code == quote.code }.state[0] == "SELLERAPPROVER_APPROVED"
		}
	}

	def "B2B customer should not be able to #action a quote"() {
		given: "a pending quote"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "customer tries to #action the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": action
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "customer can't apply action to it"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "IllegalQuoteStateError"
			data.errors[0].message == "Action [" + action + "] is not allowed for quote code [" + quote.code + "] in quote state [BUYER_SUBMITTED] having version [1]."
		}

		where:
		action << ["REJECT", "APPROVE"]
	}

	def "B2B #role can not checkout an approved quote"() {
		given: "an approved quote without discount"
		def quote = createQuoteAndApplyDiscount(MARK_RIVERS, DARRIN_HESSER, "PERCENT", 0)

		and: "Login as #role"
		authorizeCustomerManager(restClient, roleCredential)

		when: "#role tries to checkout the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CHECKOUT"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "#role can't checkout a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "QuoteError"
			data.errors[0].message == "Action [CHECKOUT] is not allowed for quote code [" + quote.code + "]"
		}

		where:
		role       | roleCredential
		"seller"   | DARRIN_HESSER
		"approver" | GLEN_HOFER

	}

	def "An anonymous user (#scenario) should fail to checkout a quote for #quoteOwner user"() {
		given: "An anonymous user"
		authorizationMethod(restClient)

		when: "he requests to checkout a quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + quoteOwner + "/quotes/12345678/action",
				body: [
						"action": "CHECKOUT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "fail to checkout the quote"
		with(response) {
			status == statusCode
			data.errors[0].message == errorMessage
			data.errors[0].type == errorType
		}

		where:
		scenario                              | quoteOwner     | authorizationMethod          | statusCode      | errorType                | errorMessage
		"not sending any Authorization Token" | "current"      | this.&removeAuthorization    | SC_UNAUTHORIZED | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"not sending any Authorization Token" | "anonymous"    | this.&removeAuthorization    | SC_UNAUTHORIZED | "AccessDeniedError"      | "Access is denied"
		"not sending any Authorization Token" | MARK_RIVERS.id | this.&removeAuthorization    | SC_UNAUTHORIZED | "UnauthorizedError"      | "Full authentication is required to access this resource"
		"as a TRUSTED_CLIENT"                 | "current"      | this.&authorizeTrustedClient | SC_BAD_REQUEST  | "UnknownIdentifierError" | "Cannot find user with propertyValue 'current'"
		"as a TRUSTED_CLIENT"                 | "anonymous"    | this.&authorizeTrustedClient | SC_UNAUTHORIZED | "AccessDeniedError"      | "Access is denied"
		"as a TRUSTED_CLIENT"                 | MARK_RIVERS.id | this.&authorizeTrustedClient | SC_UNAUTHORIZED | "AccessDeniedError"      | "Access is denied"
	}

	def "B2B customer can checkout an approved quote using #userId as user"() {
		given: "an approved quote without discount"
		def quote = createQuoteAndApplyDiscount(MARK_RIVERS, DARRIN_HESSER, "PERCENT", 0)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "customer tries to read the quote to see if he can checkout"
		def quote2 = getQuote(MARK_RIVERS.id, quote.code)
		then: "customer can see the possible actions with Checkout, also can see there is no cart"
		with(quote2) {
			state == "BUYER_OFFER"
			allowedActions.containsAll(["CHECKOUT", "CANCEL", "EDIT"])
			allowedActions.size() == 3
			cartId == null
		}

		when: "customer tries to checkout the quote "
		def response2 = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CHECKOUT"
				],
				contentType: JSON,
				requestContentType: JSON)
		then: "customer can checkout quote"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		where:
		userId << [CURRENT_USER, MARK_RIVERS.id]
	}

	def "After B2B customer applies a CHECKOUT, a new cart with the quote content is created holding #discountType discounts."() {
		given: "an approved quote with discount"
		def quote = createQuoteAndApplyDiscount(MARK_RIVERS, DARRIN_HESSER, discountType, discountRate)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "customer tries to checkout the quote"
		checkoutQuote(MARK_RIVERS.id, quote.code)

		when: "customer tries to read the quote to get cartId"
		def quote2 = getQuote(MARK_RIVERS.id, quote.code)
		then: "customer can see the possible actions with Checkout, also can see the cartId"
		with(quote2) {
			state == "BUYER_OFFER"
			allowedActions.containsAll(["CHECKOUT", "CANCEL", "EDIT"])
			allowedActions.size() == 3
			cartId != null
		}

		when: "customer tries to read the created cart for discount"
		def cart = getCart("current", quote2.cartId)
		then: "customer can see the discount in created cart"
		with(cart) {
			totalPrice.value == expectedNewPrice
		}

		where:
		discountType | expectedNewPrice | discountRate
		"PERCENT"    | 2250             | 10
		"ABSOLUTE"   | 2300             | 200
		"TARGET"     | 2000             | 2000
	}

	def "After B2B customer applies a CHECKOUT, the cart with the quote content cannot have new entries added."() {
		given: "an approved quote with discount"
		def approvedQuote = createQuoteAndApplyDiscount(MARK_RIVERS, DARRIN_HESSER, "PERCENT", 10)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "customer tries to checkout the quote"
		checkoutQuote(MARK_RIVERS.id, approvedQuote.code)

		and: "customer tries to read the quote to get cartId"
		def checkedOutQuote = getQuote(MARK_RIVERS.id, approvedQuote.code)

		when: "customer tries to add a new entry to the cart generated from the quote"
		def response = restClient.post(
			path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/" + MARK_RIVERS.id + "/carts/" + checkedOutQuote.cartId + "/entries",
			query: [
				'code'    : PRODUCT_CODE_1,
				'quantity': ADD_PRODUCT_QUANTITY
			],
			contentType: JSON,
			requestContentType: JSON
		)
		then: "an error message is received"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].type == "DomainError"
			data.errors[0].message == "The application has encountered an error"
		}
	}

	def "B2B seller should not be able to get the content of a quote that has not been submitted yet"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to view the quote that the customer just submited"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should not be able to get the content of a quote that has not been submitted yet"
		with(response) {
			status == SC_NOT_FOUND
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "NotFoundError"
		}
	}

	def "B2B seller should be able to get the content of a quote after being submitted"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to view the quote that the customer just submited"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the content and see different allowed actions than the customer"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "SELLER_REQUEST"
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY
			data.allowedActions.containsAll(["SUBMIT", "EDIT"])
		}
	}

	def "B2B seller should be able to get the content of a quote after being sent to an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to view the quote that has been sent to an approver"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the content and see different allowed actions than the customer"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "SELLERAPPROVER_PENDING"
			data.entries[0].quantity == 200
			isEmpty(data.allowedActions)
		}
	}

	def "B2B seller should be able to get the content of an #action quote"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "approver requests to #action the quote"
		restClient.post(
			path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
			body: [
					"action": action
			],
			contentType: JSON,
			requestContentType: JSON)

		Thread.sleep(1000)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to view the quote that has been sent to an approver"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the content of an APPROVED/REJECTED quote"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == quoteState
			data.entries[0].quantity == 200
		}

		where:
		action    | quoteState
		"REJECT"  | "SELLER_REQUEST"
		"APPROVE" | "SELLERAPPROVER_APPROVED"
	}

	def "B2B seller should be able to get the content of a quote that was submitted back to the customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "seller submit the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		when: "B2B seller requests to view the quote that has been sent to an approver"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the content of a quote that was submitted back to the customer"
		with(response2) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "SELLERAPPROVER_APPROVED"
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY
			isEmpty(data.allowedActions)
		}
	}

	def "B2B seller should be able to get the content of a cancelled quote and no allowed actions are returned"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "customer cancel this quote"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to view the quote that has been sent to an approver"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should be able to get the content of a cancelled quote and no allowed actions are returned"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "CANCELLED"
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY
			isEmpty(data.allowedActions)
		}
	}

	def "B2B seller cannot requote a cancelled quote on behalf of a customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "customer cancels this quote"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to requote the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes",
				body: [
						"quoteCode": quote.code
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller cannot requote this cancelled quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [REQUOTE] is not allowed for quote code [" + quote.code + "] in quote state [CANCELLED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}
	}

	def "B2B customer cannot see a quote in DRAFT mode created by a B2B seller on his/her behalf"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to create a quote"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		then: "B2B seller can create a quote from a buyer's cart"
		with(quote) {
			state == "SELLER_DRAFT"
			entries[0].quantity == ADD_PRODUCT_QUANTITY
		}

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "B2B customer requests to see the quote created by seller"
		def response2 = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then : "B2B customer cannot see a quote in DRAFT mode created by a B2B seller"
		with(response2) {
			status == SC_NOT_FOUND
			data.errors[0].message == "Quote not found"
			data.errors[0].type == "NotFoundError"
		}
	}

	def "B2B customer can see a quote created by a B2B seller on his/her behalf only after it has been submitted by B2B seller"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "B2B seller requests to create a quote"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		and: "B2B seller submits this quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "B2B customer requests to see the quote submitted by seller"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B customer can see the quote submitted by seller"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "BUYER_OFFER"
			data.entries[0].quantity == ADD_PRODUCT_QUANTITY
			data.allowedActions.containsAll(["CANCEL", "EDIT", "CHECKOUT"])
		}
	}

	def "The list of allowedActions doesn't include CANCEL when a B2B seller is getting a quote that has been submitted by a customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates and submits a quote"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to view the quote"
		def response = getQuote(MARK_RIVERS.id, quote.code)

		then: "action CANCEL isn't in the list of allowedActions"
		with(response) {
			! allowedActions.contains("CANCEL")
		}
	}

	def "B2B seller should fail to cancel a quote and the list of allowedActions doesn't include CANCEL when it have been sent to an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to cancel the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should fail to cancel a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [CANCEL] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_PENDING] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "B2B seller requests to view the quote"
		def response2 = getQuote(MARK_RIVERS.id, quote.code)

		then: "action CANCEL isn't in the list of allowedActions"
		with(response2) {
			isEmpty(allowedActions)
		}
	}

	def "B2B seller should fail to cancel a quote and the list of allowedActions doesn't include CANCEL when it has been #action by an approver"() {
		given: "create a quote and send to an approver"
		def quote = createPendingQuote(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, 200)

		and: "a registered and logged in B2B approver"
		authorizeCustomerManager(restClient, GLEN_HOFER)

		and: "approver requests to #action the quote"
		this.invokeMethod(actionRoutineName, [MARK_RIVERS.id, quote.code])

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		def expectedErrorMessage = errorMessage.replaceAll("_QUOTE_CODE_", quote.code)
		await().pollInterval(Duration.ofMillis(500)).atMost(Duration.ofSeconds(5)).ignoreException(AssertionException.class).until(new Callable<Boolean>() {
			@Override
			Boolean call() throws Exception {
				when: "B2B seller requests to cancel the quote"
				def response = restClient.post(
						path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
						body: [
								"action": "CANCEL"
						],
						contentType: JSON,
						requestContentType: JSON)

				then: "B2B seller should fail to cancel a quote"
				with(response) {
					assertTrue(status == SC_BAD_REQUEST, "httpResponseStatus")
					assertTrue(data.errors[0].message == expectedErrorMessage, "errorMessage")
					assertTrue(data.errors[0].type == "IllegalQuoteStateError", "errorType")
				}
				return true
			}
		})

		when: "B2B seller requests to view the quote"
		def response2 = getQuote(MARK_RIVERS.id, quote.code)

		then: "action CANCEL isn't in the list of allowedActions"
		with(response2) {
			isEmpty(allowedActions) ? true : !allowedActions.contains("CANCEL")
		}

		where:
		action          | actionRoutineName    | errorMessage
		"APPROVED"      | "approveQuote"       | "Action [CANCEL] is not allowed for quote code [_QUOTE_CODE_] in quote state [SELLERAPPROVER_APPROVED] having version [3]."
		"REJECTED"      | "rejectQuote"        | "Action [CANCEL] is not allowed for quote code [_QUOTE_CODE_] in quote state [SELLER_REQUEST] having version [4]."
	}

	def "B2B seller should fail to cancel a quote and the list of allowedActions doesn't include CANCEL when it have been submitted back to the customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "seller submit the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		when: "B2B seller requests to cancel the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should fail to cancel a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [CANCEL] is not allowed for quote code [" + quote.code + "] in quote state [SELLERAPPROVER_APPROVED] having version [3]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "B2B seller requests to view the quote"
		def response2 = getQuote(MARK_RIVERS.id, quote.code)

		then: "action CANCEL isn't in the list of allowedActions"
		with(response2) {
			isEmpty(allowedActions)
		}
	}

	def "B2B seller should fail to cancel a quote and the list of allowedActions doesn't include CANCEL when it have been cancelled by the customer"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "customer cancel this quote"
		cancelQuote(quote.code)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		when: "B2B seller requests to cancel the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should fail to cancel a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [CANCEL] is not allowed for quote code [" + quote.code + "] in quote state [CANCELLED] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "B2B seller requests to view the quote"
		def response2 = getQuote(MARK_RIVERS.id, quote.code)

		then: "action CANCEL isn't in the list of allowedActions"
		with(response2) {
			isEmpty(allowedActions)
		}
	}

	def "B2B seller should fail to cancel a quote and the list of allowedActions doesn't include CANCEL that he/she created on behalf of customer and that hasn't submitted yet"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a cart and adds products to it"
		def cartId = createNewCart(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "he requests to create a quote using the customers cart"
		def quote = createQuote(MARK_RIVERS.id, cartId)

		when: "B2B seller requests to cancel the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "B2B seller should fail to cancel a quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [CANCEL] is not allowed for quote code [" + quote.code + "] in quote state [SELLER_DRAFT] having version [1]."
			data.errors[0].type == "IllegalQuoteStateError"
		}

		when: "B2B seller requests to view the quote"
		def response2 = getQuote(MARK_RIVERS.id, quote.code)

		then: "action CANCEL isn't in the list of allowedActions"
		with(response2) {
			! allowedActions.contains("CANCEL")
		}
	}

	def "B2B customer should be able to get a quote with expirationTime when the quote have been submitted back to him/her"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote and submits it"
		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B seller"
		authorizeCustomerManager(restClient, DARRIN_HESSER)

		and: "seller submits the quote"
		submitQuote(MARK_RIVERS.id, quote.code)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "B2B customer requests to get the specific quote the seller just submitted"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				contentType: JSON,
				requestContentType: JSON)

		then: "the content of quote is returned and expirationTime is included"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			isNotEmpty(data.expirationTime)
		}
	}

	def "B2B user should be able to get a quote with proper fields as #scope"() {
		given: "a quote with all kinds of info"
		def quote = createQuoteWithAllInfo(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he requests to get the quote with specified fields"
		def expectedResponse = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				query: [
						'fields': fields
				],
				contentType: JSON,
				requestContentType: JSON)

		def expectedData = expectedResponse.data;

		when: "B2B customer requests to get the quote with given fields"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				query: scope,
				contentType: JSON,
				requestContentType: JSON)

		then: "the content of quote is returned with the right fields"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.equals(expectedData)
		}

		where:
		scope                    | fields
		[ "fields" : "BASIC" ]   | "code,name,state,updatedTime"
		null                     | "code,name,state,updatedTime,allowedActions,comments(author,creationDate,text,fromCustomer),description,entries(DEFAULT,comments(DEFAULT)),orderDiscounts,previousEstimatedTotal,quoteDiscounts,productDiscounts,subTotalWithDiscounts,threshold, totalItems,totalPrice,totalPriceWithTax,version,expirationTime,cartId"
		[ "fields" : "DEFAULT" ] | "code,name,state,updatedTime,allowedActions,comments(author,creationDate,text,fromCustomer),description,entries(DEFAULT,comments(DEFAULT)),orderDiscounts,previousEstimatedTotal,quoteDiscounts,productDiscounts,subTotalWithDiscounts,threshold, totalItems,totalPrice,totalPriceWithTax,version,expirationTime,cartId"
		[ "fields" : "FULL" ]    | "code,name,state,updatedTime,allowedActions,comments(author,creationDate,text,fromCustomer),description,entries(DEFAULT,comments(DEFAULT)),orderDiscounts,previousEstimatedTotal,quoteDiscounts,productDiscounts,subTotalWithDiscounts,threshold, totalItems,totalPrice,totalPriceWithTax,version,expirationTime,creationTime,cartId"
	}

	def "B2B customer should be able to get quote list with proper fields as #scope"() {
		given: "a quote with all kinds of info"
		def quote = createQuoteWithAllInfo(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he reads quote list and finds the specified one"
		def expectedResponse = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/",
				query: [
						'fields': fields
				],
				contentType: JSON,
				requestContentType: JSON)

		def expectedQuote = expectedResponse.data.quotes.find { it.code == quote.code }
		when: "he requests to get a list of quotes with given fields"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/",
				query: scope,
				contentType: JSON,
				requestContentType: JSON)

		then: "the content of the specific quote is returned and the right fields are included"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			(data.quotes.find { it.code == quote.code }).equals(expectedQuote)
		}

		where:
		scope                    | fields
		null                     | "quotes(code,name,state,updatedTime)"
		[ "fields" : "BASIC" ]   | "quotes(code,name,state,updatedTime)"
		[ "fields" : "DEFAULT" ] | "quotes(code,name,state,updatedTime,allowedActions,comments(author,creationDate,text,fromCustomer),description,entries(DEFAULT,comments(DEFAULT)),orderDiscounts,previousEstimatedTotal,quoteDiscounts,productDiscounts,subTotalWithDiscounts,threshold, totalItems,totalPrice,totalPriceWithTax,version,expirationTime,cartId)"
		[ "fields" : "FULL" ]    | "quotes(code,name,state,updatedTime,allowedActions,comments(author,creationDate,text,fromCustomer),description,entries(DEFAULT,comments(DEFAULT)),orderDiscounts,previousEstimatedTotal,quoteDiscounts,productDiscounts,subTotalWithDiscounts,threshold, totalItems,totalPrice,totalPriceWithTax,version,expirationTime,creationTime,cartId)"
	}

	def "B2B customer should be able to get a line comment in quote with proper fields as #scope"() {
		given: "a quote with all kinds of info"
		def quote = createQuoteWithAllInfo(MARK_RIVERS, DARRIN_HESSER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		and: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "he requests to get the specific quote"
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				query: [
						'fields': scope
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "the comments of entries are returned in #scope"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.entries[0].keySet().contains("comments")
			data.entries[0].comments.size() != 0
			data.entries[0].comments[0].containsKey("text")
		}

		where:
		scope << ["DEFAULT", "FULL"]
	}

	def "customer cann't checkout a quote when expirationTime is #scenario"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		when: "customer tries to checkout the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quoteCode + "/action",
				body: [
						"action": "CHECKOUT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "failed to checkout the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Quote has expired. Quote Code : [" + quoteCode + "]."
			data.errors[0].type == "CommerceQuoteExpirationTimeError"
		}

		where:
		scenario        | quoteCode
		"null"          | NULL_EXPIRED_TIME_QUOTE_CODE
		"in the past"   | EXPIRED_QUOTE_CODE
	}

	def "B2B customer should not be able to checkout a quote which is not allowed to checkout - CLC-2693/CXEC-107"() {
		given: "a registered and logged in B2B customer"
		authorizeCustomer(restClient, MARK_RIVERS)

		and: "he creates a quote"
		def quote = createQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		when: "he tries to checkout the quote"
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code + "/action",
				body: [
						"action": "CHECKOUT"
				],
				contentType: JSON,
				requestContentType: JSON)

		then: "failed to checkout the quote"
		with(response) {
			status == SC_BAD_REQUEST
			data.errors[0].message == "Action [CHECKOUT] is not allowed for quote code [" + quote.code + "]";
			data.errors[0].type == "QuoteError"
		}

		when: "customer tries to read the quote"
		def quote2 = getQuote(MARK_RIVERS.id, quote.code)

		then: "Quote status should not be changed"
		with(quote2) {
			state == "BUYER_DRAFT"
		}
	}

	protected Object createAndSubmitQuote(String userId, String productCode, int quantity) {
		String cartId = createNewCart(userId)
		addProductToCart(userId, cartId, productCode, quantity)
		def quote = createQuote(userId, cartId)
		submitQuote(userId, quote.code)
		quote = getQuote(userId, quote.code)
		return quote
	}

	protected Object createQuote(String userId, String productCode, int quantity) {
		String cartId = createNewCart(userId)
		addProductToCart(userId, cartId, productCode, quantity)
		def quote = createQuote(userId, cartId)
		return quote
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

	protected String createNewCart(String userId, String productCode, int quantity) {
		def cartId = createNewCart(userId)
		addProductToCart(userId, cartId, productCode, quantity)
		return cartId
	}

	protected void addProductToCart(String userId, String cartId, String productCode, Integer quantity) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/" + ENABLED_CONTROLLER_PATH + "/" + userId + "/carts/" + cartId + "/entries",
				query: [
						'code'    : productCode,
						'quantity': quantity
				],
				contentType: JSON,
				requestContentType: JSON
		)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.quantity == quantity
			data.entry.product.code == productCode
		}
	}

	protected Object createQuote(String userId, String cartId) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes",
				body: [
						"cartId": cartId
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}
		return response.data
	}

	protected void addLineCommentToQuote(String userId, String quoteId, String commentText, String entryNumber) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quoteId + "/entries/" + entryNumber + "/comments",
				body: [
						"text": commentText,
				],
				contentType: JSON,
				requestContentType: JSON)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}
	}

	protected void submitQuote(String userId, String quoteCode) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quoteCode + "/action",
				body: [
						"action": "SUBMIT"
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}

		// we introduce an artificial pause, to allow async processes to update the final Quote state
		Thread.sleep(1000)
	}

	protected void cancelQuote(String quoteCode) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/quotes/" + quoteCode + "/action",
				body: [
						"action": "CANCEL"
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
	}

	protected void applyVoucher(String cartId, String voucherId) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/current/carts/" + cartId + "/vouchers",
				query: [
						voucherId: voucherId
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
	}

	protected void addCommentToQuote(String userId, String quoteCode, String comment) {
		def response = restClient.post(
			path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quoteCode + "/comments",
			body: [
				"text": comment,
			],
			contentType: JSON,
			requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}
	}

	protected void setProductStockLevel(String productId, InStockStatus inStockStatus) {
		ProductService productService = Registry.getApplicationContext().getBean("productService")
		ProductModel product = productService.getProductForCode(productId)

		WarehouseService warehouseService = Registry.getApplicationContext().getBean("warehouseService")

		StockService stockService = Registry.getApplicationContext().getBean("stockService")
		stockService.setInStockStatus(product, warehouseService.getDefWarehouse(), inStockStatus)
	}

	protected Object createPendingQuote(def customer, def seller, String productCode, int quantity) {
		authorizeCustomer(restClient, customer)
		def quote = createAndSubmitQuote(customer.id, productCode, quantity)

		authorizeCustomerManager(restClient, seller)
		submitQuote(customer.id, quote.code)

		removeAuthorization(restClient)
		return quote
	}

	protected void applyDiscount(String customerId, String quoteCode, String discountType, double discountRate) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + customerId + "/quotes/" + quoteCode + "/discounts",
				body: [
						"discountRate": discountRate,
						"discountType": discountType
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_CREATED
		}
	}

	protected Object createQuoteAndApplyDiscount(Object customer, Object seller, String discountType, Double discountRate) {
		authorizeCustomer(restClient, customer)

		def quote = createAndSubmitQuote(CURRENT_USER, PRODUCT_CODE, ADD_PRODUCT_QUANTITY)

		authorizeCustomerManager(restClient, seller)
		if (discountType && discountRate) {
			applyDiscount(customer.id, quote.code, discountType, discountRate)
		}

		submitQuote(customer.id, quote.code)
		removeAuthorization(restClient)

		return quote
	}

	protected Object getQuote(String customerId, String quoteCode) {
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + customerId + "/quotes/" + quoteCode,
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
		return response.data
	}

	protected Object getCart(String customerId, String cartId) {
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + customerId + "/carts/" + cartId,
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
		return response.data
	}

	protected void checkoutQuote(String customerId, String quoteCode) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + customerId + "/quotes/" + quoteCode + "/action",
				body: [
						"action": "CHECKOUT"
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
	}

	protected approveQuote(String userId, String quoteCode) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quoteCode + "/action",
				body: [
						"action": "APPROVE"
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
	}

	protected rejectQuote(String userId, String quoteCode) {
		def response = restClient.post(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quoteCode + "/action",
				body: [
						"action": "REJECT"
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
	}

	protected void addNameAndDescriptionToQuote(def quote) {
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/current/quotes/" + quote.code,
				body: [
						"name"       : QUOTE_NEW_NAME,
						"description": QUOTE_NEW_DESCRIPTION
				],
				contentType: JSON,
				requestContentType: JSON)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
		}
	}

	protected Object createQuoteWithAllInfo(def customer, def seller, String productCode, int quantity) {
		// customer creates a quote with comments, name and description, and submits the quote
		authorizeCustomer(restClient, customer)
		def quote = createQuote(CURRENT_USER, productCode, quantity)
		addCommentToQuote(CURRENT_USER, quote.code, QUOTE_NEW_COMMENT)
		addLineCommentToQuote(CURRENT_USER, quote.code, QUOTE_NEW_COMMENT, "0")
		addNameAndDescriptionToQuote(quote)
		submitQuote(CURRENT_USER, quote.code)

		// Seller submits the quote
		authorizeCustomerManager(restClient, seller)
		submitQuote(customer.id, quote.code)

		// customer accepts the quote and goes through checkout
		authorizeCustomer(restClient, customer)
		checkoutQuote(CURRENT_USER, quote.code)
		def response = restClient.get(
				path: getBasePathWithSite() + "/users/" + MARK_RIVERS.id + "/quotes/" + quote.code,
				query: [
						'fields': "FULL"
				],
				contentType: JSON,
				requestContentType: JSON)
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.keySet().containsAll(["cartId", "code", "name", "state", "updatedTime", "allowedActions", "comments", "description",
									   "entries", "orderDiscounts", "previousEstimatedTotal", "productDiscounts", "quoteDiscounts",
									   "threshold", "totalItems", "totalPrice", "totalPriceWithTax", "version", "expirationTime"])
		}

		removeAuthorization(restClient)
		return quote
	}

	protected Object updateQuote(RESTClient restClient, String userId, String quoteCode, Object payload) {
		def response = restClient.patch(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quoteCode,
				body: payload,
				contentType: JSON,
				requestContentType: JSON)
		return response
	}

	protected Object replaceQuote(RESTClient restClient, String userId, String quoteCode, Object payload) {
		def response = restClient.put(
				path: getBasePathWithSite() + "/users/" + userId + "/quotes/" + quoteCode,
				body: payload,
				contentType: JSON,
				requestContentType: JSON)

		return response
	}

	private String getExpirationTimeValidationErrorMessage(String expirationTime) {
		return String.format("Invalid quote expiration time [%s].", DateFormat.getDateInstance(DateFormat.LONG).format(new DateTime(expirationTime).toDate()))
	}
}
