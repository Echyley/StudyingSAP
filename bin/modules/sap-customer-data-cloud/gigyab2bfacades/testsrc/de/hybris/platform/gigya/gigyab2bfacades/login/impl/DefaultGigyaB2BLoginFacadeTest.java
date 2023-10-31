/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.login.impl;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSResponse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyab2bservices.auth.impl.DefaultGigyaAuthService;
import de.hybris.platform.gigya.gigyab2bservices.auth.impl.GigyaAssetsAuthService;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaGroupsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaOrganisationData;
import de.hybris.platform.gigya.gigyafacades.consent.GigyaConsentFacade;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

/**
 * Test class for DefaultGigyaB2BLoginFacade
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaB2BLoginFacadeTest {

	@InjectMocks
	private final DefaultGigyaB2BLoginFacade gigyaLoginFacade = new DefaultGigyaB2BLoginFacade() {

		@Override
		protected GigyaGroupsData getGroupsData(final GSResponse gsResponse) {
			return groups;
		}

	};

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private DefaultGigyaAuthService gigyaAuthService;
	
	@Mock
	private GigyaAssetsAuthService gigyaGetAssetsAuthService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CMSSiteModel cmsSiteModel;

	@Mock
	private ModelService sapB2BModelService;

	@Mock
	private GigyaLoginService gigyaLoginService;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private TaskService taskService;

	@Mock
	private GigyaConsentFacade gigyaConsentFacade;

	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GigyaUserObject gigyaUserObject;

	@Mock
	private UserModel userModel;

	@Mock
	private TaskModel task;

	@Mock
	private CustomerModel customer;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	private B2BCustomerModel b2bCustomer;

	@Mock
	private GigyaGroupsData groups;

	@Mock
	private GigyaOrganisationData firstOrg;

	@Mock
	private GigyaOrganisationData secondOrg;

	@Mock
	private B2BUnitModel b2bUnit;

	@Mock
	private GSResponse gsResponse;

	@Test
	public void testCreateNewCustomerWhenEmailIsMissing() throws DuplicateUidException {
		Mockito.lenient().when(gigyaUserObject.getEmail()).thenReturn("");
		Mockito.lenient().when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID))
				.thenReturn(gigyaUserObject);
		Mockito.lenient().when(modelService.create(CustomerModel.class)).thenReturn(customer);
		Mockito.lenient().when(modelService.create(TaskModel.class)).thenReturn(task);

		Assert.assertThrows(GigyaApiException.class, () -> gigyaLoginFacade.createNewCustomer(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID));
	}

	@Test
	public void testCreateNewCustomerWhenGigyaConfigExists() throws DuplicateUidException {
		Mockito.lenient().when(gigyaUserObject.getEmail()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);
		Mockito.lenient().when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID))
				.thenReturn(gigyaUserObject);
		Mockito.lenient().when(modelService.create(CustomerModel.class)).thenReturn(customer);
		Mockito.lenient().when(modelService.create(TaskModel.class)).thenReturn(task);

		Assert.assertEquals(customer, gigyaLoginFacade.createNewCustomer(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID));
		Mockito.verify(customerAccountService).register(customer, null);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testCreateNewCustomerWithGroupsWithOrgAssociation() throws DuplicateUidException {
		Mockito.lenient().when(gigyaUserObject.getEmail()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);
		Mockito.lenient().when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID))
				.thenReturn(gigyaUserObject);
		Mockito.lenient().when(modelService.create(B2BCustomerModel.class)).thenReturn(b2bCustomer);
		
		Mockito.lenient().when(gigyaUserObject.getGroups()).thenReturn(groups);
		setupMockConditionsForB2BUser();
		Mockito.lenient().when(b2bUnitService.getUnitForUid(Mockito.anyString())).thenReturn(b2bUnit);
		
		Assert.assertEquals(b2bCustomer, gigyaLoginFacade.createNewCustomer(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID));
		verifyB2BFacadeLogic();
		Mockito.verify(sapB2BModelService).save(b2bCustomer);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testCreateNewCustomerWithGroupsWithOrgCreationAndAssociation() throws DuplicateUidException {
		Mockito.lenient().when(gigyaUserObject.getEmail()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);
		Mockito.lenient().when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID))
				.thenReturn(gigyaUserObject);
		Mockito.lenient().when(modelService.create(B2BCustomerModel.class)).thenReturn(b2bCustomer);
		Mockito.lenient().when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
		
		Mockito.lenient().when(gigyaUserObject.getGroups()).thenReturn(groups);
		setupMockConditionsForB2BUser();
		Mockito.lenient().when(b2bUnitService.getUnitForUid(Mockito.anyString())).thenReturn(null);
		Mockito.lenient().when(gigyaConfig.isCreateOrganizationOnLogin()).thenReturn(Boolean.TRUE);
		
		
		Assert.assertEquals(b2bCustomer, gigyaLoginFacade.createNewCustomer(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID));
		verifyB2BFacadeLogic();
		Mockito.verify(sapB2BModelService).save(b2bCustomer);
	}

	@Test
	public void testUpdateUserWithoutGroups() throws GSKeyNotFoundException, GigyaApiException {
		Mockito.lenient().when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.lenient().when(gsResponse.hasData()).thenReturn(Boolean.FALSE);

		gigyaLoginFacade.updateUser(gigyaConfig, customer);

		Mockito.verify(modelService).save(customer);
		Mockito.verify(taskService).scheduleTask(task);
	}

	@Test
	public void testUpdateUserWithGroups() throws GSKeyNotFoundException, GigyaApiException {
		Mockito.lenient().when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
		Mockito.doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(),
				Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.lenient().when(gsResponse.hasData()).thenReturn(Boolean.FALSE);
		setupMockConditionsForB2BUser();
		Mockito.lenient().when(b2bUnitService.getUnitForUid(Mockito.anyString())).thenReturn(null);
		Mockito.lenient().when(gigyaConfig.isCreateOrganizationOnLogin()).thenReturn(Boolean.TRUE);

		gigyaLoginFacade.updateUser(gigyaConfig, b2bCustomer);

		Mockito.verify(gigyaGetAssetsAuthService).removeAuthorisationsOfCustomer(b2bCustomer);
		verifyB2BFacadeLogic();
		Mockito.verify(modelService).save(b2bCustomer);
	}
	
	private void setupMockConditionsForB2BUser() {
		Mockito.lenient().when(modelService.create(TaskModel.class)).thenReturn(task);
		Mockito.lenient().when(groups.getOrganizations()).thenReturn(Arrays.asList(firstOrg, secondOrg));
		Mockito.lenient().when(gigyaConfig.getGigyaApiKey()).thenReturn(GigyaB2BTestUtils.SAMPLE_API_KEY);
		Mockito.lenient().when(firstOrg.getOrgId()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);
		Mockito.lenient().when(secondOrg.getOrgId()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);		
		Mockito.lenient().when(firstOrg.getBpid()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);
		Mockito.lenient().when(secondOrg.getBpid()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);
		
		Mockito.lenient().when(gigyaConfig.getAuthorizationUrl()).thenReturn("");
		Mockito.lenient().when(gigyaConfig.getGigyaUserKey()).thenReturn(GigyaB2BTestUtils.SAMPLE_GIGYA_USER_KEY);
		Mockito.lenient().when(gigyaConfig.getGigyaUserSecret()).thenReturn(GigyaB2BTestUtils.SAMPLE_GIGYA_USER_SECRET);
		
	}
	
	private void verifyB2BFacadeLogic() {
		Mockito.verify(b2bUnitService, Mockito.times(2)).addMember(b2bUnit, b2bCustomer);
		Mockito.verify(modelService, Mockito.times(2)).save(b2bUnit);
		Mockito.verify(b2bCustomer).setDefaultB2BUnit(b2bUnit);
		Mockito.verifyNoInteractions(gigyaAuthService);
		Mockito.verify(gigyaGetAssetsAuthService).assignAuthorisationsToCustomer(b2bCustomer);
		Mockito.verify(taskService).scheduleTask(task);
	}
}
