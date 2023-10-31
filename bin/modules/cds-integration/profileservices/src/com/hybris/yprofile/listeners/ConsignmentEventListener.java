/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.listeners;

import com.hybris.yprofile.services.ProfileTransactionService;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.events.ConsignmentProcessingEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.site.BaseSiteService;
import com.hybris.yprofile.common.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Event listener for consignment processing event.
 */
public class ConsignmentEventListener extends AbstractSiteEventListener<ConsignmentProcessingEvent> {

    private static final Logger LOG = Logger.getLogger(ConsignmentEventListener.class);
    private ProfileTransactionService profileTransactionService;
    private BaseSiteService baseSiteService;
    private CommerceConsentService commerceConsentService;

    @Override
    protected void onSiteEvent(ConsignmentProcessingEvent event) {

        try {
            final ConsignmentModel consignment = event.getProcess().getConsignment();

            setCurrentBaseSite(event);
            getProfileTransactionService().sendConsignmentEvent(consignment);
        } catch (Exception e) {
            LOG.error("Error sending Consignment event: " + e.getMessage());
            LOG.debug("Error sending Consignment event: ", e);
        }

    }

    protected void setCurrentBaseSite(ConsignmentProcessingEvent event) {
        getBaseSiteService().setCurrentBaseSite(event.getProcess().getConsignment().getOrder().getSite(), true);
    }

    @Override
    protected boolean shouldHandleEvent(ConsignmentProcessingEvent event) {
        return eventContainsConsignment(event)
                && eventContainsOrder(event)
                && eventContainsSite(event)
                && eventContainsUser(event)
                && eventContainsConsentReference(event);

    }

    private static boolean eventContainsConsignment(ConsignmentProcessingEvent event) {
        final ConsignmentModel consignment = event.getProcess().getConsignment();
        if (consignment == null){
            LOG.warn("Parameter event.process.consignment can not be null");
        }
        return consignment != null;
    }

    private static boolean eventContainsOrder(ConsignmentProcessingEvent event) {
        final AbstractOrderModel order = event.getProcess().getConsignment().getOrder();
        if (order == null){
            LOG.warn("Parameter event.process.consignment.order can not be null");
        }
        return order != null;
    }

    private static boolean eventContainsSite(ConsignmentProcessingEvent event) {
        final AbstractOrderModel order = event.getProcess().getConsignment().getOrder();
        final BaseSiteModel site = order.getSite();
        if (site == null){
            LOG.warn("Parameter event.process.consignment.order.site can not be null");
        }

        return site != null && SiteChannel.B2C.equals(site.getChannel());
    }

    private static boolean eventContainsUser(ConsignmentProcessingEvent event) {
        final AbstractOrderModel order = event.getProcess().getConsignment().getOrder();
        final UserModel user = order.getUser();

        if (user == null){
            LOG.warn("Parameter event.process.consignment.order.user can not be null");
        }
        return user != null;
    }

    private boolean eventContainsConsentReference(ConsignmentProcessingEvent event) {
        final AbstractOrderModel order = event.getProcess().getConsignment().getOrder();

        String consentReference = order.getConsentReference();
        if (StringUtils.isEmpty(consentReference)) {
            setCurrentBaseSite(event);
            consentReference = Utils.getActiveConsentReferenceForCustomerAndBaseSite(getBaseSiteService().getCurrentBaseSite(),
                    (CustomerModel)order.getUser(), getCommerceConsentService());
        }

        if (consentReference == null || consentReference.isEmpty()){
            LOG.warn("Parameter event.process.consignment.order.consentReference can not be null");
        }

        return consentReference != null && !consentReference.isEmpty();
    }

    public ProfileTransactionService getProfileTransactionService() {
        return profileTransactionService;
    }

    public void setProfileTransactionService(ProfileTransactionService profileTransactionService) {
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
