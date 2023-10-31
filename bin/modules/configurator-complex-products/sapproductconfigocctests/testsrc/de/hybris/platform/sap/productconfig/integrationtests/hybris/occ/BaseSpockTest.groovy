package de.hybris.platform.sap.productconfig.integrationtests.hybris.occ



import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.bootstrap.config.ConfigUtil
import de.hybris.bootstrap.config.ExtensionInfo
import de.hybris.bootstrap.config.PlatformConfig
import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoWsDTO
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO
import de.hybris.platform.sap.productconfig.integrationtests.util.SSLIssuesIgnoringHttpClientFactory
import de.hybris.platform.sap.productconfig.integrationtests.util.TestConfigFactory
import de.hybris.platform.sap.productconfig.occ.ProductConfigOrderEntryWsDTO
import de.hybris.platform.util.Utilities

import org.apache.http.client.HttpClient
import org.junit.Ignore

import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import spock.lang.Specification

@Ignore
abstract class BaseSpockTest extends Specification {
	protected static final String USERNAME = "cpq02@sap.com"
	protected static final String USERNAME2 = "cpq03@sap.com"
	protected static final String PASSWORD = "1234"
	protected static final CONFIGURATOR_TYPE_OCC = 'ccpconfigurator'
	protected static final SLASH_CONFIGURATOR_TYPE_OCC = '/' + CONFIGURATOR_TYPE_OCC
	protected static final SLASH_CONFIGURATOR_TYPE_OCC_SLASH = SLASH_CONFIGURATOR_TYPE_OCC +'/'
	protected static final String FIELD_SET_LEVEL_BASIC = "BASIC"
	protected static final String FIELD_SET_LEVEL_FULL = "FULL"
	protected static final String SLASH_CONFIG_OVERVIEW = "/configurationOverview";
	protected RESTClient restClient
	protected static ConfigObject config = TestConfigFactory.createConfig("v2", "/sapproductconfigocctests/groovytests-property-file.groovy");

	def setup() {
		restClient = createRestClient()
	}

	def cleanup() {
		restClient.shutdown()
	}

	protected getConfiguration(customerId, cartId, entryNumber, format) {
		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/'+ customerId + '/carts/'+cartId+'/entries/' + entryNumber+SLASH_CONFIGURATOR_TYPE_OCC,
				contentType : format,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: URLENC
				)

		LOG.info("RESPONSE: "+response.data.toString())
		with(response) {
			status == SC_OK
			data.rootProduct == PRODUCT_KEY
			data.configId.isEmpty() == false
		}
	}

	protected RESTClient createRestClient(uri = config.DEFAULT_HTTPS_URI) {
		def restClient = new RESTClient(uri);

		// makes sure we can access the services even without a valid SSL certificate
		HttpClient httpClient = SSLIssuesIgnoringHttpClientFactory.createHttpClient();
		restClient.setClient(httpClient);

		// makes sure an exception is not thrown and that the response is parsed
		restClient.handler.failure = restClient.handler.success

		// used to record the requests in jmeter
		//client.setProxy('localhost', 8080, null)

		return restClient;
	}

	protected static final String getBasePathWithSite() {
		return config.BASE_PATH_WITH_SITE
	}

	protected static final String getClientId() {
		return config.CLIENT_ID
	}

	protected static final String getClientSecret() {
		return config.CLIENT_SECRET
	}

	protected static final String getOAuth2TokenUri() {
		return config.OAUTH2_TOKEN_URI
	}

	protected static final String getOAuth2TokenPath() {
		return config.OAUTH2_TOKEN_ENDPOINT_PATH
	}

	protected void addAuthorization(token) {
		restClient.getHeaders().put('Authorization', ' Bearer ' + token.access_token)
	}

	protected void authorizeCustomer(customer = null) {
		def username = USERNAME
		def password = PASSWORD

		if (customer) {
			username = customer.id
			password = customer.password
		}

		def token = getOAuth2TokenUsingPassword(getClientId(), getClientSecret(), username, password)
		addAuthorization( token)
	}

	protected getOAuth2TokenUsingClientCredentials(clientId, clientSecret) {
		HttpResponseDecorator response = restClient.post(
				uri: getOAuth2TokenUri(),
				path: getOAuth2TokenPath(),
				body: [
					'grant_type'   : 'client_credentials',
					'client_id'    : clientId,
					'client_secret': clientSecret
				],
				contentType: JSON,
				requestContentType: URLENC)

		with(response) {
			if (isNotEmpty(data) && isNotEmpty(data.error)) println(data)
			assert status == SC_OK
			assert data.token_type == 'bearer'
			assert data.access_token
			assert data.expires_in
		}

		return response.data
	}

	protected getOAuth2TokenUsingPassword(clientId, clientSecret, username, password, boolean doAssert = true) {
		HttpResponseDecorator response = restClient.post(
				uri: getOAuth2TokenUri(),
				path: getOAuth2TokenPath(),
				body: [
					'grant_type'   : 'password',
					'client_id'    : clientId,
					'client_secret': clientSecret,
					'username'     : username,
					'password'     : password
				],
				contentType: JSON,
				requestContentType: URLENC)

		if (doAssert) {
			with(response) {
				if (isNotEmpty(data) && isNotEmpty(data.error)) println(data)
				assert status == SC_OK
				assert data.token_type == 'bearer'
				assert data.access_token
				assert data.expires_in
				assert data.refresh_token
			}
		}

		return response.data
	}
	
	/**
	 * Checks if a node doesn't exist or is empty. Works for JSON and XML formats.
	 *
	 * @param the node to check
	 * @return {@code true} if the node is not empty, {@code false} otherwise
	 */
	protected isEmpty(node) {
		(node == null) || (node.size() == 0)
	}

	/**
	 * Checks if a node exists and is not empty. Works for JSON and XML formats.
	 *
	 * @param the node to check
	 * @return {@code true} if the node is not empty, {@code false} otherwise
	 */
	protected isNotEmpty(node) {
		(node != null) && (node.size() > 0)
	}

	/**
	 * Same as {@link spock.lang.Specification#with(Object, groovy.lang.Closure)}, the only difference is that it returns the target object.
	 *
	 * @param target an implicit target for conditions and/or interactions
	 * @param closure a code block containing top-level conditions and/or interactions
	 * @return the target object
	 */
	def returningWith(target, closure) {
		with(target, closure)
		return target
	}

	/**
	 * Creates an anonymous empty cart
	 * @return created cart
	 */
	protected createEmptyCart(customerId="anonymous", format) {
		def cart = returningWith(restClient.post(
				path: basePathWithSite + '/users/' + customerId + '/carts',
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: URLENC), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data
		return cart
	}

	/**
	 * Add a product to cart
	 * @param client REST client to use
	 * @param customerId the customer id
	 * @param id use GUID for anonymous carts, code otherwise
	 * @param productCode Hybris product code
	 * @return cart modification
	 */
	protected addProductToCart(customerId="anonymous", cartId, productCode, format) {
		def modification = returningWith(restClient.post(
				//Note that DefaultCartLoaderStrategy calls commerceCartService.getCartForGuidAndSite when searching a cart with ID.
				//So for anonymous carts we need to call with the guid, while for user carts we would need the cart code
				path: getBasePathWithSite() + '/users/'+ customerId + '/carts/'+ cartId +'/entries',
				contentType: format,
				body: ['product': ['code': productCode]],
				requestContentType: JSON), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data

		return modification
	}

	protected String orderToJsonMapping(String productCode, String configId) throws IOException, JsonGenerationException, JsonMappingException{

		final ObjectMapper mapper = new ObjectMapper()
		def  orderEntry = new ProductConfigOrderEntryWsDTO()
		orderEntry.setConfigId(configId)
		final ProductWsDTO product = new ProductWsDTO()
		product.setCode(productCode)
		product.setConfigurable(true)
		orderEntry.setProduct(product)
		orderEntry.setQuantity(1L)
		orderEntry.setEntryNumber(0)
		def configInfo = new ConfigurationInfoWsDTO()
		configInfo.setConfiguratorType('CPQCONFIGURATOR')
		List <ConfigurationInfoWsDTO> infos = new ArrayList<>()
		infos.add(configInfo)
		orderEntry.setConfigurationInfos(infos)
		final String json = mapper.writeValueAsString(orderEntry)
		LOG.info(json.toString())
		return json
	}

	protected final boolean isExtensionInSetup(final String extension) {
		final PlatformConfig platformConfig = ConfigUtil.getPlatformConfig(Utilities.class);
		final ExtensionInfo extensionInfo = platformConfig.getExtensionInfo(extension);
		return (extensionInfo != null);
	}

	/**
	 * Assumes that the cart entry contains the CPQ_HOME_THEATER with default config
	 * and will update the configuration, so that it is complete and consistent.
	 * @param customerId
	 * @param cartId
	 * @param entryNumber
	 * @return
	 */
	protected updateEntryWithCPQHomeTheaterToValidConfig(customerId, cartId,entryNumber) {

		HttpResponseDecorator response = restClient.get(
				path: getBasePathWithSite() + '/users/'+ customerId + '/carts/' + cartId + '/entries/' + entryNumber + SLASH_CONFIGURATOR_TYPE_OCC,
				contentType : JSON,
				query : ['fields' : FIELD_SET_LEVEL_BASIC],
				requestContentType: JSON
				)
		String configId = response.data.configId;
		String productCode = response.data.rootProduct;

		// set SOURROUND MODE
		def putBody = response.data
		putBody.groups[0].attributes[0].value = "STEREO"
		HttpResponseDecorator responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId,
				body : putBody,
				contentType: JSON
				)

		// set FRONT SPEAKERS to make config complete
		response = restClient.get(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId  ,
				contentType: JSON,
				query:['groupId': '3-CPQ_FRONT_SPEAKERS@_GEN'],
				requestContentType: JSON
				)
		putBody = response.data;
		putBody.groups[3].subGroups[0].attributes[0].value = "YM_NS_F160"
		putBody.groups[3].subGroups[0].attributes[1].value = "BLK"
		responseAfterUpdate = restClient.patch(
				path: getBasePathWithSite() + SLASH_CONFIGURATOR_TYPE_OCC_SLASH + configId,
				body : putBody,
				contentType: JSON,
				)
		with(responseAfterUpdate) {
			data.complete == true
			data.consistent == true
		}

		// update cart entry
		def patchBody = orderToJsonMapping(productCode, configId)
		HttpResponseDecorator responseAfterUpdateCart = restClient.put(
				path: basePathWithSite + '/users/' + customerId + '/carts/' + cartId + '/entries/'+ entryNumber + SLASH_CONFIGURATOR_TYPE_OCC,
				body: patchBody,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: JSON,
				requestContentType: JSON
				)
		if (isNotEmpty(responseAfterUpdateCart.data)) println(responseAfterUpdateCart.data)
		with(responseAfterUpdateCart) {
			status == SC_OK
		}
		return configId
	}

	protected void validateCPQHomeTheaterConfig(overviewData, expectedConfigId) {
		with(overviewData) {
			id == expectedConfigId
			groups.size() == 2
			groups[0].groupDescription == "Powerful audio"
			groups[0].characteristicValues.size() == 1
			groups[0].characteristicValues[0].characteristic == "Surround Mode"
			groups[0].characteristicValues[0].value == "Stereo"
			groups[1].groupDescription == "Front Speakers"
			groups[1].characteristicValues.size() == 2
			groups[1].characteristicValues[0].characteristic == "Speaker Model"
			groups[1].characteristicValues[0].value == "Yamaha NS-F160"
			groups[1].characteristicValues[1].characteristic == "Speaker Color"
			groups[1].characteristicValues[1].value == "Black Gloss"
		}
	}

	protected createDefaultConfiguration(productCode, RESTClient client, format, basePathWithSite=getBasePathWithSite()) {
		def modification = returningWith(client.get(
				path: getBasePathWithSite() + '/products/' + productCode + '/configurators'+SLASH_CONFIGURATOR_TYPE_OCC,
				contentType: format,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				requestContentType: URLENC), {
					if (isNotEmpty(data) ) println(data)
					status == SC_OK
				}).data

		return modification
	}
}