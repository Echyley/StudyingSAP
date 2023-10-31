/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseaddressocctests.controllers

import static groovyx.net.http.ContentType.JSON
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import spock.lang.Unroll


@ManualTest
@Unroll
class CitiesControllerTest extends AbstractSpockFlowTest {

	def "Should get all cities for an existing region"() {
		given: "a region id"
		def regionId = "CN-51"

		when: "request to get all cities for the given region"
		def response = restClient.get(
				path: getBasePathWithSite() + "/regions/" + regionId + "/cities",
				contentType: JSON,
				requestContentType: JSON)

		then: "a list of cities are returned"
		with(response) {

			status == SC_OK
			data.cities.size() == 21
		}
	}

	def "Should not get cities for a non-existing region"() {
		given: "a region id"
		def regionId = "non-existing-id"

		when: "request to get all cities for the given region"
		def response = restClient.get(
				path: getBasePathWithSite() + "/regions/" + regionId + "/cities",
				contentType: JSON,
				requestContentType: JSON)

		then: "a list of cities are returned"
		with(response) {
			status == SC_OK
			isEmpty(data.cities)
		}
	}

	def "Should get all districts for an existing city"() {
		given: "a city id"
		def cityId = "CN-51-10"

		when: "request to get all districts for the given city"
		def response = restClient.get(
				path: getBasePathWithSite() + "/cities/" + cityId + "/districts",
				contentType: JSON,
				requestContentType: JSON)

		then: "a list of districts are returned"
		with(response) {
			status == SC_OK
			data.districts.size() == 19
		}
	}

	def "Should not to get all districts for a non-existing city"() {
		given: "a city id"
		def cityId = "non-existing-id"

		when: "request to get all districts for the given city"
		def response = restClient.get(
				path: getBasePathWithSite() + "/cities/" + cityId + "/districts",
				contentType: JSON,
				requestContentType: JSON)

		then: "a list of districts are returned"
		with(response) {
			status == SC_OK
			isEmpty(data.districts)
		}
	}
}
