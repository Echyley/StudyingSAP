/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.jalo.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserIdDecorationService;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;
import de.hybris.platform.servicelayer.user.impl.DefaultUserIdDecorationService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;


@IntegrationTest
public class UserManagerIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	private DefaultUserIdDecorationService defaultUserIdDecorationService;

	private UserManager userManager;

	private static final String USER_ID = "test@sap.com";
	private static final String FOO_DECORATOR = "|foo";
	private static final String BAR_DECORATOR = "|bar";
	private static final String BAZ_DECORATOR = "|baz";

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
	public void shouldGetUserWithNoDecoratedUserId() throws ConsistencyCheckException
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createUser(USER_ID);

		//when
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldGetCustomerWithNoDecoratedUserId() throws ConsistencyCheckException
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createCustomer(USER_ID);

		//when
		final Customer foundCustomer = userManager.getCustomerByLogin(USER_ID);

		//then
		assertThat(foundCustomer.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldNotGetUserAndThrowExceptionWhenProvidingIncorrectUserId() throws ConsistencyCheckException
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createUser(USER_ID);

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(UUID.randomUUID().toString()))
				.isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void shouldGetUserWithDecoratedUserId() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);

		//when
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldNotGetUserWithDecoratedUserIdAndThrowExceptionIfThereIsNoConfiguredUserIdDecorationStrategy()
			throws ConsistencyCheckException
	{
		//given
		configureUserIdDecorationService(Collections.emptyList());
		createUserWithDecoratedUserId(USER_ID, FOO_DECORATOR);

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(USER_ID)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void shouldGetCustomerWithDecoratedUserId() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		//when
		final Customer customer = userManager.getCustomerByLogin(USER_ID);

		//then
		assertThat(customer.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR));
		assertThat(customer.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldNotGetUserAndThrowExceptionIfFoundUserIsNoEmployeeType() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		createCustomerWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		final Customer foundCustomer = userManager.getCustomerByLogin(USER_ID);
		assertThat(foundCustomer.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR));

		//when, then
		assertThatThrownBy(() -> userManager.getEmployeeByLogin(USER_ID)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void shouldUseAllUserIdDecorationStrategiesToGetTheUser() throws ConsistencyCheckException
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
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid())
				.isEqualTo(decorateUserIdWithDecorators(USER_ID, FOO_DECORATOR, BAR_DECORATOR, BAZ_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldUseOnlyBarAndBazUserIdDecorationStrategiesToGetUserWhenTheFirstOneIsDisabled()
			throws ConsistencyCheckException
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
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, BAR_DECORATOR, BAZ_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldUseOnlyTheLastUserIdDecorationStrategyToGetUserWhenThePreviousAreDisabled() throws ConsistencyCheckException
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
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, BAZ_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldNotGetAnyUserAndThrowExceptionIfAllStrategiesAreDisabled() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(barDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		when(bazDecorationStrategy.decorateUserId(anyString())).thenReturn(Optional.empty());
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy, bazDecorationStrategy));

		createUserWithDecoratedUserId(USER_ID, FOO_DECORATOR);
		createUserWithDecoratedUserId(USER_ID, BAR_DECORATOR);
		createUserWithDecoratedUserId(USER_ID, BAZ_DECORATOR);

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(USER_ID)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void shouldGetUserWithNoDecoratedUserIdIfAllStrategiesAreDisabled() throws ConsistencyCheckException
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
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(USER_ID);
	}

	@Test
	public void shouldAddPrefixAndSuffixWhenDecoratingInOrderToGetUserWithDecoratedUserId() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		when(fooSuffixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(barPrefixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(BAR_DECORATOR + returnFirstArg(i)));
		configureUserIdDecorationService(List.of(fooSuffixDecorationStrategy, barPrefixDecorationStrategy));

		final String decoratedUserId = BAR_DECORATOR + USER_ID + FOO_DECORATOR;
		createCustomer(decoratedUserId);

		//when
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(decoratedUserId);
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldAddPrefixAndSuffixesWhenDecoratingInOrderToGetUserWithDecoratedUserId() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		when(fooSuffixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(bazSuffixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		when(barPrefixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(BAR_DECORATOR + returnFirstArg(i)));
		configureUserIdDecorationService(
				List.of(bazSuffixDecorationStrategy, fooSuffixDecorationStrategy, barPrefixDecorationStrategy));

		final String decoratedUserId = BAR_DECORATOR + USER_ID + BAZ_DECORATOR + FOO_DECORATOR;
		createCustomer(decoratedUserId);

		//when
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(decoratedUserId);
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldAddPrefixesAndSuffixesWhenDecoratingInOrderToGetUserWithDecoratedUserId() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy bazSuffixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy fooPrefixDecorationStrategy = Mockito.mock(UserIdDecorationStrategy.class);
		when(fooSuffixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		when(bazSuffixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(returnFirstArg(i) + BAZ_DECORATOR));
		when(barPrefixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(BAR_DECORATOR + returnFirstArg(i)));
		when(fooPrefixDecorationStrategy.decorateUserId(anyString()))
				.thenAnswer(i -> Optional.of(FOO_DECORATOR + returnFirstArg(i)));
		configureUserIdDecorationService(List.of(fooSuffixDecorationStrategy, barPrefixDecorationStrategy,
				bazSuffixDecorationStrategy, fooPrefixDecorationStrategy));

		final String decoratedUserId = FOO_DECORATOR + BAR_DECORATOR + USER_ID + FOO_DECORATOR + BAZ_DECORATOR;
		createCustomer(decoratedUserId);

		//when
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser.getUid()).isEqualTo(decoratedUserId);
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldNotThrowExceptionAndNotDecorateUserIdIfStrategyReturnsNull() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final UserIdDecorationStrategy barDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenReturn(null);
		when(barDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + BAR_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy, barDecorationStrategy));

		createCustomerWithDecoratedUserId(USER_ID, BAR_DECORATOR);

		//when
		final User foundUser = userManager.getUserByLogin(USER_ID);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(USER_ID, BAR_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
	}

	@Test
	public void shouldNotDecorateUserIdWhenGettingAdminByGetUserByLoginMethod()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		//when
		final User admin = userManager.getUserByLogin(UserConstants.ADMIN_EMPLOYEE_UID);

		//then
		assertThat(admin.getUid()).isEqualTo(UserConstants.ADMIN_EMPLOYEE_UID);
	}

	@Test
	public void shouldNotDecorateUserIdWhenGettingAnonymousByGetUserByLoginMethod()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(anyString())).thenAnswer(i -> Optional.of(returnFirstArg(i) + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		//when
		final User anonymous = userManager.getUserByLogin(UserConstants.ANONYMOUS_CUSTOMER_UID);

		//then
		assertThat(anonymous.getUid()).isEqualTo(UserConstants.ANONYMOUS_CUSTOMER_UID);
	}

	@Test
	public void withDecorationUserUndecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addUserPrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createUser(userId);

		//when
		final User foundUser = userManager.getUserByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(User.class);
	}

	@Test
	public void withDecorationUserDecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createUserWithDecoratedUserId(USER_ID, FOO_DECORATOR);

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(USER_ID)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void withDecorationEmployeeUndecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addEmployeePrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createEmployee(userId);

		//when
		final User foundUser = userManager.getEmployeeByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(Employee.class);
	}

	@Test
	public void withDecorationEmployeeDecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		when(fooDecorationStrategy.decorateUserId(USER_ID)).thenReturn(Optional.of(USER_ID + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createEmployeeWithDecoratedUserId(USER_ID, FOO_DECORATOR);

		//when, then
		assertThatThrownBy(() -> userManager.getEmployeeByLogin(USER_ID)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void withDecorationEmployeeAsUserUndecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addEmployeePrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createEmployee(userId);

		//when
		final User foundUser = userManager.getUserByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(Employee.class);
	}

	@Test
	public void withDecorationCustomerUndecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addCustomerPrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomer(userId);

		//when, then
		assertThatThrownBy(() -> userManager.getCustomerByLogin(userId)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void withDecorationCustomerDecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addCustomerPrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomerWithDecoratedUserId(userId, FOO_DECORATOR);

		//when
		final User foundUser = userManager.getCustomerByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(userId, FOO_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
		assertThat(foundUser).isInstanceOf(Customer.class);
	}

	@Test
	public void withDecorationCustomerAsUserUndecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addCustomerPrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomer(userId);

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(userId)).isInstanceOf(JaloItemNotFoundException.class);
	}


	@Test
	public void withDecorationCustomerAsUserDecorated() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addCustomerPrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomerWithDecoratedUserId(userId, FOO_DECORATOR);

		//when
		final User foundUser = userManager.getUserByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(userId, FOO_DECORATOR));
		assertThat(foundUser).isInstanceOf(Customer.class);
	}

	@Test
	public void withDecorationCustomerDecoratedAndUser() throws ConsistencyCheckException
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addCustomerPrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));
		createCustomerWithDecoratedUserId(userId, FOO_DECORATOR);
		createUser(userId);

		//when
		final User foundUser = userManager.getUserByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(decorateUserIdWithDecorators(userId, FOO_DECORATOR));
		assertThat(foundUser.getUid()).isNotEqualTo(USER_ID);
		assertThat(foundUser).isInstanceOf(Customer.class);
	}

	@Test
	public void withDecorationNotExistingUser()
	{
		//given
		final UserIdDecorationStrategy fooDecorationStrategy = mock(UserIdDecorationStrategy.class);
		final String userId = addCustomerPrefix();
		when(fooDecorationStrategy.decorateUserId(userId)).thenReturn(Optional.of(userId + FOO_DECORATOR));
		configureUserIdDecorationService(List.of(fooDecorationStrategy));

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(userId)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void withoutDecorationUserUndecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addUserPrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createUser(userId);

		//when
		final User foundUser = userManager.getUserByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(User.class);
	}

	@Test
	public void withoutDecorationUserDecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addUserPrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createUserWithDecoratedUserId(userId, FOO_DECORATOR);

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(userId)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void withoutDecorationEmployeeUndecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addEmployeePrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createEmployee(userId);

		//when
		final User foundUser = userManager.getEmployeeByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(Employee.class);
	}

	@Test
	public void withoutDecorationEmployeeAsUserUndecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addEmployeePrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createEmployee(userId);

		//when
		final User foundUser = userManager.getUserByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(Employee.class);
	}

	@Test
	public void withoutDecorationCustomerUndecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addCustomerPrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createCustomer(userId);

		//when
		final User foundUser = userManager.getCustomerByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(Customer.class);
	}


	@Test
	public void withoutDecorationCustomerDecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addCustomerPrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createCustomerWithDecoratedUserId(userId, FOO_DECORATOR);

		//when, then
		assertThatThrownBy(() -> userManager.getCustomerByLogin(userId)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void withoutDecorationCustomerAsUserUndecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addCustomerPrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createCustomer(userId);

		//when
		final User foundUser = userManager.getUserByLogin(userId);

		//then
		assertThat(foundUser).isNotNull();
		assertThat(foundUser.getUid()).isEqualTo(userId);
		assertThat(foundUser).isInstanceOf(Customer.class);
	}


	@Test
	public void withoutDecorationCustomerAsUserDecorated() throws ConsistencyCheckException
	{
		//given
		final String userId = addCustomerPrefix();
		configureUserIdDecorationService(Collections.emptyList());
		createCustomerWithDecoratedUserId(userId, FOO_DECORATOR);

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(userId)).isInstanceOf(JaloItemNotFoundException.class);
	}

	@Test
	public void withoutDecorationNotExistingUser()
	{
		//given
		final String userId = addCustomerPrefix();
		configureUserIdDecorationService(Collections.emptyList());

		//when, then
		assertThatThrownBy(() -> userManager.getUserByLogin(userId)).isInstanceOf(JaloItemNotFoundException.class);
	}

	private void createUser(final String userId) throws ConsistencyCheckException
	{
		userManager.createUser(userId);
	}

	private void createUserWithDecoratedUserId(final String userId, final String... decorators) throws ConsistencyCheckException
	{
		createUser(decorateUserIdWithDecorators(userId, decorators));
	}

	private void createCustomer(final String userId) throws ConsistencyCheckException
	{
		userManager.createCustomer(userId);
	}

	private void createCustomerWithDecoratedUserId(final String userId, final String... decorators)
			throws ConsistencyCheckException
	{
		createCustomer(decorateUserIdWithDecorators(userId, decorators));
	}

	private void createEmployeeWithDecoratedUserId(final String userId, final String... decorators)
			throws ConsistencyCheckException
	{
		createEmployee(decorateUserIdWithDecorators(userId, decorators));
	}

	private void createEmployee(final String userId) throws ConsistencyCheckException
	{
		userManager.createEmployee(userId);
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

	private String addUserPrefix()
	{
		return "user-" + USER_ID;
	}

	private String addEmployeePrefix()
	{
		return "emp-" + USER_ID;
	}

	private String addCustomerPrefix()
	{
		return "cust-" + USER_ID;
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