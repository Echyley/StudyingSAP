/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcarintegration.services;

import java.util.List;

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;


/**
 * Extension interface to extract CAR Order details from MultiChannel configuration
 */
public interface MultichannelOrderHistoryExtractorService extends CarOrderHistoryExtractorService
{

	/**
	 * Extract Header information for MultiChannel Orders from a CAR instances
	 * 
	 * @param feed
	 * @return OrderHistoryData containing Multichannel header from the Sales Document
	 */
	abstract CarMultichannelOrderHistoryData extractSalesDocumentHeader(final ODataFeed feed);

	/**
	 * Extract the various Sales Document Entries contained within the MultiChannel CAR instance
	 * 
	 * @param order
	 * @param feed
	 */
	abstract void extractSalesDocumentEntries(final CarMultichannelOrderHistoryData order, final ODataFeed feed);

	/**
	 * Extract the full details of the Multichannel orders from the CAR instance
	 * 
	 * @param paginationData
	 * @param feed
	 * @return List containing entries for each Order instance
	 */
	abstract List<CarMultichannelOrderHistoryData> extractMultichannelOrders(PaginationData paginationData, ODataFeed feed);

}
