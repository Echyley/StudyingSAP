/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.service.impl;

import com.sap.hybris.sapcpqsbintegration.service.SapCpqSbFetchQuoteDiscountsService;
import com.sap.hybris.saprevenuecloudproduct.dao.impl.DefaultSapSubscriptionProductDao;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Default Quote API client Service to communicate with Sap CPQ system
 */

public class DefaultSapCpqSbFetchQuoteDiscountsService implements SapCpqSbFetchQuoteDiscountsService {

    private FlexibleSearchService flexibleSearchService;
    protected static final String QUOTES_QUERY = "SELECT {quote:" + QuoteModel.PK + "} FROM {" + QuoteModel._TYPECODE
            + " as quote}";
    protected static final String WHERE_CODE_CLAUSE = " WHERE {quote:" + QuoteModel.CODE + "}=?code AND {quote:" + QuoteModel.STATE + "}=?quoteStates";
    protected static final String ORDER_BY_VERSION_DESC = " ORDER BY {quote:" + QuoteModel.VERSION + "} DESC";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpqSbFetchQuoteDiscountsService.class);
    @Override
    public Optional<QuoteModel> getCurrentQuoteVendorDiscounts(String quoteId) {

        validateParameterNotNullStandardMessage("quoteId", quoteId);

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(
                QUOTES_QUERY + WHERE_CODE_CLAUSE + ORDER_BY_VERSION_DESC);
        searchQuery.addQueryParameter("code", quoteId);
        searchQuery.addQueryParameter("quoteStates", QuoteState.BUYER_OFFER);
        searchQuery.addQueryParameter("version",1);
        searchQuery.setCount(1);

        try {
            final QuoteModel quote = getFlexibleSearchService().searchUnique(searchQuery);
            return Optional.of(quote);
        } catch (ModelNotFoundException | AmbiguousIdentifierException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Error while fetching the Quote for the quote ID:" + quoteId + ".Error :"
                        + e);
            }
            LOG.error(String.format(
                    "Error while fetching the Quote for quote  ID [%s] ",
                    quoteId));
            return Optional.empty();
        }

    }


    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
