/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor.writer;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;
import de.hybris.platform.odata2services.odata.processor.ODataNextLinkBuilder;

import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;

public class NextLinkPropertyPopulator implements ResponseWriterPropertyPopulator
{
	@Override
	public boolean isApplicable(final ItemLookupRequest itemLookupRequest)
	{
		return itemLookupRequest.getIntegrationItem() == null;
	}

	@Override
	public EntityProviderWriteProperties.ODataEntityProviderPropertiesBuilder populate(
			final EntityProviderWriteProperties properties,
			final ItemLookupRequest itemLookupRequest,
			final ItemLookupResult result)
	{
		final String nextLink = ODataNextLinkBuilder.nextLink()
		                                            .withLookupRequest(itemLookupRequest)
		                                            .withTotalCount(result.getTotalCount())
		                                            .build();
		return EntityProviderWriteProperties.fromProperties(properties)
				.nextLink(nextLink);
	}
}
