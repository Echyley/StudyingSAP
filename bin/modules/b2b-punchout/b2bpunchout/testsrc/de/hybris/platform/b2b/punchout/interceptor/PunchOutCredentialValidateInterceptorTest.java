/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.interceptor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutCredentialValidateInterceptorTest
{
	private static final String DOMAIN = "domain";
	private static final String IDENTITY = "id";

	@Mock
	private InterceptorContext ctx;
	@Mock
	private PunchOutCredentialService punchOutCredentialService;
	@Mock
	private L10NService l10NService;

	@InjectMocks
	private PunchOutCredentialValidateInterceptor validator;

	private PunchOutCredentialModel existingModel;
	private PunchOutCredentialModel newModel;

	@Before
	public void setup()
	{
		existingModel = new PunchOutCredentialModel();
		existingModel.setDomain(DOMAIN);
		existingModel.setIdentity(IDENTITY);

		newModel = new PunchOutCredentialModel();
		newModel.setDomain(DOMAIN);
		newModel.setIdentity(IDENTITY);
	}

	@Test
	public void testCreatingNewCredentials() throws InterceptorException
	{
		//the service will find the other credentials that the one being created
		when(punchOutCredentialService.getPunchOutCredential(DOMAIN, IDENTITY)).thenReturn(existingModel);

		assertThatThrownBy(() -> validator.onValidate(newModel, ctx))
			.isInstanceOf(InterceptorException.class);
	}
}
