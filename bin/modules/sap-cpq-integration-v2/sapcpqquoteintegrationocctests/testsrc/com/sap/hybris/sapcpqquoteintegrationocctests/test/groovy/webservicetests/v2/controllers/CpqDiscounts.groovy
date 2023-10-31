/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.sap.hybris.sapcpqquoteintegrationocctests.test.groovy.webservicetests.v2.controllers

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.commercewebservicestests.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest

import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

@ManualTest
@Unroll
class CpqDiscounts extends AbstractSpockFlowTest {
	static final CUSTOMER = ["id": "mark.rivers@rustic-hw.com", "password": "1234"]
	static final String QUOTECODE = "testCpqDiscount"

	def "B2B Customer should be able to see the discount of a specific quote"() {
		given: "a registered and logged in B2B customer creates a quote and it is approved"
		authorizeCustomer(restClient, CUSTOMER)

		when: "he requests to view the quote just created"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/' + CUSTOMER.id + '/quotes/' + QUOTECODE,
				query: [
						'fields': FIELD_SET_LEVEL_FULL
				],
				contentType: format,
				requestContentType: URLENC
		)

		then: "quote is returned with specified discount "
		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.errors)) {
				println(data)
			}
			status == SC_OK
			data.state == "BUYER_OFFER"
			data.entries[0].quantity == 2
			data.entries[0].containsKey('cpqDiscounts') == true
			
		}   
		 where:
        format << [JSON]
	}
}