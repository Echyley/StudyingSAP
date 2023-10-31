/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderocctests.test.groovy.webservicetests.v2.controllers

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

@ManualTest
@Unroll
class ServiceProductsControllerTest extends AbstractSpockFlowTest {

	static final String PRODUCT_CODE = "1225696"
	static final String SERVICE = "SERVICE"

	def "Retrieve Service product details: #format"() {

		when: "A request is made to retrieve Service product details"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_CODE ,
				contentType: format,
				requestContentType: URLENC
				)

		then: "Service Product details are retrieved."
		with(response) {
			println '---' + data
			status == SC_OK;
			data.code == PRODUCT_CODE
			data.productTypes.toString().contains(SERVICE)
		}
		where:
		format << [JSON]
	}
}
