/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.ENABLE_SAVE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.FILTER_STATE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.LOAD_OBJECT_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.REFRESH_BUTTON_ID;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController.SAVE_DEFINITIONS_BUTTON_ID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.lenient;

import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelController;
import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationButtonPanelControllerUnitTest;
import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;
import de.hybris.platform.integrationbackoffice.widgets.configuration.data.ExportConfigurationEditorPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorPresentationPopulator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.zkoss.zk.ui.event.Events;

import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;

@DeclaredInput(value = LOAD_OBJECT_IN_SOCKET)
@DeclaredInput(value = FILTER_STATE_IN_SOCKET, socketType = IntegrationFilterState.class)
@DeclaredInput(value = ENABLE_SAVE_IN_SOCKET, socketType = Boolean.class)
@DeclaredViewEvent(componentID = SAVE_DEFINITIONS_BUTTON_ID, eventName = Events.ON_CLICK)
@DeclaredViewEvent(componentID = REFRESH_BUTTON_ID, eventName = Events.ON_CLICK)
public class ExportConfigurationButtonPanelControllerUnitTest
		extends AbstractIntegrationButtonPanelControllerUnitTest<ExportConfigurationButtonPanelController>
{
	@Mock(lenient = true)
	private ExportConfigurationEditorPresentationPopulator exportConfigPresentationPopulator;
	@Mock(lenient = true)
	private ExportConfigurationEditorPresentation presentation;

	@InjectMocks
	private ExportConfigurationButtonPanelController buttonPanelController;

	@Before
	public void setup()
	{
		lenient().when(exportConfigPresentationPopulator.getExportConfigEditorPresentation()).thenReturn(presentation);
		super.setup();
	}

	@Override
	protected AbstractIntegrationButtonPanelController getWidgetController()
	{
		return buttonPanelController;
	}

	@Test
	public void unsavedChangesPresentTrueWhenExportConfigurationEditorPresentationIsAnyInstanceSelectedTrue()
	{
		lenient().when(presentation.isAnyInstanceSelected()).thenReturn(true);
		assertTrue("Unsaved changes present when any instance selected in export config editor",
				buttonPanelController.unsavedChangesPresent());
	}

	@Test
	public void unsavedChangesPresentFalseWhenExportConfigurationEditorPresentationIsAnyInstanceSelectedFalse()
	{
		lenient().when(presentation.isAnyInstanceSelected()).thenReturn(false);
		assertFalse("Unsaved changes not present when no instances selected in export config editor",
				buttonPanelController.unsavedChangesPresent());
	}

}
