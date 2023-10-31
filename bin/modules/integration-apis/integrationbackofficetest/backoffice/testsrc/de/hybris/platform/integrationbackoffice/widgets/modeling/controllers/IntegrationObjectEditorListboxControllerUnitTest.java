/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorListboxController.RENAME_ATTRIBUTE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorListboxController.RETYPE_ATTRIBUTE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorListboxController.SELECTED_ITEM_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CLEAR_LIST_BOX;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationBackofficeEventSender;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.RenameAttributeModalData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.IntegrationMapKeyDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectPresentationPopulator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Treeitem;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

@DeclaredInput(value = SELECTED_ITEM_IN_SOCKET, socketType = IntegrationMapKeyDTO.class)
@DeclaredInput(value = RETYPE_ATTRIBUTE_IN_SOCKET, socketType = SubtypeData.class)
@DeclaredInput(value = RENAME_ATTRIBUTE_IN_SOCKET, socketType = RenameAttributeModalData.class)
@DeclaredInput(value = CLEAR_LIST_BOX)
@NullSafeWidget(false)
public class IntegrationObjectEditorListboxControllerUnitTest
		extends AbstractWidgetUnitTest<IntegrationObjectEditorListboxController>
{
	@Mock(lenient = true)
	private transient IntegrationObjectPresentationPopulator populator;
	@Mock(lenient = true)
	private transient IntegrationObjectPresentation editorPresentation;
	@Mock(lenient = true)
	private transient IntegrationBackofficeEventSender integrationBackofficeEventSender;

	@InjectMocks
	private IntegrationObjectEditorListboxController controller;

	@Before
	public void setUp()
	{
		when(populator.getIOEditorPresentation()).thenReturn(editorPresentation);
	}

	@Test
	public void eventSenderTestNotNull()
	{
		final Treeitem treeitem = new Treeitem();
		controller.sendOnSelectEvent(treeitem);
		verify(integrationBackofficeEventSender, times(1)).sendEvent(Events.ON_SELECT, editorPresentation.getComposedTypeTree(),
				treeitem);
	}

	@Override
	protected IntegrationObjectEditorListboxController getWidgetController()
	{
		return controller;
	}
}
