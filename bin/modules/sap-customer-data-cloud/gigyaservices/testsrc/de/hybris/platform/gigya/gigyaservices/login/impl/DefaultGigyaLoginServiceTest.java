/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.login.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaUserManagementMode;
import de.hybris.platform.gigya.gigyaservices.keymanagement.GigyaKeyService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

/**
 * Test class for GigyaLoginService
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaLoginServiceTest {

	private static final String SAMPLE_UID = "uid";
	private static final String SAMPLE_UID_SIGNATURE = "uid-sig";
	private static final String SAMPLE_UID_SIGNATURE_TIMESTAMP = "uid-sig-ts";
	private static final String SAMPLE_ID_TOKEN = "sample.id.token";
	@InjectMocks
	private final DefaultGigyaLoginService gigyaLoginService = new DefaultGigyaLoginService();

	@InjectMocks
	private final DefaultGigyaLoginService gigyaLoginServiceSpy = Mockito.spy(gigyaLoginService);

	@Mock
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GenericDao<CustomerModel> gigyaUserGenericDao;

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private GigyaKeyService gigyaKeyService;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private GSObject gsObject;

	@Mock
	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	@Mock
	private Converter<CustomerModel, GSObject> gigyaUserConverter;

	@Test
	public void testVerifyGigyaCallWhenConfigIsDoesntExist() {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(null);

		Assert.assertFalse(gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE,
				SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}

	@Test
	public void testVerifyGigyaCallWhenConfigExistsAndSiteSecretExists() {
		stubGigyaConfigWithSiteSecretUserSecretNull();
		Mockito.lenient().when(gigyaConfig.getGigyaSiteSecret()).thenReturn("site-secret");
		Mockito.doReturn(Boolean.TRUE).when(gigyaLoginServiceSpy).verifyGigyaCallSiteSecret(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		Assert.assertTrue(gigyaLoginServiceSpy.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE,
				SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}

	@Test
	public void testVerifyGigyaCallWhenConfigExistsAndUserSecretExists() {
		stubGigyaConfigWithSiteSecretUserSecretNull();
		Mockito.lenient().when(gigyaConfig.getGigyaUserSecret()).thenReturn("user-secret");

		Mockito.doReturn(Boolean.TRUE).when(gigyaLoginServiceSpy).verifyGigyaCallApiUser(Mockito.any(),
				Mockito.anyString(), Mockito.anyString(), Mockito.any());

		Assert.assertTrue(gigyaLoginServiceSpy.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE,
				SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}

	@Test
	public void testVerifyGigyaCallWhenConfigExistsAndNeitherUserSecretOrSiteSecretExists() {
		stubGigyaConfigWithSiteSecretUserSecretNull();

		Assert.assertFalse(gigyaLoginService.verifyGigyaCall(gigyaConfig, SAMPLE_UID, SAMPLE_UID_SIGNATURE,
				SAMPLE_UID_SIGNATURE_TIMESTAMP));
	}

	@Test
	public void testVerifyGigyaCallIdTokenExpiryAndSignatureWhenIdTokenIsNull() {
		stubGigyaConfigWithSiteSecretUserSecretNull();

		Assert.assertFalse(gigyaLoginService.verifyGigyaCallIdTokenExpiryAndSignature(gigyaConfig, null));
	}

	@Test
	public void testVerifyGigyaCallIdTokenExpiryAndSignatureWhenGigyaConfigIdIsNull() {
		stubGigyaConfigWithSiteSecretUserSecretNull();

		Assert.assertFalse(gigyaLoginService.verifyGigyaCallIdTokenExpiryAndSignature(null, SAMPLE_ID_TOKEN));
	}

	@Test
	public void testVerifyGigyaCallIdTokenExpiryAndSignatureWhenConfigAndIdTokenExists() {
		stubGigyaConfigWithSiteSecretUserSecretNull();
		Mockito.doReturn(Boolean.TRUE).when(gigyaKeyService).validateTokenExpiryAndSignature(Mockito.eq(gigyaConfig),
				Mockito.anyString());

		Assert.assertTrue(gigyaLoginService.verifyGigyaCallIdTokenExpiryAndSignature(gigyaConfig, SAMPLE_ID_TOKEN));
	}

	@Test
	public void testverifyGigyaIdTokenContainsUIDWhenNeitherIdTokenOrUidExists() {
		stubGigyaConfigWithSiteSecretUserSecretNull();

		Assert.assertFalse(gigyaLoginService.verifyGigyaIdTokenContainsUID(null, null));
	}

	@Test
	public void testverifyGigyaIdTokenContainsUIDWhenIdTokenAndUidExists() {

		Mockito.doReturn(Boolean.TRUE).when(gigyaKeyService).validateTokenUID(Mockito.anyString(), Mockito.anyString());
		Assert.assertTrue(gigyaLoginService.verifyGigyaIdTokenContainsUID(SAMPLE_ID_TOKEN, SAMPLE_UID));
	}

	@Test
	public void testFindCustomerByGigyaUidWhenCustomerExists() {
		Mockito.lenient().when(gigyaUserGenericDao.find(Mockito.anyMap()))
				.thenReturn(Collections.singletonList(gigyaUser));

		Assert.assertNotNull(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID));
	}

	@Test
	public void testFindCustomerByGigyaUidWhenCustomerDoesntExists() {
		Mockito.lenient().when(gigyaUserGenericDao.find(Mockito.anyMap())).thenReturn(null);

		Assert.assertNull(gigyaLoginService.findCustomerByGigyaUid(SAMPLE_UID));
	}

	@Test
	public void testFetchGigyaInfoWhenGigyaUserMgmtModeIsRaas() throws GigyaApiException {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap()))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getMode()).thenReturn(GigyaUserManagementMode.RAAS);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.lenient().when(gsResponse.getData()).thenReturn(gsObject);
		Mockito.lenient().when(gsObject.toJsonString())
				.thenReturn("{\"UID\": \"123\", \"profile\" : { \"UID\": \"123\"}}");

		final GigyaUserObject userObject = gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID);

		Assert.assertNotNull(userObject);
		Assert.assertEquals("123", userObject.getUID());
	}

	@Test
	public void testFetchGigyaInfoWhenGigyaUserMgmtModeIsNotRaas() throws GigyaApiException {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap()))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getMode()).thenReturn(null);

		Assert.assertNull(gigyaLoginService.fetchGigyaInfo(gigyaConfig, SAMPLE_UID));
	}

	@Test
	public void testNotifyGigyaOfLogout() throws GigyaApiException {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap()))
				.thenReturn(Collections.singletonList(gigyaConfig));

		gigyaLoginService.notifyGigyaOfLogout(gigyaConfig, SAMPLE_UID);

		final LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("UID", SAMPLE_UID);

		Mockito.verify(gigyaService).callRawGigyaApiWithConfig(Mockito.refEq("accounts.logout"), Mockito.refEq(params),
				Mockito.refEq(gigyaConfig), Mockito.eq(2), Mockito.eq(1));
	}

	@Test
	public void testNotifyGigyaOfLogoutWhenConfigIsNull() throws GigyaApiException {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap())).thenReturn(null);

		gigyaLoginService.notifyGigyaOfLogout(null, SAMPLE_UID);

		Mockito.verifyNoMoreInteractions(gigyaService);
	}

	@Test
	public void testSendUserToGigyaWhenUserIsNotGigyaUser() throws GigyaApiException {
		Assert.assertFalse(gigyaLoginService.sendUserToGigya(Mockito.mock(EmployeeModel.class)));
	}

	@Test
	public void testSendUserToGigyaSuccessfully() throws GigyaApiException {
		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.EMPTY_LIST);
		Mockito.lenient()
				.when(gigyaConfigGenericDao
						.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.lenient().when(
				gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.lenient().when(gsResponse.getErrorCode()).thenReturn(1);

		Assert.assertFalse(gigyaLoginService.sendUserToGigya(gigyaUser));
	}

	@Test
	public void testSendUserToGigyaUnSuccessfully() throws GigyaApiException {
		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.EMPTY_LIST);
		Mockito.lenient()
				.when(gigyaConfigGenericDao
						.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.lenient().when(
				gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.lenient().when(gsResponse.getErrorCode()).thenReturn(0);

		Assert.assertTrue(gigyaLoginService.sendUserToGigya(gigyaUser));
	}

	@Test
	public void testSendUserToGigyaSuccessfullyWithFieldMappings() throws GigyaApiException {
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));
		Mockito.lenient().when(fieldMapping.isCustom()).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(fieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.lenient().when(fieldMapping.getGigyaConfig()).thenReturn(gigyaConfig);

		Mockito.lenient()
				.when(gigyaConfigGenericDao
						.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));

		Mockito.lenient().when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.lenient().when(
				gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.lenient().when(gsResponse.getErrorCode()).thenReturn(0);

		Assert.assertTrue(gigyaLoginService.sendUserToGigya(gigyaUser));
	}

	@Test
	public void testSendUserToGigyaUnSuccessfullyWithFieldMappings() throws GigyaApiException {
		final GigyaFieldMappingModel fieldMapping = Mockito.mock(GigyaFieldMappingModel.class);
		Mockito.lenient().when(gigyaFieldMappingGenericDao.find()).thenReturn(Collections.singletonList(fieldMapping));
		Mockito.lenient().when(fieldMapping.isCustom()).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(fieldMapping.getSyncDirection()).thenReturn(GigyaSyncDirection.H2G);
		Mockito.lenient().when(fieldMapping.getGigyaConfig()).thenReturn(gigyaConfig);

		Mockito.lenient()
				.when(gigyaConfigGenericDao
						.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, gigyaUser.getGyApiKey())))
				.thenReturn(Collections.singletonList(gigyaConfig));

		Mockito.lenient().when(gigyaUserConverter.convert(Mockito.any(), Mockito.any())).thenReturn(gsObject);
		Mockito.lenient().when(
				gigyaService.callRawGigyaApiWithConfigAndObject("accounts.setAccountInfo", gsObject, gigyaConfig, 2, 1))
				.thenReturn(gsResponse);
		Mockito.lenient().when(gsResponse.getErrorCode()).thenReturn(1);

		Assert.assertFalse(gigyaLoginService.sendUserToGigya(gigyaUser));
	}

	void stubGigyaConfigWithSiteSecretUserSecretNull() {
		Mockito.lenient().when(gigyaConfigGenericDao.find(Mockito.anyMap()))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.lenient().when(gigyaConfig.getGigyaSiteSecret()).thenReturn(null);
		Mockito.lenient().when(gigyaConfig.getGigyaUserSecret()).thenReturn(null);
	}
}