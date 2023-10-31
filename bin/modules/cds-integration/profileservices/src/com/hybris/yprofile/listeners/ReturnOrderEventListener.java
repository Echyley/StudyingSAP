/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.site.BaseSiteService;
import com.hybris.yprofile.common.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Event listener for create return event.
 */
public class ReturnOrderEventListener extends AbstractSiteEventListener<CreateReturnEvent> {

    private static final Logger LOG = Logger.getLogger(ReturnOrderEventListener.class);
    private ProfileTransactionService profileTransactionService;
    private BaseSiteService baseSiteService;
    private CommerceConsentService commerceConsentService;

    @Override
    protected void onSiteEvent(CreateReturnEvent event) {

        try {
            final ReturnRequestModel returnRequest = event.getReturnRequest();

            setCurrentBaseSite(event);
            getProfileTransactionService().sendReturnOrderEvent(returnRequest);

        } catch (Exception e) {
            LOG.error("Error sending Return Order event: " + e.getMessage());
            LOG.debug("Error sending Return Order event: ", e);
        }

    }

    protected void setCurrentBaseSite(CreateReturnEvent event) {
        getBaseSiteService().setCurrentBaseSite(event.getReturnRequest().getOrder().getSite(), true);
    }

    @Override
    protected boolean shouldHandleEvent(CreateReturnEvent event) {
        return eventContainsReturnRequest(event)
                && eventContainsOrder(event)
                && eventContainsSite(event)
                && eventContainsUser(event)
                && eventContainsConsentReference(event);

    }

    private static boolean eventContainsReturnRequest(CreateReturnEvent event) {
        final ReturnRequestModel returnRequest = event.getReturnRequest();
        if (returnRequest == null){
            LOG.warn("Parameter event.returnRequest can not be null");
        }
        return returnRequest != null;
    }

    private static boolean eventContainsOrder(CreateReturnEvent event) {
        final AbstractOrderModel order = event.getReturnRequest().getOrder();
        if (order == null){
            LOG.warn("Parameter event.returnRequest.order can not be null");
        }
        return order != null;
    }

    private static boolean eventContainsSite(CreateReturnEvent event) {
        final AbstractOrderModel order = event.getReturnRequest().getOrder();
        final BaseSiteModel site = order.getSite();
        if (site == null){
            LOG.warn("Parameter event.returnRequest.order.site can not be null");
        }
        return site!= null && SiteChannel.B2C.equals(site.getChannel());
    }

    private static boolean eventContainsUser(CreateReturnEvent event) {
        final AbstractOrderModel order = event.getReturnRequest().getOrder();
        final UserModel user = order.getUser();
        if (user == null){
            LOG.warn("Parameter event.returnRequest.order.user can not be null");
        }
        return user != null;
    }

    private boolean eventContainsConsentReference(CreateReturnEvent event) {
        final AbstractOrderModel order = event.getReturnRequest().getOrder();

        String consentReference = order.getConsentReference();
        if (StringUtils.isEmpty(consentReference)) {
            setCurrentBaseSite(event);
            consentReference = Utils.getActiveConsentReferenceForCustomerAndBaseSite(getBaseSiteService().getCurrentBaseSite(),
                    (CustomerModel)order.getUser(), getCommerceConsentService());
        }

        if (consentReference == null || consentReference.isEmpty()) {
            LOG.warn("Parameter event.returnRequest.order.consentReference can not be null");
        }

        return consentReference != null && !consentReference.isEmpty();
    }

    protected ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    public void setProfileTransactionService(final ProfileTransactionService profileTransactionService) {
        this.profileTransactionService = profileTransactionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CommerceConsentService getCommerceConsentService() {
        return commerceConsentService;
    }

    public void setCommerceConsentService(CommerceConsentService commerceConsentService) {
        this.commerceConsentService = commerceConsentService;
    }
}
