/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.merchandisingservicesbackoffice.editor;

import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.MerchSnConfigModel;

/**
 * Custom editor to display a dynamically generated list of indexed properties (fields)
 */
public class DefaultRollupStrategyFieldEditor implements CockpitEditorRenderer<Object>
{
	private static final String PARENT_OBJECT = "parentObject";
	private static final String INDEX_FIELD_TYPE_DELIMITER = "_";


	@Resource
	private ModelService modelService;

	@Override
	public void render(final Component parentComponent,
	                   final EditorContext<Object> editorContext,
	                   final EditorListener<Object> editorListener)
	{
		final Combobox editorView = new Combobox();
		editorView.setValue((String) editorContext.getInitialValue());

		final ListModel<String> listModel = new ListModelList<>(getIndexProperties(editorContext.getParameter(PARENT_OBJECT)));
		editorView.setModel(listModel);
		editorView.setReadonly(true);
		editorView.addEventListener(Events.ON_CHANGE, event -> handleEvent(editorView, event, editorListener));
		editorView.addEventListener(Events.ON_OK, event -> handleEvent(editorView, event, editorListener));
		editorView.setParent(parentComponent);
	}

	private void handleEvent(final Textbox editorView, final Event event, final EditorListener<Object> listener)
	{
		final String result = (String) editorView.getRawValue();
		listener.onValueChanged(StringUtils.isEmpty(result) ? "" : result);
		if (Events.ON_OK.equals(event.getName()))
		{
			listener.onEditorEvent(EditorListener.ENTER_PRESSED);
		}
	}

	private List<String> getIndexProperties(final Object productDirectoryConfigModel)
	{
		if (productDirectoryConfigModel instanceof MerchSnConfigModel)
		{
			return getIndexProperties((MerchSnConfigModel) productDirectoryConfigModel);
		}
		return getIndexProperties((MerchProductDirectoryConfigModel) productDirectoryConfigModel);
	}

	private List<String> getIndexProperties(final MerchSnConfigModel merchSnConfigModel)
	{
		return Optional.of(merchSnConfigModel)
		               .map(MerchSnConfigModel::getSnIndexType)
		               .map(SnIndexTypeModel::getFields)
		               .map(indexFieldList -> indexFieldList.stream()
		                                                    .map(index -> index.getId())
		                                                    .collect(Collectors.toList()))
		               .orElse(Collections.emptyList());

	}

	private List<String> getIndexProperties(final MerchProductDirectoryConfigModel productDirectoryConfigModel)
	{
		return Optional.of(productDirectoryConfigModel)
		               .map(MerchProductDirectoryConfigModel::getIndexedType)
		               .map(SolrIndexedTypeModel::getSolrIndexedProperties)
		               .map(indexPropertyList -> indexPropertyList.stream()
		                                                          .map(index -> index.getName() + INDEX_FIELD_TYPE_DELIMITER + index.getType().getCode())
		                                                          .collect(Collectors.toList()))
		               .orElse(Collections.emptyList());
	}
}
