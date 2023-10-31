/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.renderers;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.PasswordPolicyService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;

import com.google.common.collect.Sets;
import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.baseeditorarea.DefaultEditorAreaController;


@RunWith(MockitoJUnitRunner.class)
public class UserPasswordPanelRendererTest extends AbstractCockpitngUnitTest<UserPasswordPanelRenderer>
{
	@InjectMocks
	@Spy
	private UserPasswordPanelRenderer renderer;
	@Mock
	private UserService userService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private PasswordPolicyService passwordPolicyService;
	@Mock
	private ModelService modelService;
	private WidgetInstanceManager wim;

	@Override
	protected Class<? extends UserPasswordPanelRenderer> getWidgetType()
	{
		return UserPasswordPanelRenderer.class;
	}

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		doReturn(new Div()).when(renderer).createPasswordContainer();
		when(permissionFacade.canChangeProperty(UserModel._TYPECODE, UserModel.PASSWORD)).thenReturn(true);
		doReturn(Sets.newHashSet("a")).when(renderer).getInstalledPasswordEncodings();
		when(permissionFacade.canReadProperty(UserModel._TYPECODE, UserModel.PASSWORD)).thenReturn(true);
	}

	@Test
	public void testUserTakenFromWidgetModelBeforePasswordChange()
	{

		final UserModel user = new UserModel();
		BackofficeTestUtil.setPk(user, 1);

		final UserModel currentUserAfterRefresh = new UserModel();
		BackofficeTestUtil.setPk(currentUserAfterRefresh, 1);

		wim.getModel().setValue(DefaultEditorAreaController.MODEL_CURRENT_OBJECT, currentUserAfterRefresh);

		final DataType dataType = mock(DataType.class);
		when(dataType.getCode()).thenReturn(UserModel._TYPECODE);


		final Div parent = new Div();
		renderer.render(parent, null, user, dataType, wim);

		final Textbox password = (Textbox) parent.query("." + UserPasswordPanelRenderer.SCLASS_EDITOR_SPACER);
		final Textbox confirm = (Textbox) parent.query("." + UserPasswordPanelRenderer.SCLASS_EDITOR_CONFIRM);
		password.setText("abc");
		confirm.setText("abc");


		CockpitTestUtil.simulateEvent(password, new InputEvent(Events.ON_CHANGE, password, "new", "old"));

		verify(userService).setPassword(same(currentUserAfterRefresh), eq("abc"), eq("a"));
		verify(userService,never()).setPassword(same(user), eq("abc"), eq("a"));
	}

}
