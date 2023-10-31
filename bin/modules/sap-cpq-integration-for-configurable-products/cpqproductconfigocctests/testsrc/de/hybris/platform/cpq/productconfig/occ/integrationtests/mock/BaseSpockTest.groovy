package de.hybris.platform.cpq.productconfig.occ.integrationtests.mock

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.apache.http.HttpStatus.SC_CREATED
import static org.apache.http.HttpStatus.SC_OK

import de.hybris.platform.commercewebservicescommons.dto.order.ConfigurationInfoWsDTO
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO
import de.hybris.platform.cpq.productconfig.occ.ProductConfigOrderEntryWsDTO
import de.hybris.platform.cpq.productconfig.occ.integrationtests.util.SSLIssuesIgnoringHttpClientFactory
import de.hybris.platform.cpq.productconfig.occ.integrationtests.util.TestConfigFactory
import de.hybris.platform.util.Config

import org.apache.http.client.HttpClient
import org.junit.Ignore
import org.slf4j.LoggerFactory

import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import spock.lang.Specification

@Ignore
abstract class BaseSpockTest extends Specification {

	org.slf4j.Logger LOG = LoggerFactory.getLogger(BaseSpockTest.class)


	protected static final String P_CODE_CONF_LAPTOP = 'CONF_LAPTOP'
	protected static final String P_CODE_CONF_LAPTOP_COMPLETE = 'CONF_LAPTOP_COMPLETE'
	protected static final String P_CODE_CONF_CAMERA_BUNDLE = 'CONF_CAMERA_BUNDLE'
	protected static final String CONFIGURATOR_TYPE_OCC = 'cpqconfigurator'
	protected static final String CONFIGURATOR_TYPE = "CLOUDCPQCONFIGURATOR"
	protected static final String SLASH_CONFIGURATOR_TYPE_OCC = '/' + CONFIGURATOR_TYPE_OCC
	protected static final String SLASH_CONFIGURATOR_TYPE_OCC_SLASH = SLASH_CONFIGURATOR_TYPE_OCC +'/'
	protected static final String FIELD_SET_LEVEL_BASIC = "BASIC"
	protected static final String FIELD_SET_LEVEL_FULL = "FULL"
	protected static final String CUSTOMER_USERNAME = "cpq03"
	protected static final String CUSTOMER_PASSWORD_STRONG = "welcome"
	protected static final String B2B_USERNAME = 'cpq03'
	protected static final String B2B_PASSWORD = 'welcome'
	protected static final def B2B_CUSTOMER = [
		'id'      : B2B_USERNAME,
		'password': B2B_PASSWORD
	]
	protected static final ConfigObject config = TestConfigFactory.createConfig("v2", "/cpqproductconfigocctests/groovytests-property-file.groovy");
	protected RESTClient restClient
	def setup() {
		restClient = createRestClient()
	}

	def cleanup() {
		restClient.shutdown()
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
	 * Checks if a node doesn't exist or is empty. Works for JSON and XML formats.
	 *
	 * @param the node to check
	 * @return {@code true} if the node is not empty, {@code false} otherwise
	 */
	protected isEmpty(node) {
		(node == null) || (node.size() == 0)
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
	 * Creates an empty cart
	 * @param client REST client to use
	 * @return created cart
	 */
	protected  createEmptyCart(format, user="anonymous") {
		def cart = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/'+user+'/carts',
				contentType: format,
				requestContentType: URLENC), {
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_CREATED
				}).data
		return cart
	}

	/**
	 * This method authorizes customer. If null (default) is provided as customer, it will authorize default customer.
	 * @param customer customer to be authorized, defaults to null
	 * @param client REST client to use
	 */
	protected void authorizeCustomer(customer = null) {
		def username = CUSTOMER_USERNAME
		def password = CUSTOMER_PASSWORD_STRONG

		if (customer) {
			username = customer.id
			password = customer.password
		}

		def token = getOAuth2TokenUsingPassword(config.CLIENT_ID, config.CLIENT_SECRET, username, password)
		addAuthorization(token)
	}

	protected void addAuthorization(token) {
		restClient.getHeaders().put('Authorization', ' Bearer ' + token.access_token)
	}

	protected getOAuth2TokenUsingClientCredentials(clientId, clientSecret) {
		HttpResponseDecorator response = restClient.post(
				uri: config.OAUTH2_TOKEN_URI,
				path: config.OAUTH2_TOKEN_ENDPOINT_PATH,
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
				uri: config.OAUTH2_TOKEN_URI,
				path: config.OAUTH2_TOKEN_ENDPOINT_PATH,
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

	protected String orderToJsonMapping(String configId, String productCode=null) throws IOException, JsonGenerationException, JsonMappingException{

		final ObjectMapper mapper = new ObjectMapper()
		def  orderEntry = new ProductConfigOrderEntryWsDTO()
		orderEntry.setConfigId(configId)
		if(null != productCode) {
			final ProductWsDTO product = new ProductWsDTO()
			product.setCode(productCode)
			product.setConfigurable(true)
			orderEntry.setProduct(product)
		}
		orderEntry.setQuantity(1L)
		orderEntry.setEntryNumber(0)
		def configInfo = new ConfigurationInfoWsDTO()
		configInfo.setConfiguratorType(CONFIGURATOR_TYPE)
		List <ConfigurationInfoWsDTO> infos = new ArrayList<>()
		infos.add(configInfo)
		orderEntry.setConfigurationInfos(infos)
		final String json = mapper.writeValueAsString(orderEntry)
		LOG.info(json.toString())
		return json
	}

	protected addConfigurationToCart(cart, String productCode, String configId, format, user="anonymous") {
		String cartcode = user=="anonymous"? cart.guid : cart.code
		def response = returningWith(restClient.post(
				path: getBasePathWithSite() + '/users/'+B2B_USERNAME+'/carts/' + cartcode+ '/entries' + SLASH_CONFIGURATOR_TYPE_OCC,
				body: orderToJsonMapping(configId, productCode),
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: JSON
				),{
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_CREATED
				}).data
		return response
	}

	protected updateCartEntryConfiguration(cart, entryNumber, String configId, format, user="anonymous") {
		String cartcode = user=="anonymous"? cart.guid : cart.code
		def response = returningWith(restClient.put(
				path: getBasePathWithSite() + '/users/'+B2B_USERNAME+'/carts/' + cartcode+ '/entries/' +entryNumber+ SLASH_CONFIGURATOR_TYPE_OCC,
				body: orderToJsonMapping(configId),
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format,
				requestContentType: JSON
				),{
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data
		return response
	}


	protected getCartEntryConfigurationId(cart, entryNumber, format, user="anonymous") {
		String cartcode = user=="anonymous"? cart.guid : cart.code
		def response = returningWith(restClient.get(
				path: getBasePathWithSite() + '/users/'+B2B_USERNAME+'/carts/' + cartcode+ '/entries/' +entryNumber+ SLASH_CONFIGURATOR_TYPE_OCC,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format
				),{
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data
		return response
	}


	protected getCartEntry(cart, entryNumber, format, user="anonymous") {
		String cartcode = user=="anonymous"? cart.guid : cart.code
		def response = returningWith(restClient.get(
				path: getBasePathWithSite() + '/users/'+B2B_USERNAME+'/carts/' + cartcode+ '/entries/' +entryNumber,
				query: ['fields': FIELD_SET_LEVEL_FULL],
				contentType: format
				),{
					if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
					status == SC_OK
				}).data
		return response
	}

	/**
	 * generates a config id that the mock will accept as valid. It will create a default config for the given product on-the-fly.
	 * @param productCode
	 * @return configId
	 */
	protected String generateTestConfigId(String productCode) {
		return 'MOCK_'+ UUID.randomUUID()+'@'+productCode
	}

	protected String getB2BAwareUsersPathFragment() {
		return Config.getBoolean("occ.rewrite.overlapping.paths.enabled", false) ? "/orgUsers/" : "/users/"
	}
}