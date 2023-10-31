/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.client;

import de.hybris.platform.cpq.productconfig.services.data.BusinessContextRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCloneData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCloneRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreateRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreatedResponseData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationPatchRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.TokenResponseData;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.PATCH;

import rx.Observable;


/**
 * Accessing CPQ via REST and oAuth2
 */
@Control(timeout = "${cpqWebCom.charon.timeout:15000}")
public interface CpqClient
{

	/**
	 * Get oAuth2 token, passing the credentials of a guest user
	 *
	 * @param credentials
	 * @return Token
	 */
	@POST
	@Produces(CpqClientConstants.HTTP_PRODUCE_APPL_JSON)
	@Path("/basic/api/token")
	Observable<RawResponse<TokenResponseData>> token(String credentials);


	/**
	 * Initializes a new session configuration
	 *
	 * @param authorization
	 *           OAuth2 authorization token
	 * @param cookieless
	 *           if <code>true</code> cookieless mode requested, CPQ should then return no ASP.Net Session cookie, but
	 *           populate header field <code>x-cpq-session-id</code> with session id.
	 * @param createRequest
	 *           request body
	 * @return Response, used to extract the cookies needed for subsequent requests and configuration id
	 */
	@POST
	@Produces(CpqClientConstants.HTTP_PRODUCE_APPL_JSON)
	@Path("/api/configuration/v1/configurations")
	Observable<RawResponse<ConfigurationCreatedResponseData>> createConfiguration(
			@HeaderParam(CpqClientConstants.HTTP_HEADER_AUTHORIZATION) String authorization,
			@HeaderParam(CpqClientConstants.HTTP_HEADER_COOKIELESS_SESSION) boolean cookieless,
			ConfigurationCreateRequest createRequest);

	/**
	 * Retrieves the configuration with given ID
	 *
	 * @param authorization
	 *           OAuth2 token
	 * @param configId
	 *           Configuration ID
	 * @return Configuration Summary
	 */
	@GET
	@Produces(CpqClientConstants.HTTP_PRODUCE_APPL_JSON)
	@Path("/api/configuration/v1/configurations/{configId}")
	Observable<RawResponse<ConfigurationSummaryData>> getConfiguration(
			@HeaderParam(CpqClientConstants.HTTP_HEADER_AUTHORIZATION) String authorization,
			@PathParam(value = "configId") String configId);


	/**
	 * Deletes the configuration with given ID
	 *
	 * @param authorization
	 *           OAuth2 token
	 * @param configId
	 *           Configuration ID
	 * @return no Content
	 */
	@DELETE
	@Produces(CpqClientConstants.HTTP_PRODUCE_APPL_JSON)
	@Path("/api/configuration/v1/configurations/{configId}")
	Observable<RawResponse<Object>> deleteConfiguration(
			@HeaderParam(CpqClientConstants.HTTP_HEADER_AUTHORIZATION) String authorization,
			@PathParam(value = "configId") String configId);


	/**
	 * Clones the configuration with given ID
	 *
	 * @param authorization
	 *           OAuth2 token
	 * @param cookieless
	 *           specifies whether cpq runs in cookieless mode
	 * @param configId
	 *           Configuration ID
	 * @param cloneRequest
	 *           options for cloning process
	 * @return Configuration ID of cloned configuration
	 */
	@POST
	@Produces(CpqClientConstants.HTTP_PRODUCE_APPL_JSON)
	@Path("/api/configuration/v1/configurations/{configurationId}/clone")
	Observable<RawResponse<ConfigurationCloneData>> clone(
			@HeaderParam(CpqClientConstants.HTTP_HEADER_AUTHORIZATION) String authorization,
			@HeaderParam(CpqClientConstants.HTTP_HEADER_COOKIELESS_SESSION) boolean cookieless,
			@PathParam(CpqClientConstants.HTTP_PARAM_CONFIG_ID) String configId, ConfigurationCloneRequest cloneRequest);

	/**
	 * Sends the business context to CPQ for a given owner id
	 *
	 * @param businessContext
	 *           business context to be sent
	 * @param authorization
	 *           OAuth2 token
	 * @param ownerId
	 *           owner id for which the business context is sent
	 * @return no content
	 */
	@POST
	@Produces(CpqClientConstants.HTTP_PRODUCE_APPL_JSON)
	@Path("/api/configuration/v1/configurations/configurationContext")
	Observable<RawResponse<Object>> createOrUpdateConfigurationContext(BusinessContextRequest businessContext,
			@HeaderParam(CpqClientConstants.HTTP_HEADER_AUTHORIZATION) String authorization);

	/**
	 * Marks a persisted configuration as permanent.<br>
	 * Should be called when the UI is 'done' configuring. Afterwards admin/server is required to do any changes.
	 * UI/Client scope is not sufficient anymore to do changes.
	 *
	 * @param authorization
	 *           OAuth2 token
	 * @param configId
	 *           config id
	 * @param requestData
	 *           request body
	 * @return no content
	 */
	@PATCH
	@Produces(CpqClientConstants.HTTP_PRODUCE_APPL_JSON)
	@Path("/api/configuration/v1/configurations/{configId}")
	Observable<RawResponse<Object>> makePermanent(@HeaderParam(CpqClientConstants.HTTP_HEADER_AUTHORIZATION) String authorization,
			@PathParam(value = "configId") String configId, ConfigurationPatchRequest requestData);
}
