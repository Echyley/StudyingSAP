/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.actions.password;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Unit test for {@link ForgottenPasswordAction}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ForgottenPasswordActionTest
{
	private static final String UID = "TEST_UID";

	@Mock
	protected ModelService modelService;
	@Mock
	protected ProcessParameterHelper processParameterHelper;
	@Mock
	private UserMatchingService userMatchingService;
	@Mock
	private SessionService sessionService;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private ForgottenPasswordProcessModel forgottenPasswordProcessModel;
	@Mock
	private LanguageModel languageModel;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private BusinessProcessParameterModel parameterModel;
	@InjectMocks
	private ForgottenPasswordAction forgottenPasswordAction;

	@Before
	public void setUp() throws Exception
	{
		when(processParameterHelper.containsParameter(anyObject(), anyString())).thenReturn(true);
		when(processParameterHelper.getProcessParameterByName(anyString(), anyCollectionOf(BusinessProcessParameterModel.class)))
				.thenReturn(parameterModel);
		when(parameterModel.getValue()).thenReturn(UID);
		when(forgottenPasswordProcessModel.getLanguage()).thenReturn(languageModel);
		when(forgottenPasswordProcessModel.getCurrency()).thenReturn(currencyModel);
	}

	@Test
	public void testExecuteActionSuccess()
	{
		final AbstractSimpleDecisionAction.Transition transition = forgottenPasswordAction
				.executeAction(forgottenPasswordProcessModel);

		assertEquals("The forgotten password action should be successful", AbstractSimpleDecisionAction.Transition.OK, transition);
		verify(sessionService, times(3)).setAttribute(anyString(), anyObject());
		verify(userMatchingService).getUserByProperty(UID, CustomerModel.class);
		verify(customerAccountService).forgottenPassword(nullable(CustomerModel.class));
	}

	@Test
	public void testExecuteActionNoUid()
	{
		when(processParameterHelper.containsParameter(anyObject(), anyString())).thenReturn(false);

		final AbstractSimpleDecisionAction.Transition transition = forgottenPasswordAction
				.executeAction(forgottenPasswordProcessModel);

		assertEquals("The forgotten password action should fail due to the lack of a 'uid' field",
				AbstractSimpleDecisionAction.Transition.NOK, transition);
	}

	@Test
	public void testExecuteActionNoLanguage()
	{
		when(forgottenPasswordProcessModel.getLanguage()).thenReturn(null);

		final AbstractSimpleDecisionAction.Transition transition = forgottenPasswordAction
				.executeAction(forgottenPasswordProcessModel);

		assertEquals("The forgotten password action should fail due to the lack of a 'language' field",
				AbstractSimpleDecisionAction.Transition.NOK, transition);
	}

	@Test
	public void testExecuteActionNoCurrency()
	{
		when(forgottenPasswordProcessModel.getCurrency()).thenReturn(null);

		final AbstractSimpleDecisionAction.Transition transition = forgottenPasswordAction
				.executeAction(forgottenPasswordProcessModel);

		assertEquals("The forgotten password action should fail due to the lack of a 'currency' field",
				AbstractSimpleDecisionAction.Transition.NOK, transition);
	}
}
