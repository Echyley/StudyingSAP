/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.task.runner;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookEvent;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookEventData;
import de.hybris.platform.gigya.gigyab2bfacades.data.GigyaWebHookRequest;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.gigya.gigyab2bfacades.login.impl.DefaultGigyaB2BLoginFacade;
import de.hybris.platform.gigya.gigyab2bfacades.login.impl.GigyaB2BTestUtils;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyafacades.task.runner.GigyaToHybrisUserUpdateTaskRunner;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSKeyNotFoundException;

/**
 * Test class for GigyaWebHookEventsTaskRunner
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaWebHookEventsTaskRunnerTest {
	
	@InjectMocks
	private final GigyaWebHookEventsTaskRunner taskRunner = new GigyaWebHookEventsTaskRunner();

	@Mock
	private TaskModel taskModel;

	@Mock
	private TaskService taskService;

	@Mock
	private ModelService modelService;
	
	@Mock
	private ModelService sapB2BModelService;

	@Mock
	private GigyaService gigyaService;
	
	@Mock
	private DefaultGigyaB2BLoginFacade gigyaLoginFacade;
	
	@Mock
	private GigyaLoginService gigyaLoginService;
	
	@Mock
	private BaseSiteService baseSiteService; 
	
	@Mock
	private CMSSiteModel cmsSiteModel;
	
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
	private CustomerAccountService customerAccountService;
	
	@Mock
	private GSResponse gsResponse;
	

	private GigyaWebHookRequest gigyaWebhookRequest;


	@Test
	public void testGigyaWebHookEventsTaskWhenNoUserIdIsPresent()
	{
		
		setupMockConditions(GigyaB2BTestUtils.ACCOUNT_CREATED_WEBHOOK_EVENT);
	
		gigyaWebhookRequest.setEvents(new ArrayList<>()); //Make events empty
		
		
		Mockito.lenient().when(taskModel.getContext()).thenReturn(gigyaWebhookRequest);
		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaService);
		Mockito.verifyZeroInteractions(modelService);
	}

	@Test
	public void testProcessGigyaWebHookEventsWhenGigyaCallVerificationSucceedsAndUserExists() throws GSKeyNotFoundException
	{
		setupMockConditions(GigyaB2BTestUtils.ACCOUNT_UPDATED_WEBHOOK_EVENT);
		Mockito.lenient().when(gigyaLoginService.findCustomerByGigyaUid(GigyaB2BTestUtils.DUMMY_UID)).thenReturn(customer);
		Mockito.lenient().doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfigAndObject(Mockito.anyString(), Mockito.any(),
				Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		Mockito.lenient().when(gsResponse.hasData()).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(customer.getGyUID()).thenReturn(GigyaB2BTestUtils.DUMMY_UID);
		
		
		
		Mockito.lenient().when(taskModel.getContext()).thenReturn(gigyaWebhookRequest);
		taskRunner.run(taskService, taskModel);
		Mockito.verify(gigyaLoginFacade).updateUser(gigyaConfig, customer);
	}

	@Test
	public void testProcessGigyaWebHookEventsWhenGigyaCallVerificationSucceedsAndUserDoesntExists()
			throws DuplicateUidException
	{
		setupMockConditions(GigyaB2BTestUtils.ACCOUNT_CREATED_WEBHOOK_EVENT);
		
		Mockito.lenient().when(gigyaLoginService.findCustomerByGigyaUid(GigyaB2BTestUtils.DUMMY_UID)).thenReturn(null);
		
		
		
		Mockito.lenient().when(taskModel.getContext()).thenReturn(gigyaWebhookRequest);
		taskRunner.run(taskService, taskModel);
		
		Mockito.verify(gigyaLoginFacade).createNewCustomer(gigyaConfig, GigyaB2BTestUtils.DUMMY_UID);
	}

	@Test
	public void testProcessGigyaWebHookEventsWhenGigyaCallVerificationSucceedsAndExceptionOccurs()
			throws DuplicateUidException
	{
		setupMockConditions(GigyaB2BTestUtils.ACCOUNT_CREATED_WEBHOOK_EVENT);
		Mockito.lenient().doThrow(DuplicateUidException.class).when(gigyaLoginFacade).createNewCustomer(gigyaConfig, GigyaB2BTestUtils.DUMMY_UID);
		
		Mockito.lenient().when(taskModel.getContext()).thenReturn(gigyaWebhookRequest);
		taskRunner.run(taskService, taskModel);

		Mockito.verifyZeroInteractions(gigyaService);
		Mockito.verifyZeroInteractions(modelService);
	}
	
	
	
	void setupMockConditions(final String eventType)  {
		Mockito.lenient().when(baseSiteService.getCurrentBaseSite()).thenReturn(cmsSiteModel);
		Mockito.lenient().when(cmsSiteModel.getGigyaConfig()).thenReturn(gigyaConfig);

		Mockito.lenient().when(gigyaUserObject.getEmail()).thenReturn(GigyaB2BTestUtils.SAMPLE_UID);
		Mockito.lenient().when(gigyaLoginService.fetchGigyaInfo(gigyaConfig, GigyaB2BTestUtils.SAMPLE_UID)).thenReturn(gigyaUserObject);
		Mockito.lenient().when(modelService.create(CustomerModel.class)).thenReturn(customer);
		Mockito.lenient().when(modelService.create(TaskModel.class)).thenReturn(task); 
		
		
		gigyaWebhookRequest = GigyaB2BTestUtils.createWebHookRequest(eventType);
	}
	
}
