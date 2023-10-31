/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.sap.jdsr.writer.DsrIPassport;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.rfc.dao.SAPRFCDestinationDao;
import de.hybris.platform.servicelayer.config.ConfigurationService;


@UnitTest
public class DefaultClientPassportManagerTest
{
	private static final int CONNEECTION_ID = 1952726278;

	private static final String SYSTEM_ID = "WEF";

	private static final String FUNCTION_NAME = "RFC_GET_FUNCTION_INTERFACE";

	@InjectMocks
	private DefaultClientPassportManager defaultClientPassportManager;

	private ConfigurationService configurationService = mock(ConfigurationService.class);
	private Configuration configurationValue = mock(Configuration.class);
	private SAPRFCDestinationDao rfcDestinationDao = mock(SAPRFCDestinationDao.class);

	@Before
	public void setUp()
	{
		defaultClientPassportManager = DefaultClientPassportManager.newClientPassportManager(configurationService,
				rfcDestinationDao);
	}

	@Test
	public void testCallStarted()
	{
		when(configurationService.getConfiguration()).thenReturn(configurationValue);
		when(configurationValue.getString(anyString(),anyString())).thenReturn("0");
		when(configurationValue.getInt(anyString(),anyInt())).thenReturn(0);
		DsrIPassport dsrPassport = defaultClientPassportManager.callStarted(CONNEECTION_ID, SYSTEM_ID, FUNCTION_NAME);

		assertThat(dsrPassport.getUserId()).isEqualTo("");

	}

	@Test
	public void testCallStartedStringValue()
	{
		when(configurationService.getConfiguration()).thenReturn(configurationValue);
		when(configurationValue.getString(anyString(), anyString())).thenReturn("Any_String");

		String traceFlagKey = "sapcorejco.sap.passport.traceFlag";
		when(configurationValue.getString(traceFlagKey, "0x0000")).thenReturn("0x0000");

		when(configurationValue.getInt(anyString(), anyInt())).thenReturn(0);

		List<SAPRFCDestinationModel> rfcDestinations = new ArrayList<>();

		SAPRFCDestinationModel model = mock(SAPRFCDestinationModel.class);

		when(model.getSid()).thenReturn(SYSTEM_ID);
		when(model.getUserid()).thenReturn("TESTUSER");
		rfcDestinations.add(model);

		when(rfcDestinationDao.findRfcDestinations()).thenReturn(rfcDestinations);

		DsrIPassport dsrPassport = defaultClientPassportManager.callStarted(1952726278, SYSTEM_ID, FUNCTION_NAME);

		assertThat(dsrPassport.getUserId()).isEqualTo("TESTUSER");

	}

	@Test
	public void testCallFinished()
	{
		defaultClientPassportManager.callFinished(1952726278, 1231, 12332);

	}

}
