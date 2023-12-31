/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.services.order;

import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

public class ProfilePlaceOrderMethodHook implements CommercePlaceOrderMethodHook {

    private static final Logger LOG = Logger.getLogger(ProfilePlaceOrderMethodHook.class);

    private ConsentService consentService;
    private ModelService modelService;
    private ProfileConfigurationService profileConfigurationService;

    @Override
    public void afterPlaceOrder(CommerceCheckoutParameter parameter, CommerceOrderResult orderModel) throws InvalidCartException {
        //not needed
    }

    @Override
    public void beforePlaceOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {
        //not needed
    }

    /**
     * Stores the consent reference and the cart id in the order model
     *
     * @param parameter
     *           object containing all the information for checkout
     * @param result
     * @throws InvalidCartException
     */
    @Override
    public void beforeSubmitOrder(CommerceCheckoutParameter parameter, CommerceOrderResult result) throws InvalidCartException {

        try {
            if (!getProfileConfigurationService().isProfileTrackingPaused()) {

                final String consentReferenceId = getConsentService().getConsentReferenceFromSession();

                if (consentReferenceId != null) {
                    final OrderModel order = result.getOrder();
                    final CartModel cart = parameter.getCart();
                    order.setCartIdReference(cart.getCode());
                    order.setConsentReference(consentReferenceId);
                    getModelService().save(order);
                    getModelService().refresh(order);
                }
            }
        } catch (Exception e){
            LOG.error("Error getting consent reference from session", e);
        }
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public ConsentService getConsentService() {
        return consentService;
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
}
