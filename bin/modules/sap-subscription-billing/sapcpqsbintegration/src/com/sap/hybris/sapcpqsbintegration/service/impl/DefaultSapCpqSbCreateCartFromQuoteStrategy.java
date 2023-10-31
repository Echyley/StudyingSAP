/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.service.impl;

import de.hybris.platform.commerceservices.order.impl.CommerceCreateCartFromQuoteStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import org.apache.commons.collections.CollectionUtils;

import com.sap.hybris.sapcpqsbintegration.service.SapCpqSbConsumedDestinationService;
import static com.sap.hybris.sapcpqsbintegration.constants.SapcpqsbintegrationConstants.SCPI_DESTINATION;

public class DefaultSapCpqSbCreateCartFromQuoteStrategy extends CommerceCreateCartFromQuoteStrategy {
	
	private SapCpqSbConsumedDestinationService sapCpqSbConsumedDestinationService;
	
    @Override
    protected void postProcess(final QuoteModel original, final CartModel copy) {

        super.postProcess(original, copy);
        if ((getSapCpqSbConsumedDestinationService().checkIfDestinationExists(SCPI_DESTINATION)) && copy != null && copy.getEntries() != null) {
            //Accept and Check out
            copy.getEntries().forEach(entry -> {
                if (!CollectionUtils.isEmpty(entry.getCpqSubscriptionDetails()) && entry.getProduct().getSubscriptionCode() != null) {
                    entry.getCpqSubscriptionDetails().forEach(discountEntry -> {
                        discountEntry.setOneTimeChargeEntries(null);
                        discountEntry.setRecurringChargeEntries(null);
                        discountEntry.setUsageCharges(null);
                    });
                }
            });
        } else {
            return;
        }

    }

	public SapCpqSbConsumedDestinationService getSapCpqSbConsumedDestinationService() {
		return sapCpqSbConsumedDestinationService;
	}

	public void setSapCpqSbConsumedDestinationService(
			SapCpqSbConsumedDestinationService sapCpqSbConsumedDestinationService) {
		this.sapCpqSbConsumedDestinationService = sapCpqSbConsumedDestinationService;
	}
    
}
