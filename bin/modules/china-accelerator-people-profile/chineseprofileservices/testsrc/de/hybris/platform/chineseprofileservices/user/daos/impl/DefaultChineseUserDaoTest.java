/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileservices.user.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultChineseUserDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private DefaultChineseUserDao chineseUserDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Before
	public void prepare()
	{
		final CustomerModel customer = new CustomerModel();
		customer.setLoginDisabled(false);
		customer.setUid("test@gmail.com");
		customer.setMobileNumber("12345678910");
		customer.setEncodedPassword("123456123456");
		modelService.save(customer);
	}

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void test_Find_User_By_Email()
	{
		final UserModel user = chineseUserDao.findUserByUID("test@gmail.com");
		assertEquals("123456123456", user.getEncodedPassword());
		assertEquals("test@gmail.com", user.getUid());
	}

	@Test
	public void test_Find_Non_Isolated_User_By_Mobile()
	{
		final UserModel user = chineseUserDao.findUserByUID("12345678910");
		assertEquals("123456123456", user.getEncodedPassword());
		assertEquals("test@gmail.com", user.getUid());
	}

	@Test
	public void test_Find_Isolated_User_By_Mobile()
	{
		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("isolated-site-a");
		baseSite.setDataIsolationEnabled(true);
		final CustomerModel customer = new CustomerModel();
		customer.setLoginDisabled(false);
		customer.setUid("test@gmail.com|isolated-site-a");
		customer.setMobileNumber("12345678910");
		customer.setEncodedPassword("123456123456");
		customer.setSite(baseSite);
		modelService.save(customer);
		final UserModel user = chineseUserDao.findUserByUID("12345678910|isolated-site-a");
		assertEquals("123456123456", user.getEncodedPassword());
		assertEquals("test@gmail.com|isolated-site-a", user.getUid());
	}

	@Test
	public void test_Find_Unknown_User()
	{
		final UserModel user = chineseUserDao.findUserByUID("aaa@gmail.com");
		assertNull(user);
	}

	@Test
	public void test_Find_User_By_Null_Value()
	{
		final UserModel user = chineseUserDao.findUserByUID(null);
		assertNull(user);
	}
}
