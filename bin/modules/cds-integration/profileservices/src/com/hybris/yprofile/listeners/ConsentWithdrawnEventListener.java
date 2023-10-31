/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.consent.services.ConsentService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.ConsentWithdrawnEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.log4j.Logger;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_CONSENT;

/**
 * Event listener for Consent Withdrawn Event.
 */
public class ConsentWithdrawnEventListener extends AbstractSiteEventListener<ConsentWithdrawnEvent> {

    private static final Logger LOG = Logger.getLogger(ConsentWithdrawnEventListener.class);
    private ConsentService consentService;

    @Override
    protected void onSiteEvent(ConsentWithdrawnEvent event) {

        try {

            final ConsentModel consent = event.getConsent();

            LOG.debug("Consent Withdrawn Event Received Successfully!!!!!");
            getConsentService().deleteConsentReferenceInConsentServiceAndInConsentModel(consent, consent.getConsentTemplate().getBaseSite().getUid());
        } catch (Exception e) {
            LOG.error("Error sending Consent Withdrawn event: " + e.getMessage());
            LOG.debug("Error sending Consent Withdrawn event: ", e);
        }

    }


    @Override
    protected boolean shouldHandleEvent(ConsentWithdrawnEvent event) {
        return eventContainsConsent(event)
                && eventContainsConsentTemplate(event)
                && eventContainsSite(event)
                && eventContainsCustomer(event)
                && eventContainsCustomerConsentReference(event)
                && isProfileConsentWithdrawn(event);
    }

    private static boolean eventContainsConsent(ConsentWithdrawnEvent event) {
        final ConsentModel consent = event.getConsent();
        if (consent == null){
            LOG.warn("Parameter event.consent can not be null");
        }
        return consent != null;
    }

    private static boolean eventContainsCustomer(ConsentWithdrawnEvent event) {
        final CustomerModel customer = event.getConsent().getCustomer();
        if (customer == null){
            LOG.warn("Parameter event.consent.customer can not be null");
        }
        return customer != null;
    }

    private static boolean eventContainsConsentTemplate(ConsentWithdrawnEvent event) {
        final ConsentModel consent = event.getConsent();
        final ConsentTemplateModel consentTemplate = consent.getConsentTemplate();
        if (consentTemplate == null){
            LOG.warn("Parameter event.consent.consentTemplate can not be null");
        }
        return consentTemplate!=null;
    }

    private static boolean eventContainsSite(ConsentWithdrawnEvent event) {
        final ConsentModel consent = event.getConsent();
        final BaseSiteModel site = consent.getConsentTemplate().getBaseSite();
        if (site == null){
            LOG.warn("Parameter event.consent.consentTemplate.site can not be null");
        }
        return site != null && SiteChannel.B2C.equals(site.getChannel());
    }

    private static boolean eventContainsCustomerConsentReference(ConsentWithdrawnEvent event) {
        final ConsentModel consentModel = event.getConsent();
        final String consentReference = consentModel.getConsentReference();

        if (consentReference == null || consentReference.isEmpty()){
            LOG.warn("Parameter event.consent.consentReference can not be null");
        }

        return consentReference != null && !consentReference.isEmpty();
    }

    protected static boolean isProfileConsentWithdrawn(ConsentWithdrawnEvent event){

        return event.getConsent().getConsentTemplate().getId().equals(PROFILE_CONSENT);
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }
}
