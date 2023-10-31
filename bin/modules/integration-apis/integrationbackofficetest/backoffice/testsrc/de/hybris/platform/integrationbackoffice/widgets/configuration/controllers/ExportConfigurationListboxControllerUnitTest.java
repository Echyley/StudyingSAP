/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.configuration.controllers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;

import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

@DeclaredInput(value = "showListboxOfItem", socketType = IntegrationObjectModel.class)
@DeclaredInput(value = "clearListboxOfItem")
@DeclaredInput(value = "pageable", socketType = Pageable.class)
@NullSafeWidget(false)
@ExtensibleWidget(false)
@UnitTest
public class ExportConfigurationListboxControllerUnitTest extends AbstractWidgetUnitTest<ExportConfigurationListboxController>
{
	@Mock
	private Listbox instancesListbox;
	@Mock
	private Listhead instancesListhead;
	@Spy
	@InjectMocks
	private ExportConfigurationListboxController controller;

	@Override
	protected ExportConfigurationListboxController getWidgetController()
	{
		return controller;
	}

	@Test
	public void testClearListboxView()
	{
		final List<Listitem> mockListboxItems = new ArrayList<>(List.of(new Listitem()));
		final List<Component> mockListheadChildren = new ArrayList<>(List.of(mock(Component.class)));

		doReturn(mockListboxItems).when(instancesListbox).getItems();
		doReturn(mockListheadChildren).when(instancesListhead).getChildren();
		controller.clearListboxView();

		assertTrue(mockListboxItems.isEmpty());
		assertTrue(mockListheadChildren.isEmpty());
	}
}
