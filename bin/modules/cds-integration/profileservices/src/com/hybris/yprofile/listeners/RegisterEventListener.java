/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.impl.DefaultSessionTokenService;
import de.hybris.platform.store.BaseStoreModel;
import com.hybris.yprofile.common.Utils;
import org.apache.log4j.Logger;

public class RegisterEventListener extends AbstractSiteEventListener<RegisterEvent> {

    private static final Logger LOG = Logger.getLogger(RegisterEventListener.class);

    private ProfileTransactionService profileTransactionService;
    private DefaultSessionTokenService defaultSessionTokenService;
    private ProfileConfigurationService profileConfigurationService;
    private ConsentService consentService;
    private CommerceConsentService commerceConsentService;

    @Override
    protected void onSiteEvent(final RegisterEvent event) {

        try {
            final String consentReference = getConsentReference(event);

            if (!isValidConsentReference(consentReference)){
                LOG.debug("Consent reference is null. Could not send event to CDS");
                return;
            }

            final String baseSiteId = event.getBaseStore().getUid();
            final String sessionId = getDefaultSessionTokenService().getOrCreateSessionToken();
            final Boolean profileTagDebugSession = getProfileConfigurationService().isProfileTagDebugEnabledInSession();

            setDebugFlag(event.getCustomer(), profileTagDebugSession);
            getProfileTransactionService().sendUserRegistrationEvent(event.getCustomer(), consentReference, sessionId, baseSiteId);
            LOG.debug("Register Event Sent Successfully!");
        } catch (Exception e) {
            LOG.error("Error sending registration event: " + e.getMessage());
            LOG.debug("Error sending registration event: ", e);
        }
    }

    private void setDebugFlag(final UserModel currentUser, final Boolean profileTagDebugSession) {
        if (Boolean.TRUE.equals(profileTagDebugSession)) {
            currentUser.setProfileTagDebug(profileTagDebugSession);
        }
    }

    private String getConsentReference(RegisterEvent event) {
        final String consent = Utils.getActiveConsentReferenceFromEvent(event, getCommerceConsentService());
        if (consent == null){
            final String consentReferenceFromSession = getConsentService().getConsentReferenceFromSession();
            LOG.debug(String.format("Consent Reference in consent model is null. Returning consent reference %s from session.", consentReferenceFromSession));
            return consentReferenceFromSession;
        }
        return consent;
    }

    private boolean isValidConsentReference(final String consentReference) {
        return consentReference != null && !consentReference.isEmpty();
    }

    @Override
    protected boolean shouldHandleEvent(final RegisterEvent event) {
        return eventContainsCustomer(event) && eventContainsBaseStore(event);
    }

    private static boolean eventContainsCustomer(RegisterEvent event) {
        final CustomerModel customer = event.getCustomer();
        if (customer == null){
            LOG.warn("Parameter event.customer can not be null");
        }
        return customer != null;
    }

    private static boolean eventContainsBaseStore(RegisterEvent event) {
        final BaseStoreModel baseStore = event.getBaseStore();
        if (baseStore == null){
            LOG.warn("Parameter event.baseStore can not be null");
        }
        return baseStore != null;
    }

    
    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    private ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    public DefaultSessionTokenService getDefaultSessionTokenService() {
        return defaultSessionTokenService;
    }

    public void setDefaultSessionTokenService(DefaultSessionTokenService defaultSessionTokenService) {
        this.defaultSessionTokenService = defaultSessionTokenService;
    }

    public ConsentService getConsentService() {
        return this.consentService;
    }

    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public CommerceConsentService getCommerceConsentService() {
        return commerceConsentService;
    }

    public void setCommerceConsentService(CommerceConsentService commerceConsentService) {
        this.commerceConsentService = commerceConsentService;
    }
}
