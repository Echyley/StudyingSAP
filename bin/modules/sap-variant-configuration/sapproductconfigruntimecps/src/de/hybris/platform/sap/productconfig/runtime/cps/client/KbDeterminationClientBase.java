/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.client;



import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import rx.Observable;


/**
 * Charon client: KB determination. Used to access list of knowledge bases for a product at a certain date
 */
public interface KbDeterminationClientBase
{

	/**
	 * Find knowledge bases for given product
	 *
	 * @param sapPassport
	 *           SAP passport as string
	 * @param productId
	 *           Key or product according to modeling environment
	 * @return List of knowledge bases
	 */
	@GET
	@Produces("application/json")
	@Path("/kbdetermination")
	Observable<List<CPSMasterDataKBHeaderInfo>> getKnowledgebases(@HeaderParam("SAP-PASSPORT") String sapPassport,
			@QueryParam("productid") String productId);

	/**
	 * Find knowledge bases for given product and date. Date format needs to be according ISO 8601
	 *
	 * @param sapPassport
	 *           SAP passport as string
	 * @param productId
	 *           Key of product according to modeling environment
	 * @param date
	 *           Kb validity date in format YYYY-MM-DD
	 * @return List of knowledge bases
	 */
	@GET
	@Produces("application/json")
	@Path("/kbdetermination")
	Observable<List<CPSMasterDataKBHeaderInfo>> getKnowledgebases(@HeaderParam("SAP-PASSPORT") String sapPassport,
			@QueryParam("productid") String productId, @QueryParam("date") String date);

	/**
	 * @param sapPassport
	 *           SAP passport as string
	 *
	 * @return List of knowledge bases
	 */
	@GET
	@Produces("application/json")
	@Path("/kbdetermination")
	Observable<List<CPSMasterDataKBHeaderInfo>> getAllKnowledgebases(@HeaderParam("SAP-PASSPORT") String sapPassport);
}
