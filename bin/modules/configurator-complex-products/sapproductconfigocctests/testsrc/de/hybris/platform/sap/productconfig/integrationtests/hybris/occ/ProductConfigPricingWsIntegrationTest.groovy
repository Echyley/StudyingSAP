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
package de.hybris.platform.sap.productconfig.integrationtests.hybris.occ

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import groovyx.net.http.HttpResponseDecorator

@ManualTest
@RunWith(HybrisSpockRunner.class)
class ProductConfigPricingWsIntegrationTest extends BaseSpockTest {

	def PRODUCT_CODE = "CONF_HOME_THEATER_ML"

	def CSTIC_UI_KEY = "3-CONF_HT_AUDIO_SYSTEM@Front_Speakers@COLUMN_SPEAKER"

	def org.slf4j.Logger LOG = LoggerFactory.getLogger(ProductConfigWsIntegrationTest.class)

	@Test
	def "Retrieve summary prices for a configuration, not requesting a specific group"() {

		when: "client requests a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/'+PRODUCT_CODE+'/configurators'+ SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "it gets a new default configuration"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.hideBasePriceAndSelectedOptions == false
			data.configId.isEmpty() == false
			data.consistent == true
			data.complete == true
			data.rootProduct == PRODUCT_CODE
		}

		when: "client requests price summary for default configuration"

		HttpResponseDecorator pricingResponse = restClient.get(
				path: getBasePathWithSite() +SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ response.data.configId+ '/pricing' ,
				contentType: format
				)

		then: "it gets a price summary response"
		with(pricingResponse) {
			status == SC_OK
			data.configId == response.data.configId
			data.priceSummary.basePrice.value == 1800
			data.priceSummary.currentTotal.value == 1800
			data.priceSummary.selectedOptions.value == 0
		}
		where:
		format << [JSON]
	}

	@Test
	def "Retrieve value prices for a configuration"() {

		when: "client requests a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/'+PRODUCT_CODE+'/configurators'+ SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "it gets a new default configuration"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.consistent == true
			data.complete == true
			data.rootProduct == PRODUCT_CODE
		}
		when: "client requests value prices for new configuration"
		HttpResponseDecorator pricingResponse = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ response.data.configId+ '/pricing',
				query:['groupId': '3-CONF_HT_AUDIO_SYSTEM@Front_Speakers'],
				contentType: format
				)

		then: "it gets a value price response"
		with(pricingResponse) {
			status == SC_OK
			data.configId == response.data.configId
			data.priceSummary != null
			data.attributes.size() == 2
			data.attributes[1].csticUiKey == CSTIC_UI_KEY
			data.attributes[1].priceSupplements.size() == 3
			data.attributes[1].priceSupplements[1].attributeValueKey == "COLUMN_SPEAKER_1250"
			data.attributes[1].priceSupplements[1].priceValue.value == 70
			data.attributes[1].priceSupplements[1].priceValue.currencyIso == "USD"
			data.attributes[1].priceSupplements[2].attributeValueKey == "COLUMN_SPEAKER_2050"
			data.attributes[1].priceSupplements[2].priceValue.value == 300
			data.attributes[1].priceSupplements[2].priceValue.currencyIso == "USD"
		}
		where:
		format << [JSON]
	}
}
