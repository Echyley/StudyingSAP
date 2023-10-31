/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.client;

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import rx.Observable;


/**
 * Specifies REST APIs for CPS calls to retrieve the configuration master data
 */
public interface MasterDataClientBase
{
	/**
	 * Retrieves the configuration master data using query parameter defining requested optional data parts
	 *
	 * @param id
	 *           Knowledge base ID (DB key in the modeling system)
	 * @param lang
	 *           ISO language code
	 * @param sapPassport
	 *           SAP passport as string
	 * @param select
	 *           query parameter defining required additional data parts
	 * @return Observable wrapper around KB data
	 */
	@GET
	@Produces("application/json")
	@Path("/knowledgebases/{id}")
	Observable<CPSMasterDataKnowledgeBase> getKnowledgebase(@PathParam("id") String id,
			@HeaderParam("Accept-Language") String lang, @HeaderParam("SAP-PASSPORT") String sapPassport,
			@QueryParam("$select") String select);
}
