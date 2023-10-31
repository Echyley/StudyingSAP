/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.strategy.impl;

import static com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants.QUOTE_REPLICATION_DESTINATION_ID;

import com.sap.hybris.c4ccpiquote.service.C4CConsumedDestinationService;

import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 * Strategy answers if order requires calculation if it contains associated quote 
 */
public class DefaultC4CCpiQuoteRequiresCalculationStrategy {
	
	private C4CConsumedDestinationService c4CConsumedDestinationService;
	

    /**
     * Returns if order needs to be calculated
     */
    public boolean shouldCalculateAllValues(AbstractOrderModel order) {
    	
        String c4cQuoteId = null;
        QuoteModel quoteModel = null;
        boolean shouldCalculateAllValues = false;
        
        if (!getC4CConsumedDestinationService().checkIfDestinationExists(QUOTE_REPLICATION_DESTINATION_ID)){
        	return true;
        } else if(order instanceof QuoteModel) {
            quoteModel = (QuoteModel) order;
        } else if (order instanceof CartModel) {
            CartModel cartModel = (CartModel) order;
            quoteModel = cartModel.getQuoteReference();
        } else if (order instanceof OrderModel) {
            OrderModel orderModel = (OrderModel) order;
            quoteModel = orderModel.getQuoteReference();
        }

        if (quoteModel != null) {
            c4cQuoteId = quoteModel.getC4cQuoteExternalQuoteId();
            if (QuoteState.BUYER_DRAFT.equals(quoteModel.getState())
                    || QuoteState.SELLER_DRAFT.equals(quoteModel.getState())) {
                shouldCalculateAllValues = true;
            }
        }

        return (c4cQuoteId == null || shouldCalculateAllValues);
    }


	public C4CConsumedDestinationService getC4CConsumedDestinationService() {
		return c4CConsumedDestinationService;
	}


	public void setC4CConsumedDestinationService(C4CConsumedDestinationService c4cConsumedDestinationService) {
		c4CConsumedDestinationService = c4cConsumedDestinationService;
	}


	


    

}