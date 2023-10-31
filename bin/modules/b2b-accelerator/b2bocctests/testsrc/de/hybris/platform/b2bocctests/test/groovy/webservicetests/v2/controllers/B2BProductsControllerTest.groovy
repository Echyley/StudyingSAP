/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocctests.test.groovy.webservicetests.v2.controllers

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.util.Config
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_OK
import static org.apache.http.HttpStatus.SC_NOT_FOUND

@ManualTest
@Unroll
class B2BProductsControllerTest extends AbstractSpockFlowTest {

	static final String PRODUCTS_PATH = "/products"
	static final String COMPATIBLE_PRODUCTS_PATH = "/orgProducts"
	static final String OCC_OVERLAPPING_PATHS_FLAG = "occ.rewrite.overlapping.paths.enabled"
	static final String SECURE_BASE_SITE="/wsTestB2B"

	static final String PRODUCT_CODE = "1225694"

	static final ENABLED_PRODUCTS_PATH = Config.getBoolean(OCC_OVERLAPPING_PATHS_FLAG, false) ? COMPATIBLE_PRODUCTS_PATH : PRODUCTS_PATH

	def "Retrieve product details with compatible products path: #descriptor"() {
		when: "a request is made to retrieve product details with compatible products path"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/' + PRODUCT_CODE,
				contentType: JSON,
				requestContentType: URLENC
		)

		then: "Product details are retrieved with compatible products path"
		with(response) {
			status == SC_OK
			data.code == PRODUCT_CODE
		}

		where:
		descriptor << [ENABLED_PRODUCTS_PATH]
	}

	def "An anonymous user should fail to retrieve product details with compatible products path when requiresAuthentication true: #descriptor"() {

		given: "secure base site with requiresAuthentication=true"

		when: "a request is made to retrieve product details with compatible products path"
		HttpResponseDecorator response = restClient.get(
				path: getBasePath() + SECURE_BASE_SITE + '/products/' + PRODUCT_CODE,
				contentType: JSON,
				requestContentType: URLENC
		)

		then: "he get not found error"
		with(response) {
			status == SC_NOT_FOUND
		}

		where:
		descriptor << [ENABLED_PRODUCTS_PATH]
	}
}
