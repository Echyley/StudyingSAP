/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.integrationtests.mock

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_CREATED
 import static org.apache.http.HttpStatus.SC_NOT_FOUND

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.cpq.productconfig.occ.tests.setup.TestSetupStandalone
import de.hybris.platform.testframework.HybrisSpockRunner
import de.hybris.platform.util.Config

import org.apache.http.HttpStatus
import org.junit.Test
import org.junit.runner.RunWith

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

import static org.junit.Assume.assumeTrue;
import static org.junit.Assume.assumeFalse;

@ManualTest
@RunWith(HybrisSpockRunner.class)
class CpqQuoteIntegrationWsIntegrationTest extends BaseSpockTest {
	protected static final DELIVERY_STANDARD = 'standard-gross'

	@Test
	def "Authorize existing customer and create a quote with a complete configurable product"(){
	
		assumeFalse(TestSetupStandalone.isGetCPQConfigurationIdForQuoteEntryEndpointDisabled());
		
		given: "Customer logs in, creates cart and submits a quote"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_CUSTOMER.id)
		def cartModification = addProductToCart(restClient, B2B_CUSTOMER.id, cart.code, P_CODE_CONF_LAPTOP_COMPLETE, format)
		def entry = getCartEntry(cart, 0, format,B2B_CUSTOMER)
		LOG.info("cart entry:" +entry)

		def quoteData = createQuote(restClient, B2B_CUSTOMER, cart.code, format, HttpStatus.SC_CREATED)

		when: "user retrieves the configuration for the quote entry"
		def response = getConfigurationForQuoteEntry(restClient, B2B_CUSTOMER, quoteData.code, 0, format)
		LOG.info("status: " + response.status + " configurationData: " + response.data.toString())
		
		then: "the quote entry contains expected configuration id"
		
		with(response) {
			status == SC_OK
			data.configId != null
			data.configId.toString().startsWith("MOCK_") == true
			data.configId.toString().endsWith(P_CODE_CONF_LAPTOP_COMPLETE) == true
		}

		where:
		format << [JSON]
	}
	
	@Test
	def "Authorize existing customer and create a quote with a complete configurable product when getCPQConfigurationIdForQuoteEntry deactivated"(){
	
		assumeTrue(TestSetupStandalone.isGetCPQConfigurationIdForQuoteEntryEndpointDisabled());
		
		given: "Customer logs in, creates cart and submits a quote"
		authorizeCustomer(B2B_CUSTOMER)
		def cart = createEmptyCart(format, B2B_CUSTOMER.id)
		def cartModification = addProductToCart(restClient, B2B_CUSTOMER.id, cart.code, P_CODE_CONF_LAPTOP_COMPLETE, format)
		def entry = getCartEntry(cart, 0, format,B2B_CUSTOMER)
		LOG.info("cart entry:" +entry)

		def quoteData = createQuote(restClient, B2B_CUSTOMER, cart.code, format, HttpStatus.SC_CREATED)

		when: "user retrieves the configuration for the quote entry"
		def response = getConfigurationForQuoteEntry(restClient, B2B_CUSTOMER, quoteData.code, 0, format)
		LOG.info("status: " + response.status + " configurationData: " + response.data.toString())
		
		then: "configuration id can not be retrieved from the quote entry"
		
		with(response) {
			status == SC_NOT_FOUND
		}

		where:
		format << [JSON]
	}
	
	protected getConfigurationForQuoteEntry(RESTClient client, customer, quoteId, entryNumber, format, basePathWithSite = getBasePathWithSite()) {
		HttpResponseDecorator response = client.get(
				path: basePathWithSite + '/users/' + customer.id + '/quotes/'+ quoteId + '/entries/' + entryNumber + SLASH_CONFIGURATOR_TYPE_OCC_SLASH,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: URLENC
				)
		return response
	}
	
	protected createQuote(RESTClient client, customer, cartID, format, expectedStatus) {
	
		HttpResponseDecorator response = restClient.post(
				path: getBasePathWithSite() + "/users/" + customer.id + '/quotes',
				body: ["cartId": cartID],
				contentType: format,
				requestContentType: format
				)

		LOG.info("createQuote response data: " + response.data.toString())
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == expectedStatus
		}
		return response.data
	}

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
