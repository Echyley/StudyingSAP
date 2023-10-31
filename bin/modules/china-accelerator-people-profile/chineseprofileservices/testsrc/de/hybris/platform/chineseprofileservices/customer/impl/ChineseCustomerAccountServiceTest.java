/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileservices.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.chineseprofileservices.data.VerificationData;
import de.hybris.platform.chineseprofileservices.strategies.VerificationCodeStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;
import de.hybris.platform.servicelayer.user.daos.UserDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@IntegrationTest
public class ChineseCustomerAccountServiceTest extends ServicelayerTransactionalTest
{

	@Resource(name = "chineseCustomerAccountService")
	private DefaultChineseCustomerAccountService customerAccountService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "chineseUserDao")
	private UserDao userDao;

	@Resource(name = "verificationCodeStrategy")
	private VerificationCodeStrategy verificationCodeStrategy;

	private VerificationData data;

	private CustomerModel customer;

	private BaseSiteModel baseSite;

	private List<UserIdDecorationStrategy> userIdDecorationStrategies;

	@Mock
	private UserIdDecorationStrategy userIdDecorationStrategy;

	@Before
	public void prepare()
	{
		customer = new CustomerModel();
		customer.setLoginDisabled(false);
		customer.setUid("test@gmail.com");
		customer.setMobileNumber("12345678910");
		customer.setEncodedPassword("123456123456");
		modelService.save(customer);

		data = new VerificationData();
		data.setMobileNumber("13800000000");
		data.setTime(Calendar.getInstance().getTime());

		baseSite = new BaseSiteModel();
		baseSite.setUid("isolated-site-a");
		baseSite.setDataIsolationEnabled(true);

		userIdDecorationStrategies = new ArrayList();
		userIdDecorationStrategy = mock(UserIdDecorationStrategy.class);
		userIdDecorationStrategies.add(userIdDecorationStrategy);
		final DefaultChineseCustomerAccountService customerAccountServiceOjb = new DefaultChineseCustomerAccountService(userIdDecorationStrategies);
		customerAccountServiceOjb.setUserDao(userDao);
		customerAccountServiceOjb.setModelService(modelService);
		customerAccountServiceOjb.setVerificationCodeStrategy(verificationCodeStrategy);
		customerAccountService = Mockito.spy(customerAccountServiceOjb);
		when(customerAccountService.getUserIdDecorationStrategies()).thenReturn(userIdDecorationStrategies);
	}

	@Test
	public void test_generateVerificationCode()
	{
		final String code = customerAccountService.generateVerificationCode();
		assertEquals(4, code.length());
	}

	@Test
	public void test_updateMobileNumber()
	{
		customer.setMobileNumber("13800000000");
		modelService.save(customer);
		customerAccountService.updateMobileNumber(customer);

		final CustomerModel model = (CustomerModel) userDao.findUserByUID("13800000000");
		assertEquals("13800000000", model.getMobileNumber());
	}

	@Test
	public void test_getNonIsolatedCustomerForMobileNumber()
	{
		customer.setMobileNumber("13800000000");
		modelService.save(customer);
		when(userIdDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.of("13800000000"));
		final Optional<CustomerModel> customer = customerAccountService.getCustomerForMobileNumber("13800000000");
		assertEquals("13800000000", customer.map(CustomerModel::getMobileNumber).orElse(""));
	}

	@Test
	public void test_getIsolatedCustomerForMobileNumber()
	{
		final CustomerModel isolatedCustomer = new CustomerModel();
		isolatedCustomer.setLoginDisabled(false);
		isolatedCustomer.setUid("test@gmail.com|isolated-site-a");
		isolatedCustomer.setMobileNumber("13800000000");
		isolatedCustomer.setEncodedPassword("123456123456");
		isolatedCustomer.setSite(baseSite);
		modelService.save(isolatedCustomer);
		when(userIdDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.of("13800000000|isolated-site-a"));
		final Optional<CustomerModel> customer = customerAccountService.getCustomerForMobileNumber("13800000000");
		assertEquals("13800000000", customer.map(CustomerModel::getMobileNumber).orElse(""));
	}

	@Test
	public void test_getUnknownCustomerForMobileNumber()
	{
		modelService.remove(customer);
		when(userIdDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.of("13800000000"));
		final Optional<CustomerModel> customer = customerAccountService.getCustomerForMobileNumber("13800000000");
		assertFalse(customer.isPresent());
	}
}
