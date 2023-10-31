/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.sap.productconfig.integrationtests.hybris.occ

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.testframework.HybrisSpockRunner

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory


@ManualTest
@RunWith(HybrisSpockRunner.class)
class ProductConfigExpertModeTest extends BaseSpockTest {
	static final CUSTOMER = ["id": USERNAME, "password": PASSWORD]

	def org.slf4j.Logger LOG = LoggerFactory.getLogger(ProductConfigWsIntegrationTest.class)

	@Test
	def "Request expert data for standard user for create"() {

		when: "anonymous user creates a new configuration, requesting expert mode additions"
		def response = createConfiguration(format,'YSAP_SIMPLE_POC',true);

		then: "gets a new default configuration without kb info because user is not authorized"
		validateNoKbInfo(response)

		where:
		format << [XML, JSON]
	}

	@Test
	def "Request expert data for expert user for create"() {

		when: "expert logs in and creates a new configuration, requesting expert mode additions"
		authorizeCustomer(CUSTOMER)
		def response = createConfiguration(format,'YSAP_SIMPLE_POC',true);

		then: "gets a new default configuration with kb info because user is authorized"
		validateKbInfoExists(response, 'YSAP_SIMPLE_POC' )

		where:
		format << [XML, JSON]
	}

	@Test
	def "Do not request expert data for expert user for create"() {

		when: "expert logs in and creates a new configuration, not requesting expert mode additions"
		authorizeCustomer(CUSTOMER)
		def response = createConfiguration(format,'YSAP_SIMPLE_POC',false);

		then: "gets a new default configuration without kb info because data was not requested"
		validateNoKbInfo(response)

		where:
		format << [XML, JSON]
	}


	@Test
	def "Request expert data for standard user for update"() {

		when: "anonymous user creates a new configuration"
		def response = createConfiguration(format,'CPQ_HOME_THEATER',true);

		then: "gets a new default configuration"
		with(response) { status == SC_OK }

		when: "afterwards updates the configuration with a characteristic value, requesting expert mode data"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"
		def responseAfterUpdate = patchConfiguration(format, putBody, response.data.configId, true)

		then: "gets the updated configuration result without KB info because user is not authorized"
		validateNoKbInfo(responseAfterUpdate)

		where:
		format << [JSON]
	}

	@Test
	def "Request expert data for expert user for update"() {

		when: "expert user logs in and creates a new configuration"
		authorizeCustomer(CUSTOMER)
		def response = createConfiguration(format,'CPQ_HOME_THEATER',true);

		then: "gets a new default configuration"
		with(response) { status == SC_OK }

		when: "afterwards updates the configuration with a characteristic value, requesting expert mode data"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"
		def responseAfterUpdate = patchConfiguration(format, putBody, response.data.configId, true)

		then: "gets the updated configuration result with KB info because user is authorized"
		validateKbInfoExists(responseAfterUpdate, 'CPQ_HOME_THEATER' )

		where:
		format << [JSON]
	}

	@Test
	def "Do not request expert data for expert user for update"() {

		when: "expert user logs in and creates a new configuration"
		authorizeCustomer(CUSTOMER)
		def response = createConfiguration(format,'CPQ_HOME_THEATER',false);

		then: "gets a new default configuration"
		with(response) { status == SC_OK }

		when: "afterwards updates the configuration with a characteristic value, requesting expert mode data"
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"
		def responseAfterUpdate = patchConfiguration(format, putBody, response.data.configId, false)

		then: "gets the updated configuration result without KB info because it was not requested"
		validateNoKbInfo(responseAfterUpdate)

		where:
		format << [JSON]
	}

	@Test
	def "Request expert data for standard user for read"() {

		when: "anonymous user creates a new configuration"
		def response = createConfiguration(format,'YSAP_SIMPLE_POC',true);

		then: "gets a new default configuration with"
		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "afterwards reads the created configuration by the config ID, requesting expert mode additions"
		response = readConfiguration(format, response.data.configId, true)

		then: "gets the configuration without kb info because user is not authorized"
		validateNoKbInfo(response);

		where:
		format << [XML, JSON]
	}

	@Test
	def "Request expert data for standard user for configure cart entry"() {
		when: "anonymous creates a new cart and adds aconfigurable product"
		def cart = createEmptyCart(format)
		addProductToCart(cart.guid, 'YSAP_SIMPLE_POC', format)

		and: "afterwards configures the cart entry, requesting expert mode additions"
		def response = configureCartEntry(format, "anonymous", cart.guid, 0, true)

		then: "gets the configuration without kb info because user is not authorized"
		validateNoKbInfo(response);

		where:
		format << [XML, JSON]
	}

	@Test
	def "Request expert data for expert user for read"() {
		when: "expert user logs in and creates a new configuration"
		authorizeCustomer(CUSTOMER)
		def response = createConfiguration(format,'YSAP_SIMPLE_POC',true);

		then: "gets a new default configuration with"

		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "afterwards reads the created configuration by the config ID, requesting expert mode additions"
		response = readConfiguration(format, response.data.configId, true)

		then: "gets the configuration with kb info because user is authorized"
		validateKbInfoExists(response, 'YSAP_SIMPLE_POC' )

		where:
		format << [XML, JSON]
	}

	@Test
	def "Do not request expert data for expert user for read"() {
		when: "expert user logs in and creates a new configuration"
		authorizeCustomer(CUSTOMER)
		def response = createConfiguration(format,'YSAP_SIMPLE_POC',false);

		then: "gets a new default configuration with"

		with(response) {
			status == SC_OK
			data.configId.isEmpty() == false
		}

		when: "afterwards reads the created configuration by the config ID, not requesting expert mode additions"
		response = readConfiguration(format, response.data.configId, false)

		then: "gets the configuration without kb info because data was not requested"
		validateNoKbInfo(response);

		where:
		format << [XML, JSON]
	}

	@Test
	def "Do request expert data for expert user for configure cart entry"() {
		when: "expert user logs in and creates a new cart and adds aconfigurable product"
		authorizeCustomer(CUSTOMER)
		def cart = createEmptyCart(CUSTOMER.id, format)
		addProductToCart(CUSTOMER.id, cart.code, 'YSAP_SIMPLE_POC', format)

		and: "afterwards configures the cart entry, requesting expert mode additions"
		def response = configureCartEntry(format, CUSTOMER.id, cart.code, 0, true)

		then: "gets the configuration with kb info because user is authorized"
		validateKbInfoExists(response, 'YSAP_SIMPLE_POC' )

		where:
		format << [XML, JSON]
	}

	@Test
	def "Do not request expert data for expert user for configure cart entry"() {
		when: "expert user logs in and creates a new cart and adds aconfigurable product"
		authorizeCustomer(CUSTOMER)
		def cart = createEmptyCart(CUSTOMER.id, format)
		addProductToCart(CUSTOMER.id, cart.code, 'YSAP_SIMPLE_POC', format)

		and: "afterwards configures the cart entry, not requesting expert mode additions"
		def response = configureCartEntry(format, CUSTOMER.id, cart.code, 0, false)

		then: "gets the configuration without kb info because data was not requested"
		validateNoKbInfo(response);

		where:
		format << [XML, JSON]
	}

	protected createConfiguration(format, productCode, expMode) {
		return restClient.get(
				path: getBasePathWithSite() + '/products/'+productCode+'/configurators' + SLASH_CONFIGURATOR_TYPE_OCC,
				query : ['expMode' : expMode],
				contentType: format
				)
	}

	protected patchConfiguration(format, putBody, configId, expMode) {
		return restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId,
				body : putBody,
				query : ['expMode' : expMode],
				contentType: format
				)
	}

	protected readConfiguration(format, configId, expMode) {
		return restClient.get(
				path: getBasePathWithSite() +  SLASH_CONFIGURATOR_TYPE_OCC_SLASH+ configId,
				query : ['expMode' : expMode],
				contentType: format
				)
	}

	protected configureCartEntry(format, customerId, cartId, entryNumber, expMode) {
		return restClient.get(
				path: getBasePathWithSite() + '/users/'+ customerId + '/carts/'+ cartId +'/entries/' + entryNumber+ SLASH_CONFIGURATOR_TYPE_OCC,
				query : ['expMode' : expMode],
				contentType: format,
				requestContentType: URLENC
				)

	}

	protected void validateNoKbInfo(response) {
		with(response) {
			!isNotEmpty(data.kbKey)
			status == SC_OK
			data.configId.isEmpty() == false
		}
	}

	protected void validateKbInfoExists(response, kbName) {
		with(response) {
			status == SC_OK
			data.kbKey.isEmpty() == false
			data.kbKey.kbName == kbName
			//for mock we always use 3800 as version and build number 12
			data.kbKey.kbVersion == '3800'
			data.kbKey.kbBuildNumber == '12'
			data.configId.isEmpty() == false
		}
	}
}
