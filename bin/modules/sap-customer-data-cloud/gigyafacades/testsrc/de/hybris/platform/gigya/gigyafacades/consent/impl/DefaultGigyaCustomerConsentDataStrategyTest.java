/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.consent.impl;

import static de.hybris.platform.commercefacades.constants.CommerceFacadesConstants.USER_CONSENTS;
import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.consent.ConsentFacade;
import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaCustomerConsentDataStrategyTest {
	private static final String WITHDRAWN = "WITHDRAWN";
	private static final String GIVEN = "GIVEN";
	public static final int EXPECTED_CONSENT_MAP_SIZE = 4;

	@Mock
	private SessionService sessionService;

	@Mock
	private ConsentFacade consentFacade;

	@InjectMocks
	private DefaultGigyaCustomerConsentDataStrategy defaultGigyaCustomerConsentDataStrategy = new DefaultGigyaCustomerConsentDataStrategy();

	@Mock
	private BaseSiteService baseSiteService;

	@Test
	public void shouldPopulateCustomerConsentDataInSessionWhenUserLogsInUsingDefaultLoginPage() {
		// given
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
		final String template1 = "templateCode1", template2 = "templateCode2", template3 = "templateCode3",
				template4 = "templateCode4", //
				template5 = "templateCode5", template6 = "templateCode6";
		final List<ConsentTemplateData> consentTemplatesData = new ArrayList<ConsentTemplateData>();

		consentTemplatesData.add(createConsentTemplateData(template1, 1, true, true));
		consentTemplatesData.add(createConsentTemplateData(template2, 1, true, false));
		consentTemplatesData.add(createConsentTemplateData(template3, 1, true, true));
		consentTemplatesData.add(createConsentTemplateData(template4, 1, true, true));
		consentTemplatesData.add(createConsentTemplateData(template5, 1, false, true));
		consentTemplatesData.add(createConsentTemplateData(template6, 1, false, false));

		Mockito.when(consentFacade.getConsentTemplatesWithConsents()).thenReturn(consentTemplatesData);

		// when
		defaultGigyaCustomerConsentDataStrategy.populateCustomerConsentDataInSession();

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(sessionService).setAttribute(Mockito.eq(USER_CONSENTS), captor.capture());
		final Map consentMap = captor.getValue();

		assertEquals(EXPECTED_CONSENT_MAP_SIZE, consentMap.size());
		assertEquals(GIVEN, consentMap.get(template1));
		assertEquals(WITHDRAWN, consentMap.get(template2));
		assertEquals(GIVEN, consentMap.get(template3));
		assertEquals(GIVEN, consentMap.get(template4));
	}

	@Test
	public void shouldPopulateCustomerConsentDataInSessionWhenUserLogsInUsingCDCLoginPage() {
		// given
		BaseSiteModel baseSite = Mockito.mock(BaseSiteModel.class);
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(new GigyaConfigModel());

		final String template1 = "templateCode1", template2 = "templateCode2", template3 = "templateCode3",
				template4 = "templateCode4", //
				template5 = "templateCode5", template6 = "templateCode6";
		final List<ConsentTemplateData> consentTemplatesData = new ArrayList<ConsentTemplateData>();

		consentTemplatesData.add(createConsentTemplateData(template1, 1, true, true));
		consentTemplatesData.add(createConsentTemplateData(template2, 1, true, false));
		consentTemplatesData.add(createConsentTemplateData(template3, 1, true, true));
		consentTemplatesData.add(createConsentTemplateData(template4, 1, true, true));
		consentTemplatesData.add(createConsentTemplateData(template5, 1, false, true));
		consentTemplatesData.add(createConsentTemplateData(template6, 1, false, false));

		Mockito.when(consentFacade.getConsentTemplatesWithConsents()).thenReturn(consentTemplatesData);

		// when
		defaultGigyaCustomerConsentDataStrategy.populateCustomerConsentDataInSession();

		// then
		final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
		verify(sessionService).setAttribute(Mockito.eq(USER_CONSENTS), captor.capture());
		final Map consentMap = captor.getValue();

		assertEquals(6, consentMap.size());
		assertEquals(GIVEN, consentMap.get(template1));
		assertEquals(WITHDRAWN, consentMap.get(template2));
		assertEquals(GIVEN, consentMap.get(template3));
		assertEquals(GIVEN, consentMap.get(template4));
		assertEquals(GIVEN, consentMap.get(template5));
		assertEquals(WITHDRAWN, consentMap.get(template6));
	}

	private ConsentTemplateData createConsentTemplateData(String templateId, int templateVersion, boolean exposed,
			boolean optedIn) {
		ConsentTemplateData consentTemplateData = new ConsentTemplateData();
		consentTemplateData.setId(templateId);
		consentTemplateData.setVersion(templateVersion);
		consentTemplateData.setExposed(exposed);
		ConsentData consentData = new ConsentData();
		if (optedIn) {
			consentData.setConsentGivenDate(new Date());
		} else {
			consentData.setConsentWithdrawnDate(new Date());
		}
		consentTemplateData.setConsentData(consentData);
		return consentTemplateData;
	}

}
