/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.backoffice.orgunits.actions;

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitService;
import de.hybris.platform.commerceservices.organization.strategies.OrgUnitAuthorizationStrategy;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.actions.ActionContext;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DisableOrgUnitActionTest
{
	@Mock(lenient = true)
	private UserService userService;

	@Mock
	private OrgUnitService orgUnitService;

	@Mock
	private OrgUnitAuthorizationStrategy orgUnitAuthorizationStrategy;

	@Mock
	private NotificationService notificationService;

	@InjectMocks
	private final DisableOrgUnitAction disableOrgUnitAction = new DisableOrgUnitAction();

	private OrgUnitModel orgUnitModel;
	private UserModel currentUser;
	private ActionContext<OrgUnitModel> ctx;

	@Before
	public void setUp() throws Exception
	{
		orgUnitModel = mock(OrgUnitModel.class);
		currentUser = mock(UserModel.class);
		ctx = mock(ActionContext.class);
	}

	@Test
	public void shouldDisableOrgUnit()
	{
		BDDMockito.given(ctx.getData()).willReturn(orgUnitModel);
		disableOrgUnitAction.perform(ctx);

		Mockito.verify(orgUnitService, Mockito.times(1)).deactivateUnit(orgUnitModel);
	}

	@Test
	public void shouldNotDisableOrgUnitIfNull()
	{
		BDDMockito.given(ctx.getData()).willReturn(null);
		disableOrgUnitAction.perform(ctx);

		Mockito.verify(orgUnitService, Mockito.times(0)).deactivateUnit(Mockito.any(OrgUnitModel.class));
	}

	@Test
	public void shouldPerformDisableOrgUnitIfAuthorized()
	{
		BDDMockito.given(ctx.getData()).willReturn(orgUnitModel);
		BDDMockito.given(orgUnitModel.getActive()).willReturn(Boolean.TRUE);
		BDDMockito.given(userService.getCurrentUser()).willReturn(currentUser);
		BDDMockito.given(Boolean.valueOf(orgUnitAuthorizationStrategy.canEditUnit(currentUser))).willReturn(Boolean.TRUE);

		Assert.assertTrue("Should be able to perform", disableOrgUnitAction.canPerform(ctx));
	}

	@Test
	public void shouldNotPerformDisableOrgUnitIfNotActive()
	{
		BDDMockito.given(ctx.getData()).willReturn(orgUnitModel);
		BDDMockito.given(orgUnitModel.getActive()).willReturn(Boolean.FALSE);
		BDDMockito.given(userService.getCurrentUser()).willReturn(currentUser);

		Assert.assertFalse("Should not be able to perform", disableOrgUnitAction.canPerform(ctx));
	}

	@Test
	public void shouldNotPerformDisableOrgUnitIfNotAuthorized()
	{
		BDDMockito.given(ctx.getData()).willReturn(orgUnitModel);
		BDDMockito.given(orgUnitModel.getActive()).willReturn(Boolean.TRUE);
		BDDMockito.given(userService.getCurrentUser()).willReturn(currentUser);
		BDDMockito.given(Boolean.valueOf(orgUnitAuthorizationStrategy.canEditUnit(currentUser))).willReturn(Boolean.FALSE);

		Assert.assertFalse("Should not be able to perform", disableOrgUnitAction.canPerform(ctx));
	}

	@Test
	public void shouldNotPerformDisableOrgUnitIfNullData()
	{
		BDDMockito.given(ctx.getData()).willReturn(null);

		Assert.assertFalse("Should not be able to perform", disableOrgUnitAction.canPerform(ctx));
	}
}
