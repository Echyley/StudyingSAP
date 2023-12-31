/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.populators;

import com.hybris.yprofile.dto.Consumer;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerEmailResolutionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.mail.MailUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class IdentitiesPopulator implements Populator<UserModel, List<Consumer>> {

    private static final Logger LOG = Logger.getLogger(IdentitiesPopulator.class);

    public static final String TYPE_EMAIL = "email";
    public static final String TYPE_UID = "UID";
    public static final String CUSTOMER_ID = "CustomerId";

    private String defaultEmail;

    private ConfigurationService configurationService;

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public void populate(final UserModel userModel, final List<Consumer> identities) {

        // use UID by default, if user is registered through accelerator this is usually the email,
        // if he is registered through gigya this is something more cryptic
        // if it is an guest user it is the email with a random prefix and separated by a pipe.
        // For the last case CustomerModel.getContactEmail strips the prefix

        final String uidString = getIdentityRef(userModel);
        identities.add(createConsumer(TYPE_UID, uidString));

        Optional<String> customerId = getCustomerId(userModel);

        if (customerId.isPresent()){
            identities.add(createConsumer(CUSTOMER_ID, customerId.get()));
        }


        if (checkIfUidIsEmail(uidString)){
            identities.add(createConsumer(TYPE_EMAIL, uidString));
        } else {
            // in case of a gigya integration Address.email gets populated, take all emails we can get from all addresses
            if (userModel.getAddresses() != null) {
                userModel.getAddresses().stream()
                        .filter(addressModel -> StringUtils.isNotBlank(addressModel.getEmail()))
                        .forEach(addressModel -> identities.add(createConsumer(TYPE_EMAIL, addressModel.getEmail())));
            }
        }

    }

    protected String getIdentityRef(final UserModel userModel)
    {
        if (userModel instanceof CustomerModel) {
            final CustomerModel customerModel = (CustomerModel) userModel;
            // customerModel.getContactEmail() retrieves Email from UID
            // there is a special handling for guest users and a default value for the email address
            // see @DefaultCustomerEmailResolutionService for implementation details
            final String contactEmail = customerModel.getContactEmail();
            //  filter out default emails, as this is of no value
            if (!defaultEmail.equals(contactEmail)) {
                return contactEmail;
            }
        }
        // e.g. the gigya case
        return userModel.getUid();
    }

    protected Optional<String> getCustomerId(final UserModel userModel)
    {
        if (userModel instanceof CustomerModel) {
            final CustomerModel customerModel = (CustomerModel) userModel;

            final String customerID = customerModel.getCustomerID();
            if (customerID != null && !customerID.isEmpty()) {
                return Optional.of(customerID);
            }
        }

        return Optional.empty();
    }


    protected boolean checkIfUidIsEmail(final String uidToCheck) {
        try {
            MailUtils.validateEmailAddress(uidToCheck, "customer email");
            return true;
        } catch(EmailException e) {
            LOG.warn("Unexpected error occurred while validating the email address", e);
            return false;
        }
    }
    

    protected Consumer createConsumer(final String type, final String ref) {
        final Consumer consumer = new Consumer();
        consumer.setType(type);
        consumer.setRef(ref);
        return consumer;
    }

    protected void init() {
        this.defaultEmail = getConfigurationService().getConfiguration().getString(
                DefaultCustomerEmailResolutionService.DEFAULT_CUSTOMER_KEY,
                DefaultCustomerEmailResolutionService.DEFAULT_CUSTOMER_EMAIL);
    }
}
