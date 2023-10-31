/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.ChangeUIDEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.log4j.Logger;

import com.hybris.yprofile.common.Utils;

public class ChangeUIDEventListener extends AbstractSiteEventListener<ChangeUIDEvent> {
    private static final Logger LOG = Logger.getLogger(ChangeUIDEventListener.class);

    private ProfileTransactionService profileTransactionService;
    private CommerceConsentService commerceConsentService;

    @Override
    protected void onSiteEvent(ChangeUIDEvent event) {
        try {
            final String consentReference = Utils.getActiveConsentReferenceFromEvent(event, getCommerceConsentService());
            this.getProfileTransactionService().sendUidChangedEvent(event, consentReference);
        } catch (Exception e) {
            LOG.error("Error sending Change UID event: " + e.getMessage());
            LOG.debug("Error sending Change UID event: ", e);
        }
    }

    @Override
    protected boolean shouldHandleEvent(final ChangeUIDEvent event) {
        return (eventContainsCustomer(event)
                && eventContainsUid(event)
                && eventContainsOriginalUid(event)
                && eventContainsCustomerConsentReference(event));
    }
    
    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    private static boolean eventContainsCustomer(ChangeUIDEvent event) {
        final CustomerModel customer = event.getCustomer();

        if (customer == null){
            LOG.warn("Parameter event.customer can not be null");
        }
        return customer != null;
    }

    private static boolean eventContainsUid(ChangeUIDEvent event) {
        final String uid = getUidFromEvent(event);
        if (uid == null){
            LOG.warn("Parameter event.customer.uid can not be null");
        }
        return uid != null;
    }

    private static boolean eventContainsOriginalUid(ChangeUIDEvent event) {
        final String originalUid = getOriginalUidFromEvent(event);
        if (originalUid == null){
            LOG.warn("Parameter event.customer.originalUid can not be null");
        }
        return originalUid != null;
    }

    private boolean eventContainsCustomerConsentReference(ChangeUIDEvent event) {
        final String consentReference = Utils.getActiveConsentReferenceFromEvent(event, getCommerceConsentService());
        if (consentReference == null || consentReference.isEmpty()){
            LOG.warn("Parameter consentModel.consentReference can not be null");
        }
        return consentReference != null && !consentReference.isEmpty();
    }

    private static String getUidFromEvent(ChangeUIDEvent event) {
        return event.getCustomer().getUid();
    }

    private static String getOriginalUidFromEvent(ChangeUIDEvent event) {
        return event.getCustomer().getOriginalUid();
    }

    private ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    public CommerceConsentService getCommerceConsentService() {
        return commerceConsentService;
    }

    public void setCommerceConsentService(CommerceConsentService commerceConsentService) {
        this.commerceConsentService = commerceConsentService;
    }
}
