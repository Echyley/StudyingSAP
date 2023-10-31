/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.controllers;

import static de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService.CUSTOMER_NOT_FOUND;
import static de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService.MISSING_CART_ID;
import static de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService.MISSING_CUSTOMER_ID;
import static de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService.UNKNOWN_CART_ERROR;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants;
import de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants;
import de.hybris.platform.assistedservicewebservices.dto.CustomerRegistrationFormWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.CustomerSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.PrincipalWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;


@NeedsEmbeddedServer(webExtensions =
{ AssistedservicewebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class CustomersControllerIntegrationTest extends AbstractControllerIntegrationIntegrationTest
{
	public static final String CUSTOMER_SEARCH_URI = "/customers/search";
	public static final String CUSTOMER_LIST_PARAM = "customerListId";
	public static final String DUMMY_USER_UID = "dummyuser";
	public static final String TEMP_CUSTOMER_LIST_ID = "bopisCustomers";
	public static final String[] DUMMY_USERS =
	{ DUMMY_USER_UID + 1, DUMMY_USER_UID + 2, DUMMY_USER_UID + 3 };
	public static final String NOT_AGENT = "notasagent";
	public static final String VALID_CUSTOMER_ID = "user1";
	public static final String ANONYMOUS_CART_ID = "anonymousCart";
	public static final String NONANONYMOUS_CART_ID = "user2Cart";
	public static final String BIND_CART_URI = "/bind-cart";
	public static final String CREATE_CUSTOMER_URI = "/customers";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL_ADDRESS = "test@sap.com";
	public static final String INVALID_EMAIL_ADDRESS = "testEmail";
	public static final String INVALID_BASE_SITE_ID = "invalid_baseSite";

	public static final String LANGUAGE = "lang";

	public static final String CURRENCY = "curr";

	public static final String DEFAULT_LANGUAGE = "en";

	public static final String DEFAULT_CURRENCY = "Y";

	@Test
	public void getCustomersByUid()
	{
		//given
		//when
		final Response response = performGetCustomersCall(DUMMY_USER_UID, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final List<String> results = getCustomerNamesFromResponse(response);
		Assert.assertEquals(DUMMY_USERS.length, results.size());
	}

	@Test
	public void getFirstTwoCustomersInDescendingOrderSortedByName()
	{
		//given
		final int pageSize = 2;
		//when
		final Response response = performGetCustomersCall(DUMMY_USER_UID, AssistedserviceservicesConstants.SORT_BY_NAME_DESC,
				String.valueOf(pageSize), StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final List<String> results = getCustomerNamesFromResponse(response);
		Assert.assertEquals(pageSize, results.size());
		Assert.assertThat(results, contains(DUMMY_USERS[2], DUMMY_USERS[1]));
	}

	@Test
	public void getCustomerFromThirdPageSortedByUidInAscendingOrder()
	{
		//given
		final int pageSize = 1;
		final int page = 2;
		//when
		final Response response = performGetCustomersCall(DUMMY_USER_UID, AssistedserviceservicesConstants.SORT_BY_UID_ASC,
				String.valueOf(pageSize), String.valueOf(page));
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final List<String> results = getCustomerNamesFromResponse(response);
		Assert.assertEquals(pageSize, results.size());
		Assert.assertThat(results, contains(DUMMY_USERS[page]));
	}

	@Test
	public void getCustomersByNotExistingUid()
	{
		//given
		final String notExistingCustomerUid = "notExistingCustomer@example.com";
		//when
		final Response response = performGetCustomersCall(notExistingCustomerUid, StringUtils.EMPTY, StringUtils.EMPTY,
				StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final CustomerSearchPageWsDTO results = response.readEntity(CustomerSearchPageWsDTO.class);
		assertTrue(CollectionUtils.isEmpty(results.getEntries()));
	}

	@Test
	public void shouldGetAllCustomerByEmptyCriteria()
	{
		//given
		//when
		final Response response = performGetCustomersCall(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
				StringUtils.EMPTY);
		//then
		WebservicesAssert.assertResponse(Status.OK, response);
		final CustomerSearchPageWsDTO results = response.readEntity(CustomerSearchPageWsDTO.class);
		assertTrue(CollectionUtils.isNotEmpty(results.getEntries()));
	}

	@Test
	public void shouldGetCustomerWithOrder()
	{
		final Response get = performGetCustomersWithDifferentQueries(
				ImmutableMap.of(BASE_SITE_PARAM, BASE_SITE_ID, "orderId", "order1"));

		assertResponse(Response.Status.OK, get);
		final CustomerSearchPageWsDTO entity = get.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains(VALID_CUSTOMER_ID)).isTrue();
		assertTrue(entity.getEntries().get(0).getHasOrder());
	}

	@Test
	public void shouldGetCustomerEnrichedData() {
		final Response get = performGetCustomersWithDifferentQueries(
				ImmutableMap.of(BASE_SITE_PARAM, BASE_SITE_ID, "orderId", "order1"));

		assertResponse(Status.OK, get);
		final CustomerSearchPageWsDTO entity = get.readEntity(CustomerSearchPageWsDTO.class);
		assertThat(entity.getEntries().get(0)).isNotNull()
				.hasFieldOrProperty("lastCartId")
				.hasFieldOrProperty("userAvatar")
				.hasFieldOrProperty("hasOrder");
	}

	@Test
	public void shouldGetB2BCustomerEnrichedData() {
		try
		{
			Class.forName("de.hybris.platform.b2bwebservicescommons.dto.company.B2BUnitWsDTO");
		}
		catch (ClassNotFoundException e)
		{
			return;
		}

		final Response get = performGetCustomersWithDifferentQueries(
				ImmutableMap.of(BASE_SITE_PARAM, BASE_SITE_ID, "orderId", "order1"));

		assertResponse(Status.OK, get);
		final CustomerSearchPageWsDTO entity = get.readEntity(CustomerSearchPageWsDTO.class);
		assertThat(entity.getEntries().get(0)).isNotNull()
				.hasFieldOrProperty("orgUnit");
	}

	@Test
	public void shouldNotGetCustomerWithWrongOrder()
	{
		// call with wrong order and baseSite
		final Response get = performGetCustomersWithDifferentQueries(
				ImmutableMap.of(BASE_SITE_PARAM, BASE_SITE_ID, "orderId", "wrong_order"));
		assertResponse(Status.BAD_REQUEST, get);
	}

	@Test
	public void getCustomersByEmptyQuery()
	{
		//when
		final Response response = performGetCustomersCall(null, null, null, null);
		//then
		final List<String> results = getCustomerNamesFromResponse(response);
		assertThat(results.size() >= DUMMY_USERS.length).isTrue();
	}

	@Test
	public void shouldNotGetCustomersByUnknownCustomerListId()
	{
		// default call
		final Response simpleGetResponce = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(CUSTOMER_LIST_PARAM, "someID").queryParam(BASE_SITE_PARAM, BASE_SITE_ID).build()
				.accept(MediaType.APPLICATION_JSON).get();
		simpleGetResponce.bufferEntity();

		assertResponse(Status.BAD_REQUEST, simpleGetResponce);
	}

	@Test
	public void shouldGetCustomersByCustomerListId()
	{
		// default call
		final Response simpleGetResponce = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(CUSTOMER_LIST_PARAM, TEMP_CUSTOMER_LIST_ID).queryParam(BASE_SITE_PARAM, BASE_SITE_ID).build()
				.accept(MediaType.APPLICATION_JSON).get();
		simpleGetResponce.bufferEntity();

		assertResponse(Status.OK, simpleGetResponce);
		final CustomerSearchPageWsDTO entity = simpleGetResponce.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains(VALID_CUSTOMER_ID)).isTrue();
		assertThat(uids.contains("user2")).isTrue();
	}

	@Test
	public void shouldGetCustomersByCustomerListIdWithPaging()
	{
		// call with pagination and default sorting
		final Response getWithPaging = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(CUSTOMER_LIST_PARAM, TEMP_CUSTOMER_LIST_ID).queryParam(BASE_SITE_PARAM, BASE_SITE_ID)
				.queryParam(PAGE_SIZE, "1").queryParam(CURRENT_PAGE, "0").build().accept(MediaType.APPLICATION_JSON).get();
		getWithPaging.bufferEntity();
		assertResponse(Status.OK, getWithPaging);
		final CustomerSearchPageWsDTO entity = getWithPaging.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains("user2")).isTrue();
		assertThat(entity.getPagination().getPageSize() == NumberUtils.INTEGER_ONE).isTrue();
		assertThat(entity.getPagination().getSort().equalsIgnoreCase("byOrderDateDesc")).isTrue();
	}

	@Test
	public void shouldGetCustomersByCustomerListIdWithPagingAndSort()
	{
		// call with pagination and another sorting
		final Response getWithPagingAndSorting = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD)
				.path(CUSTOMER_SEARCH_URI).queryParam(CUSTOMER_LIST_PARAM, TEMP_CUSTOMER_LIST_ID)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID).queryParam(PAGE_SIZE, "1").queryParam(CURRENT_PAGE, "0")
				.queryParam(SORT, "byOrderDateAsc").build().accept(MediaType.APPLICATION_JSON).get();
		getWithPagingAndSorting.bufferEntity();
		assertResponse(Status.OK, getWithPagingAndSorting);
		final CustomerSearchPageWsDTO entity = getWithPagingAndSorting.readEntity(CustomerSearchPageWsDTO.class);
		final List<String> uids = entity.getEntries().stream().map(UserWsDTO::getUid).collect(Collectors.toList());
		assertThat(uids.contains(VALID_CUSTOMER_ID)).isTrue();
		assertThat(entity.getPagination().getPageSize() == NumberUtils.INTEGER_ONE).isTrue();
		assertThat(entity.getPagination().getSort().equalsIgnoreCase("byOrderDateAsc")).isTrue();
	}

	@Test
	public void getCustomersByNotAsAgent()
	{
		final Response result = getWsSecuredRequestBuilder(NOT_AGENT, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID).queryParam("query", "test").build().accept(MediaType.APPLICATION_JSON)
				.get();
		assertResponse(Status.FORBIDDEN, result);
	}

	@Test
	public void bindAnonymousCartToMissingCustomerTest()
	{
		final Response result = bindAnonymousCartToCustomer(ANONYMOUS_CART_ID, "");
		assertResponse(Status.BAD_REQUEST, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains(Localization.getLocalizedString(MISSING_CUSTOMER_ID)));
	}

	@Test
	public void bindMissingAnonymousCartToCustomerTest()
	{
		final Response result = bindAnonymousCartToCustomer("", VALID_CUSTOMER_ID);
		assertResponse(Status.BAD_REQUEST, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains(Localization.getLocalizedString(MISSING_CART_ID)));
	}

	@Test
	public void bindAnonymousCartToInvalidCustomerTest()
	{
		final Response result = bindAnonymousCartToCustomer(ANONYMOUS_CART_ID, "invalid_user_for_binding");
		assertResponse(Status.BAD_REQUEST, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains(Localization.getLocalizedString(CUSTOMER_NOT_FOUND)));
	}

	@Test
	public void bindAnonymousInvalidCartToCustomerTest()
	{
		final Response result = bindAnonymousCartToCustomer("invalidCart", VALID_CUSTOMER_ID);
		assertResponse(Status.BAD_REQUEST, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains(Localization.getLocalizedString(UNKNOWN_CART_ERROR)));
	}

	@Test
	public void bindAnonymousCartToCustomerTest()
	{
		final Response result = bindAnonymousCartToCustomer(ANONYMOUS_CART_ID, VALID_CUSTOMER_ID);
		assertResponse(Status.OK, result);

		final Response secondResult = bindAnonymousCartToCustomer(NONANONYMOUS_CART_ID, VALID_CUSTOMER_ID);
		assertResponse(Status.BAD_REQUEST, secondResult);
		final String message = secondResult.readEntity(String.class);
		// We only try to search cart with owner is anonymous in webservice mode.
		assertTrue(message != null && message.contains(Localization.getLocalizedString(UNKNOWN_CART_ERROR)));

	}

	@Test
	public void createCustomerWithValidCustomerRegistrationFormTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWsDTO.setFirstName(FIRST_NAME);
		customerRegistrationFormWsDTO.setLastName(LAST_NAME);
		customerRegistrationFormWsDTO.setEmailAddress(EMAIL_ADDRESS);
		final Response result = performCreateCustomers(OAUTH_USERNAME, OAUTH_PASSWORD, BASE_SITE_ID, customerRegistrationFormWsDTO);
		assertResponse(Status.CREATED, result);
		final UserWsDTO entity = result.readEntity(UserWsDTO.class);
		assertThat(entity).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("name", FIRST_NAME + " " + LAST_NAME)
				.hasFieldOrPropertyWithValue("uid", EMAIL_ADDRESS);
		assertThat(entity.getLanguage()).hasFieldOrPropertyWithValue("isocode", DEFAULT_LANGUAGE);
		assertThat(entity.getCurrency()).hasFieldOrPropertyWithValue("isocode", DEFAULT_CURRENCY);
	}

	@Test
	public void createCustomersWithGivenLanguageAndCurrencyTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWsDTO.setFirstName(FIRST_NAME);
		customerRegistrationFormWsDTO.setLastName(LAST_NAME);
		customerRegistrationFormWsDTO.setEmailAddress(EMAIL_ADDRESS);
		final Response result = performCreateCustomersWithGivenLanguageAndCurrency(OAUTH_USERNAME, OAUTH_PASSWORD, BASE_SITE_ID, customerRegistrationFormWsDTO,"zh", "EUR");
		assertResponse(Status.CREATED, result);
		final UserWsDTO entity = result.readEntity(UserWsDTO.class);
		assertThat(entity).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("name", FIRST_NAME + " " + LAST_NAME)
				.hasFieldOrPropertyWithValue("uid", EMAIL_ADDRESS);
		assertThat(entity.getLanguage()).hasFieldOrPropertyWithValue("isocode", "zh");
		assertThat(entity.getCurrency()).hasFieldOrPropertyWithValue("isocode", "EUR");
	}

	@Test
	public void createCustomerWithBlankAroundRegistrationParamsTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWsDTO.setFirstName(" " + FIRST_NAME + " ");
		customerRegistrationFormWsDTO.setLastName(" " + LAST_NAME + " ");
		customerRegistrationFormWsDTO.setEmailAddress(EMAIL_ADDRESS);
		final Response result = performCreateCustomers(OAUTH_USERNAME, OAUTH_PASSWORD, BASE_SITE_ID, customerRegistrationFormWsDTO);
		assertResponse(Status.CREATED, result);
		final UserWsDTO entity = result.readEntity(UserWsDTO.class);
		assertThat(entity).isNotNull()
				.hasFieldOrPropertyWithValue("firstName", FIRST_NAME)
				.hasFieldOrPropertyWithValue("lastName", LAST_NAME)
				.hasFieldOrPropertyWithValue("name", FIRST_NAME + " " + LAST_NAME)
				.hasFieldOrPropertyWithValue("uid", EMAIL_ADDRESS);
		assertThat(entity.getLanguage()).hasFieldOrPropertyWithValue("isocode", DEFAULT_LANGUAGE);
		assertThat(entity.getCurrency()).hasFieldOrPropertyWithValue("isocode", DEFAULT_CURRENCY);
	}

	@Test
	public void createCustomerWithInvalidEmailAddressTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWsDTO.setFirstName(FIRST_NAME);
		customerRegistrationFormWsDTO.setLastName(LAST_NAME);
		customerRegistrationFormWsDTO.setEmailAddress(INVALID_EMAIL_ADDRESS);
		final Response result = performCreateCustomers(OAUTH_USERNAME, OAUTH_PASSWORD, BASE_SITE_ID, customerRegistrationFormWsDTO);
		assertResponse(Status.BAD_REQUEST, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains("This field is not a valid email addresss."));
	}

	@Test
	public void createCustomerWithoutFirstNameTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWithoutFirstName = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWithoutFirstName.setLastName(LAST_NAME);
		customerRegistrationFormWithoutFirstName.setEmailAddress(EMAIL_ADDRESS);
		final Response result = performCreateCustomers(OAUTH_USERNAME, OAUTH_PASSWORD, BASE_SITE_ID, customerRegistrationFormWithoutFirstName);
		assertResponse(Status.BAD_REQUEST, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains("This field is required.") && message.contains("firstName"));
	}

	@Test
	public void createCustomerWithoutLastNameTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWithoutLastName = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWithoutLastName.setFirstName(FIRST_NAME);
		customerRegistrationFormWithoutLastName.setEmailAddress(EMAIL_ADDRESS);
		final Response result = performCreateCustomers(OAUTH_USERNAME, OAUTH_PASSWORD, BASE_SITE_ID, customerRegistrationFormWithoutLastName);
		assertResponse(Status.BAD_REQUEST, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains("This field is required.") && message.contains("lastName"));
	}

	@Test
	public void createCustomerWithInvalidBaseSiteTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWsDTO.setFirstName(FIRST_NAME);
		customerRegistrationFormWsDTO.setLastName(LAST_NAME);
		customerRegistrationFormWsDTO.setEmailAddress(EMAIL_ADDRESS);
		final Response result = performCreateCustomers(OAUTH_USERNAME, OAUTH_PASSWORD, INVALID_BASE_SITE_ID, customerRegistrationFormWsDTO);
		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains("The requested BaseSite: " + INVALID_BASE_SITE_ID + " doesn't exist"));
	}

	@Test
	public void createCustomerWithTokenNotBelongToAgentTest() {
		final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO = new CustomerRegistrationFormWsDTO();
		customerRegistrationFormWsDTO.setFirstName(FIRST_NAME);
		customerRegistrationFormWsDTO.setLastName(LAST_NAME);
		customerRegistrationFormWsDTO.setEmailAddress(EMAIL_ADDRESS);
		final Response result = performCreateCustomers(NOT_AGENT, OAUTH_PASSWORD, BASE_SITE_ID, customerRegistrationFormWsDTO);
		assertResponse(Status.FORBIDDEN, result);
		final String message = result.readEntity(String.class);
		assertTrue(message != null && message.contains("Access is denied"));
	}

	protected Response performCreateCustomers(final String oauthUserName, final String oauthPwd, final String baseSite, final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO) {
		final Response result = getWsSecuredRequestBuilder(oauthUserName, oauthPwd).path(CREATE_CUSTOMER_URI)
				.queryParam(BASE_SITE_PARAM, baseSite)
				.build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(customerRegistrationFormWsDTO, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		return result;
	}

    protected Response performCreateCustomersWithGivenLanguageAndCurrency(final String oauthUserName, final String oauthPwd, final String baseSite, final CustomerRegistrationFormWsDTO customerRegistrationFormWsDTO, final String language, final String currency) {
        final Response result = getWsSecuredRequestBuilder(oauthUserName, oauthPwd).path(CREATE_CUSTOMER_URI)
				.queryParam(BASE_SITE_PARAM, baseSite).queryParam(LANGUAGE, language).queryParam(CURRENCY, currency)
                .build().accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity(customerRegistrationFormWsDTO, MediaType.APPLICATION_JSON));
        result.bufferEntity();
        return result;
    }

	protected Response performGetCustomersWithDifferentQueries(final Map<String, String> params)
	{
		WsSecuredRequestBuilder builder = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI);
		final Set<Map.Entry<String, String>> entries = params.entrySet();
		for (final Map.Entry<String, String> e : entries)
		{
			builder = builder.queryParam(e.getKey(), e.getValue());
		}
		final Response response = builder.build().accept(MediaType.APPLICATION_JSON).get();
		response.bufferEntity();
		return response;
	}

	protected Response performGetCustomersCall(final String searchCritera, final String sort, final String pageSize,
			final String currentPage)
	{
		final String queryParamKey = "query";
		final Response result = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(CUSTOMER_SEARCH_URI)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID).queryParam(queryParamKey, searchCritera).queryParam(SORT, sort)
				.queryParam(PAGE_SIZE, pageSize).queryParam(CURRENT_PAGE, currentPage).build().accept(MediaType.APPLICATION_JSON)
				.get();
		result.bufferEntity();
		return result;
	}

	protected List<String> getCustomerNamesFromResponse(final Response response)
	{
		final CustomerSearchPageWsDTO results = response.readEntity(CustomerSearchPageWsDTO.class);
		return results.getEntries().stream().map(PrincipalWsDTO::getName).collect(Collectors.toList());
	}

	protected Response bindAnonymousCartToCustomer(final String cartId, final String customerId)
	{
		final Response result = getWsSecuredRequestBuilder(OAUTH_USERNAME, OAUTH_PASSWORD).path(BIND_CART_URI)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID)
				.queryParam("customerId", customerId)
				.queryParam("cartId", cartId)
				.build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(null, MediaType.APPLICATION_JSON));
		result.bufferEntity();
		return result;
	}
}
