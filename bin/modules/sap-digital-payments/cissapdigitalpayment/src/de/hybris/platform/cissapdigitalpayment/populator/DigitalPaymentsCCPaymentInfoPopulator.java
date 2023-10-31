/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsPollModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import org.springframework.util.Assert;


/**
 * Populates {@link CCPaymentInfoData} with {@link CisSapDigitalPaymentPollRegisteredCardResult Model}.
 */
public class DigitalPaymentsCCPaymentInfoPopulator<S extends DigitalPaymentsPollModel, T extends CCPaymentInfoData>
        implements Populator<S, T> {

    private CustomerFacade customerFacade;

    @Override
    public void populate(final S source, final T target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (null != source.getPaytCardByDigitalPaymentSrvc()) {
            //This subscription ID set is getting overridden while calling createSubscription method in CustomerAccountService
            target.setSubscriptionId(source.getPaytCardByDigitalPaymentSrvc());
        }

        CustomerData customerData = customerFacade.getCurrentCustomer();
        AddressData defaultBillingAddress = customerData.getDefaultBillingAddress();
        target.setBillingAddress(defaultBillingAddress);

        target.setCardType(source.getPaymentCardType());
        target.setExpiryMonth(source.getPaymentCardExpirationMonth());
        target.setExpiryYear(source.getPaymentCardExpirationYear());
        target.setCardNumber(source.getPaymentCardMaskedNumber());
        target.setAccountHolderName(source.getPaymentCardHolderName());

    }


    public void setCustomerFacade(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

}
