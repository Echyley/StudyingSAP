/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.daos.PunchOutCredentialDao;
import de.hybris.platform.b2b.punchout.model.B2BCustomerPunchOutCredentialMappingModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutCredentialServiceTest
{
	private static final int EXPIRED_DAYS = 180;
	private final static String DUMMY_EMAIL = "dummy@hybris.com";
	private final static String SHARED_SECRET = "VerySecret1234$";

	@InjectMocks
	private DefaultPunchOutCredentialService punchoutCredentialService;

	private CXML requestXML;
	private Credential credential;

	@Mock
	private PunchOutCredentialDao credentialDao;

	@Mock
	private PunchOutCredentialModel credentialModel;

	@Mock
	private B2BCustomerPunchOutCredentialMappingModel mappingModel;

	@Mock
	private B2BCustomerModel customerModel;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
		final Header header = (Header) requestXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		credential = header.getSender().getCredential().iterator().next();

		when(customerModel.getEmail()).thenReturn(DUMMY_EMAIL);
		when(mappingModel.getB2bCustomer()).thenReturn(customerModel);
		when(credentialModel.getB2BCustomerPunchOutCredentialMapping()).thenReturn(mappingModel);
		when(credentialModel.getSharedsecret()).thenReturn(SHARED_SECRET);
		punchoutCredentialService.setCredentialDao(credentialDao);
	}

	@Test
	public void testGetCustomerForCredentialNotExistentCustomer()
	{
		when(this.credentialDao.getPunchOutCredential(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		final B2BCustomerModel customerModel = punchoutCredentialService.getCustomerForCredential(credential);
		assertNull(customerModel);
	}

	@Test
	public void testGetCustomerForCredential()
	{
		when(this.credentialDao.getPunchOutCredential(Mockito.anyString(), Mockito.anyString())).thenReturn(credentialModel);
		punchoutCredentialService.setCredentialDao(credentialDao);
		customerModel = punchoutCredentialService.getCustomerForCredential(credential);
		assertEquals(DUMMY_EMAIL, customerModel.getEmail());
	}

	@Test
	public void testGetPunchOutCredential()
	{
		when(this.credentialDao.getPunchOutCredential(Mockito.anyString(), Mockito.anyString())).thenReturn(credentialModel);
		final PunchOutCredentialModel punchoutCredentialModel = punchoutCredentialService.getPunchOutCredential(
				credential.getDomain(), credential.getIdentity().getContent().get(0).toString());

		assertNotNull(punchoutCredentialModel);
		assertEquals(SHARED_SECRET, punchoutCredentialModel.getSharedsecret());

	}

	@Test
	public void shouldRetrieveTheSharedSecret()
	{
		final Header header = (Header) requestXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		final Credential credential = header.getSender().getCredential().iterator().next();

		final String password = punchoutCredentialService.extractSharedSecret(credential);
		assertEquals(SHARED_SECRET, password);
	}

	@Test
	public void testGetExpiredPunchOutCredentials()
	{
		final List<PunchOutCredentialModel> credentialModels = new ArrayList<>();
		credentialModels.add(new PunchOutCredentialModel());
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInt(anyString(), anyInt())).thenReturn(EXPIRED_DAYS);
		when(credentialDao.getExpiredPunchOutCredentials(anyInt())).thenReturn(credentialModels);
		final List<PunchOutCredentialModel> resultList = punchoutCredentialService.getExpiredPunchOutCredentials();
		assertEquals(credentialModels, resultList);
	}
}
