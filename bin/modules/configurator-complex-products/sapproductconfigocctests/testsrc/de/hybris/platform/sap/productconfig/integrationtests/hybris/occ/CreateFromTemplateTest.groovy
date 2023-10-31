/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
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

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient


@ManualTest
@RunWith(HybrisSpockRunner.class)
class CreateFromTemplateTest extends BaseSpockTest {
	 

			def org.slf4j.Logger LOG = LoggerFactory.getLogger(CreateFromTemplateTest.class)
			protected static final ATTRIBUTE_SOURROUND_MODE = "CPQ_HT_SURROUND_MODE"
			protected static final VALUE_STEREO = "STEREO"	

			@Test
			def "Create configuration with changed values and use this configuration as a template"() {

				when: "Anonymous user creates a new configuration"
				HttpResponseDecorator response = restClient.get(
						path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
						contentType: format
						)

				then: "Gets a new default configuration with an unspecified first attribute"
				LOG.info("RESPONSE: "+response.data.toString())
				with(response) { 
					status == SC_OK
					data.groups[0].attributes[0].name == ATTRIBUTE_SOURROUND_MODE
					data.groups[0].attributes[0].domainValues[0].name == VALUE_STEREO	
					data.groups[0].attributes[0].domainValues[0].selected == false								
				}
				
				when: "Afterwards updates the configuration with a characteristic value"
				def putBody = response.data
				putBody.groups[0].attributes[0].value = VALUE_STEREO


				HttpResponseDecorator responseAfterUpdate = restClient.patch(
						path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
						body : putBody,
						contentType: format
						)

				then: "Gets the updated configuration result and expects to see the changed value"
				with(responseAfterUpdate) {
					status == SC_OK
					data.groups[0].attributes[0].name == ATTRIBUTE_SOURROUND_MODE
					data.groups[0].attributes[0].value == VALUE_STEREO
				}
				
				when:"Now creates new configuration, using the previous one as template"
				def templateConfigId = response.data.configId
				HttpResponseDecorator responseCreateFromTemplate = restClient.get(
						path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
						query : ['configIdTemplate' : templateConfigId],
						contentType: format
						)	
				
				then: "Gets the results of the new configuration and expects to see the values of the template"
				with(responseCreateFromTemplate) {
					status == SC_OK
					data.configId != 	templateConfigId	
					data.groups[0].attributes[0].name == ATTRIBUTE_SOURROUND_MODE
					data.groups[0].attributes[0].value == VALUE_STEREO
				}				
				
				where:
				format << [JSON]
			}
}
