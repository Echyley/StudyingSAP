/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.service;

import de.hybris.platform.core.model.order.QuoteModel;


import java.util.Optional;

/**
 * Generated {@link SapCpqSbFetchQuoteDiscountsService}
 */
public interface SapCpqSbFetchQuoteDiscountsService {
    /**
     * Get Quote discounts
     * @param quoteId logo code
     * @return  QuoteModel with the discounts of {@link QuoteModel}
     */
    public Optional<QuoteModel> getCurrentQuoteVendorDiscounts(String quoteId);
}
