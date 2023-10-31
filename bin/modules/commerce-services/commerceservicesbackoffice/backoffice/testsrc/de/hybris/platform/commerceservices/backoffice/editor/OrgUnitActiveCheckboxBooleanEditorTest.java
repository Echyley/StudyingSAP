/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.backoffice.editor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitService;
import de.hybris.platform.commerceservices.organization.strategies.OrgUnitAuthorizationStrategy;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrgUnitActiveCheckboxBooleanEditorTest
		extends AbstractCockpitEditorRendererUnitTest<Boolean, OrgUnitActiveCheckboxBooleanEditor>
{
	private final OrgUnitActiveCheckboxBooleanEditor targetEditor = new OrgUnitActiveCheckboxBooleanEditor();

	@InjectMocks
	@Spy
	private OrgUnitActiveCheckboxBooleanEditor spyEditor;

	@Mock
	private UserService userService;

	@Mock
	private OrgUnitAuthorizationStrategy orgUnitAuthorizationStrategy;

	@Mock
	Editor ancestor;

	@Mock
	private WidgetModel widgetModel;

	@Mock
	private OrgUnitService orgUnitService;

	@Mock
	private EditorContext<Boolean> editorContext;

	@Mock
	private EditorListener<Boolean> editorListener;

	@Override
	public OrgUnitActiveCheckboxBooleanEditor getEditorInstance()
	{
		return targetEditor;
	}

	class FakeOrgUnitActiveEditorView extends OrgUnitActiveCheckboxBooleanEditorView
	{
		private final Checkbox myCheckbox;
		private ViewMode mode;
		private boolean checked;
		private EventListener<CheckEvent> checkEventEventListener;

		public FakeOrgUnitActiveEditorView()
		{
			myCheckbox = new Checkbox();
		}

		@Override
		public void update(final ViewMode viewMode)
		{
			this.mode = viewMode;
		}

		@Override
		public void addCheckEventListener(final EventListener<CheckEvent> wrapperListener)
		{
			this.checkEventEventListener = wrapperListener;
			myCheckbox.addEventListener(Events.ON_CHECK, wrapperListener);
		}

		@Override
		public void setChecked(final boolean checked)
		{
			this.checked = checked;
		}
	}

	private FakeOrgUnitActiveEditorView testView;

	private OrgUnitModel currentOrgUnit;

	@Test
	@Override
	public void testExtensibleFields()
	{
		//pass
	}

	@Before
	public void setUp()
	{
		currentOrgUnit = new OrgUnitModel();
		currentOrgUnit.setActive(true);

		testView = new FakeOrgUnitActiveEditorView();
		when(editorContext.getInitialValue()).thenReturn(true);
		doReturn(orgUnitAuthorizationStrategy).when(spyEditor).getOrgUnitAuthorizationStrategy();
		when(orgUnitAuthorizationStrategy.canEditUnit(any())).thenReturn(true);
		doReturn(widgetModel).when(spyEditor).getWidgetModel(any());
		doReturn(testView).when(spyEditor).createView();
		doReturn(userService).when(spyEditor).getUserService();
	}

	@Test
	public void shouldBeEditableCleanModeWhenHasPermissionAndNotBlockedByParent()
	{
		spyEditor.render(ancestor, editorContext, editorListener);
		Assert.assertEquals(OrgUnitActiveCheckboxBooleanEditorView.ViewMode.EDITABLE_CLEAN, testView.mode);
		Assert.assertTrue(testView.checked);
	}

	@Test
	public void shouldBeEditableActivatingModeWhenChangedToActive()
	{
		when(editorContext.getInitialValue()).thenReturn(false);
		spyEditor.render(ancestor, editorContext, editorListener);
		Assert.assertEquals(OrgUnitActiveCheckboxBooleanEditorView.ViewMode.EDITABLE_CLEAN, testView.mode);
		Assert.assertFalse(testView.checked);

		CockpitTestUtil.simulateEvent(testView.myCheckbox, new CheckEvent(Events.ON_CHECK, ancestor, true));
		Assert.assertEquals(OrgUnitActiveCheckboxBooleanEditorView.ViewMode.EDITABLE_ACTIVATING, testView.mode);
	}

	@Test
	public void shouldBeEditableDeactivatingModeWhenChangedToInactive()
	{
		spyEditor.render(ancestor, editorContext, editorListener);
		Assert.assertEquals(OrgUnitActiveCheckboxBooleanEditorView.ViewMode.EDITABLE_CLEAN, testView.mode);

		CockpitTestUtil.simulateEvent(testView.myCheckbox, new CheckEvent(Events.ON_CHECK, ancestor, false));
		Assert.assertEquals(OrgUnitActiveCheckboxBooleanEditorView.ViewMode.EDITABLE_DEACTIVATING, testView.mode);
	}

	@Test
	public void shouldBeDisabledNoPermissionModeWhenHasNoPermission()
	{
		when(orgUnitAuthorizationStrategy.canEditUnit(any())).thenReturn(false);
		spyEditor.render(ancestor, editorContext, editorListener);
		Assert.assertEquals(OrgUnitActiveCheckboxBooleanEditorView.ViewMode.DISABLED_NO_PERMISSION, testView.mode);
	}

	@Test
	public void shouldBeDisabledBlockedByParentModeWhenBlockedByParent()
	{
		final OrgUnitModel inActiveParent = new OrgUnitModel();
		inActiveParent.setActive(false);
		currentOrgUnit.setActive(false);
		when(orgUnitService.getParent(any())).thenReturn(java.util.Optional.of(inActiveParent));
		when(editorContext.getInitialValue()).thenReturn(false);
		doReturn(currentOrgUnit).when(spyEditor).getCurrentOrgUnitModel();
		spyEditor.render(ancestor, editorContext, editorListener);
		Assert.assertEquals(OrgUnitActiveCheckboxBooleanEditorView.ViewMode.DISABLED_BLOCKED_BY_PARENT, testView.mode);
	}

}
