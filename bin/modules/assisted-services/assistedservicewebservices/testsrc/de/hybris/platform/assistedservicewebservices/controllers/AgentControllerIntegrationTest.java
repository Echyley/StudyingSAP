/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.controllers;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedservicewebservices.constants.AssistedservicewebservicesConstants;
import de.hybris.platform.assistedservicewebservices.dto.ASMPointOfServiceListWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.ASMPointOfServiceWsDTO;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;



import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
		{ AssistedservicewebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class AgentControllerIntegrationTest extends AbstractControllerIntegrationIntegrationTest
{
	public static final String AGENT_URI = "/agents/";
	public static final String ASAGENT = "new_asagent";
	private static final String CURRENT_USER = "current";
	public static final String DUMMY_USER_UID = "dummyuser";
	public static final String NOT_ASAGENT = "notasagent";
	public static final String STORE_URI = "/pointOfServices";
	public static final String NO_STORE_ASSIGNED_AGENT = "asagentwithnocustomerlists";

	@Test
	public void shouldGetAgentsStores()
	{
		final String path = AGENT_URI + ASAGENT + STORE_URI;
		final Response response = requestAgentStores(ASAGENT, path);
		final ASMPointOfServiceListWsDTO results = response.readEntity(
				ASMPointOfServiceListWsDTO.class);
		final List<ASMPointOfServiceWsDTO> pointOfServicesList = results.getPointOfServices();
		assertResponse(Response.Status.OK, response);
		assertThat(pointOfServicesList).hasSize(2).extracting("id", "name").contains(tuple("Nakano", "Nakano"), tuple("ws_warehouse_n", "Warehouse North"));
	}

	@Test
	public void shouldGetAgentsStoresWithCurrent()
	{
		final String path = AGENT_URI + CURRENT_USER + STORE_URI;
		final Response response = requestAgentStores(ASAGENT, path);
		final ASMPointOfServiceListWsDTO results = response.readEntity(
				ASMPointOfServiceListWsDTO.class);
		final List<ASMPointOfServiceWsDTO> pointOfServicesList = results.getPointOfServices();
		assertResponse(Response.Status.OK, response);
		assertThat(pointOfServicesList).hasSize(2).extracting("id", "name").contains(tuple("Nakano", "Nakano"), tuple("ws_warehouse_n", "Warehouse North"));
	}

	@Test
	public void shouldGetStoresWithIgoringBaseSite()
	{
		final String url = AGENT_URI + ASAGENT + STORE_URI;
		final Response response = getWsSecuredRequestBuilder(ASAGENT, OAUTH_PASSWORD).path(url)
				.queryParam(BASE_SITE_PARAM, BASE_SITE_ID)
				.build()
				.accept(MediaType.APPLICATION_JSON).get();
		response.bufferEntity();
		final ASMPointOfServiceListWsDTO results = response.readEntity(
				ASMPointOfServiceListWsDTO.class);
		final List<ASMPointOfServiceWsDTO> pointOfServicesList = results.getPointOfServices();
		assertResponse(Response.Status.OK, response);
		assertThat(pointOfServicesList).hasSize(2).extracting("id", "name").contains(tuple("Nakano", "Nakano"), tuple("ws_warehouse_n", "Warehouse North"));
	}

	@Test
	public void shouldNotGetStoresWithNonExistAgent()
	{
		final String path = AGENT_URI + DUMMY_USER_UID + STORE_URI;
		final Response response = requestAgentStores(ASAGENT, path);
		assertResponse(Response.Status.BAD_REQUEST, response);
	}

	@Test
	public void shouldGetEmptyStoresWithNoStoreAssignedAgent()
	{
		final String path = AGENT_URI + NO_STORE_ASSIGNED_AGENT + STORE_URI;
		final Response response = requestAgentStores(NO_STORE_ASSIGNED_AGENT, path);
		final ASMPointOfServiceListWsDTO results = response.readEntity(
				ASMPointOfServiceListWsDTO.class);
		assertResponse(Response.Status.OK, response);
		assertTrue(isEmpty(results.getPointOfServices()));
	}

	@Test
	public void shouldNotGetStoresWithInvalidToken()
	{
		final String path = AGENT_URI + ASAGENT + STORE_URI;
		final Response response = requestAgentStores(DUMMY_USER_UID, path);
		assertResponse(Response.Status.UNAUTHORIZED, response);
	}




	@Test
	public void shouldNotGetStoresWithNotInAsAgentGroupAgent()
	{
		final String path = AGENT_URI + NOT_ASAGENT + STORE_URI;
		final Response response = requestAgentStores(NOT_ASAGENT, path);
		assertResponse(Response.Status.FORBIDDEN, response);
	}


	protected Response requestAgentStores(final String userName, final String url)
	{
		final Response response = getWsSecuredRequestBuilder(userName, OAUTH_PASSWORD).path(url)
				.build()
				.accept(MediaType.APPLICATION_JSON).get();
		response.bufferEntity();
		return response;
	}

	private boolean isEmpty (List<ASMPointOfServiceWsDTO> object) {
		return ((object == null) || (object.size() == 0));
	}

}
