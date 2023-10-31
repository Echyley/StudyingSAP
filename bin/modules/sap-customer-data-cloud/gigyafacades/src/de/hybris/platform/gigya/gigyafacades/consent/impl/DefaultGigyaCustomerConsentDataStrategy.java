/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.consent.impl;

import static de.hybris.platform.commercefacades.constants.CommerceFacadesConstants.USER_CONSENTS;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commercefacades.consent.impl.DefaultCustomerConsentDataStrategy;
import de.hybris.platform.site.BaseSiteService;

public class DefaultGigyaCustomerConsentDataStrategy extends DefaultCustomerConsentDataStrategy {

	private BaseSiteService baseSiteService;

	@Override
	public void populateCustomerConsentDataInSession() {
		BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();

		// Populate all the Consent opt-in for sites using CDC as consent management
		// solution
		if (baseSite != null && baseSite.getGigyaConfig() != null) {
			final Stream<ConsentTemplateData> activeConsentData = getConsentFacade().getConsentTemplatesWithConsents()
					.stream();

			final Map<String, String> consentsMap = activeConsentData
					.collect(Collectors.toMap(ConsentTemplateData::getId, this::getConsentState));

			getSessionService().setAttribute(USER_CONSENTS, consentsMap);
		} else {
			super.populateCustomerConsentDataInSession();
		}
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}
}
