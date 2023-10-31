/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.site.BaseSiteService;
import com.hybris.yprofile.common.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Event listener for order submit event.
 */
public class NewOrderEventListener extends AbstractSiteEventListener<SubmitOrderEvent>
{
    private static final Logger LOG = Logger.getLogger(NewOrderEventListener.class);
    private ProfileTransactionService profileTransactionService;
    private BaseSiteService baseSiteService;
    private CommerceConsentService commerceConsentService;

    @Override
    protected void onSiteEvent(final SubmitOrderEvent event) {
        try {
            final OrderModel order = event.getOrder();

            setCurrentBaseSite(event);
            getProfileTransactionService().sendSubmitOrderEvent(order);
        } catch (Exception e) {
            LOG.error("Error sending New Order event: " + e.getMessage());
            LOG.debug("Error sending New Order event: ", e);
        }
    }

    protected void setCurrentBaseSite(final SubmitOrderEvent event) {

        if (getBaseSiteService().getCurrentBaseSite() == null) {
            getBaseSiteService().setCurrentBaseSite(event.getOrder().getSite(), true);
        }
    }

    @Override
    protected boolean shouldHandleEvent(SubmitOrderEvent event) {
        return eventContainsOrder(event)
                && eventContainsSite(event)
                && eventContainsUser(event)
                && eventContainsConsentReference(event);

    }

    private static boolean eventContainsOrder(SubmitOrderEvent event) {
        final AbstractOrderModel order = event.getOrder();
        if (order == null){
            LOG.warn("Parameter event.order can not be null");
        }
        return order != null;
    }

    private static boolean eventContainsSite(SubmitOrderEvent event) {
        final AbstractOrderModel order = event.getOrder();
        final BaseSiteModel site = order.getSite();
        if (site == null){
            LOG.warn("Parameter event.order.site can not be null");
        }
        return site != null && SiteChannel.B2C.equals(site.getChannel());
    }

    private static boolean eventContainsUser(SubmitOrderEvent event) {
        final AbstractOrderModel order = event.getOrder();
        final UserModel user = order.getUser();
        if (user == null){
            LOG.warn("Parameter event.order.user can not be null");
        }
        return user != null;
    }

    private boolean eventContainsConsentReference(SubmitOrderEvent event) {
        final AbstractOrderModel order = event.getOrder();

        String consentReference = order.getConsentReference();
        if (StringUtils.isEmpty(consentReference)) {
            setCurrentBaseSite(event);
            consentReference = Utils.getActiveConsentReferenceForCustomerAndBaseSite(getBaseSiteService().getCurrentBaseSite(),
                    (CustomerModel)order.getUser(), getCommerceConsentService());
        }

        if (consentReference == null || consentReference.isEmpty()){
            LOG.warn("Parameter event.order.consentReference can not be null");
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
