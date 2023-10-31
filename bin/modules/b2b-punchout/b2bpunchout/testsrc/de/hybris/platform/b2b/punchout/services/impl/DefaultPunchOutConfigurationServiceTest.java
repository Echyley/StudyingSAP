/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutConfigurationServiceTest
{

	private static final String PUNCHOUT_SESSION_ID = "1234567890";
	private static final String PUNCHOUT_SESSION_URL_PATH = "cxml/session";
	private static final String DOMAIN = "Domain";
	private static final String IDENTITY = "Identity";
	private static final String PUNCHOUT_CUSTOMER_UID = "punchoutCustomerUid";
	private static final Object PUNCHOUT_AUTH_TOKEN = "0987654321";

	@InjectMocks
	private final DefaultPunchOutConfigurationService defaultPunchOutConfigurationService = new DefaultPunchOutConfigurationService();
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private PunchOutSessionService punchoutSessionService;
	@Mock
	private PunchOutCredentialService punchOutCredentialService;

	@Before
	public void setUp()
	{
		defaultPunchOutConfigurationService.setPunchOutSessionUrlPath(PUNCHOUT_SESSION_URL_PATH);
	}

	@Test
	public void testGetPunchOutLoginUrl()
	{
		final PunchOutSession punchoutSession = new PunchOutSession();
		final List<Organization> initiatedBy = new ArrayList<>();
		final Organization organization = new Organization();
		organization.setDomain(DOMAIN);
		organization.setIdentity(IDENTITY);
		initiatedBy.add(organization);
		punchoutSession.setInitiatedBy(initiatedBy);
		doReturn(PUNCHOUT_SESSION_ID).when(punchoutSessionService).getCurrentPunchOutSessionId();

		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		doReturn(baseSiteModel).when(baseSiteService).getCurrentBaseSite();

		final B2BCustomerModel b2BCustomerModel = new B2BCustomerModel();
		b2BCustomerModel.setUid(PUNCHOUT_CUSTOMER_UID);
		doReturn(PUNCHOUT_SESSION_URL_PATH).when(siteBaseUrlResolutionService)
				.getWebsiteUrlForSite(any(), anyString(), anyBoolean(), anyString(),
						anyString());

		final String loginUrl = defaultPunchOutConfigurationService.getPunchOutLoginUrl();
		assertThat(loginUrl).isNotNull();
	}

	@Test
	public void testGetDefaultCostCenter()
	{
		final Configuration configuration = new DefaultConfigurationBuilder();
		configuration.addProperty("b2bpunchout.checkout.costcenter.default", "defaultCostCenter");
		doReturn(configuration).when(configurationService).getConfiguration();

		assertThat(defaultPunchOutConfigurationService.getDefaultCostCenter()).isEqualTo("defaultCostCenter");
	}

	@Test
	public void testIsOrderVerificationEnabled()
	{
		final Configuration configuration = new DefaultConfigurationBuilder();
		configuration.addProperty("b2bpunchout.order.verification.enabled", "false");
		doReturn(configuration).when(configurationService).getConfiguration();

		assertThat(defaultPunchOutConfigurationService.isOrderVerificationEnabled()).isFalse();
	}
}
