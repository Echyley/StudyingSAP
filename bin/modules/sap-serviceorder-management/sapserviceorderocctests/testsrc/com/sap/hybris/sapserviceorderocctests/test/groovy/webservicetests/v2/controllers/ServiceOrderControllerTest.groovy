/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocctests.test.groovy.webservicetests.v2.controllers

import static groovyx.net.http.ContentType.*

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.carts.AbstractCartTest
import de.hybris.platform.util.Config

import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

@ManualTest
@Unroll
class ServiceOrderControllerTest extends AbstractCartTest {

	static final String B2BADMIN_USERNAME = "mark.rivers.approval@rustic-retail-hw.com"
	static final String B2BADMIN_PASSWORD = "1234"

	static final String B2BCUSTOMER_USERNAME = "mark.rivers@rustic-hw.com"
	static final String B2BCUSTOMER_PASSWORD = "1234"

	static final String PRODUCT_HW1210_9342 = "HW1210-9342"

	static final String OCC_OVERLAPPING_PATHS_FLAG = "occ.rewrite.overlapping.paths.enabled"
	static final ENABLED_CONTROLLER_PATH = Config.getBoolean(OCC_OVERLAPPING_PATHS_FLAG, false) ? COMPATIBLE_CONTROLLER_PATH : CONTROLLER_PATH
	static final String CONTROLLER_PATH = "/users"
	static final String COMPATIBLE_CONTROLLER_PATH = "/orgUsers"
	static final ORDER_PATH = "/orders"
	static final CARTID = "xyz"

	protected static final SCHEDULE_AT = "{ \"scheduledAt\" : \"2021-01-31T09:00:00+0000\"}"

	def setup() {
		authorizeTrustedClient(restClient)
	}





	def "B2B Customer creates a new cart with service scheduling details: #format"() {
		given: "a registered and logged in customer"
		def b2bCustomer = [
			'id'      : B2BCUSTOMER_USERNAME,
			'password': B2BCUSTOMER_PASSWORD
		]
		authorizeCustomer(restClient, b2bCustomer)

		def cart = createCart(restClient, b2bCustomer, JSON, basePathWithSite)

		addProductToCartOnline(restClient, b2bCustomer, cart.code, PRODUCT_HW1210_9342, 1, JSON)

		when: "he requests to create a new order with service scheduling details"

		HttpResponseDecorator response = restClient.put(
				path: basePathWithSite + '/users/' + b2bCustomer.id + '/carts/' + cart.code + '/serviceOrder/serviceScheduleSlot',
				body: SCHEDULE_AT,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC
				)
		then: "Cart is retrieved"
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
			status == SC_OK
		}

		where:
		format << [JSON]
	}
}
