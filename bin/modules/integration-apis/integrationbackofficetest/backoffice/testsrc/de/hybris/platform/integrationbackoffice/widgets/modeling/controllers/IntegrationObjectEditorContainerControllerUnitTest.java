/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationEditorContainerController.FILTER_STATE_CHANGE_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationEditorContainerController.REFRESH_CONTAINER_IN_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.LOAD_IO_OUTPUT_SOCKET;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.CreateIntegrationObjectModalData;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.CreateVirtualAttributeModalData;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.SelectedClassificationAttributesData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Collection;

import org.junit.Test;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

@DeclaredInput(value = REFRESH_CONTAINER_IN_SOCKET, socketType = String.class)
@DeclaredInput(value = FILTER_STATE_CHANGE_IN_SOCKET, socketType = IntegrationFilterState.class)
@DeclaredInput(value = "receiveObjectComboBox", socketType = IntegrationObjectModel.class)
@DeclaredInput(value = "createIntegrationObjectEvent", socketType = CreateIntegrationObjectModalData.class)
@DeclaredInput(value = "saveEvent", socketType = String.class)
@DeclaredInput(value = "receiveDelete")
@DeclaredInput(value = "metadataModalEvent", socketType = String.class)
@DeclaredInput(value = "openItemTypeIOIModalEvent", socketType = String.class)
@DeclaredInput(value = "openVirtualAttributeModal", socketType = String.class)
@DeclaredInput(value = "auditReportEvent")
@DeclaredInput(value = "receiveClone")
@DeclaredInput(value = "saveButtonItemTypeMatch", socketType = Collection.class)
@DeclaredInput(value = "cloneIntegrationObjectEvent", socketType = CreateIntegrationObjectModalData.class)
@DeclaredInput(value = "addClassificationAttributesEvent", socketType = SelectedClassificationAttributesData.class)
@DeclaredInput(value = "addVirtualAttributeEvent", socketType = CreateVirtualAttributeModalData.class)
@NullSafeWidget(false)
@ExtensibleWidget(false)
@UnitTest
public class IntegrationObjectEditorContainerControllerUnitTest
		extends AbstractWidgetUnitTest<IntegrationObjectEditorContainerController>
{
	private final IntegrationObjectEditorContainerController containerController = new IntegrationObjectEditorContainerController();

	@Override
	protected IntegrationObjectEditorContainerController getWidgetController()
	{
		return containerController;
	}

	@Test
	public void testLoadObject()
	{
		final IntegrationObjectPresentation presentation = mock(IntegrationObjectPresentation.class);
		containerController.setEditorPresentation(presentation);
		final IntegrationObjectModel io = new IntegrationObjectModel();
		doNothing().when(presentation).setSelectedIntegrationObject(io);
		doNothing().when(presentation).setModified(false);
		executeInputSocketEvent("receiveObjectComboBox", io);
		assertSocketOutput(LOAD_IO_OUTPUT_SOCKET, 1, "");
	}
}
