/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.util.CXmlDateUtil;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.HashMap;

import org.cxml.CXML;
import org.cxml.ProfileResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProfileResponsePopulatorTest
{
	@InjectMocks
	private DefaultProfileResponsePopulator defaultProfileResponsePopulator;

	@Mock
	private CXmlDateUtil cXmlDateUtil;

	@Mock
	private SessionService sessionService;

	private CXML source;

	@Before
	public void setUp() throws FileNotFoundException
	{
		source = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
	}

	@Test
	public void testPopulationWithoutProvidedURLS()
	{
		final ProfileResponse target = new ProfileResponse();
		when(sessionService.getAttribute(PunchOutUtils.SUPPORTED_TRANSACTION_URL_PATHS)).thenReturn(null);

		defaultProfileResponsePopulator.populate(source, target);

		assertThat(target).isNotNull();
		verify(cXmlDateUtil, times(2)).formatDate(any(ZonedDateTime.class));
		verify(sessionService).getAttribute(PunchOutUtils.SUPPORTED_TRANSACTION_URL_PATHS);
		assertThat(target.getTransaction().size()).isZero();
	}

	@Test
	public void testPopulationWithProvidedURLs()
	{
		final ProfileResponse target = new ProfileResponse();
		final var URLs = new HashMap<String, String>();
		URLs.put("key1", "value1");
		when(sessionService.getAttribute(PunchOutUtils.SUPPORTED_TRANSACTION_URL_PATHS)).thenReturn(URLs);

		defaultProfileResponsePopulator.populate(source, target);
		assertThat(target.getTransaction().get(0).getRequestName()).isEqualTo("key1");
		assertThat(target.getTransaction().get(0).getURL().getvalue()).isEqualTo("value1");

		assertThat(target).isNotNull();
		verify(cXmlDateUtil, times(2)).formatDate(any(ZonedDateTime.class));
	}
}
