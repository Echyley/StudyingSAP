/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.auth.impl;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.gigya.gigyab2bservices.constants.Gigyab2bservicesConstants;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Test class for DefaultGigyaAuthService
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaAssetsAuthServiceTest
{

	private static final String SAMPLE_B2B_UID = "sample-b2b-uid";
	private static final String SAMPLE_GROUP = "sample-group";
	private static final String PATH = "path";
	private static final String TYPE = "type";
	private static final String ATTRIBUTES = "attributes";
	private static final String SAMPLE_GIGYA_USER_KEY = "gigya-user-key";
	private static final String SAMPLE_GIGYA_USER_SECRET = "auth-user-secret";

	@InjectMocks
	private final GigyaAssetsAuthService gigyaAuthService = new GigyaAssetsAuthService();

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private UserService userService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private B2BCustomerModel customer;

	@Mock
	private B2BUnitModel b2bUnit;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private GSObject gsObject;

	@Mock
	private UserGroupModel userGroup;

	@Test
	public void testGetAssetsAPIAssignAuthorisationsToCustomerWhenAuthorizationExists() throws GSKeyNotFoundException
	{
		setupGigyaConfig();

		Mockito.lenient().when(customer.getDefaultB2BUnit()).thenReturn(b2bUnit);
		Mockito.lenient().when(b2bUnit.getUid()).thenReturn(SAMPLE_B2B_UID);
		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(GSObject.class), Mockito.eq(gigyaConfig), Mockito.anyInt(), Mockito.anyInt()))
		                .thenReturn(gsResponse);
		Mockito.lenient().when(gsResponse.hasData()).thenReturn(true);
		Mockito.lenient().when(gsResponse.getData()).thenReturn(gsObject);
		Mockito.lenient().when(gsObject.get(Gigyab2bservicesConstants.ALLOWED_ASSETS_ATTR)).thenReturn(createGigyaActionsAccessDataList());

		gigyaAuthService.assignAuthorisationsToCustomer(customer);

		Mockito.verify(customer).setGroups(Mockito.anySet());
	}

	@Test
	public void testGetAssetsAPIAssignAuthorisationsToCustomerWhenFetchingAuthorizationFails()
	{
		setupGigyaConfig();
		
		Mockito.lenient().when(gigyaService.callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(GSObject.class), Mockito.eq(gigyaConfig), Mockito.anyInt(), Mockito.anyInt()))
		                .thenThrow(GigyaApiException.class);
		Mockito.lenient().when(userService.getUserGroupForUID(SAMPLE_GROUP)).thenReturn(userGroup);
		
		Mockito.lenient().when(customer.getDefaultB2BUnit()).thenReturn(b2bUnit);
		Mockito.lenient().when(b2bUnit.getUid()).thenReturn(SAMPLE_B2B_UID);
		gigyaAuthService.assignAuthorisationsToCustomer(customer);
		// verify that setGroups is not called and the method exits gracefully
		Mockito.verify(customer, Mockito.never()).setGroups(Mockito.anySet());
	}

	@Test
	public void testRemoveAuthorisationsOfCustomerWhenCustomerExists()
	{
		Mockito.lenient().when(customer.getGroups()).thenReturn(Collections.singleton(userGroup));
		Mockito.lenient().when(userGroup.getUid()).thenReturn("b2badmingroup");

		gigyaAuthService.removeAuthorisationsOfCustomer(customer);

		Mockito.verify(customer).setGroups(Collections.emptySet());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAuthorisationsOfCustomerWhenCustomerDoesntExists()
	{
		gigyaAuthService.removeAuthorisationsOfCustomer(null);
	}

	private GSArray createGigyaActionsAccessDataList()
	{
		GSArray gigyaAccessList = new GSArray();
		GSObject allowedAssetData = new GSObject();
		allowedAssetData.put(PATH, SAMPLE_GROUP);
		allowedAssetData.put(TYPE, Gigyab2bservicesConstants.COMMERCE_ASSET_TYPE);
		allowedAssetData.put(ATTRIBUTES, Collections.singletonMap(Gigyab2bservicesConstants.ROLE_NAME_ATTR, Collections.singletonList(SAMPLE_GROUP)));

		gigyaAccessList.add(allowedAssetData);
		return gigyaAccessList;
	}
	
	private void setupGigyaConfig() {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.lenient().when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaConfig.getAuthorizationUrl()).thenReturn("");
		Mockito.lenient().when(gigyaConfig.getGigyaUserKey()).thenReturn(SAMPLE_GIGYA_USER_KEY);
		Mockito.lenient().when(gigyaConfig.getGigyaUserSecret()).thenReturn(SAMPLE_GIGYA_USER_SECRET);
	}
}