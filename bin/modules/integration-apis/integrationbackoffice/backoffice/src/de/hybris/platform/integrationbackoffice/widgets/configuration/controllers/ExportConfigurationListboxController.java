/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.controllers;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationbackoffice.widgets.common.services.IntegrationSortingService;
import de.hybris.platform.integrationbackoffice.widgets.configuration.data.ExportConfigurationEditorPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ExportConfigurationEditorPresentationPopulator;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.util.YTestTools;
import com.hybris.cockpitng.util.type.BackofficeTypeUtils;
import com.hybris.cockpitng.widgets.common.NotifyingWidgetComponentRenderer;
import com.hybris.cockpitng.widgets.common.NotifyingWidgetComponentRendererFactory;
import com.hybris.cockpitng.widgets.util.UILabelUtil;

/**
 * Controller for entity instances listbox of the export configuration editor.
 */
public class ExportConfigurationListboxController extends DefaultWidgetController
{
	private static final Logger LOG = Log.getLogger(ExportConfigurationListboxController.class);
	private static final String GET_ALL_INSTANCES_QUERY = "GET {%s}";
	private static final String HFLEX_COLUMN_MIN_WIDTH = "30";
	private static final String HFLEX_COLUMN_MIN_WIDTH_ATTR = "flexMinWidth";
	private static final String SCLASS_COL_HEADER = "yw-listview-colheader";
	private static final String HFLEX_MIN = "min";
	private static final String SCLASS_COL_HEADER_FILL = "yw-listview-colheader-fill";

	@WireVariable
	private transient NotifyingWidgetComponentRendererFactory<Listitem, ListView, Object> defaultNotifyingWidgetComponentRendererFactory;
	@WireVariable
	private transient BackofficeTypeUtils backofficeTypeUtils;
	@WireVariable
	private transient ExportConfigurationEditorPresentationPopulator exportConfigPresentationPopulator;
	private transient ExportConfigurationEditorPresentation exportConfigPresentation;
	@WireVariable
	private transient IntegrationSortingService integrationSortingService;
	@WireVariable
	private transient LabelService labelService;
	@WireVariable
	private transient I18NService i18NService;
	@WireVariable
	private transient FlexibleSearchService flexibleSearchService;

	private transient NotifyingWidgetComponentRenderer<Listitem, ListView, Object> renderer;

	private String typeCache = null;
	private transient Pageable<ItemModel> pageableCache = null;

	private transient ListView config;
	private Listbox instancesListbox;
	private Listhead instancesListhead;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		exportConfigPresentation = exportConfigPresentationPopulator.getExportConfigEditorPresentation();
		initListbox();
		initRenderer();
	}

	/**
	 * Triggered when clicking the item of list pane. Clear and generate listBox with a collection of instances of the type which
	 * is the root type of the {@link IntegrationObjectModel}. Do nothing if clicking the same item of configuration. The listBox
	 * will have no knowledge when refresh is clicked, while the {@link IntegrationObjectModel}'s rootType and the latest result will
	 * be cached and used later when this method is triggered with same ioModel.
	 *
	 * @param ioModel retrieve all instances of ioModel's root type and generate the listBox.
	 */
	@SocketEvent(socketId = "showListboxOfItem")
	public void changeListboxView(final IntegrationObjectModel ioModel)
	{
		if (ioModel == null || exportConfigPresentation.isSelectedEntity(ioModel))
		{
			return;
		}
		clearListbox();
		exportConfigPresentation.setSelectedEntity(ioModel);
		setHeaderConfig();

		changeViewWithCacheOrByTriggeringSearch(ioModel);
	}

	private void changeViewWithCacheOrByTriggeringSearch(final IntegrationObjectModel ioModel)
	{
		final String rootItemType = ioModel.getRootItem().getCode();
		if (rootItemType.equals(typeCache) && pageableCache != null)
		{
			changeListboxViewForSearchResult(pageableCache);
		}
		else
		{
			sendOutput("advancedSearchType", ioModel.getRootItem().getCode());
			typeCache = ioModel.getRootItem().getCode();
		}
	}

	@SocketEvent(socketId = "clearListboxOfItem")
	public void clearListboxView()
	{
		clearListbox();
		config = null;
	}

	@SocketEvent(socketId = "pageable")
	public void changeListboxViewForSearchResult(final Pageable<ItemModel> pageable)
	{
		if (config == null || StringUtils.isBlank(exportConfigPresentation.getSelectedEntityRoot()))
		{
			LOG.error("Please select a Entity type.");
			return;
		}
		if (pageable == null)
		{
			LOG.error("Search Engine failed to generate a result.");
			return;
		}

		this.pageableCache = pageable;

		final List<ItemModel> objectList = pageable.getCurrentPage();
		buildHeaders(config);
		renderEntries(objectList, config);

		final String countFraction = exportConfigPresentation.getSelectedEntityInstances().size() + "/"
				+ countInstancesOfType(exportConfigPresentation.getSelectedEntityRoot());
		sendOutput("currentSelectedCount", countFraction);
	}

	private void clearListbox()
	{
		instancesListbox.getItems().clear();
		instancesListhead.getChildren().clear();
	}

	private int countInstancesOfType(final String type)
	{
		return flexibleSearchService.search(String.format(GET_ALL_INSTANCES_QUERY, type)).getCount();

	}

	private void setHeaderConfig()
	{
		final String currentType = exportConfigPresentation.getSelectedEntityRoot();
		if (StringUtils.isNotEmpty(currentType))
		{
			config = loadConfigurationForListView(currentType);
		}
	}

	private ListView loadConfigurationForListView(final String type)
	{
		final String component = "listview";
		final DefaultConfigContext configContext = new DefaultConfigContext(component, type);
		return getListViewConfiguration(type, configContext);
	}

	private ListView getListViewConfiguration(final String type, final DefaultConfigContext configContext)
	{
		try
		{
			return getWidgetInstanceManager().loadConfiguration(configContext, ListView.class);
		}
		catch (final CockpitConfigurationException e)
		{
			LOG.error("Couldn't find the configuration of ListView for type: '{}'", type, e);
		}
		return null;
	}

	private void renderEntries(final List<ItemModel> currentPage, final ListView config)
	{
		final ListModelArray<ItemModel> simpleListModel = new ListModelArray<>(currentPage);
		simpleListModel.setMultiple(true);
		getModel().setValue("listModel", simpleListModel);
		renderEntries(simpleListModel, config);
	}

	private void renderEntries(final ListModelArray<ItemModel> simpleListModel, final ListView config)
	{
		updateSelectionStatus(simpleListModel);
		if (simpleListModel.getSize() > 0 && renderer != null)
		{
			final HashedMap indexes = new HashedMap();
			getModel().setValue("listModelIndexes", indexes);
			instancesListbox.setItemRenderer((row, entry, index) -> {
				renderer.render(row, config, entry, getDataType(entry), getWidgetInstanceManager());
				indexes.put(entry, index);
			});
		}
	}

	private DataType getDataType(final Object data)
	{
		final String type = backofficeTypeUtils.getTypeFacade().getType(data);
		try
		{
			return backofficeTypeUtils.getTypeFacade().load(type);
		}
		catch (final TypeNotFoundException e)
		{
			LOG.error("Could not resolve type: '{}' when rendering entries.", type);
		}
		return null;
	}

	private void onSelectItemEvent(final Event event)
	{
		if (event instanceof SelectEvent)
		{
			final SelectEvent<Listitem, Object> selectEvent = (SelectEvent<Listitem, Object>) event;
			final Set<ItemModel> selectedInstances = exportConfigPresentation.getSelectedEntityInstances();
			selectEvent.getUnselectedItems()
			           .stream()
			           .map(item -> (ItemModel) item.getValue())
			           .forEach(selectedInstances::remove);
			selectEvent.getSelectedItems()
			           .stream()
			           .map(item -> (ItemModel) item.getValue())
			           .forEach(selectedInstances::add);
			exportConfigPresentation.setSelectedEntityInstances(selectedInstances);


			final String countFraction = selectedInstances.size() + "/" + countInstancesOfType(
					exportConfigPresentation.getSelectedEntityRoot());
			sendOutput("currentSelectedCount", countFraction);
		}
	}


	private void initRenderer()
	{
		this.renderer = defaultNotifyingWidgetComponentRendererFactory.createWidgetComponentRenderer("listViewRenderer");
	}

	private void updateSelectionStatus(final ListModelArray<ItemModel> simpleListModel)
	{
		simpleListModel.setMultiple(instancesListbox.isMultiple());
		instancesListbox.setModel(simpleListModel);
		final Collection<ItemModel> selection = exportConfigPresentation.getSelectedEntityInstances();
		simpleListModel.setSelection(selection);
	}

	private void buildHeaders(final ListView columnConfig)
	{
		if (!instancesListhead.getChildren().isEmpty())
		{
			instancesListhead.getChildren().clear();
		}
		instancesListhead.addEventListener("onColSize", event -> Clients.resize(instancesListbox.getParent()));

		addCheckMarkToListHead();

		final List<ListColumn> columns = columnConfig.getColumn();
		if (CollectionUtils.isNotEmpty(columns))
		{
			for (final ListColumn column : columns)
			{
				applyAttribute(column);
			}
			addFillColumn(columns);
		}
	}

	private void addCheckMarkToListHead() {
		Listheader listHeader = new Listheader(null, null);
		listHeader.setHflex("min");
		instancesListhead.appendChild(listHeader);
	}

	private void applyAttribute(final ListColumn column)
	{
		Listheader columnHeader = null;
		final DataAttribute dataAttribute = getDataAttribute(column.getQualifier());
		if (dataAttribute != null)
		{
			columnHeader = createListHeader(dataAttribute);
		}
		else
		{
			columnHeader = createListHeader(column);
		}
		instancesListhead.appendChild(columnHeader);
		applyColumnAttributes(column, columnHeader);
		UITools.modifySClass(columnHeader, SCLASS_COL_HEADER, true);
	}

	private void addFillColumn(final List<ListColumn> columns)
	{
		final boolean shouldAddFillColumn = allColumnsHaveHflexMin(columns);
		if (shouldAddFillColumn)
		{
			final Listheader fill = new Listheader();
			instancesListhead.appendChild(fill);
			UITools.modifySClass(fill, SCLASS_COL_HEADER, true);
			UITools.modifySClass(fill, SCLASS_COL_HEADER_FILL, true);
		}
	}

	private boolean allColumnsHaveHflexMin(final List<ListColumn> columns)
	{
		return columns.stream().allMatch(column -> StringUtils.isBlank(column.getHflex()) || column.getHflex().equals(HFLEX_MIN));
	}

	private void applyColumnAttributes(final ListColumn column, final Listheader columnHeader)
	{
		if (StringUtils.isNotBlank(column.getWidth()))
		{
			columnHeader.setWidth(column.getWidth());
		}
		else if (StringUtils.isNotBlank(column.getHflex()))
		{
			columnHeader.setHflex(column.getHflex());
			columnHeader.setClientAttribute(HFLEX_COLUMN_MIN_WIDTH_ATTR, HFLEX_COLUMN_MIN_WIDTH);
		}
		else
		{
			columnHeader.setHflex(HFLEX_MIN);
		}
	}

	private Listheader createListHeader(final DataAttribute dataAttribute)
	{
		final String qualifier = dataAttribute.getQualifier();

		final Locale locale = i18NService.getCurrentLocale();
		final String listColumnHeaderLabel = dataAttribute.getLabel(locale);
		final Listheader listHeader = new Listheader(listColumnHeaderLabel, null);
		YTestTools.modifyYTestId(listHeader, "listheader_" + qualifier);
		listHeader.setValue(qualifier);
		listHeader.setLabel(listColumnHeaderLabel);
		if (integrationSortingService.isAttributeSortable(dataAttribute))
		{
			listHeader.setSort("auto(" + qualifier + ")");
		}
		listHeader.setTooltiptext(listColumnHeaderLabel);
		return listHeader;
	}

	private Listheader createListHeader(final ListColumn column)
	{
		final String typeCode = exportConfigPresentation.getSelectedEntityRoot();
		final String qualifier = column.getQualifier();
		final String listColumnHeaderLabel = UILabelUtil.getColumnHeaderLabel(column, typeCode, labelService);
		final Listheader listHeader = new Listheader(listColumnHeaderLabel, null);
		YTestTools.modifyYTestId(listHeader, "listheader_" + qualifier);
		listHeader.setValue(qualifier);
		listHeader.setTooltiptext(getLabel("integrationbackoffice.exportConfigurationListbox.columnFormat.clickToSort",
				new Object[]{ listColumnHeaderLabel }));
		return listHeader;
	}

	private DataAttribute getDataAttribute(final String qualifier)
	{
		final String type = exportConfigPresentation.getSelectedEntityRoot();
		try
		{
			return backofficeTypeUtils.getTypeFacade()
			                          .load(type)
			                          .getAttribute(qualifier);
		}
		catch (final TypeNotFoundException e)
		{
			LOG.error("Cannot load type: '{}' when getting data attribute.", type);
		}
		return null;
	}

	private void initListbox()
	{
		instancesListbox.setEmptyMessage(getLabel("integrationbackoffice.exportConfigurationListbox.columnFormat.noData"));
		instancesListbox.setMultiple(true);
		instancesListbox.setCheckmark(true);
		instancesListbox.addEventListener("onSelect", this::onSelectItemEvent);
	}
}
