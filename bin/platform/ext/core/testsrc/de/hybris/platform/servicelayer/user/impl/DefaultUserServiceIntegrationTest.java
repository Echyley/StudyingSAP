/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.servicelayer.user.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserIdDecorationService;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

@IntegrationTest
public class DefaultUserServiceIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	private DefaultUserService defaultUserService;

	@Resource
	private DefaultUserIdDecorationService defaultUserIdDecorationService;

	@Resource
	private ModelService modelService;

	private static final String USER_ID = "test@sap.com";
	private static final String FOO_DECORATOR = "|foo";
	private static final String BAR_DECORATOR = "|bar";
	private static final String BAZ_DECORATOR = "|baz";

	@After
	public void tearDown()
	{
		defaultUserService.setUserIdDecorationService(defaultUserIdDecorationService);
	}

	@Test
	public void shouldGetUserWithNoDecoratedUserId()
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createUser(USER_ID);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldGetCustomerWithNoDecoratedUserId()
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createCustomer(USER_ID);

		//when
		final CustomerModel foundCustomer = defaultUserService.getUserForUID(USER_ID, CustomerModel.class);

		//then
		assertThat(foundCustomer).isNotNull();
		assertThat(foundCustomer.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldNotGetUserAndThrowExceptionWhenProvidingIncorrectUserId()
	{
		//given
		final String uid = UUID.randomUUID().toString();
		configureUserIdDecorationService(Collections.emptyList());
		createUser(USER_ID);

		//when, then
		assertThatThrownBy(() -> defaultUserService.getUserForUID(uid)).isInstanceOf(
				UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotGetUserWithDecoratedUserIdAndThrowExceptionIfThereIsNoConfiguredUserIdDecorationStrategy()
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createUserWithDecoratedUserId(USER_ID, FOO_DECORATOR);

		//when, then
		assertThatThrownBy(() -> defaultUserService.getUserForUID(USER_ID)).isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldGetUserIfThereIsNoUserWithDecoratedUserIdAndUserIdDecorationStrategyIsConfigured()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createUser(USER_ID);

		//when
		final UserModel user = defaultUserService.getUserForUID(USER_ID, UserModel.class);

		//then
		assertThat(user).isNotNull();
		assertThat(user.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldGetCustomerWithDecoratedUserId()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		//when
		final CustomerModel customer = defaultUserService.getUserForUID(USER_ID, CustomerModel.class);

		//then
		assertThat(customer).isNotNull();
		assertThat(customer.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR));
		assertThat(customer.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldGetCustomerWithDecoratedUserIdWhenExistsEmployeeWithTheSameUid()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createUser(USER_ID);
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);

		//when
		final CustomerModel customer = defaultUserService.getUserForUID(USER_ID, CustomerModel.class);

		//then
		assertThat(customer).isNotNull();
		assertThat(customer.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR));
	}

	@Test
	public void shouldNotGetUserAndThrowExceptionIfFoundUserIsCustomerType()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		final CustomerModel foundCustomer = (CustomerModel) defaultUserService.getUserForUID(USER_ID);
		assertThat(foundCustomer).isNotNull();
		assertThat(foundCustomer.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR));

		//when, then
		assertThatThrownBy(() -> defaultUserService.getUserForUID(USER_ID, EmployeeModel.class)).isInstanceOf(
				ClassMismatchException.class);
	}

	@Test
	public void shouldNotGetCustomerAndThrowExceptionIfExistsNonDecoratedCustomerWhenExistsDecoration()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomer(USER_ID);

		//when, then
		assertThatThrownBy(() -> defaultUserService.getUserForUID(USER_ID)).isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldGetEmployeeIfExistsNonDecoratedEmployeeWhenExistsDecoration()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createEmployee(USER_ID);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull().isInstanceOf(EmployeeModel.class);
		assertThat(foundUser.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldNotFindUserIfExistsNonDecoratedCustomerWhenExistsDecoration()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomer(USER_ID);

		//when, then
		assertThat(defaultUserService.isUserExisting(USER_ID)).isFalse();
	}

	@Test
	public void shouldFindUserIfExistsNonDecoratedEmployeeWhenExistsDecoration()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createEmployee(USER_ID);

		//when, then
		assertThat(defaultUserService.isUserExisting(USER_ID)).isTrue();
	}

	@Test
	public void shouldUseAllUserIdDecorationStrategiesToGetTheCustomer()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		when(bazDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR, BAR_DECORATOR, BAZ_DECORATOR);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(
				decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR, BAR_DECORATOR, BAZ_DECORATOR));
		assertThat(foundUser).isInstanceOf(CustomerModel.class);
	}

	@Test
	public void shouldUseOnlyBarAndBazUserIdDecorationStrategiesToGetCustomerWhenTheFirstOneIsDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		when(bazDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createCustomerWithDecoratedUserId(USER_ID, BAR_DECORATOR, BAZ_DECORATOR);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, BAR_DECORATOR, BAZ_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldUseOnlyTheLastUserIdDecorationStrategyToGetCustomerWhenThePreviousAreDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createCustomerWithDecoratedUserId(USER_ID, BAZ_DECORATOR);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, BAZ_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldNotGetAnyUserAndThrowExceptionIfAllStrategiesAreDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		createCustomerWithDecoratedUserId(USER_ID, BAR_DECORATOR);
		createCustomerWithDecoratedUserId(USER_ID, BAZ_DECORATOR);

		//when, then
		assertThatThrownBy(() -> defaultUserService.getUserForUID(USER_ID)).isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldGetUserWithNoDecoratedUserIdIfAllStrategiesAreDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createUser(USER_ID);
		createUserWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		createUserWithDecoratedUserId(USER_ID, BAR_DECORATOR);
		createUserWithDecoratedUserId(USER_ID, BAZ_DECORATOR);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldGetCustomerWithNoDecoratedUserIdIfAllStrategiesAreDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createUser(USER_ID);
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		createCustomerWithDecoratedUserId(USER_ID, BAR_DECORATOR);
		createCustomerWithDecoratedUserId(USER_ID, BAZ_DECORATOR);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldGetEmployeeWithNoDecoratedUserIdIfAllStrategiesAreDisabled()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createUser(USER_ID);
		createEmployeeWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		createEmployeeWithDecoratedUserId(USER_ID, BAR_DECORATOR);
		createEmployeeWithDecoratedUserId(USER_ID, BAZ_DECORATOR);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldAddPrefixAndSuffixWhenDecoratingInOrderToGetustomerWithDecoratedUserId()
	{
		//given
		final UserIdDecorationStrategy fooSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		when(fooSuffixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(barPrefixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(BAR_DECORATOR + returnFirstArg(i)));
		configureUserIdDecorationService(List.of(fooSuffixDecorationStrategy, barPrefixDecorationStrategy));

		final String decoratedUserId = BAR_DECORATOR + USER_ID + FOO_DECORATOR;
		createCustomer(decoratedUserId);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decoratedUserId);
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldAddPrefixAndSuffixesWhenDecoratingInOrderToGetCustomerWithDecoratedUserId()
	{
		//given
		final UserIdDecorationStrategy fooSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		when(fooSuffixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(bazSuffixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		when(barPrefixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(BAR_DECORATOR + returnFirstArg(i)));
		configureUserIdDecorationService(
				List.of(bazSuffixDecorationStrategy, fooSuffixDecorationStrategy, barPrefixDecorationStrategy));

		final String decoratedUserId = BAR_DECORATOR + USER_ID + BAZ_DECORATOR + FOO_DECORATOR;
		createCustomer(decoratedUserId);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decoratedUserId);
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldAddPrefixesAndSuffixesWhenDecoratingInOrderToGetCustomerWithDecoratedUserId()
	{
		//given
		final UserIdDecorationStrategy fooSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy fooPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		when(fooSuffixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(bazSuffixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		when(barPrefixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(BAR_DECORATOR + returnFirstArg(i)));
		when(fooPrefixDecorationStrategy.decorateUserId(anyString())).thenAnswer(
				i -> Optional.of(FOO_DECORATOR + returnFirstArg(i)));

		configureUserIdDecorationService(
				List.of(fooSuffixDecorationStrategy, barPrefixDecorationStrategy, bazSuffixDecorationStrategy,
						fooPrefixDecorationStrategy));

		final String decoratedUserId = FOO_DECORATOR + BAR_DECORATOR + USER_ID + FOO_DECORATOR + BAZ_DECORATOR;
		createCustomer(decoratedUserId);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decoratedUserId);
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldThrowExceptionIfUserIdIsNull()
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createUser(USER_ID);

		//when, then
		assertThatThrownBy(() -> defaultUserService.getUserForUID(null)).isInstanceOf(IllegalArgumentException.class)
		                                                                .hasMessage("The given userID is null!");
	}

	@Test
	public void shouldNotThrowExceptionAndNotDecorateCustomerIdIfStrategyReturnsNull()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(null);
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy));

		createCustomerWithDecoratedUserId(USER_ID, BAR_DECORATOR);

		//when
		final UserModel foundUser = defaultUserService.getUserForUID(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, BAR_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldNotDecorateUserIdWhenGettingAdmin()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		//when
		final UserModel admin = defaultUserService.getAdminUser();

		//then
		assertThat(admin.getUid()).isEqualTo(UserConstants.ADMIN_EMPLOYEE_UID);
	}

	@Test
	public void shouldNotDecorateUserIdWhenGettingAnonymous()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		//when
		final UserModel anonymous = defaultUserService.getAnonymousUser();

		//then
		assertThat(anonymous.getUid()).isEqualTo(UserConstants.ANONYMOUS_CUSTOMER_UID);
	}

	private void createUser(final String userId)
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(userId);

		modelService.save(user);
	}

	private void createUserWithDecoratedUserId(final String userId, final String... decorators)
	{
		createUser(decorateUserIdWithDecorators(userId, decorators));
	}

	private void createEmployeeWithDecoratedUserId(final String userId, final String... decorators)
	{
		createEmployee(decorateUserIdWithDecorators(userId, decorators));
	}

	private void createEmployee(final String userId)
	{
		final EmployeeModel emplyee = modelService.create(EmployeeModel.class);
		emplyee.setUid(userId);

		modelService.save(emplyee);
	}

	private void createCustomer(final String userId)
	{
		final CustomerModel customer = modelService.create(CustomerModel.class);
		customer.setUid(userId);

		modelService.save(customer);
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

	private static String returnFirstArg(final InvocationOnMock invocation)
	{
		return (String) invocation.getArguments()[0];
	}

	private void configureUserIdDecorationService(final List<UserIdDecorationStrategy> userIdDecorationStrategies)
	{
		final UserIdDecorationService userIdDecorationService = new DefaultUserIdDecorationService(userIdDecorationStrategies);

		defaultUserService.setUserIdDecorationService(userIdDecorationService);
	}
}
