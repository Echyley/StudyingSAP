/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cpq.productconfig.occ.integrationtests.mock

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner
import de.hybris.platform.util.Config

import org.apache.http.HttpStatus
import org.junit.Test
import org.junit.runner.RunWith

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

@ManualTest
@RunWith(HybrisSpockRunner.class)
class CpqOrderIntegrationWsIntegrationTest extends BaseSpockTest {
	protected static final DELIVERY_STANDARD = 'standard-gross'

	@Test
	def "Authorize existing customer and place order with a complete configurable product"(){

		given: "Customer logs in, creates cart and prepares order"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_CUSTOMER.id)

		def cartModification = addProductToCart(restClient, B2B_CUSTOMER.id, cart.code, P_CODE_CONF_LAPTOP_COMPLETE, format)

		def address = getAddress(restClient, B2B_CUSTOMER)
		setDeliveryAddressForCart(restClient, B2B_CUSTOMER, cart.code, address.addresses[0].id, format)

		def deliveryModes = getDeliveryModes(restClient, B2B_CUSTOMER, cart.code, format)
		setDeliveryModeForCart(restClient, B2B_CUSTOMER, cart.code, DELIVERY_STANDARD, format)

		def paymentDetails = getPaymentDetails(restClient, B2B_CUSTOMER, format)
		def paymentDetailsId = paymentDetails.payments[0].id
		setPaymentDetailsForCart(restClient, B2B_CUSTOMER, cart.code, paymentDetailsId, format)

		def entry = getCartEntry(cart, 0, format,B2B_CUSTOMER)
		LOG.info("cart entry:" +entry)


		if (!Config.getBoolean("cpqproductconfigservices.tests.ignoreCoDeployemntIssues", false)) {
			with(entry){
				// configurable bundle module enforces jalo pricing, which is incompatible as we only support sl pricing, hence price will be wrong
				Double.valueOf(basePrice.value.toString()) == 1080.0
			}
		}
		def orderData = placeOrder(restClient, B2B_CUSTOMER, cart.code, format, HttpStatus.SC_OK)

		when: "user retrieves overview for order entry"
		def configurationData = getConfigurationForOrderEntry(restClient, B2B_CUSTOMER, orderData.code, 0, format)

		then: "he gets the configuration for an order entry"

		with(configurationData) {
			configId != null
			configId.toString().startsWith("MOCK_") == true
			configId.toString().endsWith(P_CODE_CONF_LAPTOP_COMPLETE) == true
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Authorize existing customer and place order with an incomplete configurable product"(){

		given: "Customer logs in, creates cart and prepares order"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_CUSTOMER.id)

		def cartModification = addProductToCart(restClient, B2B_CUSTOMER.id, cart.code, P_CODE_CONF_LAPTOP, format)

		def address = getAddress(restClient, B2B_CUSTOMER)
		setDeliveryAddressForCart(restClient, B2B_CUSTOMER, cart.code, address.addresses[0].id, format)

		def deliveryModes = getDeliveryModes(restClient, B2B_CUSTOMER, cart.code, format)
		setDeliveryModeForCart(restClient, B2B_CUSTOMER, cart.code, DELIVERY_STANDARD, format)

		def paymentDetails = getPaymentDetails(restClient, B2B_CUSTOMER, format)
		def paymentDetailsId = paymentDetails.payments[0].id
		setPaymentDetailsForCart(restClient, B2B_CUSTOMER, cart.code, paymentDetailsId, format)

		def entry = getCartEntry(cart, 0, format,B2B_CUSTOMER)
		LOG.info("cart entry:" +entry)


		if (!Config.getBoolean("cpqproductconfigservices.tests.ignoreCoDeployemntIssues", false)) {
			with(entry){
				// configurable bundle module enforces jalo pricing, which is incompatible as we only support sl pricing, hence price will be wrong
				Double.valueOf(basePrice.value.toString()) == 1080.0
			}
		}
		// Place Order should fail with Bad Request (400)
		def orderData = placeOrder(restClient, B2B_CUSTOMER, cart.code, format, HttpStatus.SC_BAD_REQUEST)


		where:
		format << [XML, JSON]
	}

	protected getConfigurationForOrderEntry(RESTClient client, customer, orderId, entryNumber, format, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.get(
				path: basePathWithSite + '/users/'+ customer.id + '/orders/'+orderId+'/entries/' + entryNumber + SLASH_CONFIGURATOR_TYPE_OCC_SLASH,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: URLENC
				)

		LOG.info("configurationData: " + response.data.toString())
		with(response) {
			status == SC_OK
		}
		return response.data
	}

	protected void setPaymentDetailsForCart(RESTClient client, customer, cartID, paymentDetailsId, format, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.put(
				path: basePathWithSite + '/users/' + customer.id + '/carts/' + cartID + '/paymentdetails',
				query: ['paymentDetailsId': paymentDetailsId],
				contentType: format,
				requestContentType: URLENC
				)
		if (isNotEmpty(response.data)) println(response.data)
		with(response) {
			status == SC_OK
		}
	}

	protected placeOrder(RESTClient client, customer, cartID, format, expectedStatus) {
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + getB2BAwareUsersPathFragment() + customer.id + '/orders',
				query: [
					'cartId'      : cartID,
					'termsChecked': true,
					'fields'      : FIELD_SET_LEVEL_FULL
				],
				contentType: format,
				requestContentType: URLENC
				)

		LOG.info("placeOrder response data: " + response.data.toString())
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == expectedStatus
		}
		return response.data
	}

	protected getDeliveryModes(RESTClient client, customer, cartID, format, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/carts/' + cartID + '/deliverymodes',
				contentType: format,
				requestContentType: URLENC)

		if (isNotEmpty(response.data) && isNotEmpty(response.data.errors)) println(response.data)
		with(response) {
			status == SC_OK
		}
		return response.data
	}

	protected void setDeliveryModeForCart(RESTClient client, customer, cartID, deliveryModeId, format, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.put(
				path: basePathWithSite + '/users/' + customer.id + '/carts/' + cartID + '/deliverymode',
				query: ['deliveryModeId': deliveryModeId],
				contentType: format,
				requestContentType: URLENC
				)
		if (isNotEmpty(response.data)) println(response.data)
		with(response) {
			status == SC_OK
		}
	}

	protected getPaymentDetails(RESTClient client, customer, format, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + '/users/' + customer.id + '/paymentdetails',
				query: ["fields": FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC)
		if (isNotEmpty(response.data)) println(response.data)
		with(response) {
			status == SC_OK
		}
		return response.data
	}

	protected void setDeliveryAddressForCart(RESTClient client, customer, cartID, addressID, format, basePathWithSite = getBasePathWithSite()) {
		def response = client.put(
				path: basePathWithSite + getB2BAwareUsersPathFragment() + customer.id + '/carts/' + cartID + '/addresses/delivery',
				query: ['addressId': addressID],
				contentType: format,
				requestContentType: URLENC
				)
		with(response) {
			status == SC_OK
		}
	}

	protected getAddress(RESTClient client, user, format = JSON, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.get(
				path: getBasePathWithSite() + '/users/' + user.id + "/addresses",
				query: [
					'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: format,
				requestContentType: URLENC)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
			data.addresses != null
			data.addresses[0].id != null
		}

		return response.data
	}

	/**
	 * Add a product to cart
	 * @param client REST client to use
	 * @param customerId the customer id
	 * @param guid The cart guid
	 * @param productCode Hybris product code
	 * @return cart modification
	 */
	protected addProductToCart(RESTClient client, customerId, guid, productCode, format, basePathWithSite=getBasePathWithSite()) {
		def modification = returningWith(client.post(
				//Note that DefaultCartLoaderStrategy calls commerceCartService.getCartForGuidAndSite when searching a cart with ID.
				//So for anonymous carts we need to call with the guid, while for user carts we would need the cart code
				path: basePathWithSite + getB2BAwareUsersPathFragment() + customerId + '/carts/'+ guid +'/entries',
				contentType: format,
				query: [ 'code': productCode ],
				requestContentType: URLENC), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data

		return modification
	}
}
