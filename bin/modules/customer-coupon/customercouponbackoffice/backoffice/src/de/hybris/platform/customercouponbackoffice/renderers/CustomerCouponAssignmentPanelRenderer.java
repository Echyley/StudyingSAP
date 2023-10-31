/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponbackoffice.renderers;

import de.hybris.platform.customercouponservices.model.CustomerCouponModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Attribute;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomPanel;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.common.ProxyRenderer;
import com.hybris.cockpitng.widgets.editorarea.renderer.EditorAreaRendererUtils;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.DefaultEditorAreaPanelRenderer;


/**
 * Renders the editor area for displaying the coupon campaign URL
 */
public class CustomerCouponAssignmentPanelRenderer extends DefaultEditorAreaPanelRenderer
{
	private static final String LABEL_CAMPAIGN_URL = "label.coupon.campaign.url";
	private static final String CELL_SCLASS = "yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-attrcell";
	private static final String LABEL_CLASS = "yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-attrcell-label";
	private static final String LABEL_CLASS_MODIFIED = "yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-attrcell-label-mandatory-attribute";

	private static final String CAMPAIGN_URL_QUALIFIER = "campaignURL";
	private static final String CURRENT_OBJECT = "currentObject";
	private static final String STRING_EDITOR_ID = "com.hybris.cockpitng.editor.defaulttext";


	@Override
	public void render(final Component parent, final AbstractPanel panel, final Object object,
			final DataType dataType, final WidgetInstanceManager widgetInstanceManager)
	{
		final CustomPanel customPanel = (CustomPanel) panel;
		final List<Attribute> attributes = customPanel.getAttributeOrCustom().stream().filter(ap -> ap instanceof Attribute)
				.map(ap -> (Attribute) ap).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(attributes))
		{
			for (final Attribute attribute : attributes)
			{
				final Div div = new Div();
				div.setParent(parent);

				final String qualifier = attribute.getQualifier();
				if (qualifier.equalsIgnoreCase(CAMPAIGN_URL_QUALIFIER))
				{
					div.setSclass(SCLASS_EDITOR_CONTAINER);
					renderCampainURL((CustomerCouponModel) object, widgetInstanceManager, qualifier, div);
				}
				else
				{
					new ProxyRenderer<>(this, parent, panel, object).render(createAttributeRenderer(), div, attribute, object,
							dataType, widgetInstanceManager);
				}
			}
		}
	}

	
	protected void renderCampainURL(final CustomerCouponModel coupon, final WidgetInstanceManager widgetInstanceManager,
			final String qualifier, final Div div)
	{
		final WidgetModel model = widgetInstanceManager.getModel();
		final String campaignURL = generateCouponCampaignURL(coupon);
		model.setValue(qualifier, campaignURL);

		final Editor editor = new Editor();
		editor.setProperty(qualifier);
		editor.setType(String.class.getName());
		editor.setWidgetInstanceManager(widgetInstanceManager);
		editor.setDefaultEditor(STRING_EDITOR_ID);
		editor.setEditorLabel(qualifier);
		editor.setReadOnly(true);
		editor.afterCompose();

		final Label label = new Label(Labels.getLabel(LABEL_CAMPAIGN_URL));
		label.setSclass(LABEL_CLASS);
		label.setTooltiptext(Labels.getLabel(LABEL_CAMPAIGN_URL));
		label.setParent(div);
		UITools.modifySClass(label, LABEL_CLASS_MODIFIED, true);

		final Hbox hbox = new Hbox();
		hbox.setParent(div);
		final Cell cell = new Cell();
		cell.setParent(hbox);
		cell.setSclass(CELL_SCLASS);
		cell.appendChild(label);
		cell.appendChild(editor);

		EditorAreaRendererUtils.setAfterSaveListener(model, qualifier, new EventListener()
		{
			@Override
			public void onEvent(final Event event) throws Exception
			{
				final CustomerCouponModel coupon = model.getValue(CURRENT_OBJECT, CustomerCouponModel.class);
				model.setValue(qualifier, generateCouponCampaignURL(coupon));
				model.changed();
			}
		}, false);
	}
	
	protected String generateCouponCampaignURL(final CustomerCouponModel coupon)
	{
		if (Objects.isNull(coupon) || StringUtils.isEmpty(coupon.getCouponId()))
		{
			return StringUtils.EMPTY;
		}

		return "/my-account/coupon/claim/" + coupon.getCouponId();
	}
	
	
}
