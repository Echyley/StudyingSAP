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
class ConflictWsIntegrationTest extends BaseSpockTest {

	protected static final PRODUCT_KEY_IMMEDIATE_RESOLUTION = 'CONF_CAMERA_SL'
	protected static final PRODUCT_KEY_NO_IMMEDIATE_RESOLUTION = 'CONF_HOME_THEATER_ML'
 
	org.slf4j.Logger LOG = LoggerFactory.getLogger(ConflictWsIntegrationTest.class)


	@Test
	def "Configure a product with immediate conflict resolution active"() {

		when: "Anonymous user creates a new configuration for a configurable camera"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/'+PRODUCT_KEY_IMMEDIATE_RESOLUTION+'/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "they get a new default configuration, which declares that conflicts need to be resolved immediately"
		with(response) { 
			status == SC_OK
			data.immediateConflictResolution == true
		}

		when: "User updates the configuration and selects camera mode professional"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "P"


		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "they receive a success response which still declares that conflicts need to be fixed immediately"
		with(responseAfterUpdate) {
			status == SC_OK
			data.immediateConflictResolution == true
		}
		where:
		format << [JSON]
	}
	
	@Test
	def "Configure a product with no immediate conflict resolution active"() {

		when: "Anonymous user creates a new configuration for a home theater"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/'+PRODUCT_KEY_NO_IMMEDIATE_RESOLUTION+'/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "they get a new default configuration, which declares that conflicts don't need to be resolved immediately"
		with(response) { 
			status == SC_OK
			data.immediateConflictResolution == false
		}

		where:
		format << [JSON]
	}	

}
