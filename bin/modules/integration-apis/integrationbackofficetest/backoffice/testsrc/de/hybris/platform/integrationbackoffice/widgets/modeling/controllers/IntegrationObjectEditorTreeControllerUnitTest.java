/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.AUTO_SELECT_RELATION_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.CHECK_FOR_STRUCT_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.CREATE_DYNAMIC_NODE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.CREATE_TREE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.LOAD_IO_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.RENAME_NODES_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.RETYPE_NODES_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.controllers.IntegrationObjectEditorTreeController.VIEW_EVENT_COMPONENT_ID;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CLEAR_TREE;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationBackofficeEventSender;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DataStructureBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.TreePopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.CreateTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.RenameTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.RetypeTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionConverter;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionTrimmer;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectPresentationPopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Treeitem;

import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.cockpitng.util.notifications.NotificationService;

@DeclaredInput(value = LOAD_IO_IN_SOCKET, socketType = String.class)
@DeclaredInput(value = RETYPE_NODES_IN_SOCKET, socketType = RetypeTreeData.class)
@DeclaredInput(value = CREATE_TREE_IN_SOCKET, socketType = CreateTreeData.class)
@DeclaredInput(value = CHECK_FOR_STRUCT_IN_SOCKET, socketType = AbstractListItemDTO.class)
@DeclaredInput(value = CREATE_DYNAMIC_NODE_IN_SOCKET, socketType = AbstractListItemDTO.class)
@DeclaredInput(value = RENAME_NODES_IN_SOCKET, socketType = RenameTreeData.class)
@DeclaredInput(value = AUTO_SELECT_RELATION_IN_SOCKET, socketType = Treeitem.class)
@DeclaredInput(value = CLEAR_TREE)
@DeclaredViewEvent(componentID = VIEW_EVENT_COMPONENT_ID, eventName = Events.ON_SELECT)
@DeclaredGlobalCockpitEvent(eventName = ObjectCRUDHandler.OBJECTS_DELETED_EVENT, scope = CockpitEvent.SESSION)
@NullSafeWidget(false)
public class IntegrationObjectEditorTreeControllerUnitTest extends AbstractWidgetUnitTest<IntegrationObjectEditorTreeController>
{
	@Mock(lenient = true)
	private transient TreePopulator treePopulator;
	@Mock(lenient = true)
	private transient IntegrationObjectPresentationPopulator populator;
	@Mock(lenient = true)
	private transient IntegrationObjectPresentation editorPresentation;
	@Mock(lenient = true)
	private transient IntegrationObjectDefinitionConverter integrationObjectDefinitionConverter;
	@Mock(lenient = true)
	private transient ReadService readService;
	@Mock(lenient = true)
	private transient NotificationService notificationService;
	@Mock(lenient = true)
	private transient IntegrationObjectDefinitionTrimmer integrationObjectDefinitionTrimmer;
	@Mock(lenient = true)
	private transient DataStructureBuilder dataStructureBuilder;
	@Mock(lenient = true)
	private transient IntegrationBackofficeEventSender integrationBackofficeEventSender;

	@InjectMocks
	private IntegrationObjectEditorTreeController controller;

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
		verify(integrationBackofficeEventSender, times(1)).sendEvent(Events.ON_SELECT, controller.composedTypeTree, treeitem);
	}

	@Override
	protected IntegrationObjectEditorTreeController getWidgetController()
	{
		return controller;
	}
}
