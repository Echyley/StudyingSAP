/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigurationUserIdProviderImplTest
{
	private static final String USER_UID = "user uid";
	private static final String SESSION_ID = "session id";
	@Mock
	private UserService userService;
	@Mock
	private SessionService sessionService;
	@Mock
	private Session session;
	@Mock
	private UserModel user;
	@InjectMocks
	private ProductConfigurationUserIdProviderImpl classUnderTest;

	@Before
	public void setup()
	{
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(user.getUid()).thenReturn(USER_UID);
		Mockito.when(sessionService.getCurrentSession()).thenReturn(session);
		Mockito.when(session.getSessionId()).thenReturn(SESSION_ID);
	}

	@Test
	public void testGetCurrentUserIdAnonymous()
	{
		Mockito.when(userService.isAnonymousUser(user)).thenReturn(true);
		final String currentUserId = classUnderTest.getCurrentUserId();
		assertNotNull(currentUserId);
		assertEquals(SESSION_ID, currentUserId);
	}

	@Test
	public void testGetCurrentUserIdUserInSession()
	{
		final String currentUserId = classUnderTest.getCurrentUserId();
		assertNotNull(currentUserId);
		assertEquals(USER_UID, currentUserId);
	}

	@Test
	public void testIsAnonymousNamedCase()
	{
		assertFalse(classUnderTest.isAnonymousUser());
	}

	@Test
	public void testIsAnonymous()
	{
		Mockito.when(userService.isAnonymousUser(user)).thenReturn(true);
		assertTrue(classUnderTest.isAnonymousUser());
	}
}
