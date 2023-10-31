/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.sap.productconfig.integrationtests.hybris.occ

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.sap.productconfig.testdata.occ.setup.TestSetupStandalone
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

import groovyx.net.http.HttpResponseDecorator

@ManualTest
@RunWith(HybrisSpockRunner.class)
class ProductConfigMessagesWsIntegrationTest extends BaseSpockTest {

	protected static final PRODUCT_KEY = 'CPQ_LAPTOP'

	@After
	public static void tearDown() {
		TestSetupStandalone.ensureSupportProductConfigMessagesInactive()
	}

	@Test
	def "Configure a product with messages on product, characteristic and value level when product config messages are supported"() {

		when: "Anonymous user creates a new configuration for a CPQ Laptop"
		TestSetupStandalone.ensureSupportProductConfigMessagesActive()
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_KEY +'/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "they get a new default configuration"
		with(response) { 
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "User updates the configuration and selects 32GB memory"
		def putBody = response.data
		putBody.groups[0].attributes[3].value = "32GB"

		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "they receive an updated configuration which has now messages on the product, characteristic and characteristic value level"
		with(responseAfterUpdate) {
			status == SC_OK
			data.configId.isEmpty() == false
			
			data.messages.size() == 1
			data.messages[0].message == "Message for product"
			data.messages[0].severity == "CONFIG"
			
			data.groups[0].attributes[3].messages.size() == 1
			data.groups[0].attributes[3].messages[0].message == "Message for characteristic"
			data.groups[0].attributes[3].messages[0].extendedMessage == "Extended message for characteristic"
			data.groups[0].attributes[3].messages[0].severity == "CONFIG"
			data.groups[0].attributes[3].messages[0].promoType == "PROMO_APPLIED"
			data.groups[0].attributes[3].messages[0].endDate == "12/31/22"
			
			data.groups[0].attributes[3].domainValues[1].messages.size() == 1
			data.groups[0].attributes[3].domainValues[1].messages[0].message == "Message for characteristic value"
			data.groups[0].attributes[3].domainValues[1].messages[0].extendedMessage == "Extended message for characteristic value"
			data.groups[0].attributes[3].domainValues[1].messages[0].severity == "CONFIG"
			data.groups[0].attributes[3].domainValues[1].messages[0].promoType == "PROMO_APPLIED"
			data.groups[0].attributes[3].domainValues[1].messages[0].endDate == "12/31/22"
		}
		
		when: "Afterwards get the configuration overview by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ configId +'/configurationOverview'  ,
				contentType: format
				)

		then: "verify that the configuration overview has a message on the product level"
		with(response) {
			status == SC_OK
			data.id == configId
			data.messages.size() == 1
			data.messages[0].message == "Message for product"
			data.messages[0].severity == "CONFIG"
		}
		where:
		format << [JSON]
	}
	
	@Test
	def "Configure a product with messages on product, characteristic and value level when product config messages are not supported"() {

		when: "Anonymous user creates a new configuration for a CPQ Laptop"
		TestSetupStandalone.ensureSupportProductConfigMessagesInactive()
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_KEY +'/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "they get a new default configuration"
		with(response) { 
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "User updates the configuration and selects 32GB memory"
		def putBody = response.data
		putBody.groups[0].attributes[3].value = "32GB"

		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "they receive an updated configuration which still has no messages on the product, characteristic and characteristic value level"
		with(responseAfterUpdate) {
			status == SC_OK
			data.configId.isEmpty() == false
			
			isEmpty(data.messages) == true
			isEmpty(data.groups[0].attributes[3].messages) == true
			isEmpty(data.groups[0].attributes[3].domainValues[1].messages) == true
		}
		
		when: "Afterwards get the configuration overview by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ configId +'/configurationOverview'  ,
				contentType: format
				)

		then: "verify that the configuration overview has no message on the product level"
		with(response) {
			status == SC_OK
			data.id == configId
			isEmpty(data.messages) == true
		}
		where:
		format << [JSON]
	}

}
