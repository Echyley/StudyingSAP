/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.login.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.io.InvalidClassException;
import java.util.Collections;
import java.util.HashMap;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSLogger;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyab2bservices.auth.impl.GigyaAssetsAuthService;
import de.hybris.platform.gigya.gigyafacades.consent.GigyaConsentFacade;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;



@IntegrationTest
public class DefaultGigyaB2BLoginFacadeIntegrationTest extends ServicelayerTest {

	// injecting class object here
	@Resource
	@InjectMocks
	private DefaultGigyaB2BLoginFacade gigyaLoginFacade;

	@Resource
	@InjectMocks
	private GigyaLoginService gigyaLoginService;
	
	@Resource
	@InjectMocks
	private GigyaAssetsAuthService gigyaAssetsAuthService;

	@Resource
	private ModelService modelService;

	@Resource(name = "gigyaUserGenericDao")
	private GenericDao<CustomerModel> gigyaUserGenericDao;

	@Mock
	private GigyaService gigyaService;
	
	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private GSObject gsObject;
	
	@Mock
	private GigyaUserObject gigyaUserObject;
	
	@Mock
	private GigyaConsentFacade gigyaConsentFacade;
	
	private GigyaJsOnLoginInfo jsInfo;

	private CMSSiteModel cmsSite;
	
	private GSObject gsObjectModel;
	
	private GSObject gsObjectData;
	
	private GSObject updatedGsObjectData;
	

	@Before
	public void setUp() throws ImpExException, Exception{
		ServicelayerTest.createCoreData();
		
		cmsSite = GigyaB2BTestUtils.createCMSSiteModel();
		
		importCsv("/gigyab2bfacades/testGigyaB2BLoginData.impex", "utf-8");
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testGigyaLoginWithRegisterUser()
			throws InvalidClassException, GSKeyNotFoundException, NullPointerException, DuplicateUidException {
		createDummyB2BUser1();

		GSResponse gsResponseModel = new GSResponse(GigyaB2BTestUtils.GS_RESPONSE, gsObjectData, 1, new GSLogger());
		doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfig(anyString(), any(), any(), anyInt(),
				anyInt());
		doReturn(gsObject).when(gsResponse).getData();
		doReturn(new HashMap<String, Object>()).when(gigyaUserObject).getPreferences();
		doReturn(GigyaB2BTestUtils.STATUS_CODE_200).when(gsObject).getInt(GigyaB2BTestUtils.GS_STATUS_CODE);
		doReturn(GigyaB2BTestUtils.ZERO).when(gsObject).getInt(GigyaB2BTestUtils.GS_ERROR_CODE);
		doReturn(gsResponseModel).when(gigyaService).callRawGigyaApiWithConfigAndObject(anyString(), any(), any(),
				anyInt(), anyInt());
		doNothing().when(gigyaConsentFacade).synchronizeConsents(any(), any());
		
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(cmsSite);
		assertNotNull(gigyaLoginService);
		assertNotNull(gigyaAssetsAuthService);
		assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig()));
		B2BCustomerModel customer = (B2BCustomerModel) gigyaUserGenericDao
				.find(Collections.singletonMap(B2BCustomerModel.UID, GigyaB2BTestUtils.DUMMY_EMAIL1)).get(0);
		assertNotNull(customer);
		StringBuilder fullName = new StringBuilder(GigyaB2BTestUtils.DUMMY_FIRSTNAME1).append(" ").append(GigyaB2BTestUtils.DUMMY_LASTNAME1);
		assertEquals(fullName.toString(), customer.getName());
		getModelService().remove(customer);
	}	
	
	@Test
	public void testGigyaLoginWithUpdateCustomer()
			throws InvalidClassException, GSKeyNotFoundException, NullPointerException {	
		
		createDummyB2BUser2();
		GSResponse gsResponseModel = new GSResponse(GigyaB2BTestUtils.GS_RESPONSE, gsObjectData, 1, new GSLogger());
		
		updateDummyB2BUser2();
		
		GSResponse updatedGsResponse = new GSResponse(GigyaB2BTestUtils.GS_RESPONSE, updatedGsObjectData, 1, new GSLogger());
	
		doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfig(anyString(), any(), any(), anyInt(),
				anyInt());
		doReturn(gsObject).when(gsResponse).getData();
		doReturn(new HashMap<String, Object>()).when(gigyaUserObject).getPreferences();
		doReturn(GigyaB2BTestUtils.STATUS_CODE_200).when(gsObject).getInt(GigyaB2BTestUtils.GS_STATUS_CODE);
		doReturn(GigyaB2BTestUtils.ZERO).when(gsObject).getInt(GigyaB2BTestUtils.GS_ERROR_CODE);
		doNothing().when(gigyaConsentFacade).synchronizeConsents(any(), any());
		Mockito.when(gigyaService.callRawGigyaApiWithConfigAndObject(anyString(), any(), any(), anyInt(),
				anyInt())).thenReturn(gsResponseModel, updatedGsResponse);
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(cmsSite);
		//Create B2B User
		gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig());
		//Update B2B User
		assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig()));
		B2BCustomerModel customer = (B2BCustomerModel) gigyaUserGenericDao
				.find(Collections.singletonMap(B2BCustomerModel.UID, GigyaB2BTestUtils.DUMMY_EMAIL2)).get(0);
		assertNotNull(customer);
		StringBuilder fullName = new StringBuilder(GigyaB2BTestUtils.UPDATED_FIRSTNAME2).append(" ").append(GigyaB2BTestUtils.DUMMY_LASTNAME2);
		assertEquals(fullName.toString(), customer.getName());
		getModelService().remove(customer);

	}
	
	
	protected ModelService getModelService()
	{
		return modelService;
	}

	protected void createDummyB2BUser1() {
		jsInfo = GigyaB2BTestUtils.createJSInfo();
		jsInfo.setUID(GigyaB2BTestUtils.DUMMY_EMAIL1);

		gsObjectModel = new GSObject();
		gsObjectData = new GSObject();	
		
		gsObjectModel.put(GigyaB2BTestUtils.EMAIL, GigyaB2BTestUtils.DUMMY_EMAIL1);
		gsObjectModel.put(GigyaB2BTestUtils.FIRSTNAME, GigyaB2BTestUtils.DUMMY_FIRSTNAME1);
		gsObjectModel.put(GigyaB2BTestUtils.LASTNAME, GigyaB2BTestUtils.DUMMY_LASTNAME1);
		
		gsObjectData.put(GigyaB2BTestUtils.PROFILE, gsObjectModel);
		gsObjectData.put(GigyaB2BTestUtils.GROUPS, GigyaB2BTestUtils.createGroupData());
		gsObjectData.put(GigyaB2BTestUtils.UID, GigyaB2BTestUtils.DUMMY_EMAIL1);
		gsObjectData.put(GigyaB2BTestUtils.PREFERENCES, new GSObject());
	}
	
	protected void createDummyB2BUser2() {
		jsInfo = GigyaB2BTestUtils.createJSInfo();
		jsInfo.setUID(GigyaB2BTestUtils.DUMMY_UID);
		
		gsObjectModel = new GSObject();
		gsObjectData = new GSObject();

		gsObjectModel.put(GigyaB2BTestUtils.EMAIL, GigyaB2BTestUtils.DUMMY_EMAIL2);
		gsObjectModel.put(GigyaB2BTestUtils.FIRSTNAME, GigyaB2BTestUtils.DUMMY_FIRSTNAME2);
		gsObjectModel.put(GigyaB2BTestUtils.LASTNAME, GigyaB2BTestUtils.DUMMY_LASTNAME2);
		
		gsObjectData.put(GigyaB2BTestUtils.PROFILE, gsObjectModel);
		gsObjectData.put(GigyaB2BTestUtils.GROUPS, GigyaB2BTestUtils.createGroupData());
		gsObjectData.put(GigyaB2BTestUtils.UID, GigyaB2BTestUtils.DUMMY_UID);
		gsObjectData.put(GigyaB2BTestUtils.PREFERENCES, new GSObject());
		
	}

	protected void updateDummyB2BUser2() {
		jsInfo = GigyaB2BTestUtils.createJSInfo();
		jsInfo.setUID(GigyaB2BTestUtils.DUMMY_UID);	
		
		GSObject updatedGsObject = new GSObject();
		updatedGsObjectData = new GSObject();
		
		updatedGsObject.put(GigyaB2BTestUtils.EMAIL, GigyaB2BTestUtils.DUMMY_EMAIL2);
		updatedGsObject.put(GigyaB2BTestUtils.FIRSTNAME, GigyaB2BTestUtils.UPDATED_FIRSTNAME2);
		updatedGsObject.put(GigyaB2BTestUtils.LASTNAME, GigyaB2BTestUtils.DUMMY_LASTNAME2);
		updatedGsObjectData.put(GigyaB2BTestUtils.GROUPS,  GigyaB2BTestUtils.createGroupData());
		updatedGsObjectData.put(GigyaB2BTestUtils.PROFILE, updatedGsObject);
		updatedGsObjectData.put(GigyaB2BTestUtils.UID, GigyaB2BTestUtils.DUMMY_UID);
		updatedGsObjectData.put(GigyaB2BTestUtils.PREFERENCES, new GSObject());
		
	}
	
}
