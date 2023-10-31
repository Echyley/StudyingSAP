/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.UpdatedProfileEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.store.BaseStoreModel;
import com.hybris.yprofile.common.Utils;
import org.apache.log4j.Logger;

public class UpdatedProfileEventListener extends AbstractSiteEventListener<UpdatedProfileEvent> {

    private static final Logger LOG = Logger.getLogger(UpdatedProfileEventListener.class);

    private ProfileTransactionService profileTransactionService;
    private CommerceConsentService commerceConsentService;

    @Override
    protected void onSiteEvent(UpdatedProfileEvent event) {

        try {
            final String consentReference = Utils.getActiveConsentReferenceFromEvent(event, getCommerceConsentService());
            final String baseSiteId = event.getBaseStore().getUid();
            this.getProfileTransactionService().sendPersonalDetailsChangedEvent(event.getCustomer(), baseSiteId, consentReference);
        } catch (Exception e) {
            LOG.error("Error sending Updated Address event: " + e.getMessage());
            LOG.debug("Error sending Updated Address event: ", e);
        }
    }

    @Override
    protected boolean shouldHandleEvent(UpdatedProfileEvent event) {
        return eventContainsCustomer(event)
                && eventContainsBaseStore(event)
                && eventContainsCustomerConsentReference(event);
    }

    private static boolean eventContainsCustomer(UpdatedProfileEvent event) {
        final CustomerModel customer = event.getCustomer();
        if (customer == null){
            LOG.warn("Parameter event.customer can not be null");
        }
        return customer != null;
    }

    private static boolean eventContainsBaseStore(UpdatedProfileEvent event) {
        final BaseStoreModel baseStore = event.getBaseStore();
        if (baseStore == null){
            LOG.warn("Parameter event.baseStore can not be null");
        }
        return baseStore != null;
    }

    private boolean eventContainsCustomerConsentReference(UpdatedProfileEvent event) {
        final String consentReference = Utils.getActiveConsentReferenceFromEvent(event, getCommerceConsentService());
        if (consentReference == null || consentReference.isEmpty()){
            LOG.warn("Parameter consentModel.consentReference can not be null");
        }
        return consentReference != null && !consentReference.isEmpty();
    }

    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
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
