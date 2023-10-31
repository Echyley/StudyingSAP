/*
 * [y] hybris Platform
 *
 * Copyright (c) 2020 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.integrationtests.hybris.occ

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import groovyx.net.http.HttpResponseDecorator



@ManualTest
@RunWith(HybrisSpockRunner.class)
class SavedCartIntegrationWsIntegrationTest extends BaseSpockTest {

	org.slf4j.Logger LOG = LoggerFactory.getLogger(SavedCartIntegrationWsIntegrationTest.class)
	protected static final PRODUCT_KEY = 'CPQ_HOME_THEATER'
	static final CUSTOMER = ["id": USERNAME, "password": PASSWORD]
	protected static final DELIVERY_STANDARD = 'standard-gross'

	@Test
	def "Authorize existing customer and create saved cart with configurable product"(){
		
		if(isExtensionInSetup("sapproductconfigservicesssc")) {
			LOG.info("Session lifecycle implemented by sapproductconfigservicesssc is not fully compatible with OCC, hence skipping SavedCartIntegrationWsIntegrationTest");
			return;
		}


		given: "Customer logs in and cart is created"
		authorizeCustomer(CUSTOMER)
		def cart = createEmptyCart(CUSTOMER.id, format)

		when: "Logged in customer tries to add a configurable product to the cart"
		def modification = addProductToCart(CUSTOMER.id, cart.code, PRODUCT_KEY, format)
		def entryNumber = modification.entry.entryNumber;
		getConfiguration(CUSTOMER.id,cart.code,entryNumber,format);

		String configId = updateEntryWithCPQHomeTheaterToValidConfig( CUSTOMER.id, cart.code, entryNumber);
		LOG.info("Configuration with id added to cart entry: "+configId);

		and: "and saves the cart"

		saveCart(CUSTOMER.id, cart.code,format);

		then: "the configuration attached to the item could be read successfully from the saved cart and contains a configuration id"

		when:"user retrieves config overview for the saved cart entry"
		// call new API to fetch config data
		def overviewData = getOverviewForCartEntry(CUSTOMER.id, cart.code, entryNumber, format)

		then: "they get the overview data matching the configuration changes made previously in the cart."
		validateCPQHomeTheaterConfig(overviewData, configId);

		where:
		format <<[XML, JSON]
	}



	protected saveCart(customerId, cartId,format) {
		def response = restClient.patch(
				path: getBasePathWithSite() + '/users/' + customerId + '/carts/' + cartId + '/save',
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC
				)
		LOG.info("SAVED CART RESPONSE:" +response.data.toString());
		with(response) {
			status == SC_OK
		}
	}


	protected getOverviewForCartEntry(customerId, cartId, entryNumber, format) {
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/'+ customerId + '/carts/'+cartId+'/entries/' + entryNumber + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + 'configurationOverview',
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: URLENC
				)

		LOG.info("overviewData: " + response.data.toString())
		with(response) {
			status == SC_OK
		}
		return response.data
	}
}
