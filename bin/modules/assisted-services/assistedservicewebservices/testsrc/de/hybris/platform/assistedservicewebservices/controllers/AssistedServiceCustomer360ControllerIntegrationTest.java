/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.controllers;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedservicecustomerinterestsfacades.customer360.provider.CustomerInterestsDataProvider;
import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicepromotionfacades.data.provider.CSACouponsModelProvider;
import de.hybris.platform.assistedservicepromotionfacades.data.provider.CSAPromotionsModelProvider;
import de.hybris.platform.assistedservicepromotionfacades.data.provider.CustomerCouponsModelProvider;
import de.hybris.platform.assistedservicepromotionfacades.data.provider.CustomerPromotionsModelProvider;
import de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants;
import de.hybris.platform.assistedservicewebservices.dto.C360ActivityTypeWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360ActivityWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360Overview;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerCouponList;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerCouponWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProductInterestList;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360QueryWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360Cart;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponList;
import de.hybris.platform.assistedservicewebservices.dto.C360CustomerProfile;
import de.hybris.platform.assistedservicewebservices.dto.C360PromotionList;
import de.hybris.platform.assistedservicewebservices.dto.C360ReviewList;
import de.hybris.platform.assistedservicewebservices.dto.C360StoreLocation;
import de.hybris.platform.assistedservicewebservices.dto.C360SavedCart;
import de.hybris.platform.assistedservicewebservices.dto.Customer360ListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360ActivityList;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.jaxb.Jaxb2HttpMessageConverter;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;


@NeedsEmbeddedServer(webExtensions =
        { AssistedservicewebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class AssistedServiceCustomer360ControllerIntegrationTest extends AbstractControllerIntegrationIntegrationTest{
    private static final String CUSTOMER1_ID = "user1";
    private static final String CUSTOMER2_ID = "user2";
    private static final String CUSTOMER_PASSWORD = "123456";
    private static final String STORE_LOCATION_TYPE = "c360StoreLocation";
    private static final String REVIEW_LIST_TYPE = "c360ReviewList";
    private static final String HEADING_TYPE = "c360Overview";
    private static final String CUSTOMER_PRODUCT_INTEREST_LIST_TYPE = "c360CustomerProductInterestList";
    private static final String INVALID_TYPE = "invalidType";
    private static final String INVALID_OAUTH_PASSWORD = "invalidOauthPassword";
    private static final String NON_EXISTING_USER = "nonExistingUser";
    private static final String INVALID_BASE_SITE_ID = "invalidBaseSiteId";
    private static final String COUPON_LIST_TYPE = "c360CouponList";
    private static final String PROMOTION_LIST_TYPE = "c360PromotionList";
    private static final String CUSTOMER_COUPON_LIST_TYPE = "c360CustomerCouponList";
    private static final String CART_TYPE = "c360Cart";
    private static final String SAVED_CART_TYPE = "c360SavedCart";
    private static final String ACTIVITY_LIST_TYPE = "c360ActivityList";
    private static final String PROFILE_TYPE = "c360CustomerProfile";

    @Resource(name = "jsonHttpMessageConverter")
    private Jaxb2HttpMessageConverter defaultJsonHttpMessageConverter;

    @Resource(name = "CSAPromotionsModelProvider")
    private CSAPromotionsModelProvider cSAPromotionsModelProvider;

    @Resource(name = "CSACouponsModelProvider")
    private CSACouponsModelProvider cSACouponsModelProvider;

    @Resource(name = "CustomerPromotionsModelProvider")
    private CustomerPromotionsModelProvider customerPromotionsModelProvider;

    @Resource(name = "customerCouponsModelProvider")
    private CustomerCouponsModelProvider customerCouponsModelProvider;

    @Resource(name = "customerInterestsModelProvider")
    private CustomerInterestsDataProvider customerInterestsModelProvider;

    @Resource(name ="customer360ModelProvidersMap")
    Map<String, FragmentModelProvider> customer360ModelProvidersMap;

    @Before
    public void setup() throws Exception
    {
        // Fix issue the map is merged later than the test, only could be reproduced in ECPP :(
        if (customer360ModelProvidersMap != null)
        {
            customer360ModelProvidersMap.put("CSAPromotionsFragment", cSAPromotionsModelProvider);
            customer360ModelProvidersMap.put("CSACouponsFragment", cSACouponsModelProvider);
            customer360ModelProvidersMap.put("customerPromotionsFragment", customerPromotionsModelProvider);
            customer360ModelProvidersMap.put("CSACustomerCouponsFragment", customerCouponsModelProvider);
            customer360ModelProvidersMap.put("customerInterestsFragment", customerInterestsModelProvider);
        }
    }
    @Test
    public void shouldGetC360StoreLocation() throws JAXBException {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(STORE_LOCATION_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);
        C360StoreLocation c360StoreLocation = (C360StoreLocation) entity.getValue().get(0);
        assertThat(c360StoreLocation.getAddress()).isEqualTo("New York United States of America 10019");
    }

    @Test
    public void shouldGetC360ReviewList() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(REVIEW_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);
        C360ReviewList c360ReviewList = (C360ReviewList) entity.getValue().get(0);
        assertThat(c360ReviewList.getReviews()).hasSize(2);
    }

    @Test
    public void shouldGetC360CustomerProductInterests() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(CUSTOMER_PRODUCT_INTEREST_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);
        C360CustomerProductInterestList c360CustomerProductInterestList = (C360CustomerProductInterestList) entity.getValue().get(0);
        assertThat(c360CustomerProductInterestList.getType()).isEqualTo(CUSTOMER_PRODUCT_INTEREST_LIST_TYPE);
        assertThat(c360CustomerProductInterestList.getCustomerProductInterests()).hasSize(1);
        assertThat(c360CustomerProductInterestList.getCustomerProductInterests().get(0).getProduct().getCode()).isEqualTo("278688");
    }

    @Test
    public void shouldGetEmptyC360CustomerProductInterests() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER2_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(CUSTOMER_PRODUCT_INTEREST_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);
        C360CustomerProductInterestList c360CustomerProductInterestList = (C360CustomerProductInterestList) entity.getValue().get(0);
        assertTrue(isEmpty(c360CustomerProductInterestList.getCustomerProductInterests()));
    }



    @Test
    public void shouldGetC360Cart() throws JAXBException {
        final String path = "/" + BASE_SITE_ID + "/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(CART_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);
        C360Cart c360Cart = (C360Cart) entity.getValue().get(0);
        assertThat(c360Cart.getCart().getCode()).isEqualTo("user1Cart");
        assertThat(c360Cart.getCart().getEntries()).hasSize(1);
        assertThat(c360Cart.getCart().getEntries().get(0).getProductCode()).isEqualTo("278688");
    }

    @Test
    public void shouldGetC360SavedCart() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(SAVED_CART_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);

        C360SavedCart c360SavedCart = (C360SavedCart) entity.getValue().get(0);
        assertThat(c360SavedCart.getSavedCart().getCode()).isEqualTo("user1SavedCart");
        assertThat(c360SavedCart.getSavedCart().getEntries()).hasSize(1);
        assertThat(c360SavedCart.getSavedCart().getEntries().get(0).getProductCode()).isEqualTo("280916");
    }

    @Test
    public void shouldGetNullWhenRequestingC360SavedCartWithNoSavedCart() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER2_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(SAVED_CART_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);
        C360SavedCart c360SavedCart = (C360SavedCart) entity.getValue().get(0);
        assertThat(c360SavedCart.getSavedCart()).isNull();
    }

    @Test
    public void shouldGetC360CouponList() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(COUPON_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);
        C360CouponList c360CouponList = (C360CouponList) entity.getValue().get(0);
        assertThat(c360CouponList.getCoupons()).hasSize(3);
    }

    @Test
    public void shouldGetEmptyPromotionList() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(PROMOTION_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);

        assertThat(entity.getValue()).hasSize(1);
        C360PromotionList c360PromotionList = (C360PromotionList) entity.getValue().get(0);
        assertTrue(isEmpty(c360PromotionList.getPromotions()));
    }

    @Test
    public void shouldGet403WhenMakingRequestAsCustomer() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(STORE_LOCATION_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                CUSTOMER1_ID,
                CUSTOMER_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertThat(response.getStatus()).isEqualTo(SC_FORBIDDEN);
    }

    @Test
    public void shouldGet400WhenMakingRequestWithInvalidType() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(INVALID_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertThat(response.getStatus()).isEqualTo(SC_BAD_REQUEST);
    }

    @Test
    public void shouldGet401WhenMakingRequestWithInvalidToken() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(CUSTOMER_PRODUCT_INTEREST_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                INVALID_OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertThat(response.getStatus()).isEqualTo(SC_UNAUTHORIZED);
    }

    @Test
    public void shouldGet400WhenMakingRequestWithNonExistingUser() throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + NON_EXISTING_USER + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(CUSTOMER_PRODUCT_INTEREST_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertThat(response.getStatus()).isEqualTo(SC_BAD_REQUEST);
    }

    @Test
    public void shouldGet400WhenMakingRequestWithInvalidBaseSite() throws JAXBException
    {
        final String path = "/"+ INVALID_BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(CUSTOMER_PRODUCT_INTEREST_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertThat(response.getStatus()).isEqualTo(SC_BAD_REQUEST);
    }

    @Test
    public void shouldGetC360AssignableCustomerCouponList() throws JAXBException
    {
        final Map<String, String> additionalRequestParameters = Map.ofEntries(Map.entry("searchQuery", ""), Map.entry("assignable", "true"));
        final Customer360ListWsDTO entity = getCustomerCoupon360ListWs(additionalRequestParameters);

        assertThat(entity.getValue()).hasSize(1);
        C360CustomerCouponList c360CustomerCouponList = (C360CustomerCouponList) entity.getValue().get(0);
        assertThat(c360CustomerCouponList.getType()).isEqualToIgnoringCase(CUSTOMER_COUPON_LIST_TYPE);
        assertThat(c360CustomerCouponList.getCustomerCoupons()).hasSize(2);
        for (C360CustomerCouponWsDTO couponWsDTO :c360CustomerCouponList.getCustomerCoupons()) {
            assertThat(couponWsDTO.getCode()).matches("(customerCouponTestInASM)|(customerCouponTestInASM3)");
        }
    }

    @Test
    public void shouldGetC360AssignedCustomerCouponList() throws JAXBException
    {
        final Map<String, String> additionalRequestParameters = Map.ofEntries(Map.entry("searchQuery", ""), Map.entry("assignable", "false"));
        final Customer360ListWsDTO entity = getCustomerCoupon360ListWs(additionalRequestParameters);

        assertThat(entity.getValue()).hasSize(1);
        C360CustomerCouponList c360CustomerCouponList = (C360CustomerCouponList) entity.getValue().get(0);
        assertThat(c360CustomerCouponList.getType()).isEqualToIgnoringCase(CUSTOMER_COUPON_LIST_TYPE);
        assertThat(c360CustomerCouponList.getCustomerCoupons()).hasSize(1);
        assertThat(c360CustomerCouponList.getCustomerCoupons().get(0).getCode()).isEqualToIgnoringCase("customerCouponTestInASM2");
    }

    @Test
    public void shouldGetC360AssignableCustomerCouponListWhenSearchQuery() throws JAXBException
    {
        final Map<String, String> additionalRequestParameters = Map.ofEntries(Map.entry("searchQuery", "A1220"), Map.entry("assignable", "true"));
        final Customer360ListWsDTO entity = getCustomerCoupon360ListWs(additionalRequestParameters);

        assertThat(entity.getValue()).hasSize(1);
        C360CustomerCouponList c360CustomerCouponList = (C360CustomerCouponList) entity.getValue().get(0);
        assertThat(c360CustomerCouponList.getType()).isEqualToIgnoringCase(CUSTOMER_COUPON_LIST_TYPE);
        assertThat(c360CustomerCouponList.getCustomerCoupons()).hasSize(1);
        assertThat(c360CustomerCouponList.getCustomerCoupons().get(0).getCode()).isEqualToIgnoringCase("customerCouponTestInASM3");
    }

    private Customer360ListWsDTO getCustomerCoupon360ListWs(Map<String, String> additionalRequestParameters) throws JAXBException
    {
        final String path = "/"+ BASE_SITE_ID +"/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();
        customer360QueryWsDTO.setAdditionalRequestParameters(additionalRequestParameters);

        customer360QueryWsDTO.setType(CUSTOMER_COUPON_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Collections.singletonList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
              OAUTH_USERNAME,
              OAUTH_PASSWORD,
              path,
              customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        return unmarshallResult(response, Customer360ListWsDTO.class);
    }

    @Test
    public void shouldGetC360Overview() throws JAXBException
    {
        final String path = "/" + BASE_SITE_ID + "/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        customer360QueryWsDTO.setType(HEADING_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(OAUTH_USERNAME, OAUTH_PASSWORD, path, customer360QueryListWsDTO);

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);
        assertThat(entity.getValue()).hasSize(1);
        C360Overview c360Overview = (C360Overview) entity.getValue().get(0);

        assertThat(c360Overview.getType()).isEqualTo("c360Overview");
        assertThat(c360Overview.getOverview().getEmail()).isEqualTo("user1");
        assertThat(c360Overview.getOverview().getName()).isEqualTo("First Test User");
        assertThat(c360Overview.getOverview().getCartCode()).isEqualTo("user1Cart");
        assertThat(c360Overview.getOverview().getCartSize()).isEqualTo(2);

        Assert.assertNotNull(c360Overview.getOverview().getAddress());
        assertThat(c360Overview.getOverview().getAddress().getFirstName()).isEqualTo("Klaus");
        assertThat(c360Overview.getOverview().getAddress().getLastName()).isEqualTo("Demokunde");
        Assert.assertNotNull(c360Overview.getOverview().getAddress().getCountry());
        assertThat(c360Overview.getOverview().getAddress().getCountry().getIsocode()).isEqualTo("JP");
        assertThat(c360Overview.getOverview().getAddress().getPostalCode()).isEqualTo("28277");
        assertThat(c360Overview.getOverview().getAddress().getShippingAddress()).isEqualTo(true);
        assertThat(c360Overview.getOverview().getAddress().getEmail()).isEqualTo("develop@hybris.de");
        assertThat(c360Overview.getOverview().getAddress().getVisibleInAddressBook()).isEqualTo(true);
        assertThat(c360Overview.getOverview().getAddress().getCompanyName()).isEqualTo("hybris GmbH");
        assertThat(c360Overview.getOverview().getAddress().getPhone()).isEqualTo("1234567890");
        assertThat(c360Overview.getOverview().getAddress().getLine1()).isEqualTo("testAddr");
        assertThat(c360Overview.getOverview().getAddress().getLine2()).isEqualTo("1");
        assertThat(c360Overview.getOverview().getAddress().getTown()).isEqualTo("Bremen");
        Assert.assertNotNull(c360Overview.getOverview().getAddress().getRegion());
        assertThat(c360Overview.getOverview().getAddress().getRegion().getIsocode()).isEqualTo("JP-05");
        assertThat(c360Overview.getOverview().getAddress().getTitleCode()).isEqualTo("mr");
        assertThat(c360Overview.getOverview().getAddress().getFormattedAddress()).isEqualTo("testAddr, 1, Bremen, Akita, 28277");
        assertThat(c360Overview.getOverview().getAddress().getDefaultAddress()).isEqualTo(false);

        assertThat(simpleDateFormat.format(c360Overview.getOverview().getSignedUpAt())).isEqualTo("14.02.2008 00:00:00");

        assertThat(c360Overview.getOverview().getLatestOpenedTicketId()).isEqualTo("test-ticket-1");
        assertThat(simpleDateFormat.format(c360Overview.getOverview().getLatestOpenedTicketCreatedAt())).isEqualTo("18.04.2010 00:00:00");
        assertThat(c360Overview.getOverview().getUserAvatar().getFormat()).isEqualTo("mobile");
        assertThat(c360Overview.getOverview().getUserAvatar().getUrl()).isEqualTo("/medias/testdata/media/exampleproduct/image_01.jpg");

        assertThat(c360Overview.getOverview().getLatestOrderCode()).isEqualTo("order1");
        assertThat(simpleDateFormat.format(c360Overview.getOverview().getLatestOrderTime())).isEqualTo("26.01.2010 10:58:00");
        assertThat(c360Overview.getOverview().getLatestOrderTotal()).isEqualTo("Y100.0000");

    }


    @Test
    public void shouldGetC360CustomerProfile() throws JAXBException
    {
        final String path = "/" + BASE_SITE_ID + "/users/" + CUSTOMER1_ID + "/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();


        customer360QueryWsDTO.setType(PROFILE_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(OAUTH_USERNAME, OAUTH_PASSWORD, path, customer360QueryListWsDTO);

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);
        assertThat(entity.getValue()).hasSize(1);
        C360CustomerProfile c360CustomerProfile = (C360CustomerProfile) entity.getValue().get(0);

        assertThat(c360CustomerProfile.getType()).isEqualTo(PROFILE_TYPE);
        assertThat(c360CustomerProfile.getProfile()).isNotNull();
    }

    @Test
    public void shouldGetC360CustomerActivityList() throws JAXBException
    {
        final String path = "/" + BASE_SITE_ID + "/users/activityuser1/customer360";
        final Customer360QueryListWsDTO customer360QueryListWsDTO = new Customer360QueryListWsDTO();
        final Customer360QueryWsDTO customer360QueryWsDTO = new Customer360QueryWsDTO();

        customer360QueryWsDTO.setType(ACTIVITY_LIST_TYPE);
        customer360QueryListWsDTO.setCustomer360Queries(Arrays.asList(customer360QueryWsDTO));

        final Response response = getCustomer360WSCall(
                OAUTH_USERNAME,
                OAUTH_PASSWORD,
                path,
                customer360QueryListWsDTO
        );

        assertResponse(Response.Status.OK, response);

        final Customer360ListWsDTO entity = unmarshallResult(response, Customer360ListWsDTO.class);
        assertThat(entity.getValue()).hasSize(1);
        C360ActivityList c360ActivityList = (C360ActivityList) entity.getValue().get(0);
        assertThat(c360ActivityList.getType()).isEqualTo(ACTIVITY_LIST_TYPE);
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getAssociatedTypeId).filter("test-ticket-2"::equals).findFirst().isPresent());
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getAssociatedTypeId).filter("activityUser1Cart"::equals).findFirst().isPresent());
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getAssociatedTypeId).filter("order6"::equals).findFirst().isPresent());
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getAssociatedTypeId).filter("activityUser1SavedCart"::equals).findFirst().isPresent());
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getType).map(C360ActivityTypeWsDTO::getCode).filter("TICKET"::equals).findFirst().isPresent());
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getType).map(C360ActivityTypeWsDTO::getCode).filter("CART"::equals).findFirst().isPresent());
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getType).map(C360ActivityTypeWsDTO::getCode).filter("SAVED CART"::equals).findFirst().isPresent());
        assertTrue(c360ActivityList.getActivities().stream().map(C360ActivityWsDTO::getType).map(C360ActivityTypeWsDTO::getCode).filter("ORDER"::equals).findFirst().isPresent());
    }

    protected Response getCustomer360WSCall(final String username, final String password, final String path, final Customer360QueryListWsDTO customer360QueryListWsDTO) throws JAXBException
    {
        final Response result = getWsSecuredRequestBuilder(username, password)
              .path(path)
              .build()
              .accept(MediaType.APPLICATION_JSON)
              .post(Entity.entity(customer360QueryListWsDTO, MediaType.APPLICATION_JSON));
        result.bufferEntity();
        return result;
    }

    public String marshallDto(final Object input, final Class<?> c) throws JAXBException
    {
        final Marshaller marshaller = defaultJsonHttpMessageConverter.createMarshaller(c);
        final StringWriter writer = new StringWriter();
        marshaller.marshal(input, writer);
        return writer.toString();
    }

    protected <C> C unmarshallResult(final Response result, final Class<C> c) throws JAXBException
    {
        final Unmarshaller unmarshaller = defaultJsonHttpMessageConverter.createUnmarshaller(c);
        final StreamSource source = new StreamSource(result.readEntity(InputStream.class));
        final C entity = unmarshaller.unmarshal(source, c).getValue();
        return entity;
    }

    /**
     * Checks if a collection object doesn't exist or is empty. Works for JSON and XML formats.
     *
     * @param the collection object to check
     * @return {@code true} if the object is empty, {@code false} otherwise
     */
    private boolean isEmpty(Collection object) {
        return ((object == null) || (object.size() == 0));
    }
}
