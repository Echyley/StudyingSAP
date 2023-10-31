/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutAuthenticationProviderTest
{
	public static final String PUNCHOUT_USER_ID = "punchoutUserId@cxml.org";
	public static final String ANONYMOUS = "anonymous";

	@InjectMocks
	private PunchOutAuthenticationProvider punchOutAuthenticationProvider;

	@Mock
	private UserService userService;
	@Mock
	private CartService cartService;
	@Mock
	private UserDetailsService userDetailsService;

	@Test
	public void testAuthenticate()
	{
		final CartModel cartModel = new CartModel();
		final UserModel userModel = new UserModel();
		userModel.setUid(PUNCHOUT_USER_ID);
		cartModel.setUser(userModel);
		when(cartService.getSessionCart()).thenReturn(cartModel);

		final CustomerModel customerModel = new CustomerModel();
		customerModel.setUid(ANONYMOUS);

		final Authentication authentication = new UsernamePasswordAuthenticationToken(PUNCHOUT_USER_ID, null);
		doReturn(createUserDetails(authentication)).when(userDetailsService).loadUserByUsername(authentication.getName());

		try(
				final MockedStatic mockedUserManager = mockStatic(UserManager.class);
				final MockedStatic mockedRegistry = mockStatic(Registry.class);
				final MockedStatic mockedJaloConnection = mockStatic(JaloConnection.class);
				final MockedStatic mockedJaloSession = mockStatic(JaloSession.class)
		) {
			final JaloSession jaloSession = mock(JaloSession.class);

			final JaloConnection jaloConnection = mock(JaloConnection.class);
			when(jaloConnection.isSystemInitialized()).thenReturn(true);

			final UserManager userManager = mock(UserManager.class);
			when(userManager.getUserByLogin(PUNCHOUT_USER_ID)).thenReturn(new User());

			mockedRegistry.when(Registry::hasCurrentTenant).thenReturn(true);
			mockedJaloConnection.when(JaloConnection::getInstance).thenReturn(jaloConnection);
			mockedJaloSession.when(JaloSession::getCurrentSession).thenReturn(jaloSession);
			mockedUserManager.when(UserManager::getInstance).thenReturn(userManager);

			final Authentication response = punchOutAuthenticationProvider.authenticate(authentication);

			assertThat(response).isNotNull();
			assertThat(response.isAuthenticated()).isTrue();
			assertThat(response.getName()).isEqualTo(PUNCHOUT_USER_ID);
			verify(jaloSession).setUser(any());
			verify(cartService, never()).setSessionCart(null);
		}
	}

	@Test
	public void testAuthenticateWrongUser()
	{
		final CartModel cartModel = new CartModel();
		final UserModel userModel = new UserModel();
		userModel.setUid(PUNCHOUT_USER_ID);
		cartModel.setUser(userModel);
		when(cartService.getSessionCart()).thenReturn(cartModel);

		final CustomerModel customerModel = new CustomerModel();
		customerModel.setUid(ANONYMOUS);
		when(userService.getAnonymousUser()).thenReturn(customerModel);

		final Authentication authentication = new UsernamePasswordAuthenticationToken("wrongUser", null);

		try(
				final MockedStatic mockedRegistry = mockStatic(Registry.class);
				final MockedStatic mockedJaloConnection = mockStatic(JaloConnection.class)
		) {
			final JaloConnection jaloConnection = mock(JaloConnection.class);
			when(jaloConnection.isSystemInitialized()).thenReturn(true);
			mockedRegistry.when(Registry::hasCurrentTenant).thenReturn(true);
			mockedJaloConnection.when(JaloConnection::getInstance).thenReturn(jaloConnection);
			when(userDetailsService.loadUserByUsername(authentication.getName())).thenThrow(new UsernameNotFoundException(""));
			assertThatThrownBy(() -> punchOutAuthenticationProvider.authenticate(authentication)).isInstanceOf(BadCredentialsException.class);
		}
	}

	private UserDetails createUserDetails(Authentication authentication) {
		final UserDetails userDetails = mock(UserDetails.class);
		when(userDetails.isEnabled()).thenReturn(true);
		when(userDetails.isAccountNonExpired()).thenReturn(true);
		when(userDetails.isAccountNonLocked()).thenReturn(true);
		when(userDetails.isCredentialsNonExpired()).thenReturn(true);
		when(userDetails.getUsername()).thenReturn(authentication.getName());
		return userDetails;
	}
}
