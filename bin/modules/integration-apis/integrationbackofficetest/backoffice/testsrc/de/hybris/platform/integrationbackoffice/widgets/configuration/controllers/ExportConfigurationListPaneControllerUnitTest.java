/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.configuration.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.configuration.controllers.ExportConfigurationListPaneController.HEADER_LABEL;
import static de.hybris.platform.integrationbackoffice.widgets.configuration.controllers.ExportConfigurationListPaneController.POPULATE_LISTBOX_INPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.configuration.controllers.ExportConfigurationListPaneController.UPDATE_ITEM_COUNT_INPUT_SOCKET;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils;
import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationListPaneControllerUnitTest;
import de.hybris.platform.integrationbackoffice.widgets.configuration.data.ExportConfigurationEditorPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorPresentationPopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorService;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

@DeclaredInput(value = POPULATE_LISTBOX_INPUT_SOCKET)
@DeclaredInput(value = UPDATE_ITEM_COUNT_INPUT_SOCKET, socketType = String.class)
@NullSafeWidget
public class ExportConfigurationListPaneControllerUnitTest
		extends AbstractIntegrationListPaneControllerUnitTest<ExportConfigurationListPaneController>
{
	@Mock(lenient = true)
	private ExportConfigurationSearchService exportConfigurationSearchService;
	@Mock(lenient = true)
	private ExportConfigurationEditorPresentationPopulator exportConfigPresentationPopulator;
	@Mock(lenient = true)
	private ExportConfigurationEditorService exportConfigEditorService;
	@Mock(lenient = true)
	private ExportConfigurationEditorPresentation exportConfigEditorPresentation;

	@Spy
	@InjectMocks
	private ExportConfigurationListPaneController controller;

	private static final String EXPECTED_LIST_HEADER = "Configurations [# Items Selected]";

	@Before
	public void setup()
	{
		lenient().when(exportConfigPresentationPopulator.getExportConfigEditorPresentation())
		         .thenReturn(exportConfigEditorPresentation);
		super.setup();
	}

	@Override
	protected ExportConfigurationListPaneController getWidgetController()
	{
		return controller;
	}

	@Test
	public void verifyComponentInitializationSetsHeaderAndPopulatesPane()
	{
		lenient().when(controller.getLabel(HEADER_LABEL)).thenReturn(EXPECTED_LIST_HEADER);
		controller.initialize(mock(Component.class));

		assertEquals(EXPECTED_LIST_HEADER, listheader().getLabel());
		verify(controller, times(1)).populateListbox();
	}

	@Test
	@Ignore("https://cxjira.sap.com/browse/IAPI-5377")
	public void testPopulateListboxIsFilledWithExportableTypes()
	{
		// setup
		final ComposedTypeModel composedTypeModel1 = IntegrationbackofficetestUtils.composedTypeModel("Product");
		final IntegrationObjectItemModel ioi1 = IntegrationbackofficetestUtils.integrationObjectItemModel("ProductAlias",
				composedTypeModel1);

		final ComposedTypeModel composedTypeModel2 = IntegrationbackofficetestUtils.composedTypeModel("Webhook");
		final IntegrationObjectItemModel ioi2 = IntegrationbackofficetestUtils.integrationObjectItemModel("WebhookAlias",
				composedTypeModel2);

		final IntegrationObjectModel io1 = IntegrationbackofficetestUtils.integrationObjectModelMock("ProdIO", ioi1);
		final IntegrationObjectModel io2 = IntegrationbackofficetestUtils.integrationObjectModelMock("WebhookIO", ioi2);
		final List<ItemModel> mockedItemModelList = List.of(mock(ItemModel.class), mock(ItemModel.class), mock(ItemModel.class));

		lenient().when(exportConfigurationSearchService.getExportableIntegrationObjects()).thenReturn(Set.of(io1, io2));
		lenient().when(exportConfigEditorPresentation.getSelectedInstancesCountForEntity(io1.getCode())).thenReturn(0);
		lenient().when(exportConfigEditorPresentation.getSelectedInstancesCountForEntity(io2.getCode())).thenReturn(2);
		lenient().when(exportConfigEditorService.findItemModelInstances(io1.getRootItem().getType().getCode())).thenReturn(
				mockedItemModelList);
		lenient().when(exportConfigEditorService.findItemModelInstances(io2.getRootItem().getType().getCode())).thenReturn(
				mockedItemModelList);

		// test
		executeInputSocketEvent(POPULATE_LISTBOX_INPUT_SOCKET);

		final String io1ExpectedLabel = "Product";
		final String io2ExpectedLabel = "Webhook [2/3]";

		assertEquals(2, listbox().getItems().size());

		assertThat(listbox().getItems())
				.extracting("value", "label")
				.containsExactlyInAnyOrder(tuple(io1, io1ExpectedLabel), tuple(io2, io2ExpectedLabel));
	}
}
