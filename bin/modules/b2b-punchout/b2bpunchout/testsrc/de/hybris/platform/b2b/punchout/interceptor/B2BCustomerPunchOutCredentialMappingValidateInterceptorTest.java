/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.interceptor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.punchout.model.B2BCustomerPunchOutCredentialMappingModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BCustomerPunchOutCredentialMappingValidateInterceptorTest
{
	@InjectMocks
	private B2BCustomerPunchOutCredentialMappingValidateInterceptor validator;

	@Mock
	private InterceptorContext ctx;
	@Mock
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> customerService;
	@Mock
	private PunchOutCredentialService punchOutCredentialService;
	@Mock
	private L10NService l10NService;

	@Test
	public void testInexistentCustomer()
	{
		final B2BCustomerPunchOutCredentialMappingModel model = buildModel(null);

		assertThatThrownBy(() -> validator.onValidate(model, ctx))
			.isInstanceOf(InterceptorException.class);
	}

	@Test
	public void testNoIdentities()
	{
		final Set<PunchOutCredentialModel> emptyIdentities = new HashSet<>();
		final B2BCustomerPunchOutCredentialMappingModel model = buildModel(emptyIdentities);
		model.setCredentials(new HashSet<>());

		assertThatThrownBy(() -> validator.onValidate(model, ctx))
			.isInstanceOf(InterceptorException.class);
	}

	@Test
	public void testCredentialAlreadyExistent()
	{
		final PunchOutCredentialModel credential = spy(new PunchOutCredentialModel());
		final Set<PunchOutCredentialModel> identities = new HashSet<>();
		identities.add(credential);
		final B2BCustomerPunchOutCredentialMappingModel model = buildModel(identities);
		model.getB2bCustomer().setUid(Long.toString(System.nanoTime()));
		final B2BCustomerPunchOutCredentialMappingModel existentModel = buildModel(identities);
		existentModel.getB2bCustomer().setUid(Long.toString(System.nanoTime()));
		credential.setB2BCustomerPunchOutCredentialMapping(existentModel);
		when(customerService.getUserForUID(nullable(String.class))).thenReturn(new B2BCustomerModel());
		when(punchOutCredentialService.getPunchOutCredential(nullable(String.class), nullable(String.class)))
				.thenReturn(credential);

		assertThatThrownBy(() -> validator.onValidate(model, ctx))
			.isInstanceOf(InterceptorException.class);
	}

	private B2BCustomerPunchOutCredentialMappingModel buildModel(final Set<PunchOutCredentialModel> identities)
	{
		final B2BCustomerPunchOutCredentialMappingModel model = spy(new B2BCustomerPunchOutCredentialMappingModel());
		final B2BCustomerModel customer = spy(new B2BCustomerModel());
		model.setB2bCustomer(customer);
		model.setCredentials(identities);
		return model;
	}
}
