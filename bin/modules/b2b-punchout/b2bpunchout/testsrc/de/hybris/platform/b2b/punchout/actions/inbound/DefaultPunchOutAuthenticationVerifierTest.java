/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutAuthenticationVerifierTest
{
	@Mock
	private PunchOutCredentialService credentialService;

	@InjectMocks
	private DefaultPunchOutAuthenticationVerifier punchoutAuthenticationVerifier;

	private CXML requestXML;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
	}

	@Test
	public void shouldValidateHeaderOnCreation() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/wrongPunchoutSetupRequest.xml");

		assertThatThrownBy(() -> punchoutAuthenticationVerifier.verify(requestXML))
				.isInstanceOf(PunchOutException.class);
	}

	@Test
	public void shouldThrowExceptionSinceWrongCredentials()
	{
		when(credentialService.getCustomerForCredential(any())).thenReturn(null);

		assertThatThrownBy(() -> punchoutAuthenticationVerifier.verify(requestXML))
				.isInstanceOf(PunchOutException.class)
				.hasFieldOrPropertyWithValue("errorCode", "401");
	}
}
