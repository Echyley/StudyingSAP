/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.services.impl;

import com.hybris.charon.RawResponse;
import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.dto.Order;
import com.hybris.yprofile.dto.UID;
import com.hybris.yprofile.dto.User;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.ProfileTransactionService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.commerceservices.event.ChangeUIDEvent;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.hybris.yprofile.consent.services.ConsentService;

import java.util.Optional;


/**
 * Implementation for {@link ProfileTransactionService}. Communication service to send transactions to Profile
 */
public class DefaultProfileTransactionService implements ProfileTransactionService {
    private static final Logger LOG = Logger.getLogger(DefaultProfileTransactionService.class);
    private static final String NULL = "null";
    private static final String ACCOUNT_REGISTRATION_EVENT_TYPE = "profileservices.account.registration.event.type";
    private static final String LOGIN_EVENT_TYPE = "profileservices.login.event.type";
    private static final String SUBMIT_ORDER_EVENT_TYPE = "profileservices.submit.order.event.type";
    private static final String SHIPMENT_ORDER_EVENT_TYPE = "profileservices.shipment.update.event.type";
    private static final String RETURN_ORDER_EVENT_TYPE = "profileservices.return.event.type";

    //slim events
    private static final String UID_CHANGED_EVENT_SCHEMA = "profileservices.uid.changed.schema";
    private static final String ADDRESS_CHANGED_SCHEMA = "profileservices.address.changed.schema";
    private static final String ADDRESS_DELETED_SCHEMA = "profileservices.address.deleted.schema";
    private static final String PERSONAL_DETAILS_CHANGED_SCHEMA = "profileservices.persolan.details.changed.schema";

    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private ProfileConfigurationService profileConfigurationService;

    private Converter<OrderModel, Order> profileOrderEventConverter;

    private Converter<ConsignmentModel, Order> profileConsignmentEventConverter;

    private Converter<ReturnRequestModel, Order> profileReturnEventConverter;

    private Converter<UserModel, User> profileUserEventConverter;

    private Converter<ChangeUIDEvent, UID> profileUIDConverter;

    private ConsentService consentService;

    /**
     * Send order to CDS.
     * @param orderModel the order model
     */
    @Override
    public void sendSubmitOrderEvent(final OrderModel orderModel) {
        final Order order = getProfileOrderEventConverter().convert(orderModel);
        final UserModel userModel = orderModel.getUser();

        this.sendOrder(getConsentReference(orderModel), order, Config.getString(SUBMIT_ORDER_EVENT_TYPE, StringUtils.EMPTY), getDebugFlagValue(userModel));
    }

    /**
     * Send consignment to CDS.
     * @param consignmentModel the consignment model
     */
    @Override
    public void sendConsignmentEvent(final ConsignmentModel consignmentModel) {

        final OrderModel orderModel = (OrderModel) consignmentModel.getOrder();
        final Order order = getProfileConsignmentEventConverter().convert(consignmentModel);
        final UserModel userModel = orderModel.getUser();

        this.sendOrder(getConsentReference(orderModel), order, Config.getString(SHIPMENT_ORDER_EVENT_TYPE, StringUtils.EMPTY), getDebugFlagValue(userModel));
    }

    /**
     * Send return to CDS.
     * @param returnRequestModel the return request
     */
    @Override
    public void sendReturnOrderEvent(final ReturnRequestModel returnRequestModel) {

        final OrderModel orderModel = returnRequestModel.getOrder();

        final Order order = getProfileReturnEventConverter().convert(returnRequestModel);
        final UserModel userModel = orderModel.getUser();

        this.sendOrder(getConsentReference(orderModel), order, Config.getString(RETURN_ORDER_EVENT_TYPE, StringUtils.EMPTY), getDebugFlagValue(userModel));
    }

    protected void sendOrder(final String consentReference, final Order order, final String eventType, final String debugEnabled) {
        if (shouldSendEvent(consentReference, order.getChannelRef())) {
            getClient().sendTransaction(eventType, consentReference, order.getChannelRef(), debugEnabled, order)
                    .subscribe(response -> logSuccess(Optional.ofNullable(response), order, consentReference),
                            error -> logError(error, order, consentReference));
        }
    }

    protected boolean shouldSendEvent(final String consentReference, final String baseSiteId) {
        return !getProfileConfigurationService().isProfileTrackingPaused()
                && getProfileConfigurationService().isConfigurationPresent()
                && isValidConsentReference(consentReference);
    }

    /**
     * Send user registration event to CDS.
     * @param userModel the user model
     * @param consentReferenceId consent reference
     * @param sessionId ec session id
     * @param baseSiteId storefront name like 'electronics'
     */
    @Override
    public void sendUserRegistrationEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String baseSiteId) {
        this.sendUserEvent(userModel, consentReferenceId, sessionId, baseSiteId, Config.getString(ACCOUNT_REGISTRATION_EVENT_TYPE, StringUtils.EMPTY));
    }

    /**
     * Send changed UID event to CDS.
     * @param event the changeUIDEvent. The populator knows how to extract the appropriate information
     * @param consentReferenceId consent reference
     */
    @Override
    public void sendUidChangedEvent(final ChangeUIDEvent event, final String consentReferenceId) {
        final String baseSiteId = event.getBaseStore().getUid();
        if(!this.shouldSendEvent(consentReferenceId, baseSiteId)){
            return;
        }
        final UID uid = getProfileUIDConverter().convert(event);
        final String debugEnabled = getDebugFlagValue(event.getCustomer());

        this.getClient().sendSlimEvent(Config.getString(UID_CHANGED_EVENT_SCHEMA, StringUtils.EMPTY), consentReferenceId, baseSiteId, debugEnabled, uid)
                .subscribe(response -> logSuccess(Optional.ofNullable(response), uid, consentReferenceId),
                        error -> logError(error, uid, consentReferenceId));
    }

    /**
     * Sends address saved event to CDS.
     * @param userModel the user model
     * @param consentReferenceId the consent reference
     * @param baseSiteId site id from the event
     */
    @Override
    public void sendAddressSavedEvent(final UserModel userModel, final String baseSiteId, final String consentReferenceId) {
        sendUserSlimEvent(userModel, baseSiteId, consentReferenceId, Config.getString(ADDRESS_CHANGED_SCHEMA, StringUtils.EMPTY));
    }

    /**
     * Sends address deleted event to CDS.
     * @param userModel the user model
     * @param consentReferenceId the consent reference
     * @param baseSiteId site id from the event
     */
    @Override
    public void sendAddressDeletedEvent(final UserModel userModel, final String baseSiteId, final String consentReferenceId) {
        sendUserSlimEvent(userModel, baseSiteId, consentReferenceId, Config.getString(ADDRESS_DELETED_SCHEMA, StringUtils.EMPTY));
    }

    /**
     * Sends personal details updated event to CDS.
     * @param userModel the user model
     * @param consentReferenceId the consent reference
     * @param baseSiteId site id from the event
     */
    @Override
    public void sendPersonalDetailsChangedEvent(final UserModel userModel, final String baseSiteId, final String consentReferenceId) {
        sendUserSlimEvent(userModel, baseSiteId, consentReferenceId, Config.getString(PERSONAL_DETAILS_CHANGED_SCHEMA, StringUtils.EMPTY));

    }

    private void sendUserSlimEvent(final UserModel userModel,final String baseSiteId,final String consentReferenceId, final String schema) {
        if (!shouldSendEvent(consentReferenceId, baseSiteId)) {
            return;
        }

        final User user = getUser(userModel, baseSiteId);

        final String debugEnabled = getDebugFlagValue(userModel);
        getClient().sendSlimEvent(schema, consentReferenceId, baseSiteId, debugEnabled, user)
                .subscribe(response -> logSuccess(Optional.ofNullable(response), user, consentReferenceId),
                        error -> logError(error, user, consentReferenceId));
    }

    private User getUser(UserModel userModel, String baseSiteId) {
        final User user = getProfileUserEventConverter().convert(userModel);
        user.setChannelRef(baseSiteId);
        return user;
    }

    /**
     * Send user login event to CDS.
     * @param userModel the user model
     * @param consentReferenceId consent reference
     * @param sessionId ec session id
     * @param baseSiteId storefront name like 'electronics'
     */
    @Override
    public void sendLoginEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String baseSiteId) {
        this.sendUserEvent(userModel, consentReferenceId, sessionId, baseSiteId, Config.getString(LOGIN_EVENT_TYPE, StringUtils.EMPTY));
    }

    protected void sendUserEvent(final UserModel userModel, final String consentReferenceId, final String sessionId, final String baseSiteId, final String eventType) {
        final User user = getUser(userModel, baseSiteId);
        user.setType(eventType);
        user.setSessionId(sessionId);

        final String debugEnabled = getDebugFlagValue(userModel);

        if (shouldSendEvent(consentReferenceId, baseSiteId)) {
            getClient().sendTransaction(eventType, consentReferenceId, baseSiteId, debugEnabled, user)
                    .subscribe(response -> logSuccess(Optional.ofNullable(response), user, consentReferenceId),
                            error -> logError(error, user, consentReferenceId));
        }
    }

    protected static String getDebugFlagValue(final UserModel userModel){
        if(Boolean.TRUE.equals(userModel.getProfileTagDebug())){
            return "1";
        }
        return "0";
    }

    protected static boolean isValidConsentReference(String consentReferenceId) {
        return StringUtils.isNotBlank(consentReferenceId) && !NULL.equals(consentReferenceId);
    }

    protected static void logSuccess(final Optional<RawResponse> rawResponse, final Object obj, final String consentReferenceId) {
        if (LOG.isDebugEnabled()) {
            final String event = Utils.parseObjectToJson(obj);
            LOG.debug(event + " sent to CDS with Consent Reference: " + consentReferenceId);
        }

        rawResponse.ifPresent(response ->  {
            final int statusCode = response.getStatusCode();
            final Optional<String> optionalContext = response.header("hybris-context-trace-id");

            optionalContext.
                    ifPresent(contextTraceId -> LOG.info("Event sent to CDS. " +
                            "With Status: " + statusCode + ", " +
                            "Consent Reference: " + consentReferenceId + " and " +
                            "Context-Trace-ID: " + contextTraceId));
        });
    }

    protected static void logError(final Throwable error, final Object obj, final String consentReference) {
        if (LOG.isDebugEnabled()) {
            final String event = Utils.parseObjectToJson(obj);
            LOG.debug(String.format("Sending event %s to CDS with consent-reference %s failed.", event, consentReference), error);
        }

        LOG.warn("Error sending transaction to CDS", error);
    }


    protected String getConsentReference(final OrderModel orderModel) {
        String consentReference = orderModel.getConsentReference();
        if (consentReference == null) {
            consentReference = getConsentService().getConsentReferenceFromConsentModel();
        }
        return consentReference;
    }

    protected ProfileClient getClient(){
        return getRetrieveRestClientStrategy().getProfileRestClient();
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public Converter<OrderModel, Order> getProfileOrderEventConverter() {
        return profileOrderEventConverter;
    }

    public void setProfileOrderEventConverter(Converter<OrderModel, Order> profileOrderEventConverter) {
        this.profileOrderEventConverter = profileOrderEventConverter;
    }

    public Converter<ConsignmentModel, Order> getProfileConsignmentEventConverter() {
        return profileConsignmentEventConverter;
    }

    public void setProfileConsignmentEventConverter(Converter<ConsignmentModel, Order> profileConsignmentEventConverter) {
        this.profileConsignmentEventConverter = profileConsignmentEventConverter;
    }

    public Converter<ReturnRequestModel, Order> getProfileReturnEventConverter() {
        return profileReturnEventConverter;
    }

    public void setProfileReturnEventConverter(Converter<ReturnRequestModel, Order> profileReturnEventConverter) {
        this.profileReturnEventConverter = profileReturnEventConverter;
    }

    public Converter<UserModel, User> getProfileUserEventConverter() {
        return profileUserEventConverter;
    }

    public void setProfileUserEventConverter(Converter<UserModel, User> profileUserEventConverter) {
        this.profileUserEventConverter = profileUserEventConverter;
    }

    public Converter<ChangeUIDEvent, UID> getProfileUIDConverter() {
        return this.profileUIDConverter;
    }

    public void setProfileUIDConverter(Converter<ChangeUIDEvent, UID> profileUIDConverter) {
        this.profileUIDConverter = profileUIDConverter;
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }
}
