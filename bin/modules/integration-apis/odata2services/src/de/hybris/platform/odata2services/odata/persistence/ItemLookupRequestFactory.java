/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence;

import de.hybris.platform.integrationservices.search.WhereClauseConditions;

import javax.validation.constraints.NotNull;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;

/**
 * Factory to create an {@link ItemLookupRequest}
 */
public interface ItemLookupRequestFactory
{
	/**
	 * Creates an {@link ItemLookupRequest} from the given parameters
	 *
	 * @param uriInfo Used to create the ItemLookupRequest
	 * @param context Used to create the ItemLookupRequest
	 * @param contentType Used to create the ItemLookupRequest
	 * @return ItemLookupRequest instance
	 */
	ItemLookupRequest create(UriInfo uriInfo, ODataContext context, String contentType);

	/**
	 * Creates an {@link ItemLookupRequest} that can be used to filter
	 * items matching the integration key
	 *
	 * @param context Used to create the ItemLookupRequest
	 * @param edmEntitySet Used to create the ItemLookupRequest
	 * @param oDataEntry Used to create the ItemLookupRequest
	 * @param integrationKey Used to create the ItemLookupRequest
	 * @return ItemLookupRequest instance
	 * @throws EdmException if OData problem
	 */
	ItemLookupRequest create(ODataContext context, EdmEntitySet edmEntitySet, ODataEntry oDataEntry, String integrationKey) throws EdmException;

	/**
	 * Creates an {@link ItemLookupRequest} that can be used to filter items matching a given attribute, e.g. {@code version = 'Staged'}
	 *
	 * @param context HTTP request context that carries URL parameters, etc
	 * @param entitySet requested entity set
	 * @param filter a filter based on the attribute values to be added to the produced request. These conditions replace
	 * conditions possibly existing in the {@code context}.
	 * @return ItemLookupRequest instance new request instance
	 * @throws EdmException if OData problem
	 */
	@NotNull ItemLookupRequest createWithFilter(final ODataContext context,
			final EdmEntitySet entitySet,
			final WhereClauseConditions filter) throws EdmException;

	/**
	 * Creates an {@link ItemLookupRequest} from the given parameters
	 *
	 * @param uriInfo Used to create the ItemLookupRequest
	 * @param context Used to create the ItemLookupRequest
	 * @return ItemLookupRequest instance
	 */
	ItemLookupRequest create(DeleteUriInfo uriInfo, ODataContext context);

	/**
	 * Creates an {@link ItemLookupRequest} from the given parameters
	 *
	 * @param request base request to create new request from
	 * @param entitySet entity set of the new lookup request
	 * @param oDataEntry the new ODataEntry for the lookup request
	 * @return ItemLookupRequest instance
	 * @throws EdmException if OData problem
	 */
	ItemLookupRequest createFrom(ItemLookupRequest request, final EdmEntitySet entitySet, final ODataEntry oDataEntry) throws EdmException;
}
