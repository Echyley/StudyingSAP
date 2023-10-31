/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.controllers;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.permissionswebservices.constants.PermissionswebservicesConstants;
import de.hybris.platform.permissionswebservices.controllers.AbstractPermissionsWebServicesTest;
import de.hybris.platform.permissionswebservices.dto.CatalogPermissionsListWsDTO;
import de.hybris.platform.permissionswebservices.dto.PermissionsListWsDTO;
import de.hybris.platform.permissionswebservices.dto.PermissionsWsDTO;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.webservicescommons.jaxb.Jaxb2HttpMessageConverter;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions = { PermissionswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class SmarteditPermissionsContractTest extends AbstractPermissionsWebServicesTest
{
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private static final String READ = "read";
	private static final String WRITE = "write";
	private static final String REMOVE = "remove";
	private static final String CREATE = "create";
	private static final String CHANGE = "change";
	private static final String TESTADMIN = "testadmin";
	private static final String TESTADMIN_PWD = "1234";
	private static final String PRINCIPAL_UID = "principalUid";
	private static final String CHANGE_PERM = "changerights";
	private static final String SE_TESTER_1 = "se-permission-tester1";
	private static final String SE_TESTER_2 = "se-permission-tester2";
	private static final String SE_GLOBAL_PERMISSION = "se-global-permission";
	private static final String SEARCH_GLOBAL = "/permissions/global/search";
	private static final String SEARCH_CATALOGS = "/permissions/catalogs/search";
	private static final String SEARCH_TYPE = "/permissions/types/search";
	private static final String SEARCH_ATTRIBUTES = "/permissions/attributes/search";
	private static final Map<String, String> CV_PERMISSIONS;
	private static final Map<String, String> TYPE_PERMISSIONS;
	private static final Map<String, String> ATTRIBUTES_PERMISSIONS;

	static
	{
		CV_PERMISSIONS = new HashMap<>();
		CV_PERMISSIONS.put(READ, TRUE);
		CV_PERMISSIONS.put(WRITE, TRUE);

		TYPE_PERMISSIONS = new HashMap<>();
		TYPE_PERMISSIONS.put(READ, TRUE);
		TYPE_PERMISSIONS.put(CHANGE, TRUE);
		TYPE_PERMISSIONS.put(CREATE, FALSE);
		TYPE_PERMISSIONS.put(REMOVE, TRUE);

		ATTRIBUTES_PERMISSIONS = new HashMap<>();
		ATTRIBUTES_PERMISSIONS.put(READ, TRUE);
		ATTRIBUTES_PERMISSIONS.put(CHANGE, TRUE);
		ATTRIBUTES_PERMISSIONS.put(CREATE, TRUE);
		ATTRIBUTES_PERMISSIONS.put(REMOVE, FALSE);
		ATTRIBUTES_PERMISSIONS.put(CHANGE_PERM, TRUE);
	}

	@Resource(name = "jsonHttpMessageConverter")
	private Jaxb2HttpMessageConverter defaultJsonHttpMessageConverter;

	private static final String VERSION = "v1";

	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void importTestData() throws ImpExException
	{
		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(PermissionswebservicesConstants.EXTENSIONNAME)//
				.path(VERSION)//
				.client("mobile_android", "secret");
		importData(new ClasspathImpExResource("/smarteditwebservices/test/impex/testpermissions.impex", "UTF-8"));
	}

	@Test
	public void shouldReturnTrueForUserHaveGlobalPermission() throws JAXBException
	{
		//Assign global permission to se-group1
		insertGlobalPermission("se-group1", SE_GLOBAL_PERMISSION);

		//when posting with a test user
		final Response result = wsSecuredRequestBuilder//
				.path(SEARCH_GLOBAL)//
				.queryParam("permissionNames", SE_GLOBAL_PERMISSION)//
				.resourceOwner(TESTADMIN, TESTADMIN_PWD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.post(Entity.json(Map.of(PRINCIPAL_UID, SE_TESTER_1)));

		//then we receive a 200
		Assert.assertEquals(200, result.getStatus());

		final PermissionsWsDTO entity = unmarshallResult(result, PermissionsWsDTO.class);
		Assert.assertEquals(TRUE, entity.getPermissions().get(SE_GLOBAL_PERMISSION));
	}

	@Test
	public void shouldReturnFalseForUserDontHaveGlobalPermission() throws JAXBException
	{

		//when posting with a test user
		final Response result = wsSecuredRequestBuilder//
				.path(SEARCH_GLOBAL)//
				.queryParam("permissionNames", SE_GLOBAL_PERMISSION)//
				.resourceOwner(TESTADMIN, TESTADMIN_PWD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.post(Entity.json(Map.of(PRINCIPAL_UID, SE_TESTER_2)));

		//then we receive a 200
		Assert.assertEquals(200, result.getStatus());

		final PermissionsWsDTO entity = unmarshallResult(result, PermissionsWsDTO.class);
		Assert.assertEquals(FALSE, entity.getPermissions().get(SE_GLOBAL_PERMISSION));
	}

	@Test
	public void shouldReturnCorrectPermissionsForCatalogVersion() throws JAXBException
	{
		//when posting with a test user
		final Response result = wsSecuredRequestBuilder//
				.path(SEARCH_CATALOGS)//
				.queryParam("catalogId", "se-catalog1")//
				.queryParam("catalogVersion", "Staged")//
				.resourceOwner(TESTADMIN, TESTADMIN_PWD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.post(Entity.json(Map.of(PRINCIPAL_UID, SE_TESTER_1)));

		//then we receive a 200
		Assert.assertEquals(200, result.getStatus());

		//should return the correct permissions
		final CatalogPermissionsListWsDTO entity = unmarshallResult(result, CatalogPermissionsListWsDTO.class);
		Assert.assertEquals(entity.getPermissionsList().get(0).getPermissions(), CV_PERMISSIONS);
	}

	@Test
	public void shouldReturnCorrectPermissionsForType() throws JAXBException
	{
		//when posting with a test user
		final Response result = wsSecuredRequestBuilder//
				.path(SEARCH_TYPE)//
				.queryParam("types", "AbstractCMSComponent")//
				.queryParam("permissionNames", "read,change,create,remove")//
				.resourceOwner(TESTADMIN, TESTADMIN_PWD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.post(Entity.json(Map.of(PRINCIPAL_UID, SE_TESTER_1)));

		//then we receive a 200
		Assert.assertEquals(200, result.getStatus());

		//should return the correct permissions
		final PermissionsListWsDTO entity = unmarshallResult(result, PermissionsListWsDTO.class);
		Assert.assertEquals(entity.getPermissionsList().get(0).getPermissions(), TYPE_PERMISSIONS);
	}

	@Test
	public void shouldReturnCorrectPermissionsForAttributes() throws JAXBException
	{
		//when posting with a test user
		final Response result = wsSecuredRequestBuilder//
				.path(SEARCH_ATTRIBUTES)//
				.queryParam("permissionNames", "read,change,create,remove,changerights")//
				.queryParam("attributes", "Item.owner")//
				.resourceOwner(TESTADMIN, TESTADMIN_PWD)//
				.grantResourceOwnerPasswordCredentials()//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.post(Entity.json(Map.of(PRINCIPAL_UID, SE_TESTER_1)));

		//then we receive a 200
		Assert.assertEquals(200, result.getStatus());

		//should return the correct permissions
		final CatalogPermissionsListWsDTO entity = unmarshallResult(result, CatalogPermissionsListWsDTO.class);
		Assert.assertEquals(entity.getPermissionsList().get(0).getPermissions(), ATTRIBUTES_PERMISSIONS);
	}

	protected <C> C unmarshallResult(final Response result, final Class<C> c) throws JAXBException
	{
		final Unmarshaller unmarshaller = defaultJsonHttpMessageConverter.createUnmarshaller(c);
		final StreamSource source = new StreamSource(result.readEntity(InputStream.class));
		final C entity = unmarshaller.unmarshal(source, c).getValue();
		return entity;
	}
}
