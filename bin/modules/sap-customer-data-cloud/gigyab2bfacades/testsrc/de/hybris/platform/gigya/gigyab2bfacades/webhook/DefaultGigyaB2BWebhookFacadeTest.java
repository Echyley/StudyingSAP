/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.webhook;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.gigya.socialize.GSResponse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;
import de.hybris.platform.gigya.gigyab2bfacades.login.impl.GigyaB2BTestUtils;
import de.hybris.platform.gigya.gigyab2bfacades.webhook.impl.DefaultGigyaB2BWebhookFacade;
import de.hybris.platform.gigya.gigyab2bservices.auth.GigyaAuthService;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaGroupsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaOrganisationData;
import de.hybris.platform.gigya.gigyafacades.consent.GigyaConsentFacade;
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
public class DefaultGigyaB2BWebhookFacadeTest {
	
	@InjectMocks
	private final DefaultGigyaB2BWebhookFacade gigyaWebhookFacade = new DefaultGigyaB2BWebhookFacade();

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private GigyaAuthService gigyaAuthService;

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
	public void testValidateWebHookJWTTokenWhenGigyaCallVerificationFails() {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(cmsSiteModel);
		Mockito.lenient().when(cmsSiteModel.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient().when(gigyaLoginService.verifyGigyaCallIdTokenExpiryAndSignature(gigyaConfig,
				GigyaB2BTestUtils.SAMPLE_JWT_TOKEN)).thenReturn(Boolean.FALSE);
		Mockito.lenient()
				.when(gigyaLoginService.verifyGigyaIdTokenContainsPayloadHash(GigyaB2BTestUtils.SAMPLE_JWT_TOKEN,
						GigyaB2BTestUtils.SAMPLE_JWT_PAYLOAD_HASH))
				.thenReturn(Boolean.FALSE);
		Assert.assertFalse(gigyaWebhookFacade.validateWebHookJWTToken("", GigyaB2BTestUtils.SAMPLE_BASESITE_ID, ""));
		Assert.assertFalse(gigyaWebhookFacade.validateWebHookJWTToken("", GigyaB2BTestUtils.SAMPLE_BASESITE_ID,
				GigyaB2BTestUtils.SAMPLE_JWT_PAYLOAD_HASH));
		Assert.assertFalse(gigyaWebhookFacade.validateWebHookJWTToken(GigyaB2BTestUtils.SAMPLE_JWT_TOKEN,
				GigyaB2BTestUtils.SAMPLE_BASESITE_ID, GigyaB2BTestUtils.SAMPLE_JWT_PAYLOAD_HASH));
	}

	@Test
	public void testValidateWebHookJWTTokenWhenGigyaCallVerificationIsSuccess() {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(cmsSiteModel);
		Mockito.lenient().when(cmsSiteModel.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.lenient()
				.when(gigyaLoginService.verifyGigyaCallIdTokenExpiryAndSignature(gigyaConfig, GigyaB2BTestUtils.SAMPLE_JWT_TOKEN))
				.thenReturn(Boolean.TRUE);
		Mockito.lenient()
				.when(gigyaLoginService.verifyGigyaIdTokenContainsPayloadHash(GigyaB2BTestUtils.SAMPLE_JWT_TOKEN,
						GigyaB2BTestUtils.SAMPLE_JWT_PAYLOAD_HASH))
				.thenReturn(Boolean.TRUE);
		Assert.assertTrue(gigyaWebhookFacade.validateWebHookJWTToken(GigyaB2BTestUtils.SAMPLE_JWT_TOKEN,
				GigyaB2BTestUtils.SAMPLE_BASESITE_ID, GigyaB2BTestUtils.SAMPLE_JWT_PAYLOAD_HASH));
	}

	@Test
	public void testReceiveGigyaWebHookEvents() {
		Mockito.lenient()
				.when(gigyaLoginService.verifyGigyaCallIdTokenExpiryAndSignature(Mockito.eq(gigyaConfig), Mockito.anyString()))
				.thenReturn(Boolean.TRUE);
		Mockito.lenient()
				.when(gigyaLoginService.verifyGigyaIdTokenContainsPayloadHash(Mockito.anyString(),
						Mockito.anyString()))
				.thenReturn(Boolean.TRUE);
		Mockito.lenient().when(modelService.create(TaskModel.class)).thenReturn(task);
		
		GigyaWebHookRequest gigyaWebhookRequest = GigyaB2BTestUtils.createWebHookRequest(GigyaB2BTestUtils.ACCOUNT_CREATED_WEBHOOK_EVENT);
		gigyaWebhookFacade.receiveGigyaWebHookEvents(gigyaWebhookRequest);
		

		Mockito.verify(task).setContext(gigyaWebhookRequest);
		Mockito.verify(taskService).scheduleTask(task);
	}


}
