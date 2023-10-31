/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileservices.security.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chineseprofileservices.security.ChineseUserDetailsService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;
import de.hybris.platform.servicelayer.user.daos.UserDao;
import de.hybris.platform.spring.security.CoreUserDetails;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@IntegrationTest
public class ChineseUserDetailsServiceTest extends ServicelayerTransactionalTest
{
	private final String USER_1 = "a@sap.com";
	private final String USER_1_MOBILE = "18108050323";
	private final String UNKNOWN_USER = "d@sap.com";
	private final String ISOLATED_USER = "b@sap.com";

	@Resource
	private ChineseUserDetailsService chineseUserDetailsService;

	@Resource
	private UserDao userDao;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private ModelService modelService;

	private List<UserIdDecorationStrategy> userIdDecorationStrategies;

	@Mock
	private UserIdDecorationStrategy userIdDecorationStrategy;

	@Before
	public void prepare() throws ImpExException
	{
		importCsv("/chineseprofileservices/test/impex/chineseprofileservices-test-data.impex", "utf-8");
		userIdDecorationStrategies = new ArrayList();
		userIdDecorationStrategy = mock(UserIdDecorationStrategy.class);
		userIdDecorationStrategies.add(userIdDecorationStrategy);
		final ChineseUserDetailsService chineseUserDetailsServiceObj = new ChineseUserDetailsService(userIdDecorationStrategies);
		chineseUserDetailsServiceObj.setUserDao(userDao);
		chineseUserDetailsServiceObj.setCommonI18NService(commonI18NService);
		chineseUserDetailsServiceObj.setModelService(modelService);
		chineseUserDetailsService = Mockito.spy(chineseUserDetailsServiceObj);
		when(chineseUserDetailsService.getUserIdDecorationStrategies()).thenReturn(userIdDecorationStrategies);
	}


	@Test
	public void test_Load_Exists_Nonisolated_User() throws JaloInvalidParameterException, JaloSecurityException, JaloBusinessException
	{
		when(userIdDecorationStrategy.decorateUserId(USER_1)).thenReturn(Optional.of(USER_1));
		final CoreUserDetails coreUserDetail = chineseUserDetailsService.loadUserByUsername(USER_1);

		assertEquals("12341234", coreUserDetail.getPassword());
		assertEquals(new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP"), coreUserDetail.getAuthorities().toArray()[0]);
	}

	@Test
	public void test_Load_Exists_Isolated_User() throws JaloInvalidParameterException, JaloSecurityException, JaloBusinessException
	{
		when(userIdDecorationStrategy.decorateUserId(ISOLATED_USER)).thenReturn(Optional.of("b@sap.com|test-site-a"));
		final CoreUserDetails coreUserDetail = chineseUserDetailsService.loadUserByUsername(ISOLATED_USER);

		assertEquals("12341234", coreUserDetail.getPassword());
		assertEquals("b@sap.com|test-site-a", coreUserDetail.getUsername());
	}

	@Test
	public void test_Load_Exists_User_Mobile() throws JaloInvalidParameterException, JaloSecurityException, JaloBusinessException
	{
		when(userIdDecorationStrategy.decorateUserId(USER_1_MOBILE)).thenReturn(Optional.of(USER_1_MOBILE));
		final CoreUserDetails coreUserDetail = chineseUserDetailsService.loadUserByUsername(USER_1_MOBILE);

		assertEquals("12341234", coreUserDetail.getPassword());
		assertEquals(new SimpleGrantedAuthority("ROLE_CUSTOMERGROUP"), coreUserDetail.getAuthorities().toArray()[0]);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void test_Load_Unknown_User()
	{
		when(userIdDecorationStrategy.decorateUserId(UNKNOWN_USER)).thenReturn(Optional.of(UNKNOWN_USER));
		chineseUserDetailsService.loadUserByUsername(UNKNOWN_USER);
	}

	@Test
	public void testLoadUserByNullName()
	{
		final CoreUserDetails result;
		result = chineseUserDetailsService.loadUserByUsername(null);
		assertNull(result);
	}
}
