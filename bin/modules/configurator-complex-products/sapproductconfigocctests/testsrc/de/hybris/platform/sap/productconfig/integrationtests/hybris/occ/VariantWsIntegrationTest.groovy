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
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import groovyx.net.http.HttpResponseDecorator

@ManualTest
@RunWith(HybrisSpockRunner.class)
class VariantWsIntegrationTest extends BaseSpockTest {

	protected static final PRODUCT_KEY = 'CONF_CAMERA_SL-PROF-BLACK'
	protected static final PRODUCT_KEY_CHANGEABLE_VARIANT = 'CONF_M_PIPE-30-20-PL'
	protected static final PRODUCT_KEY_BASE = 'CONF_CAMERA_SL'
	org.slf4j.Logger LOG = LoggerFactory.getLogger(VariantWsIntegrationTest.class)


	@Test
	def "Create a new cart with a variant from catalog"() {

		when: "consumer tries to add a variant product to the cart"

		def cart = createEmptyCart(format)
		def modification = addProductToCart(cart.guid, PRODUCT_KEY, format)

		then: "the item was added successfully"
		if(isNotEmpty(modification)&&isNotEmpty(modification.errors))
			println(modification)
		modification.statusCode == 'success'


		where:
		format <<[XML, JSON]
	}

	@Test
	def "Read configuration for an existing variant part of the cart"() {

		when: "consumer reads configuration for a cart entry"
		def cart = createEmptyCart(format);
		def modification = addProductToCart(cart.guid, PRODUCT_KEY, format)
		def entryNumber = 0

		HttpResponseDecorator response = restClient.get(
				path: basePathWithSite + '/users/anonymous/carts/'+cart.guid+'/entries/' + entryNumber+SLASH_CONFIGURATOR_TYPE_OCC,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: URLENC
				)

		then: "the configuration attached to the item could be read successfully and contains the base product code"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.rootProduct == PRODUCT_KEY_BASE
		}

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Read generic configuration aspects for an existing variant part of the cart"() {

		when: "consumer reads configuration for a cart entry via generic endpoint"
		def cart = createEmptyCart(format)
		def modification = addProductToCart(cart.guid, PRODUCT_KEY, format)
		def entryNumber = 0

		HttpResponseDecorator response = restClient.get(
				path: basePathWithSite + '/users/anonymous/carts/'+cart.guid,
				contentType : format,
				query : ['fields' : 'DEFAULT'],
				requestContentType: URLENC
				)


		then: "the variant is part of the cart, and the generic configuration aspects attached to the item could be read successfully"

		LOG.info("RESPONSE FROM GENERIC CART READ: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.entries[0].entryNumber == 0
			data.entries[0].product.code == PRODUCT_KEY
			data.entries[0].configurationInfos.size() == 4
			data.entries[0].configurationInfos[0].configuratorType == 'CPQCONFIGURATOR'
			data.entries[0].configurationInfos[0].configurationLabel == 'Mode'
			data.entries[0].configurationInfos[0].configurationValue == 'Professional'
			data.entries[0].configurationInfos[1].configuratorType == 'CPQCONFIGURATOR'
			data.entries[0].configurationInfos[1].configurationLabel == 'Body Color'
			data.entries[0].configurationInfos[1].configurationValue == 'Black'

			data.entries[0].statusSummaryList.size() == 1
			data.entries[0].statusSummaryList[0].status == 'NONE'
			data.entries[0].statusSummaryList[0].numberOfIssues == 1
		}

		where:
		format <<[XML, JSON]
	}

	@Test
	def "Configure a variant, add the configuration to the cart, and validate results in cart"() {

		when: "consumer tries to add a configuration based on a variant to the cart"

		def cart = createEmptyCart(format)
		def configuration = createDefaultConfiguration(PRODUCT_KEY, restClient, format)

		def postBody = orderToJsonMapping(PRODUCT_KEY, configuration.configId)

		HttpResponseDecorator response = restClient.post(
				path: basePathWithSite + '/users/anonymous/carts/' + cart.guid + '/entries' + SLASH_CONFIGURATOR_TYPE_OCC,
				body: postBody,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: requestFormat
				)

		then: "the entry, along with its configuration, was added successfully"
		with(response) { status == SC_CREATED }

		when: "consumer views details of the variants configuration in cart"
		HttpResponseDecorator responseFromCart = restClient.get(
				path: basePathWithSite + '/users/anonymous/carts/'+cart.guid,
				contentType : format,
				query : ['fields' : 'DEFAULT'],
				requestContentType: URLENC
				)

		then: "the cart shows the configurable root product, and the generic configuration aspects match the variants configuration"

		with(responseFromCart) {
			status == SC_OK
			data.entries[0].entryNumber == 0
			data.entries[0].product.code == PRODUCT_KEY_BASE
			data.entries[0].configurationInfos.size() == 4
			data.entries[0].configurationInfos[0].configuratorType == 'CPQCONFIGURATOR'
			data.entries[0].configurationInfos[0].configurationLabel == 'Mode'
			data.entries[0].configurationInfos[0].configurationValue == 'Professional'
			data.entries[0].configurationInfos[1].configuratorType == 'CPQCONFIGURATOR'
			data.entries[0].configurationInfos[1].configurationLabel == 'Body Color'
			data.entries[0].configurationInfos[1].configurationValue == 'Black'
		}

		where:
		requestFormat | format
		JSON          | JSON
	}
	@Test
	def "Configure a changeable variant, add the configuration to the cart, and validate results in cart"() {

		when: "consumer tries to add a configuration based on a changeable variant to the cart"

		def cart = createEmptyCart(format)
		def configuration = createDefaultConfiguration(PRODUCT_KEY_CHANGEABLE_VARIANT, restClient, format)

		def postBody = orderToJsonMapping(PRODUCT_KEY_CHANGEABLE_VARIANT, configuration.configId)

		HttpResponseDecorator response = restClient.post(
				path: basePathWithSite + '/users/anonymous/carts/' + cart.guid + '/entries' + SLASH_CONFIGURATOR_TYPE_OCC,
				body: postBody,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: requestFormat
				)

		then: "the entry, along with its configuration, was added successfully"
		with(response) { status == SC_CREATED }

		when: "consumer views details of the variants configuration in cart"
		HttpResponseDecorator responseFromCart = restClient.get(
				path: basePathWithSite + '/users/anonymous/carts/'+cart.guid,
				contentType : format,
				query : ['fields' : 'DEFAULT'],
				requestContentType: URLENC
				)

		then: "the cart still shows the changeable variant, and the generic configuration aspects match the variants configuration"

		with(responseFromCart) {
			status == SC_OK
			data.entries[0].entryNumber == 0
			data.entries[0].product.code == PRODUCT_KEY_CHANGEABLE_VARIANT
			data.entries[0].configurationInfos.size() == 3
			data.entries[0].configurationInfos[0].configuratorType == 'CPQCONFIGURATOR'
			data.entries[0].configurationInfos[0].configurationLabel == 'type of pipe'
			data.entries[0].configurationInfos[0].configurationValue == 'Plastic'
			data.entries[0].configurationInfos[1].configuratorType == 'CPQCONFIGURATOR'
			data.entries[0].configurationInfos[1].configurationLabel == 'Outer Diamater in cm'
			data.entries[0].configurationInfos[1].configurationValue == '30'
		}

		where:
		requestFormat | format
		JSON          | JSON
	}

	@Test
	def "Read matching variants according to a configuration"() {

		when: "Anonymous user creates a new configuration for a configurable camera"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/'+PRODUCT_KEY_BASE+'/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "they get a new default configuration"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) { status == SC_OK }

		when: "Updating the configuration and selecting camera mode professional"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "P"


		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "they receive a success response"
		with(responseAfterUpdate) {
			status == SC_OK
		}
		when: "Reading matching variants"
		HttpResponseDecorator responseAfterVariantRead = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId+'/variants',

				contentType: format
				)

		then: "they get a list of variants, containing 2 items, that represent professional cameras"

		with(responseAfterVariantRead) {
			status == SC_OK
			data.size() == 2
			data[0].productCode == "CONF_CAMERA_SL-PROF-BLACK"
			data[1].productCode == "CONF_CAMERA_SL-PROF-METALLIC"
		}
		where:
		format << [JSON]
	}

	@Test
	def "Read price and image data for a matching variant"() {

		when: "Anonymous user creates a new configuration for a configurable camera"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/'+PRODUCT_KEY_BASE+'/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "they get a new default configuration"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) { status == SC_OK }

		when: "Reading matching variants"
		HttpResponseDecorator responseAfterVariantRead = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId+'/variants',

				contentType: format
				)

		then: "they get a list of all variants for the camera, containing price and image information"

		with(responseAfterVariantRead) {
			status == SC_OK
			data.size() == 4
			data[0].productCode == "CONF_CAMERA_SL-PROF-BLACK"
			data[0].price.value == 3300.0
			data[0].imageData.format == "cartIcon"
			data[0].imageData.imageType == "PRIMARY"
		}
		where:
		format << [JSON]
	}
}
