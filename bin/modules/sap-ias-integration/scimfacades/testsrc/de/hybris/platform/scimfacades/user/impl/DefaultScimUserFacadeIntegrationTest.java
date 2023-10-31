/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.user.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.ScimUserEmail;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@IntegrationTest
public class DefaultScimUserFacadeIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private DefaultScimUserFacade scimUserFacade;

	private static final String USER_ID = "user-id";
	private static final String NEW_USER_ID = "newUser";
	private static final String TEST_TEST_COM = "test@test.com";
	private static final String NEWTEST_NEWTEST_COM = "newtest@newtest.com";
	
	private ScimUser user;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private static final Logger LOG = Logger.getLogger(DefaultScimUserFacadeIntegrationTest.class);

	@Before
	public void setUp() throws ScimException
	{
		LOG.info("Default Scim User Facade Integration Test");
		user = new ScimUser();
		user.setId(USER_ID);
		user.setUserType("employee");
		final ScimUserEmail email = new ScimUserEmail();
		email.setValue(TEST_TEST_COM);
		email.setPrimary(true);
		user.setEmails(Collections.singletonList(email));
		scimUserFacade.createUser(user);
	}

	@Test
	public void shouldCreateUser() {
		final ScimUser newUser = createNewUser();
		final EmployeeModel exampleEmployee = new EmployeeModel();
		exampleEmployee.setScimUserId(NEW_USER_ID);
		final EmployeeModel employee = flexibleSearchService.getModelByExample(exampleEmployee);
		Assert.assertEquals(newUser.getId(), employee.getScimUserId());
		Assert.assertEquals(NEWTEST_NEWTEST_COM, employee.getUid());

	}

	@Test
	public void shouldGetUser() {
		final ScimUser returnedUser = scimUserFacade.getUser(USER_ID);
		Assert.assertEquals(user.getId(), returnedUser.getId());
		final ScimUserEmail email = user.getEmails().get(0);
		Assert.assertEquals(TEST_TEST_COM, email.getValue());
		Assert.assertTrue(email.getPrimary());
	}

	@Test
	public void shouldGetAllUsers()
	{
		createNewUser();
		final String[] ids =
		{ NEW_USER_ID, USER_ID };
		final List<ScimUser> returnedScimUsers = scimUserFacade.getUsers();
		for (final ScimUser returnedUser : returnedScimUsers)
		{
			if (returnedUser.getId() != null)
			{
				Assert.assertTrue(Arrays.asList(ids).contains(returnedUser.getId()));
			}

		}
	}

	@Test
	public void shouldDeleteUser() {
		final boolean returnedValue = scimUserFacade.deleteUser(USER_ID);
		Assert.assertTrue(returnedValue);
		final UserModel exampleEmployee = new UserModel();
		exampleEmployee.setScimUserId(USER_ID);
		LOG.info(flexibleSearchService.getModelByExample(exampleEmployee));
		Assert.assertFalse(exampleEmployee.isLoginDisabled());
	}

	@Test
	public void shouldUpdateUser() {
		user.setActive(true);
		scimUserFacade.updateUser(USER_ID, user);

		final EmployeeModel exampleEmployee = new EmployeeModel();
		exampleEmployee.setScimUserId(USER_ID);
		final UserModel employee = flexibleSearchService.getModelByExample(exampleEmployee);
		Assert.assertFalse(employee.isLoginDisabled());
		Assert.assertEquals(TEST_TEST_COM, employee.getUid());
	}

	private ScimUser createNewUser() {
		final ScimUser newUser = new ScimUser();
		newUser.setId(NEW_USER_ID);
		newUser.setUserType("employee");
		final ScimUserEmail newEmail = new ScimUserEmail();
		newEmail.setValue(NEWTEST_NEWTEST_COM);
		newEmail.setPrimary(true);
		newUser.setEmails(Collections.singletonList(newEmail));
		return scimUserFacade.createUser(newUser);
	}



}