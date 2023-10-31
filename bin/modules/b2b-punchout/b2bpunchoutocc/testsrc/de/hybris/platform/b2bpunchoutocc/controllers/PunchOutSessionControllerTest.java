/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionExpired;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel;
import de.hybris.platform.b2b.punchout.security.PunchOutUserAuthenticationStrategy;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2bpunchoutocc.dto.PunchOutSessionInfoWsDTO;
import de.hybris.platform.b2bpunchoutocc.dto.RequisitionFormDataWsDTO;
import de.hybris.platform.b2bpunchoutocc.exception.PunchOutCartMissingException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cxml.CXML;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.IOException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutSessionControllerTest
{
	@InjectMocks
	@Spy
	private PunchOutSessionController punchOutSessionController;
	@Mock
	private PunchOutSessionService punchoutSessionService;
	@Mock
	private DataMapper dataMapper;
	@Spy
	private PunchOutSession punchOutSession;
	@Mock
	private PunchOutService punchOutService;
	@Mock
	private PunchOutUserAuthenticationStrategy punchOutUserAuthenticationStrategy;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Captor
	private ArgumentCaptor<RequisitionFormDataWsDTO> captor;
	private static final String SID01 = "000001";
	private static final String SID02 = "000002";
	private static final String BROWSERFORMPOST_URL = "http://ariba.acme.com:1616/punchoutexit";
	private static final String fields = "FULL";

	@Before
	public void setUp()
	{
	}

	@Test
	public void getPunchOutSessionInfo()
	{
		final String validCartId = "validCartId";
		final String validCustomerId = "validCustomerId";
		final String validToken = "validToken";
		final String validTokenType = "validTokenType";
		final String validSid = "validSid";
		final String validUid = "validUid";
		when(punchoutSessionService.loadStoredPunchOutSessionModel(validSid)).thenReturn(null);
		try
		{
			punchOutSessionController.getPunchOutSessionInfo(validSid, fields, request, response);
		}
		catch (Exception e)
		{
			Assert.assertTrue("sid is not supplied or not existing", e instanceof NotFoundException);
		}

		final StoredPunchOutSessionModel storedPunchOutSessionModel = Mockito.mock(StoredPunchOutSessionModel.class);
		when(punchoutSessionService.loadStoredPunchOutSessionModel(validSid)).thenReturn(storedPunchOutSessionModel);
		when(storedPunchOutSessionModel.getPunchOutSession()).thenReturn(punchOutSession);
		doThrow(new PunchOutSessionExpired("PunchOut session has expired")).when(punchoutSessionService)
				.checkAndActivatePunchOutSession(punchOutSession);
		try
		{
			punchOutSessionController.getPunchOutSessionInfo(validSid, fields, request, response);
		}
		catch (Exception e)
		{
			Assert.assertTrue("no valid sid found", e instanceof NotFoundException);
		}

		doNothing().when(punchoutSessionService).checkAndActivatePunchOutSession(punchOutSession);
		final B2BCustomerModel b2BCustomerModel = Mockito.mock(B2BCustomerModel.class);
		when(punchoutSessionService.loadB2BCustomerModel(punchOutSession)).thenReturn(b2BCustomerModel);
		when(b2BCustomerModel.getUid()).thenReturn(validUid);
		when(b2BCustomerModel.getCustomerID()).thenReturn(validCustomerId);
		final PunchOutSessionInfoWsDTO punchOutSessionInfoWsDTO = Mockito.mock(PunchOutSessionInfoWsDTO.class);
		when(dataMapper.map(punchOutSession, PunchOutSessionInfoWsDTO.class)).thenReturn(punchOutSessionInfoWsDTO);
		try
		{
			punchOutSessionController.getPunchOutSessionInfo(validSid, fields, request, response);
		}
		catch (Exception e)
		{
			Assert.assertTrue("get empty cart", e instanceof PunchOutCartMissingException);
		}

		final CartModel cartModel = Mockito.mock(CartModel.class);
		when(storedPunchOutSessionModel.getCart()).thenReturn(cartModel);
		when(cartModel.getCode()).thenReturn(validCartId);
		doNothing().when(punchOutUserAuthenticationStrategy).authenticate(validUid, request, response);
		final OAuth2AccessToken punchOutToken = Mockito.mock(OAuth2AccessToken.class);
		when(punchoutSessionService.getPunchOutTokenByUidAndSid(validUid, validSid)).thenReturn(punchOutToken);
		when(punchOutToken.getValue()).thenReturn(validToken);
		when(punchOutToken.getTokenType()).thenReturn(validTokenType);
		punchOutSessionController.getPunchOutSessionInfo(validSid, fields, request, response);
		verify(punchOutSessionInfoWsDTO).setCustomerId(validCustomerId);
		verify(punchOutSessionInfoWsDTO).setCartId(validCartId);
		verify(punchOutSessionInfoWsDTO).setToken(any());
		verify(dataMapper).map(punchOutSessionInfoWsDTO, PunchOutSessionInfoWsDTO.class, fields);
	}

	@Test
	public void testGetRequisitionFormDataWhenDiscardCartEntriesIsFalse() throws IOException
	{
		final CXML requestXMLWithCartInfo = PunchOutUtils
				.unmarshallCXMLFromFile("b2bpunchout/test/punchoutRequisitionResponseWithCartInfo.xml");
		punchOutSession.setBrowserFormPostUrl(BROWSERFORMPOST_URL);

		when(punchoutSessionService.loadPunchOutSession(SID01)).thenReturn(punchOutSession);
		when(punchOutService.processPunchOutOrderMessage()).thenReturn(requestXMLWithCartInfo);

		punchOutSessionController.getRequisitionFormData(false, SID01, "DEFAULT");

		verify(punchoutSessionService).loadPunchOutSession(SID01);
		verify(punchOutService).processPunchOutOrderMessage();
		verify(dataMapper).map(captor.capture(), eq(RequisitionFormDataWsDTO.class), eq("DEFAULT"));
		assertThat(captor.getValue()).isNotNull().hasFieldOrPropertyWithValue("browseFormPostUrl", BROWSERFORMPOST_URL)
				.hasFieldOrPropertyWithValue("orderAsCXML", PunchOutUtils.transformCXMLToBase64(requestXMLWithCartInfo));
	}

	@Test
	public void testGetRequisitionFormDataWhenDiscardCartEntriesIsTrue() throws IOException
	{
		final CXML requestXMLWithOutCartInfo = PunchOutUtils
				.unmarshallCXMLFromFile("b2bpunchout/test/punchoutRequisitionResponseWithOutCartInfo.xml");
		punchOutSession.setBrowserFormPostUrl(BROWSERFORMPOST_URL);

		when(punchoutSessionService.loadPunchOutSession(SID02)).thenReturn(punchOutSession);
		when(punchOutService.processCancelPunchOutOrderMessage()).thenReturn(requestXMLWithOutCartInfo);

		punchOutSessionController.getRequisitionFormData(true, SID02, "DEFAULT");

		verify(punchoutSessionService).loadPunchOutSession(SID02);
		verify(punchOutService).processCancelPunchOutOrderMessage();
		verify(dataMapper).map(captor.capture(), eq(RequisitionFormDataWsDTO.class), eq("DEFAULT"));
		assertThat(captor.getValue()).isNotNull().hasFieldOrPropertyWithValue("browseFormPostUrl", BROWSERFORMPOST_URL)
				.hasFieldOrPropertyWithValue("orderAsCXML", PunchOutUtils.transformCXMLToBase64(requestXMLWithOutCartInfo));
	}
}
