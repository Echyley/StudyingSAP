/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.controllers;

import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationListPaneController;
import de.hybris.platform.integrationbackoffice.widgets.configuration.data.ExportConfigurationEditorPresentation;
import de.hybris.platform.integrationbackoffice.widgets.configuration.utility.ExportConfigurationUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorPresentationPopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorService;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2services.export.ExportConfigurationSearchService;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Listitem;

import com.hybris.cockpitng.annotations.SocketEvent;

/**
 * Controller for integrationbackoffice export configuration list pane.
 * Listbox is populated by exportable {@link IntegrationObjectModel}
 */
public class ExportConfigurationListPaneController extends AbstractIntegrationListPaneController
{
	protected static final String OUTPUT_SOCKET = "sendClickedItem";
	protected static final String POPULATE_LISTBOX_INPUT_SOCKET = "populateListbox";
	protected static final String UPDATE_ITEM_COUNT_INPUT_SOCKET = "currentSelectedCount";
	protected static final String HEADER_LABEL = "integrationbackoffice.exportConfig.listPane.columnHeader.item";

	@WireVariable
	protected transient ExportConfigurationSearchService exportConfigurationSearchService;
	@WireVariable
	private transient ExportConfigurationEditorPresentationPopulator exportConfigPresentationPopulator;
	protected transient ExportConfigurationEditorPresentation exportConfigPresentation;
	@WireVariable
	protected transient ExportConfigurationEditorService exportConfigEditorService;

	@Override
	public void initialize(final Component comp)
	{
		super.initialize(comp);
		exportConfigPresentation = exportConfigPresentationPopulator.getExportConfigEditorPresentation();
		setListheaderLabel(getLabel(HEADER_LABEL));
		populateListbox();
	}

	@Override
	@SocketEvent(socketId = POPULATE_LISTBOX_INPUT_SOCKET)
	public void populateListbox()
	{
		listPaneListbox.getChildren().clear();
		listPaneListbox.appendChild(listPaneListhead);
		final Set<IntegrationObjectModel> exportableTypes = exportConfigurationSearchService.getExportableIntegrationObjects();
		exportableTypes.forEach(type -> {
			final Listitem item = ExportConfigurationUtils.createListitem(type);
			ExportConfigurationUtils.appendCountToLabel(item, determineFraction(type));
			addListitemEvent(item);
			listPaneListbox.appendChild(item);
		});
	}

	@SocketEvent(socketId = UPDATE_ITEM_COUNT_INPUT_SOCKET)
	public void updateItemCount(final String countFraction)
	{
		if (!StringUtils.isEmpty(countFraction))
		{
			final Listitem currentItem = listPaneListbox.getSelectedItem();

			ExportConfigurationUtils.appendCountToLabel(currentItem, countFraction);
		}
	}

	@Override
	public void addListitemEvent(final Listitem listitem)
	{
		listitem.addEventListener(Events.ON_CLICK, event -> sendOutput(OUTPUT_SOCKET, listitem.getValue()));
	}

	private String determineFraction(final IntegrationObjectModel type)
	{
		final String rootItemTypeCode = type.getRootItem().getType().getCode();

		final int numerator = exportConfigPresentation.getSelectedInstancesCountForEntity(type.getCode());
		final int denominator = exportConfigEditorService.findItemModelInstances(rootItemTypeCode).size();

		return numerator + "/" + denominator;
	}
}
