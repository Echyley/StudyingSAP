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
import de.hybris.platform.sap.productconfig.testdata.occ.setup.TestSetupStandalone
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.After
import org.junit.Assume
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

import groovyx.net.http.HttpResponseDecorator


@ManualTest
@RunWith(HybrisSpockRunner.class)
class ProductConfigWsIntegrationTest extends BaseSpockTest {

	static final CUSTOMER = ["id": USERNAME, "password": PASSWORD]
	static final CUSTOMER2 = ["id": USERNAME2, "password": PASSWORD]

	def org.slf4j.Logger LOG = LoggerFactory.getLogger(ProductConfigWsIntegrationTest.class)

	@After
	public static void tearDown() {
		TestSetupStandalone.ensureGetDefaultConfigurationEnhancementsInactive()
	}

	@Test
	def "Get a new simple default configuration"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/YSAP_SIMPLE_POC/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration withh all fields mapped"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.consistent == true
			data.complete == false
			data.totalNumberOfIssues == 0
			data.pricingEnabled == true
			data.rootProduct == "YSAP_SIMPLE_POC"
			data.groups.size() == 1
			data.groups[0].id == "1-YSAP_SIMPLE_POC@_GEN"
			data.groups[0].name == "_GEN"
			data.groups[0].description == "[_GEN]"
			data.groups[0].configurable == true
			data.groups[0].attributes.size() == 1
			data.groups[0].attributes[0].key=="1-YSAP_SIMPLE_POC@_GEN@YSAP_POC_SIMPLE_FLAG"
			data.groups[0].attributes[0].name=="YSAP_POC_SIMPLE_FLAG"
			data.groups[0].attributes[0].langDepName=="Simple Flag: Hide options"
			data.groups[0].attributes[0].type=="CHECK_BOX"
			data.groups[0].attributes[0].validationType=="NONE"
			data.groups[0].attributes[0].visible==true
			data.groups[0].attributes[0].required==false
			data.groups[0].attributes[0].typeLength==0
			data.groups[0].attributes[0].numberScale==0
			data.groups[0].attributes[0].negativeAllowed==false
			data.groups[0].attributes[0].intervalInDomain==false
			data.groups[0].attributes[0].maxlength==1
			data.groups[0].attributes[0].domainValues.size() == 1
			data.groups[0].attributes[0].domainValues[0].key == "X"
			data.groups[0].attributes[0].domainValues[0].langDepName == "Hide"
			data.groups[0].attributes[0].domainValues[0].name == "X"
			data.groups[0].attributes[0].domainValues[0].readonly == false
			data.groups[0].attributes[0].domainValues[0].selected == true
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a new multilevel default configuration requesting attributes for all groups"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format,
				query:['provideAllAttributes': 'true']
				)

		then: "gets a new default configuration with correct structure"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.totalNumberOfIssues == 1
			data.groups.size() == 3
			data.groups[0].name == "1"
			data.groups[0].description == "Powerful audio"
			data.groups[0].id=="1-CPQ_HOME_THEATER@1"
			data.groups[0].attributes.size() == 3
			data.groups[0].attributes[0].name == "CPQ_HT_SURROUND_MODE"
			data.groups[0].attributes[0].domainValues.size() == 2
			data.groups[0].attributes[0].longText == "Surround Mode to support - will influence speaker setup"
			data.groups[0].attributes[1].name == "CPQ_HT_SUBWOOFER"
			data.groups[0].attributes[1].domainValues.size() == 1
			data.groups[0].attributes[2].name == "CPQ_HT_POWER2"
			data.groups[0].attributes[2].domainValues.size() == 3

			data.groups[1].name == "2"
			data.groups[1].description == "Visual entertainment"
			data.groups[1].id=="1-CPQ_HOME_THEATER@2"
			data.groups[1].attributes.size() == 3
			data.groups[1].attributes[0].name == "CPQ_HT_INCLUDE_TV"
			data.groups[1].attributes[0].domainValues.size() == 1
			data.groups[1].attributes[1].name == "CPQ_HT_INCLUDE_BR"
			data.groups[1].attributes[0].domainValues.size() == 1
			data.groups[1].attributes[2].name == "CPQ_HT_VIDEO_SOURCES"
			data.groups[1].attributes[2].domainValues.size() == 6

			data.groups[2].name =="CPQ_RECEIVER"
			data.groups[2].description=="Receiver"
			data.groups[2].id=="2-CPQ_RECEIVER"
			data.groups[2].subGroups.size() == 1
			data.groups[2].subGroups[0].name =="CPQ_RECEIVER"
			data.groups[2].subGroups[0].description=="Receiver"
			data.groups[2].subGroups[0].id=="2-CPQ_RECEIVER@_GEN"
			data.groups[2].subGroups[0].attributes.size() == 3
			data.groups[2].subGroups[0].attributes[0].name == "CPQ_HT_RECV_MODEL2"
			data.groups[2].subGroups[0].attributes[0].domainValues.size() == 8
			data.groups[2].subGroups[0].attributes[1].name == "CPQ_HT_RECV_MODE"
			data.groups[2].subGroups[0].attributes[1].domainValues.size() == 2
			data.groups[2].subGroups[0].attributes[2].name == "CPQ_HT_POWER2"
			data.groups[2].subGroups[0].attributes[2].domainValues.size() == 3
		}
		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a new multilevel default configuration requesting only attributes for first group (default behavior)."() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with correct structure"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 3
			data.groups[0].name == "1"
			data.groups[0].description == "Powerful audio"
			data.groups[0].id=="1-CPQ_HOME_THEATER@1"
			data.groups[0].complete == false
			data.groups[0].consistent == true
			data.groups[0].attributes.size() == 3
			data.groups[0].attributes[0].name == "CPQ_HT_SURROUND_MODE"
			data.groups[0].attributes[0].domainValues.size() == 2
			data.groups[0].attributes[0].longText == "Surround Mode to support - will influence speaker setup"
			data.groups[0].attributes[0].retractBlocked == false
			data.groups[0].attributes[1].name == "CPQ_HT_SUBWOOFER"
			data.groups[0].attributes[1].domainValues.size() == 1
			data.groups[0].attributes[1].retractBlocked == false
			data.groups[0].attributes[2].name == "CPQ_HT_POWER2"
			data.groups[0].attributes[2].domainValues.size() == 3
			data.groups[0].attributes[2].retractBlocked == false

			data.groups[1].name == "2"
			data.groups[1].description == "Visual entertainment"
			data.groups[1].id=="1-CPQ_HOME_THEATER@2"
			data.groups[1].attributes == null || data.groups[1].attributes.size() == 0

			data.groups[2].name =="CPQ_RECEIVER"
			data.groups[2].description=="Receiver"
			data.groups[2].id=="2-CPQ_RECEIVER"
			data.groups[2].subGroups.size() == 1
			data.groups[2].subGroups[0].name =="CPQ_RECEIVER"
			data.groups[2].subGroups[0].description=="Receiver"
			data.groups[2].subGroups[0].id=="2-CPQ_RECEIVER@_GEN"
			data.groups[2].subGroups[0].attributes == null || data.groups[2].subGroups[0].attributes.size() == 0
		}
		where:
		format << [XML, JSON]
	}

	@Test
	def "Change configuration and see updated value"() {

		when: "Anonymous user creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "Gets a new default configuration that is not complete"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) { status == SC_OK }
		when: "Afterwards updates the configuration with a characteristic value"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"


		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "Gets the updated configuration result and expects to see the changed value"
		with(responseAfterUpdate) {
			status == SC_OK
			data.groups[0].attributes[0].name == "CPQ_HT_SURROUND_MODE"
			data.groups[0].attributes[0].value == "STEREO"
			data.groups[0].complete == true
			data.groups[0].consistent == true
		}
		where:
		format << [JSON]
	}

	@Test
	def "Retract values for an attribute"() {

		when: "Anonymous user creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "Gets a new default configuration that is not complete"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.groups[0].complete == false
			data.groups[0].consistent == true
		}
		when: "Afterwards updates the configuration with a characteristic value"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"


		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "Gets the updated configuration result and expects to see the changed value"
		with(responseAfterUpdate) {
			status == SC_OK
			data.groups[0].attributes[0].name == "CPQ_HT_SURROUND_MODE"
			data.groups[0].attributes[0].value == "STEREO"
			data.groups[0].complete == true
			data.groups[0].consistent == true
		}
		when: "Afterwards updates the configuration and triggers retract for the attribute"
		def putRetractBody = responseAfterUpdate.data
		putRetractBody.groups[0].attributes[0].retractTriggered = true
		HttpResponseDecorator responseAfterRetract = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putRetractBody,
				contentType: format
				)


		then: "Gets the updated configuration result and expects to see the value gone"
		with(responseAfterRetract) {
			status == SC_OK
			data.groups[0].attributes[0].name == "CPQ_HT_SURROUND_MODE"
			data.groups[0].attributes[0].value == null
			data.groups[0].complete == false
			data.groups[0].consistent == true
		}
		where:
		format << [JSON]
	}

	@Test
	def "Change configuration and see new instances resulting from value change"() {

		when: "Anonymous user creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators/' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "Gets a new default configuration that contains 3 groups, excluding their subgroups"
		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.groups.size() == 3
		}
		when: "Afterwards updates the configuration with a characteristic value that causes additional instances"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"
		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				contentType: format
				)

		then: "Gets the updated configuration result and expects to see an extended list of UI groups"
		with(responseAfterUpdate) {
			status == SC_OK
			data.groups.size() == 4
		}
		where:
		format << [JSON]
	}

	@Test
	def "Get a configuration by the config id"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/YSAP_SIMPLE_POC/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "Afterwards get the created configuration by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() +  SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ configId,
				contentType: format
				)

		then: "gets the newly create configuration by config ID with all fields mapped"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId == configId
			data.consistent == true
			data.complete == false
			data.rootProduct == "YSAP_SIMPLE_POC"
			data.groups.size() == 1
			data.groups[0].id == "1-YSAP_SIMPLE_POC@_GEN"
			data.groups[0].name == "_GEN"
			data.groups[0].description == "[_GEN]"
			data.groups[0].configurable == true
			data.groups[0].complete == true
			data.groups[0].consistent == true
			data.groups[0].attributes.size() == 1
			data.groups[0].attributes[0].key == "1-YSAP_SIMPLE_POC@_GEN@YSAP_POC_SIMPLE_FLAG"
			data.groups[0].attributes[0].name == "YSAP_POC_SIMPLE_FLAG"
			data.groups[0].attributes[0].langDepName == "Simple Flag: Hide options"
			data.groups[0].attributes[0].type == "CHECK_BOX"
			data.groups[0].attributes[0].validationType == "NONE"
			data.groups[0].attributes[0].visible == true
			data.groups[0].attributes[0].required == false
			data.groups[0].attributes[0].typeLength == 0
			data.groups[0].attributes[0].numberScale == 0
			data.groups[0].attributes[0].intervalInDomain == false
			data.groups[0].attributes[0].maxlength == 1
			data.groups[0].attributes[0].domainValues.size() == 1
			data.groups[0].attributes[0].domainValues[0].key == "X"
			data.groups[0].attributes[0].domainValues[0].langDepName == "Hide"
			data.groups[0].attributes[0].domainValues[0].name == "X"
			data.groups[0].attributes[0].domainValues[0].readonly == false
			data.groups[0].attributes[0].domainValues[0].selected == true
		}

		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a multilevel configuration by the config id"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "Afterwards get the created configuration by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId ,
				contentType: format
				)

		then: "gets the newly create configuration by config ID with all fields mapped and attributes for all groups"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 3
			data.groups[0].name == "1"
			data.groups[0].description == "Powerful audio"
			data.groups[0].id=="1-CPQ_HOME_THEATER@1"
			data.groups[0].attributes.size() == 3
			data.groups[0].attributes[0].name == "CPQ_HT_SURROUND_MODE"
			data.groups[0].attributes[0].domainValues.size() == 2
			data.groups[0].attributes[0].longText == "Surround Mode to support - will influence speaker setup"
			data.groups[0].attributes[1].name == "CPQ_HT_SUBWOOFER"
			data.groups[0].attributes[1].domainValues.size() == 1
			data.groups[0].attributes[2].name == "CPQ_HT_POWER2"
			data.groups[0].attributes[2].domainValues.size() == 3

			data.groups[1].name == "2"
			data.groups[1].description == "Visual entertainment"
			data.groups[1].id=="1-CPQ_HOME_THEATER@2"
			data.groups[1].attributes.size() == 3
			data.groups[1].attributes[0].name == "CPQ_HT_INCLUDE_TV"
			data.groups[1].attributes[0].domainValues.size() == 1
			data.groups[1].attributes[1].name == "CPQ_HT_INCLUDE_BR"
			data.groups[1].attributes[0].domainValues.size() == 1
			data.groups[1].attributes[2].name == "CPQ_HT_VIDEO_SOURCES"
			data.groups[1].attributes[2].domainValues.size() == 6

			data.groups[2].name =="CPQ_RECEIVER"
			data.groups[2].description=="Receiver"
			data.groups[2].id=="2-CPQ_RECEIVER"
			data.groups[2].subGroups.size() == 1
			data.groups[2].subGroups[0].name =="CPQ_RECEIVER"
			data.groups[2].subGroups[0].description=="Receiver"
			data.groups[2].subGroups[0].id=="2-CPQ_RECEIVER@_GEN"
			data.groups[2].subGroups[0].attributes.size() == 3
			data.groups[2].subGroups[0].attributes[0].name == "CPQ_HT_RECV_MODEL2"
			data.groups[2].subGroups[0].attributes[0].domainValues.size() == 8
			data.groups[2].subGroups[0].attributes[1].name == "CPQ_HT_RECV_MODE"
			data.groups[2].subGroups[0].attributes[1].domainValues.size() == 2
			data.groups[2].subGroups[0].attributes[2].name == "CPQ_HT_POWER2"
			data.groups[2].subGroups[0].attributes[2].domainValues.size() == 3
		}


		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a multilevel configuration by the config id specifying a group"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "Afterwards get the created configuration by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId  ,
				contentType: format,
				query:['groupId': '1-CPQ_HOME_THEATER@2']
				)

		then: "gets the newly created configuration by config ID with all fields mapped but attributes only for the requested group"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 3
			data.groups[0].name == "1"
			data.groups[0].description == "Powerful audio"
			data.groups[0].id=="1-CPQ_HOME_THEATER@1"
			data.groups[0].attributes == null || data.groups[0].attributes.size() == 0

			data.groups[1].name == "2"
			data.groups[1].description == "Visual entertainment"
			data.groups[1].id=="1-CPQ_HOME_THEATER@2"
			data.groups[1].attributes.size() == 3
			data.groups[1].attributes[0].name == "CPQ_HT_INCLUDE_TV"
			data.groups[1].attributes[0].domainValues.size() == 1
			data.groups[1].attributes[1].name == "CPQ_HT_INCLUDE_BR"
			data.groups[1].attributes[0].domainValues.size() == 1
			data.groups[1].attributes[2].name == "CPQ_HT_VIDEO_SOURCES"
			data.groups[1].attributes[2].domainValues.size() == 6

			data.groups[2].name =="CPQ_RECEIVER"
			data.groups[2].description=="Receiver"
			data.groups[2].id=="2-CPQ_RECEIVER"
			data.groups[2].subGroups.size() == 1
			data.groups[2].subGroups[0].name =="CPQ_RECEIVER"
			data.groups[2].subGroups[0].description=="Receiver"
			data.groups[2].subGroups[0].id=="2-CPQ_RECEIVER@_GEN"
			data.groups[2].subGroups[0].attributes == null || data.groups[2].subGroups[0].attributes.size() == 0
		}


		where:
		format << [XML, JSON]
	}

	@Test
	def "Get a configuration with conflicts and check if conflict group contains attributes even if another group is requested"() {

		when: "creates a new configuration"
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CONF_HOME_THEATER_ML/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "Afterwards get the created configuration by the config ID"
		String configId = response.data.configId

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId  ,
				contentType: format,
				query:['groupId': '2-CONF_HT_VIDEO_SYSTEM@PROJECTOR']
				)

		then: "gets the newly created configuration by config ID with all fields mapped but attributes only for the requested group"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 5
			data.groups[0].description == "[_GEN]"
			data.groups[0].id=="1-CONF_HOME_THEATER_ML@_GEN"
			data.groups[0].attributes == null || data.groups[0].attributes.size() == 0

			data.groups[1].name == "CONF_HT_VIDEO_SYSTEM"
			data.groups[1].description == "Video System"
			data.groups[1].id=="2-CONF_HT_VIDEO_SYSTEM"
			data.groups[1].attributes == null || data.groups[1].attributes.size() == 0
			data.groups[1].subGroups.size() == 2
			data.groups[1].subGroups[0].name =="PROJECTOR"
			data.groups[1].subGroups[0].description=="Projector"
			data.groups[1].subGroups[0].id=="2-CONF_HT_VIDEO_SYSTEM@PROJECTOR"
			data.groups[1].subGroups[0].attributes.size() == 1
			data.groups[1].subGroups[0].attributes[0].name == "PROJECTOR_TYPE"
			data.groups[1].subGroups[1].name =="FLAT_PANEL_TV"
			data.groups[1].subGroups[1].description=="Flat-panel TV"
			data.groups[1].subGroups[1].id=="2-CONF_HT_VIDEO_SYSTEM@FLAT_PANEL_TV"
			data.groups[1].subGroups[1].attributes == null || data.groups[1].subGroups[1].attributes.size() == 0

			data.groups[2].name == "CONF_HT_AUDIO_SYSTEM"
			data.groups[2].description == "Audio System"
			data.groups[2].id=="3-CONF_HT_AUDIO_SYSTEM"
			data.groups[2].attributes == null || data.groups[2].attributes.size() == 0
			data.groups[2].subGroups.size() == 4

			data.groups[3].name == "CONF_HT_COMPONENTS_SYSTEM"
			data.groups[3].description == "Source Components"
			data.groups[3].id=="4-CONF_HT_COMPONENTS_SYSTEM"
			data.groups[3].attributes == null || data.groups[3].attributes.size() == 0
			data.groups[3].subGroups.size() == 1

			data.groups[4].name == "CONF_HT_EXTRAS"
			data.groups[4].description == "Extras"
			data.groups[4].id=="5-CONF_HT_EXTRAS"
			data.groups[4].attributes == null || data.groups[4].attributes.size() == 0
			data.groups[4].subGroups.size() == 1
		}

		when: "Afterwards update the configuration with a characteristic value"
		def putBody = response.data
		putBody.groups[1].subGroups[0].attributes[0].value = "PROJECTOR_LCD"


		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "Gets the updated configuration result and expects to see the changed value"
		with(responseAfterUpdate) {
			status == SC_OK
			data.groups[1].subGroups[0].attributes[0].name == "PROJECTOR_TYPE"
			data.groups[1].subGroups[0].attributes[0].value == "PROJECTOR_LCD"
			data.groups[1].subGroups[0].complete == true
			data.groups[1].subGroups[0].consistent == true
		}


		when: "Afterwards request attributes on group for source components"

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId  ,
				contentType: format,
				query:['groupId': '4-CONF_HT_COMPONENTS_SYSTEM@_GEN']
				)

		then: "check that attributes for requested group are available"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 5
			data.groups[0].id=="1-CONF_HOME_THEATER_ML@_GEN"
			data.groups[0].attributes == null || data.groups[0].attributes.size() == 0

			data.groups[1].id=="2-CONF_HT_VIDEO_SYSTEM"
			data.groups[1].attributes == null || data.groups[1].attributes.size() == 0

			data.groups[2].id=="3-CONF_HT_AUDIO_SYSTEM"
			data.groups[2].attributes == null || data.groups[2].attributes.size() == 0
			data.groups[2].subGroups.size() == 4

			data.groups[3].id=="4-CONF_HT_COMPONENTS_SYSTEM"
			data.groups[3].attributes == null || data.groups[3].attributes.size() == 0
			data.groups[3].subGroups.size() == 1
			data.groups[3].subGroups[0].attributes.size() == 3

			data.groups[4].id=="5-CONF_HT_EXTRAS"
			data.groups[4].attributes == null || data.groups[4].attributes.size() == 0
		}


		when: "Afterwards set value to provoke a conflict"
		putBody = response.data
		putBody.groups[3].subGroups[0].attributes[2].value = "GAMING_CONSOLE_YES"

		HttpResponseDecorator responseAfterUpdate2 = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + response.data.configId,
				body : putBody,
				contentType: format
				)

		then: "Gets the updated configuration result and expects to see the changed value"
		with(responseAfterUpdate2) {
			status == SC_OK
			data.groups[4].subGroups[0].attributes[2].name == "GAMING_CONSOLE"
			data.groups[4].subGroups[0].attributes[2].value == "GAMING_CONSOLE_YES"
			data.groups[4].subGroups[0].complete == true
			data.groups[4].subGroups[0].consistent == false
		}

		when: "Finally request the configuration for a group"

		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId  ,
				contentType: format,
				query:['groupId': '2-CONF_HT_VIDEO_SYSTEM@PROJECTOR']
				)

		then: "check that also the attributes for the conflict group are returned"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.groups.size() == 6
			data.groups[0].name == "CONFLICT_HEADER"
			data.groups[0].id=="CONFLICT_HEADER"
			data.groups[0].attributes == null || data.groups[0].attributes.size() == 0
			data.groups[0].subGroups.size() == 1
			data.groups[0].subGroups[0].name =="Gaming Console"
			data.groups[0].subGroups[0].description=="Gaming console cannot be selected with LCD projector"
			data.groups[0].subGroups[0].id=="CONFLICT2108260335"
			data.groups[0].subGroups[0].attributes.size() == 2
			data.groups[0].subGroups[0].attributes[0].name == "GAMING_CONSOLE"
			data.groups[0].subGroups[0].attributes[1].name == "PROJECTOR_TYPE"

			data.groups[1].description == "[_GEN]"
			data.groups[1].id=="1-CONF_HOME_THEATER_ML@_GEN"
			data.groups[1].attributes == null || data.groups[1].attributes.size() == 0

			data.groups[2].name == "CONF_HT_VIDEO_SYSTEM"
			data.groups[2].description == "Video System"
			data.groups[2].id=="2-CONF_HT_VIDEO_SYSTEM"
			data.groups[2].attributes == null || data.groups[2].attributes.size() == 0
			data.groups[2].subGroups.size() == 2
			data.groups[2].subGroups[0].name =="PROJECTOR"
			data.groups[2].subGroups[0].description=="Projector"
			data.groups[2].subGroups[0].id=="2-CONF_HT_VIDEO_SYSTEM@PROJECTOR"
			data.groups[2].subGroups[0].attributes.size() == 2
			data.groups[2].subGroups[0].attributes[0].name == "PROJECTOR_TYPE"
			data.groups[2].subGroups[0].attributes[1].name == "LIGHT_SOURCE"
			data.groups[2].subGroups[1].name =="PROJECTION_SCREEN"
			data.groups[2].subGroups[1].description=="Projection Screen"
			data.groups[2].subGroups[1].id=="2-CONF_HT_VIDEO_SYSTEM@PROJECTION_SCREEN"
			data.groups[2].subGroups[1].attributes == null || data.groups[2].subGroups[1].attributes.size() == 0

			data.groups[3].name == "CONF_HT_AUDIO_SYSTEM"
			data.groups[3].description == "Audio System"
			data.groups[3].id=="3-CONF_HT_AUDIO_SYSTEM"
			data.groups[3].attributes == null || data.groups[3].attributes.size() == 0
			data.groups[3].subGroups.size() == 4

			data.groups[4].name == "CONF_HT_COMPONENTS_SYSTEM"
			data.groups[4].description == "Source Components"
			data.groups[4].id=="4-CONF_HT_COMPONENTS_SYSTEM"
			data.groups[4].attributes == null || data.groups[4].attributes.size() == 0
			data.groups[4].subGroups.size() == 1

			data.groups[5].name == "CONF_HT_EXTRAS"
			data.groups[5].description == "Extras"
			data.groups[5].id=="5-CONF_HT_EXTRAS"
			data.groups[5].attributes == null || data.groups[5].attributes.size() == 0
			data.groups[5].subGroups.size() == 1
		}


		where:
		format << [JSON]
	}
	@Test
	def "Check for newConfiguration field (flag toggle.sapproductconfigservices.getDefaultConfigurationEnhancements.enabled is activated)"() {

		//cannot run for default lifecycle (SSC)
		Assume.assumeFalse(isExtensionInSetup("sapproductconfigservicesssc"))

		when: "creates a new configuration"
		TestSetupStandalone.ensureGetDefaultConfigurationEnhancementsActive()
		authorizeCustomer(CUSTOMER)
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CONF_CAMERA_SL/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with newConfiguration==true"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.newConfiguration == true
		}

		when: "creates another configuration"
		response = restClient.get(
				path: getBasePathWithSite() + '/products/CONF_CAMERA_SL/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a default configuration with newConfiguration==false"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.newConfiguration == false
		}

		where:
		format << [JSON]
	}

	@Test
	def "Check for newConfiguration field (flag toggle.sapproductconfigservices.getDefaultConfigurationEnhancements.enabled is not activated)"() {

		//cannot run for default lifecycle (SSC)
		Assume.assumeFalse(isExtensionInSetup("sapproductconfigservicesssc"))

		when: "creates a new configuration"
		TestSetupStandalone.ensureGetDefaultConfigurationEnhancementsInactive()
		authorizeCustomer(CUSTOMER)
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CONF_CAMERA_SL/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with newConfiguration==false because flag is not activated"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.newConfiguration == null
		}

		when: "creates another configuration"
		response = restClient.get(
				path: getBasePathWithSite() + '/products/CONF_CAMERA_SL/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a default configuration with newConfiguration==false"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
			data.newConfiguration == null
		}

		where:
		format << [JSON]
	}

	@Test
	def "Get new configuration if forceReset is set to true (flag toggle.sapproductconfigservices.getDefaultConfigurationEnhancements.enabled is activated)"() {

		//cannot run for default lifecycle (SSC)
		Assume.assumeFalse(isExtensionInSetup("sapproductconfigservicesssc"))

		when: "creates a new configuration without forceReset flag set"
		TestSetupStandalone.ensureGetDefaultConfigurationEnhancementsActive()
		authorizeCustomer(CUSTOMER)
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		LOG.info("RESPONSE: " + response.data.toString())

		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		def oldConfigId = response.data.configId;

		when: "creates another configuration without forceReset flag set"
		response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a default configuration with the same configId as in the first call"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId == oldConfigId
		}

		when: "creates another configuration with forceReset flag set"
		response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				query : ['forceReset' : true],
				contentType: format
				)

		then: "gets a default configuration with a new configId"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId != oldConfigId
		}

		where:
		format << [JSON]
	}

	@Test
	def "Get same configuration if forceReset is set to true but flag toggle.sapproductconfigservices.getDefaultConfigurationEnhancements.enabled is not activated"() {

		//cannot run for default lifecycle (SSC)
		Assume.assumeFalse(isExtensionInSetup("sapproductconfigservicesssc"))

		when: "creates a new configuration without forceReset flag set"
		authorizeCustomer(CUSTOMER)
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		LOG.info("RESPONSE: " + response.data.toString())

		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		def oldConfigId2 = response.data.configId;

		TestSetupStandalone.ensureGetDefaultConfigurationEnhancementsInactive()
		when: "creates another configuration with forceReset flag set but flag is not activated"
		response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				query : ['forceReset' : true],
				contentType: format
				)

		then: "gets a default configuration with the same configId as before"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId == oldConfigId2
		}

		where:
		format << [JSON]
	}

	@Test
	def "Check that user 'B' cannot force a reset of configuration of user 'A'"() {

		//cannot run for default lifecycle (SSC)
		Assume.assumeFalse(isExtensionInSetup("sapproductconfigservicesssc"))

		when: "User 'A' creates a new configuration without forceReset flag set"
		TestSetupStandalone.ensureGetDefaultConfigurationEnhancementsActive()
		authorizeCustomer(CUSTOMER)
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets a new default configuration with"
		LOG.info("RESPONSE: " + response.data.toString())

		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		def configIdUserA = response.data.configId;

		authorizeCustomer(CUSTOMER2)
		when: "User 'B' creates a configuration with forceReset flag set"
		response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				query : ['forceReset' : true],
				contentType: format
				)

		then: "gets a default configuration with a new configId"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId != configIdUserA
		}

		when: "User 'A' creates another configuration without forceReset flag set"
		authorizeCustomer(CUSTOMER)
		response = restClient.get(
				path: getBasePathWithSite() + '/products/CPQ_HOME_THEATER/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format
				)

		then: "gets the same default configuration as before"
		LOG.info("RESPONSE: " + response.data.toString())
		with(response) {
			status == SC_OK
			data.configId == configIdUserA
		}

		where:
		format << [JSON]
	}
}
