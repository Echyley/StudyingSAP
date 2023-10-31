/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.spring.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserIdDecorationService;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;
import de.hybris.platform.servicelayer.user.impl.DefaultUserIdDecorationService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@IntegrationTest
public class CoreAuthenticationProviderTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private DefaultUserIdDecorationService defaultUserIdDecorationService;

	private UserManager userManager;
	private final CoreAuthenticationProvider coreAuthenticationProvider = spy(CoreAuthenticationProvider.class);
	private final RejectUserPreAuthenticationChecks rejectUserPreAuthenticationChecks = spy(
			RejectUserPreAuthenticationChecks.class);
	private final CoreUserDetailsService coreUserDetailsService = new CoreUserDetailsService();

	private static final String USER_ID = "test@sap.com";
	private static final String USER_PASSWORD = "1234";
	private static final String FOO_DECORATOR = "|foo";
	private static final String BAR_DECORATOR = "|bar";

	@Before
	public void setUp()
	{
		userManager = UserManager.getInstance();
		coreAuthenticationProvider.setUserDetailsService(coreUserDetailsService);
		coreAuthenticationProvider.setPreAuthenticationChecks(rejectUserPreAuthenticationChecks);
	}

	@After
	public void tearDown()
	{
		userManager.setUserIdDecorationService(defaultUserIdDecorationService);
	}

	@Test
	public void shouldAuthenticateExistingUser()
	{
		//given
		createUser(USER_ID);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);

		//when
		final Authentication authentication = coreAuthenticationProvider.authenticate(testAuthentication);

		//then
		assertThat(authentication).isNotNull();
	}

	@Test
	public void shouldThrowExceptionWhenAuthenticatingNotExistingUser()
	{
		//given
		createUser(USER_ID);
		final Authentication testAuthentication = createTestAuthentication(UUID.randomUUID().toString(), USER_PASSWORD);

		//when, then
		assertThatThrownBy(() -> coreAuthenticationProvider.authenticate(testAuthentication)).isInstanceOf(
				BadCredentialsException.class);
	}

	@Test
	public void shouldAuthenticateUserWithDecoratedUserId()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);

		//when
		final Authentication authentication = coreAuthenticationProvider.authenticate(testAuthentication);

		//then
		assertThat(authentication).isNotNull();
	}

	@Test
	public void shouldAuthenticateUserWithDecoratedUserIdByManyStrategies()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR, BAR_DECORATOR);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);

		//when
		final Authentication authentication = coreAuthenticationProvider.authenticate(testAuthentication);

		//then
		assertThat(authentication).isNotNull();
	}

	@Test
	public void shouldLookUpUserByPKWhenAuthenticatingAndPKIsNotNull()
	{
		//given
		createUser(USER_ID);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);

		//when
		coreAuthenticationProvider.authenticate(testAuthentication);

		//then
		verify(coreAuthenticationProvider, times(1)).getUserByPK(any());
		verify(coreAuthenticationProvider, times(0)).getUserByLogin(any());
		verify(rejectUserPreAuthenticationChecks, times(1)).getUserByPK(any());
		verify(rejectUserPreAuthenticationChecks, times(0)).getUserByLogin(any());
	}

	@Test
	public void shouldLookUpUserByLoginWhenAuthenticatingAndPKIsNull()
	{
		//given
		createUser(USER_ID);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);
		when(coreAuthenticationProvider.retrieveUser(USER_ID)).thenReturn(getCoreUserDetailsWithNullPK(USER_ID));

		//when
		coreAuthenticationProvider.authenticate(testAuthentication);

		//then
		verify(coreAuthenticationProvider, times(1)).getUserByLogin(any());
		verify(coreAuthenticationProvider, times(0)).getUserByPK(any());
		verify(rejectUserPreAuthenticationChecks, times(1)).getUserByLogin(any());
		verify(rejectUserPreAuthenticationChecks, times(0)).getUserByPK(any());
	}

	@Test
	public void shouldLookUpUserByLoginWhenAuthenticatingAndUserDetailsIsNotInstanceOfCoreUserDetails()
	{
		//given
		createUser(USER_ID);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);
		when(coreAuthenticationProvider.retrieveUser(USER_ID)).thenReturn(getUserDetails(USER_ID));

		//when
		coreAuthenticationProvider.authenticate(testAuthentication);

		//then
		verify(coreAuthenticationProvider, times(1)).getUserByLogin(any());
		verify(coreAuthenticationProvider, times(0)).getUserByPK(any());
	}

	@Test
	public void shouldThrowExceptionIfStrategyIsNotAwareOfAlreadyDecoratedUserID()
	{
		/* the exception will be thrown in case that configured UserIdDecorationStrategy is not resistance for situations where
		the such strategy can decorate the same user identifier many times */

		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createUserWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);
		when(coreAuthenticationProvider.getPKFromUserDetails(any())).thenReturn(null);

		//when, then
		assertThatThrownBy(() -> coreAuthenticationProvider.authenticate(testAuthentication)).isInstanceOf(
				BadCredentialsException.class);
	}

	@Test
	public void shouldAuthenticateUserIfStrategyIsAwareOfAlreadyDecoratedUserID()
	{
		//given
		final UserIdDecorationStrategy awareAlreadyDecoratedUserIdDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(awareAlreadyDecoratedUserIdDecorationStrategy.decorateUserId(USER_ID)).thenReturn(
				Optional.of(USER_ID + FOO_DECORATOR));
		when(awareAlreadyDecoratedUserIdDecorationStrategy.decorateUserId(USER_ID + FOO_DECORATOR)).thenReturn(Optional.empty());
		configureUserIdDecorationService(List.of(awareAlreadyDecoratedUserIdDecorationStrategy));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		final Authentication testAuthentication = createTestAuthentication(USER_ID, USER_PASSWORD);
		when(coreAuthenticationProvider.getPKFromUserDetails(any())).thenReturn(null);

		//when
		final Authentication authentication = coreAuthenticationProvider.authenticate(testAuthentication);

		//then
		assertThat(authentication).isNotNull();
	}

	private CoreUserDetails getCoreUserDetailsWithNullPK(final String userId)
	{
		final CoreUserDetails coreUserDetails = coreUserDetailsService.loadUserByUsername(userId);

		return new CoreUserDetails(coreUserDetails.getUsername(), coreUserDetails.getPassword(), coreUserDetails.isEnabled(),
				coreUserDetails.isAccountNonExpired(), coreUserDetails.isCredentialsNonExpired(),
				coreUserDetails.isAccountNonLocked(), coreUserDetails.getAuthorities(), coreUserDetails.getLanguageISO(), null);
	}

	private org.springframework.security.core.userdetails.User getUserDetails(final String userId)
	{
		final CoreUserDetails coreUserDetails = coreUserDetailsService.loadUserByUsername(userId);

		return new org.springframework.security.core.userdetails.User(coreUserDetails.getUsername(),
				coreUserDetails.getPassword(), coreUserDetails.isEnabled(),
				coreUserDetails.isAccountNonExpired(), coreUserDetails.isCredentialsNonExpired(),
				coreUserDetails.isAccountNonLocked(), coreUserDetails.getAuthorities());
	}

	private void createUser(final String userId)
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(userId);
		user.setEncodedPassword(USER_PASSWORD);

		modelService.save(user);
	}

	private void createUserWithDecoratedUserId(final String userId, final String... decorators)
	{
		createUser(decorateUserIdWithDecorators(userId, decorators));
	}

	private void createCustomer(final String userId)
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setUid(userId);
		user.setEncodedPassword(USER_PASSWORD);

		modelService.save(user);
	}

	private void createCustomerWithDecoratedUserId(final String userId, final String... decorators)
	{
		createCustomer(decorateUserIdWithDecorators(userId, decorators));
	}

	private String decorateUserIdWithDecorators(final String userId, final String... decorators)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(userId);

		for (final String decorator : decorators)
		{
			stringBuilder.append(decorator);
		}

		return stringBuilder.toString();
	}

	private Authentication createTestAuthentication(final String principal, final String credentials)
	{

		return new UsernamePasswordAuthenticationToken(principal, credentials);
	}

	private static String returnFirstArg(final InvocationOnMock invocation)
	{
		return (String) invocation.getArguments()[0];
	}

	private void configureUserIdDecorationService(final List<UserIdDecorationStrategy> userIdDecorationStrategies)
	{
		final UserIdDecorationService userIdDecorationService = new DefaultUserIdDecorationService(userIdDecorationStrategies);

		userManager.setUserIdDecorationService(userIdDecorationService);
	}
}