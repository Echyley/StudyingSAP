/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.servicelayer.user.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

@UnitTest
public class DefaultUserIdDecorationServiceTest
{

	private static final String USER_ID = "test@sap.com";
	private static final String FOO_DECORATOR = "|foo";
	private static final String BAR_DECORATOR = "|bar";
	private static final String BAZ_DECORATOR = "|baz";

	private DefaultUserIdDecorationService defaultUserIdDecorationService;

	@Test
	public void shouldNotDecorateUserIdIfThereIsEmptyListOfStrategies()
	{
		//given
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(Collections.emptyList());

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID);
	}

	@Test
	public void shouldNotDecorateUserIdIfStrategyReturnsOptionalEmpty()
	{
		//given
		final UserIdDecorationStrategy emptyDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(emptyDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(List.of(emptyDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID);
	}

	@Test
	public void shouldDecorateUserIdWithFooUserIdDecorationStrategy()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(List.of(fooDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID + FOO_DECORATOR);
	}

	@Test
	public void shouldDecorateUserIdWithFooAndThenWithBarUserIdDecorationStrategy()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(
				List.of(fooDecorationStrategy, barDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID + FOO_DECORATOR + BAR_DECORATOR);
	}

	@Test
	public void shouldDecorateUserIdWithBarAndThenWithFooUserIdDecorationStrategy()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(
				List.of(barDecorationStrategy, fooDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID + BAR_DECORATOR + FOO_DECORATOR);
	}

	@Test
	public void shouldDecorateUserIdOnlyWithBarAndBazUserIdDecorationStrategiesIfFooIsDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		when(bazDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(
				List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID + BAR_DECORATOR + BAZ_DECORATOR);
	}

	@Test
	public void shouldDecorateUserIdWithTheLastStrategyOnTheListIfPreviousOnesAreDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.of(USER_ID + BAZ_DECORATOR));
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(
				List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID + BAZ_DECORATOR);
	}

	@Test
	public void shouldNotDecorateUserIdIfStrategiesAreDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.empty());
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(
				List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(USER_ID);
	}

	@Test
	public void shouldAddPrefixAndSuffixInOrderToDecorateUserId()
	{
		//given
		final UserIdDecorationStrategy fooSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		when(fooSuffixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(barPrefixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(BAR_DECORATOR + returnFirstArg(i)));
		defaultUserIdDecorationService = new DefaultUserIdDecorationService(
				List.of(fooSuffixDecorationStrategy, barPrefixDecorationStrategy));

		//when
		final String decoratedUserId = defaultUserIdDecorationService.decorateUserId(USER_ID);

		//then
		assertThat(decoratedUserId).isEqualTo(BAR_DECORATOR + USER_ID + FOO_DECORATOR);
	}

	private static String returnFirstArg(final InvocationOnMock invocation)
	{
		return (String) invocation.getArguments()[0];
	}
}


