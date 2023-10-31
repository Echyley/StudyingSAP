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
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith

import groovyx.net.http.HttpResponseDecorator

@ManualTest
@RunWith(HybrisSpockRunner.class)
class GenericConfigProductWsIntegrationTest extends BaseSpockTest {
	final PRODUCT_KEY = 'KD990SOL'
	final PRODUCT_KEY_NON_CONFIGURABLE = 'KD991SOL'
	final PRODUCT_NAME = 'SAP Solar Installation'
	final PRODUCT_NAME_NON_CONFIGURABLE = 'Non configurable product'
	final CONFIG_TYPE = 'CPQCONFIGURATOR'

	@Test
	def "Get product details with configurator type"() {

		when: "reading a product"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_KEY,
				contentType: format
				)

		then: "consumer gets product details with configuration type"
		with(response) {
			status == SC_OK;
			data.code == PRODUCT_KEY
			data.configurable == true
			data.configuratorType == CONFIG_TYPE
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Get product details for non-configurable product"() {

		when: "reading a non-configurable product"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_KEY_NON_CONFIGURABLE,
				contentType: format
				)

		then: "consumer gets product details without configuration type"
		with(response) {
			status == SC_OK;
			data.code == PRODUCT_KEY_NON_CONFIGURABLE
			data.configurable == false
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Product search including configurator type"() {

		when: "searching for products with product name as free text search"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				query: [ 'query': PRODUCT_NAME],

				contentType: format
				)

		then: "consumer gets search result with products and configurator types"
		with(response) {
			status == SC_OK;
			data.products.size() > 0
			data.products[0].code == PRODUCT_KEY
			data.products[0].configurable == true
			data.products[0].configuratorType == CONFIG_TYPE
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Product search for all products"() {

		when: "searching for all products"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/search',
				query: ['fields': 'products,sorts,pagination'],

				contentType: format
				)

		then: "consumer gets at least 9 products"
		with(response) {
			status == SC_OK;
			data.products.size() > 8
		}

		where:
		format << [XML, JSON]
	}
}
