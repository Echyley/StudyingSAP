/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.c4ccpiquote.constants.C4ccpiquoteConstants;
import com.sap.hybris.c4ccpiquote.service.C4CCpiQuoteService;

import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;


/**
 * Default Implementation for C4CCpiQuoteService
 */

public class DefaultC4CCpiQuoteService implements C4CCpiQuoteService
{

	protected static final String SALES_AREA_QUERY = "SELECT {bsc:" + SAPConfigurationModel.PK + "} FROM {"
			+ SAPConfigurationModel._TYPECODE + " as bsc}";
	protected static final String SALES_AREA_WHERE_CLAUSE = " WHERE {bsc:" + SAPConfigurationModel.SAPCOMMON_SALESORGANIZATION
			+ "}=?salesOrg  AND {bsc:" + SAPConfigurationModel.SAPCOMMON_DISTRIBUTIONCHANNEL + "}=?distChannel AND {bsc:"
			+ SAPConfigurationModel.SAPCOMMON_DIVISION + "}=?division";

	protected static final String QUOTES_QUERY = "SELECT {quote:" + QuoteModel.PK + "} FROM {" + QuoteModel._TYPECODE
			+ " as quote}";
	protected static final String WHERE_CODE_CLAUSE = " WHERE {quote:" + QuoteModel.CODE + "}=?code";
	protected static final String ORDER_BY_VERSION_DESC = " ORDER BY {quote:" + QuoteModel.VERSION + "} DESC";


	private FlexibleSearchService flexibleSearchService;


	@Override
	public String getSiteAndStoreFromSalesArea(final String salesOrganization, final String distributionChannel,
			final String division)
	{

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(SALES_AREA_QUERY + SALES_AREA_WHERE_CLAUSE);
		searchQuery.addQueryParameter(C4ccpiquoteConstants.SALES_ORG, salesOrganization);
		searchQuery.addQueryParameter(C4ccpiquoteConstants.DISTRIBUTION_CHANNEL, distributionChannel);
		searchQuery.addQueryParameter(C4ccpiquoteConstants.DIVISION, division);
		searchQuery.setCount(1);

		final SAPConfigurationModel scm = getFlexibleSearchService().searchUnique(searchQuery);
		final List<BaseStoreModel> baseStores = new ArrayList<>(scm.getBaseStores());

		return baseStores.get(0).getUid();
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}


	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}




}
