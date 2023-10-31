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
import org.slf4j.LoggerFactory

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator


@ManualTest
@RunWith(HybrisSpockRunner.class)
class ProductConfigWsMediaTest extends BaseSpockTest {

	def org.slf4j.Logger LOG = LoggerFactory.getLogger(ProductConfigWsMediaTest.class)

	@Test
	def "Get a multilevel configuration and check for media at characteristic level"() {

		when: "reads a configuration"
		HttpResponseDecorator response = getVisualEntertainmentGroup(format)

		then: "receives media along with the response"
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 3


			data.groups[1].name == "2"
			data.groups[1].description == "Visual entertainment"
			data.groups[1].id=="1-CPQ_HOME_THEATER@2"
			data.groups[1].attributes.size() == 3
			data.groups[1].attributes[0].name == "CPQ_HT_INCLUDE_TV"
			data.groups[1].attributes[0].images.size() == 1
			data.groups[1].attributes[0].images[0].format == "CSTIC_IMAGE"
		}


		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a multilevel configuration and check for media at characteristic value level"() {

		when: "reads a configuration"
		HttpResponseDecorator response = getVisualEntertainmentGroup(format)

		then: "receives media along with the response for the characteristic value"
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 3


			data.groups[1].name == "2"
			data.groups[1].description == "Visual entertainment"
			data.groups[1].id=="1-CPQ_HOME_THEATER@2"
			data.groups[1].attributes.size() == 3
			data.groups[1].attributes[2].name == "CPQ_HT_VIDEO_SOURCES"
			data.groups[1].attributes[2].domainValues[0].images.size() == 1
			data.groups[1].attributes[2].domainValues[0].images[0].format == "VALUE_IMAGE"
		}


		where:
		format << [XML, JSON]
	}

	protected getVisualEntertainmentGroup(ContentType format) {
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators/ccpconfigurator',
				contentType: format
				)
		String configId = response.data.configId

		def responseFromGet = restClient.get(
				path: getBasePathWithSite() + '/ccpconfigurator/' + configId ,
				contentType: format,
				query:['groupId': '1-CPQ_HOME_THEATER@2']
				)

		return responseFromGet
	}
}
