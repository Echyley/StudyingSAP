/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.user.interceptors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.InterceptorRegistry;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.seed.TestDataCreator;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


@IntegrationTest
public class ModifySystemUsersInterceptorTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	private final PropertyConfigSwitcher pcsDeactivateAdminsBlocked = new PropertyConfigSwitcher(ModifySystemUsersInterceptor.USER_DEACTIVATION_BLOCK_FOR_ALL_ADMINS);

	@After
	public void tearDown()
	{
		pcsDeactivateAdminsBlocked.switchBackToDefault();
	}

	@Test
	public void testInterceptorAsPreparerInstalled()
	{
		final InterceptorRegistry reg = ((DefaultModelService) modelService).getInterceptorRegistry();
		final Collection<ValidateInterceptor> validaters = reg.getValidateInterceptors("Principal");
		assertThat(validaters.isEmpty()).isFalse();
		boolean found = false;
		for (final ValidateInterceptor inter : validaters)
		{
			if (inter instanceof ModifySystemUsersInterceptor)
			{
				found = true;
				break;
			}
		}
		assertThat(found).isTrue();
	}

	@Test
	public void testInterceptorAsRemoverInstalled()
	{
		final InterceptorRegistry reg = ((DefaultModelService) modelService).getInterceptorRegistry();
		final Collection<RemoveInterceptor> removers = reg.getRemoveInterceptors("Principal");
		assertThat(removers.isEmpty()).isFalse();
		boolean found = false;
		for (final RemoveInterceptor inter : removers)
		{
			if (inter instanceof ModifySystemUsersInterceptor)
			{
				found = true;
				break;
			}
		}
		assertThat(found).isTrue();
	}

	@Test
	public void shouldThrowExceptionWhenSetDisableLoginForAdmin()
	{
		final EmployeeModel admin = userService.getAdminUser();
		admin.setLoginDisabled(false);
		modelService.save(admin);

		admin.setLoginDisabled(true);
		saveShouldThrowException(admin);
	}

	@Test
	public void shouldThrowExceptionWhenDeactivateAdmin()
	{
		final EmployeeModel admin = userService.getAdminUser();
		admin.setDeactivationDate(null);
		modelService.save(admin);

		admin.setDeactivationDate(new Date());
		saveShouldThrowException(admin);
	}

	@Test
	public void shouldSucceedSetDisableLoginForAdminsWhenBlockIsFalse() {
		pcsDeactivateAdminsBlocked.switchToValue("false");
		final TestDataCreator testDataCreator = new TestDataCreator(this.modelService);
		final UserModel userInAdminGroup = testDataCreator.createEmployee("dennis", "Dennis Ritchie");
		userInAdminGroup.setGroups(Collections.singleton(userService.getAdminUserGroup()));
		modelService.save(userInAdminGroup);

		UserModel dennis = userService.getUserForUID("dennis");
		dennis.setLoginDisabled(true);
		modelService.save(dennis);

		dennis = userService.getUserForUID("dennis");
		Assert.assertTrue(dennis.isLoginDisabled());
	}

	@Test
	public void shouldSucceedSetDeactivationDateForAdminsWhenBlockIsFalse() {
		pcsDeactivateAdminsBlocked.switchToValue("false");
		final TestDataCreator testDataCreator = new TestDataCreator(this.modelService);
		final UserModel userInAdminGroup = testDataCreator.createEmployee("dennis", "Dennis Ritchie");
		userInAdminGroup.setGroups(Collections.singleton(userService.getAdminUserGroup()));
		modelService.save(userInAdminGroup);

		UserModel dennis = userService.getUserForUID("dennis");
		dennis.setDeactivationDate(new Date());
		modelService.save(dennis);

		dennis = userService.getUserForUID("dennis");
		Assert.assertNotNull(dennis.getDeactivationDate());
	}

	@Test
	public void shouldFailSetDisableLoginForAdminsWhenBlockIsTrue() {
		pcsDeactivateAdminsBlocked.switchToValue("true");
		final TestDataCreator testDataCreator = new TestDataCreator(this.modelService);
		final UserModel userInAdminGroup = testDataCreator.createEmployee("dennis", "Dennis Ritchie");
		userInAdminGroup.setGroups(Collections.singleton(userService.getAdminUserGroup()));
		modelService.save(userInAdminGroup);

		final UserModel dennis = userService.getUserForUID("dennis");
		dennis.setLoginDisabled(true);
		saveShouldThrowException((EmployeeModel)dennis);
	}

	@Test
	public void shouldFailSetDisableLoginForSubGroupOfAdminWhenBlockIsTrue() {
		pcsDeactivateAdminsBlocked.switchToValue("true");
		final TestDataCreator testDataCreator = new TestDataCreator(this.modelService);
		final UserModel userInAdminGroup = testDataCreator.createEmployee("dennis", "Dennis Ritchie");
		final UserGroupModel subGroup = testDataCreator.createUserGroup("subadmingroup", "Sub Group of Admin");
		subGroup.setGroups(Collections.singleton(userService.getAdminUserGroup()));
		userInAdminGroup.setGroups(Collections.singleton(subGroup));
		modelService.save(userInAdminGroup);

		final UserModel dennis = userService.getUserForUID("dennis");
		dennis.setLoginDisabled(true);
		saveShouldThrowException((EmployeeModel)dennis);
	}

	@Test
	public void shouldFailSetDeactivationDateForAdminsWhenBlockIsTrue() {
		pcsDeactivateAdminsBlocked.switchToValue("true");
		final TestDataCreator testDataCreator = new TestDataCreator(this.modelService);
		final UserModel userInAdminGroup = testDataCreator.createEmployee("dennis", "Dennis Ritchie");
		userInAdminGroup.setGroups(Collections.singleton(userService.getAdminUserGroup()));
		modelService.save(userInAdminGroup);

		final UserModel dennis = userService.getUserForUID("dennis");
		dennis.setDeactivationDate(new Date());
		saveShouldThrowException((EmployeeModel)dennis);
	}

	@Test
	public void shouldFailSetDeactivationDateForSubGroupOfAdminWhenBlockIsTrue() {
		pcsDeactivateAdminsBlocked.switchToValue("true");
		final TestDataCreator testDataCreator = new TestDataCreator(this.modelService);
		final UserModel userInAdminGroup = testDataCreator.createEmployee("dennis", "Dennis Ritchie");
		final UserGroupModel subGroup = testDataCreator.createUserGroup("subadmingroup", "Sub Group of Admin");
		subGroup.setGroups(Collections.singleton(userService.getAdminUserGroup()));
		userInAdminGroup.setGroups(Collections.singleton(subGroup));
		modelService.save(userInAdminGroup);

		final UserModel dennis = userService.getUserForUID("dennis");
		dennis.setDeactivationDate(new Date());
		saveShouldThrowException((EmployeeModel)dennis);
	}

	@Test
	public void shouldSucceedSetAttributesForNonEmployeeUser() {
		final TestDataCreator testDataCreator = new TestDataCreator(this.modelService);
		final UserModel user = testDataCreator.createUser("dennis", "Dennis");
		UserModel dennis = userService.getUserForUID("dennis");
		dennis.setName("Dennis Ritchie");
		dennis.setLoginDisabled(true);
		modelService.save(dennis);

		dennis = userService.getUserForUID("dennis");
		Assert.assertEquals("Dennis Ritchie", dennis.getName());
	}

	private void saveShouldThrowException(final EmployeeModel admin)
	{
		try
		{
			modelService.save(admin);
			fail("expected ModelSavingException");
		}
		catch (final ModelSavingException e)
		{
			assertThat(e.getCause()).isInstanceOf(InterceptorException.class);
			assertThat(((InterceptorException) e.getCause()).getInterceptor()).isInstanceOf(ModifySystemUsersInterceptor.class);
		}
	}
}

