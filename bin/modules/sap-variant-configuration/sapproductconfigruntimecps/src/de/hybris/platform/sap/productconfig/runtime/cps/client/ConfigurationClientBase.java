/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.client;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCreateConfigInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.PATCH;

import rx.Observable;


/**
 * Specifies REST APIs for CPS calls to create, update and release a configuration runtime object
 */
public interface ConfigurationClientBase
{
	/**
	 * Updates a configuration runtime object
	 *
	 * @param characteristicInput
	 *           Changes to a characteristic
	 * @param cfgId
	 *           ID of runtime object
	 * @param itemId
	 *           ID of item that carries the characteristics to be changed
	 * @param csticId
	 *           ID of characteristic (language independent name)
	 * @param sessionCookieAsString
	 *           Cookie identifying the session
	 * @param cfCookieAsString
	 *           Cookie identifying the node
	 * @param eTag
	 *           eTag id for optimistic locking
	 * @param sapPassport
	 *           SAP passport as string
	 *
	 * @return Response. The observable cannot be specified, otherwise the charon method invocation fails
	 */
	@PATCH
	@Produces("application/json")
	@Path("/configurations/{cfgId}/items/{itemId}/characteristics/{csticId}")
	Observable updateConfiguration(CPSCharacteristicInput characteristicInput, @PathParam("cfgId") String cfgId,
			@PathParam("itemId") String itemId, @PathParam("csticId") String csticId,
			@HeaderParam("Cookie") String sessionCookieAsString, @HeaderParam("Cookie") String cfCookieAsString,
			@HeaderParam("If-Match") String eTag, @HeaderParam("SAP-PASSPORT") String sapPassport);

	/**
	 * Updates a configuration runtime object
	 *
	 * @param characteristicInput
	 *           Changes to a characteristic
	 * @param cfgId
	 *           ID of runtime object
	 * @param itemId
	 *           ID of item that carries the characteristics to be changed
	 * @param csticId
	 *           ID of characteristic (language independent name)
	 * @param eTag
	 *           eTag id for optimistic locking
	 * @param sapPassport
	 *           SAP passport as string
	 *
	 * @return Response. The observable cannot be specified, otherwise the charon method invocation fails
	 */
	@PATCH
	@Produces("application/json")
	@Path("/configurations/{cfgId}/items/{itemId}/characteristics/{csticId}")
	Observable updateConfiguration(CPSCharacteristicInput characteristicInput, @PathParam("cfgId") String cfgId,
			@PathParam("itemId") String itemId, @PathParam("csticId") String csticId, @HeaderParam("If-Match") String eTag,
			@HeaderParam("SAP-PASSPORT") String sapPassport);

	/**
	 * Get a configuration runtime object
	 *
	 * @param cfgId
	 *                                 ID of runtime object
	 * @param lang
	 *                                 language in which language-dependent fields are returned (e.g. conflict texts)
	 * @param sessionCookieAsString
	 *                                 Cookie identifying the session
	 * @param cfCookieAsString
	 *                                 Cookie identifying the node
	 * @param sapPassport
	 *                                 SAP passport as string
	 * @param select
	 *                                 filter to specify which level of detailed shall be returned. Must contain at least
	 *                                 one or any combination of the following options
	 *                                 <code>subItems,characteristicGroups,variantConditions,characteristics,possibleValues,values</code>
	 * @return Observable wrapping the configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/configurations/{cfgId}")
	Observable<RawResponse<CPSConfiguration>> getConfiguration(@PathParam("cfgId") String cfgId,
			@HeaderParam("Accept-Language") String lang, @HeaderParam("Cookie") String sessionCookieAsString,
			@HeaderParam("Cookie") String cfCookieAsString, @HeaderParam("SAP-PASSPORT") String sapPassport,
			@QueryParam("$select") String select);


	/**
	 * Gets details for a group, including domain values
	 *
	 * @param cfgId
	 *                       ID of runtime object
	 * @param itemId
	 *                       instance ID
	 * @param filter
	 *                       group filter, contains one group
	 * @param lang
	 *                       language in which language-dependent fields are returned (e.g. conflict texts)
	 * @param sapPassport
	 *                       SAP passport as string
	 * @return Observable wrapping the configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/configurations/{cfgId}/items/{itemId}")
	Observable<RawResponse<CPSItem>> getGroup(@PathParam("cfgId") String cfgId, @PathParam("itemId") String itemId,
			@QueryParam("$filter") String filter, @HeaderParam("Accept-Language") String lang,
			@HeaderParam("SAP-PASSPORT") String sapPassport);

	/**
	 * Gets details for a group, including domain values
	 *
	 * @param cfgId
	 *                                 ID of runtime object
	 * @param itemId
	 *                                 instance ID
	 * @param filter
	 *                                 group filter, contains one group
	 * @param lang
	 *                                 language in which language-dependent fields are returned (e.g. conflict texts)
	 * @param sessionCookieAsString
	 *                                 Cookie identifying the session
	 * @param cfCookieAsString
	 *                                 Cookie identifying the node
	 * @param sapPassport
	 *                                 SAP passport as string
	 * @return Observable wrapping the configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/configurations/{cfgId}/items/{itemId}")
	Observable<RawResponse<CPSItem>> getGroup(@PathParam("cfgId") String cfgId, @PathParam("itemId") String itemId,
			@QueryParam("$filter") String filter, @HeaderParam("Accept-Language") String lang,
			@HeaderParam("Cookie") String sessionCookieAsString, @HeaderParam("Cookie") String cfCookieAsString,
			@HeaderParam("SAP-PASSPORT") String sapPassport);

	/**
	 * Get a configuration runtime object
	 *
	 * @param cfgId
	 *                       ID of runtime object
	 * @param lang
	 *                       language in which language-dependent fields are returned (e.g. conflict texts)
	 * @param sapPassport
	 *                       SAP passport as string
	 * @param select
	 *                       filter to specify which level of detailed shall be returned. Must contain at least one or any
	 *                       combination of the following options
	 *                       <code>subItems,characteristicGroups,variantConditions,characteristics,possibleValues,values</code>
	 * @return Observable wrapping the configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/configurations/{cfgId}")
	Observable<RawResponse<CPSConfiguration>> getConfiguration(@PathParam("cfgId") String cfgId,
			@HeaderParam("Accept-Language") String lang, @HeaderParam("SAP-PASSPORT") String sapPassport,
			@QueryParam("$select") String select);

	/**
	 * Create a configuration runtime object
	 *
	 * @param createConfigInput
	 *                             Data we need to create a default configuration. We typically only provide the product
	 *                             code
	 * @param lang
	 *                             language in which language-dependent fields are returned (e.g. conflict texts)
	 * @param sapPassport
	 *                             SAP passport as string
	 * @param autoCleanup
	 *                             only if <code>true</code>, cleanUp of old configs will be handled by CPS Service.
	 *                             Typically configs will be auto deleted after a certain time. Otherwise configs are
	 *                             persisted forever, unless they are deleted with a delete request.
	 * @param select
	 *                             filter to specify which level of detailed shall be returned. Must contain at least one or
	 *                             any combination of the following options
	 *                             <code>subItems,characteristicGroups,variantConditions,characteristics,possibleValues,values</code>
	 * @return Response wrapping the configuration object
	 */
	@POST
	@Produces("application/json")
	@Path("/configurations")
	Observable<RawResponse<CPSConfiguration>> createDefaultConfiguration(CPSCreateConfigInput createConfigInput,
			@HeaderParam("Accept-Language") String lang, @HeaderParam("SAP-PASSPORT") String sapPassport,
			@QueryParam("autoCleanup") String autoCleanup, @QueryParam("$select") String select);

	/**
	 * Get the external representation of a configuration runtime object
	 *
	 * @param cfgId
	 *           ID of runtime object
	 * @param sessionCookieAsString
	 *           Cookie identifying the session
	 * @param cfCookieAsString
	 *           Cookie identifying the server node
	 * @param sapPassport
	 *           SAP passport as string
	 *
	 * @return Observable wrapping the external representation of a configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/externalConfigurations/{cfgId}")
	Observable<CPSExternalConfiguration> getExternalConfiguration(@PathParam("cfgId") String cfgId,
			@HeaderParam("Cookie") String sessionCookieAsString, @HeaderParam("Cookie") String cfCookieAsString,
			@HeaderParam("SAP-PASSPORT") String sapPassport);

	/**
	 * Get the external representation of a configuration runtime object
	 *
	 * @param cfgId
	 *           ID of runtime object
	 * @param sapPassport
	 *           SAP passport as string
	 *
	 * @return Observable wrapping the external representation of a configuration object
	 */
	@GET
	@Produces("application/json")
	@Path("/externalConfigurations/{cfgId}")
	Observable<CPSExternalConfiguration> getExternalConfiguration(@PathParam("cfgId") String cfgId,
			@HeaderParam("SAP-PASSPORT") String sapPassport);

	/**
	 * Create a configuration runtime object based on a external representation
	 *
	 * @param sapPassport
	 *           SAP passport as string
	 * @param externalConfiguration
	 *           External representation of a configuration
	 * @param autoCleanup
	 *           only if <code>true</code>, cleanUp of old configs will be handled by CPS Service. Typically configs will
	 *           be auto deleted after a certain time. Otherwise configs are persisted forever, unless they are deleted
	 *           with a delete request.
	 * @param select
	 *           filter to specify which level of detailed shall be returned. Must contain at least one or any
	 *           combination of the following options
	 *           <code>subItems,characteristicGroups,variantConditions,characteristics,possibleValues,values</code>
	 * @return Response wrapping the configuration object
	 */
	@POST
	@Produces("application/json")
	@Path("/externalConfigurations")
	Observable<RawResponse<CPSConfiguration>> createRuntimeConfigurationFromExternal(
			@HeaderParam("SAP-PASSPORT") String sapPassport, CPSExternalConfiguration externalConfiguration,
			@QueryParam("autoCleanup") String autoCleanup, @QueryParam("$select") String select);

	/**
	 * Release a configuration runtime object
	 *
	 * @param cfgId
	 *           ID of runtime object
	 * @param sessionCookieAsString
	 *           Cookie identifying the session
	 * @param cfCookieAsString
	 *           Cookie identifying the server node
	 * @param eTag
	 *           eTag id for optimistic locking
	 * @param sapPassport
	 *           SAP passport as string
	 * @return Response. The observable cannot be specified, otherwise the charon method invocation fails
	 */

	@DELETE
	@Produces("application/json")
	@Path("/configurations/{cfgId}")
	Observable deleteConfiguration(@PathParam("cfgId") String cfgId, @HeaderParam("Cookie") String sessionCookieAsString,
			@HeaderParam("Cookie") String cfCookieAsString, @HeaderParam("If-Match") String eTag,
			@HeaderParam("SAP-PASSPORT") String sapPassport);

	/**
	 * Release a configuration runtime object
	 *
	 * @param cfgId
	 *           ID of runtime object
	 * @param eTag
	 *           eTag id for optimistic locking
	 * @param sapPassport
	 *           SAP passport as string
	 * @return Response. The observable cannot be specified, otherwise the charon method invocation fails
	 */

	@DELETE
	@Produces("application/json")
	@Path("/configurations/{cfgId}")
	Observable deleteConfiguration(@PathParam("cfgId") String cfgId, @HeaderParam("If-Match") String eTag,
			@HeaderParam("SAP-PASSPORT") String sapPassport);
}
