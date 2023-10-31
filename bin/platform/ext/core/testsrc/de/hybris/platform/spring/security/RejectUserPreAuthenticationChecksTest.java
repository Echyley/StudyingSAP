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
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserIdDecorationService;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;
import de.hybris.platform.servicelayer.user.impl.DefaultUserIdDecorationService;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.security.core.userdetails.UserDetails;

@IntegrationTest
public class RejectUserPreAuthenticationChecksTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private DefaultUserIdDecorationService defaultUserIdDecorationService;

	private UserManager userManager;
	private final RejectUserPreAuthenticationChecks rejectUserPreAuthenticationChecks = spy(
			RejectUserPreAuthenticationChecks.class);
	private final CoreUserDetailsService coreUserDetailsService = new CoreUserDetailsService();

	private static final String USER_ID = "test@sap.com";
	private static final String FOO = "|foo";
	private static final String USER_PASSWORD = "1234";

	@Before
	public void setUp()
	{
		userManager = UserManager.getInstance();
	}

	@After
	public void tearDown()
	{
		userManager.setUserIdDecorationService(defaultUserIdDecorationService);
	}

	@Test
	public void shouldNotThrowExceptionIfStrategyIsNotAwareOfMultipleDecorationAndLookUpUserIsByPK()
	{
		//given
		createCustomer(USER_ID + FOO);
		final UserIdDecorationStrategy notAwareDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(notAwareDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO));
		configureUserIdDecorationService(List.of(notAwareDecorationStrategy));
		final CoreUserDetails userDetails = getCoreUserDetails(USER_ID);
		assertThat(userDetails.getUsername()).isEqualTo(USER_ID + FOO);

		//when
		rejectUserPreAuthenticationChecks.check(userDetails);

		//then
		verify(rejectUserPreAuthenticationChecks, times(1)).getUserByPK(any());
		verify(rejectUserPreAuthenticationChecks, times(0)).getUserByLogin(any());
	}

	@Test
	public void shouldThrowExceptionIfStrategyIsNotAwareOfMultipleDecorationAndLookupUserIsByLogin()
	{
		//given
		createCustomer(USER_ID + FOO);
		final UserIdDecorationStrategy notAwareDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(notAwareDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO));
		configureUserIdDecorationService(List.of(notAwareDecorationStrategy));
		final CoreUserDetails userDetails = getCoreUserDetails(USER_ID);
		when(rejectUserPreAuthenticationChecks.getPKFromUserDetails(userDetails)).thenReturn(null);


		//when, then
		assertThatThrownBy(() -> rejectUserPreAuthenticationChecks.check(userDetails)).isInstanceOf(
				JaloItemNotFoundException.class);
		verify(rejectUserPreAuthenticationChecks, times(0)).getUserByPK(any());
		verify(rejectUserPreAuthenticationChecks, times(1)).getUserByLogin(any());
	}

	@Test
	public void shouldNotThrowExceptionIfStrategyIsAwareOfMultipleDecorationAndLookupCustomerIsByLogin()
	{
		//given
		createCustomer(USER_ID + FOO);
		final UserIdDecorationStrategy awareDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(awareDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO));
		when(awareDecorationStrategy.decorateUserId(USER_ID + FOO)).thenReturn(Optional.empty());
		configureUserIdDecorationService(List.of(awareDecorationStrategy));
		final CoreUserDetails userDetails = getCoreUserDetails(USER_ID);
		when(rejectUserPreAuthenticationChecks.getPKFromUserDetails(userDetails)).thenReturn(null);

		//when
		rejectUserPreAuthenticationChecks.check(userDetails);

		//then
		verify(rejectUserPreAuthenticationChecks, times(0)).getUserByPK(any());
		verify(rejectUserPreAuthenticationChecks, times(1)).getUserByLogin(any());
	}

	@Test
	public void shouldReturnNullForPKIfUserDetailsAreNotInstanceOfCoreUserDetails()
	{
		//given
		final UserDetails userDetails = mock(org.springframework.security.core.userdetails.User.class);

		//when
		final PK fromUserDetails = rejectUserPreAuthenticationChecks.getPKFromUserDetails(userDetails);

		//then
		assertThat(fromUserDetails).isNull();
	}

	@Test
	public void shouldReturnNullForPKIfCoreUserDetailsDoesNotContainPK()
	{
		//given
		final CoreUserDetails coreUserDetails = mock(CoreUserDetails.class);
		when(coreUserDetails.getPk()).thenReturn(null);

		//when
		final PK fromUserDetails = rejectUserPreAuthenticationChecks.getPKFromUserDetails(coreUserDetails);

		//then
		assertThat(fromUserDetails).isNull();
	}

	@Test
	public void shouldReturnPKForCoreUserDetails()
	{
		//given
		final CoreUserDetails coreUserDetails = mock(CoreUserDetails.class);
		final PK pk = PK.fromLong(12345L);
		when(coreUserDetails.getPk()).thenReturn(pk);

		//when
		final PK fromUserDetails = rejectUserPreAuthenticationChecks.getPKFromUserDetails(coreUserDetails);

		//then
		assertThat(fromUserDetails).isNotNull().isEqualTo(pk);
	}

	private CoreUserDetails getCoreUserDetails(final String userId)
	{
		return coreUserDetailsService.loadUserByUsername(userId);
	}

	private void createCustomer(final String userId)
	{
		final CustomerModel user = modelService.create(CustomerModel.class);
		user.setUid(userId);
		user.setEncodedPassword(USER_PASSWORD);

		modelService.save(user);
	}

	private void configureUserIdDecorationService(final List<UserIdDecorationStrategy> userIdDecorationStrategies)
	{
		final UserIdDecorationService userIdDecorationService = new DefaultUserIdDecorationService(userIdDecorationStrategies);

		userManager.setUserIdDecorationService(userIdDecorationService);
	}

	private static String returnFirstArg(final InvocationOnMock invocation)
	{
		return (String) invocation.getArguments()[0];
	}
}