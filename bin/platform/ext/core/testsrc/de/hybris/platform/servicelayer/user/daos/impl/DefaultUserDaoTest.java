/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.user.daos.impl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.internal.dao.SortParameters.SortOrder;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Tests the implementation of the UserDao.
 */
@IntegrationTest
public class DefaultUserDaoTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private DefaultUserDao userDao;

	@Test
	public void testFoundNothing()
	{
		assertNull("does not match expected result", userDao.findUserByUID("xxx"));
	}

	@Test
	public void testFoundOneUser()
	{
		final UserModel user = userDao.findUserByUID("anonymous");
		assertNotNull("does not match expected result", user);
		assertEquals("anonymous is not a customer", CustomerModel.class, user.getClass());
	}

	@Test
	public void testMultipleFoundNothing()
	{
		final Collection<UserModel> users = userDao.findUsersByUids(SortOrder.DESCENDING, "xxx");
		assertNotNull("does not match expected result", users);
		assertEquals("does not match expected result", 0, users.size());
	}

	@Test
	public void testMultipleNullArgument()
	{
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userDao.findUsersByUids(null, null));
	}

	@Test
	public void testMultipleEmptyArgument()
	{
		final Collection<UserModel> users = userDao.findUsersByUids(null);
		assertNotNull("does not match expected result", users);
		assertEquals("does not match expected result", 0, users.size());
	}

	@Test
	public void testMultipleFoundOneUser()
	{
		final Collection<UserModel> users = userDao.findUsersByUids(SortOrder.DESCENDING, "anonymous");
		assertNotNull("does not match expected result", users);
		assertEquals("anonymous", users.iterator().next().getUid());
	}

	@Test
	public void testMultipleFoundMoreUsersDescendingOrder()
	{
		final Collection<UserModel> users = userDao.findUsersByUids(SortOrder.DESCENDING, "anonymous", "admin");
		assertNotNull("does not match expected result", users);
		assertEquals(2, users.size());
		assertEquals("anonymous", users.iterator().next().getUid());
	}

	@Test
	public void testMultipleFoundMoreUsersAscendingOrder()
	{
		final Collection<UserModel> users = userDao.findUsersByUids(SortOrder.ASCENDING, "anonymous", "admin");
		assertNotNull("does not match expected result", users);
		assertEquals(2, users.size());
		assertEquals("admin", users.iterator().next().getUid());
	}

	@Test
	public void testMultipleFoundMoreUsersDefaultOrder()
	{
		final Collection<UserModel> users = userDao.findUsersByUids(null, "anonymous", "admin");
		assertNotNull("does not match expected result", users);
		assertEquals(2, users.size());
		assertEquals("admin", users.iterator().next().getUid());
	}
}
