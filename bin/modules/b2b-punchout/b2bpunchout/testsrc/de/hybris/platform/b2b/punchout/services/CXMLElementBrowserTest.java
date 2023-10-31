/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.cxml.OrderRequest;
import org.cxml.ProfileResponse;
import org.cxml.PunchOutSetupRequest;
import org.cxml.PunchOutSetupResponse;
import org.cxml.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CXMLElementBrowserTest
{
	private final CXML cXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");;

	@InjectMocks
	private final CXMLElementBrowser cXMLElementBrowser = new CXMLElementBrowser(cXML);


	public CXMLElementBrowserTest() throws FileNotFoundException
	{
	}

	@Before
	public void setUp() throws FileNotFoundException
	{
		final PunchOutSetupResponse punchOutSetupResponse = new PunchOutSetupResponse();
		final Response response = new Response();
		response.getProfileResponseOrPunchOutSetupResponseOrProviderSetupResponseOrGetPendingResponseOrSubscriptionListResponseOrSubscriptionContentResponseOrSupplierListResponseOrSupplierDataResponseOrAuthResponseOrDataResponseOrOrganizationDataResponse()
				.add(punchOutSetupResponse);
		cXML.getHeaderOrMessageOrRequestOrResponse().add(response);
	}

	@Test
	public void testFindHeader()
	{
		assertThat(cXMLElementBrowser.findHeader()).isNotNull();
	}

	@Test
	public void testFindRequestByType()
	{
		assertThat(cXMLElementBrowser.findRequestByType(PunchOutSetupRequest.class)).isNotNull();
		assertThat(cXMLElementBrowser.findRequestByType(OrderRequest.class)).isNull();
	}

	@Test
	public void testFindRequest()
	{
		assertThat(cXMLElementBrowser.findRequest()).isNotNull();
	}

	@Test
	public void testFindResponseByType()
	{
		assertThat(cXMLElementBrowser.findResponseByType(PunchOutSetupResponse.class)).isNotNull();
		assertThat(cXMLElementBrowser.findResponseByType(ProfileResponse.class)).isNull();
	}

	@Test
	public void testFindResponse()
	{
		assertThat(cXMLElementBrowser.findResponse()).isNotNull();
	}

	@Test
	public void hasResponse()
	{
		assertThat(cXMLElementBrowser.hasResponse()).isTrue();
	}
}
