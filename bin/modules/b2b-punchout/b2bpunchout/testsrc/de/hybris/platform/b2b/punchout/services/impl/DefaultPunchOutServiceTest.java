/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.actions.inbound.PunchOutInboundProcessor;
import de.hybris.platform.b2b.punchout.actions.outbound.PunchOutOutboundProcessor;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutServiceTest
{
	private static final String TEST_CUSTOMER_UID = "TestCustomerUID";

	@InjectMocks
	private DefaultPunchOutService punchOutService;

	@Mock
	private PunchOutOutboundProcessor punchOutOrderMessageProcessor;
	@Mock
	private PunchOutOutboundProcessor punchOutCancelOrderMessageProcessor;
	@Mock
	private PunchOutInboundProcessor punchOutProfileRequestProcessor;
	@Mock
	private PunchOutInboundProcessor punchOutSetupRequestProcessor;
	@Mock
	private PunchOutInboundProcessor punchOutOrderRequestProcessor;
	@Mock
	private PunchOutCredentialService punchOutCredentialService;
	@Mock
	private CXML request;
	@Mock
	private CXML response;

	@Test
	public void testProcessPunchOutSetUpRequest()
	{
		doReturn(response).when(punchOutSetupRequestProcessor).generatecXML(request);
		assertThat(punchOutService.processPunchOutSetUpRequest(request)).isNotNull();
	}

	@Test
	public void testProcessPunchOutOrderMessage()
	{
		doReturn(response).when(punchOutOrderMessageProcessor).generatecXML();
		assertThat(punchOutService.processPunchOutOrderMessage()).isNotNull();
	}

	@Test
	public void testProcessCancelPunchOutOrderMessage()
	{
		doReturn(response).when(punchOutCancelOrderMessageProcessor).generatecXML();
		assertThat(punchOutService.processCancelPunchOutOrderMessage()).isNotNull();
	}

	@Test
	public void testProcessPurchaseOrderRequest()
	{
		doReturn(response).when(punchOutOrderRequestProcessor).generatecXML(request);
		assertThat(punchOutService.processPurchaseOrderRequest(request)).isNotNull();
	}

	@Test
	public void testRetrieveIdentity() throws FileNotFoundException
	{
		request = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleProfileRequest.xml");
		final B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid(TEST_CUSTOMER_UID);
		doReturn(customer).when(punchOutCredentialService).getCustomerForCredentialNoAuth(any());

		final String response = punchOutService.retrieveIdentity(request);

		assertThat(response).isEqualTo(TEST_CUSTOMER_UID);
	}

	@Test
	public void testProcessProfileRequest()
	{
		doReturn(response).when(punchOutProfileRequestProcessor).generatecXML(request);
		assertThat(punchOutService.processProfileRequest(request)).isNotNull();
	}
}
