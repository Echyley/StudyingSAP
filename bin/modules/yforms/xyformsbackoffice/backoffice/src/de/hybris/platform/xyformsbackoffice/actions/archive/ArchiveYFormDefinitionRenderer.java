/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.actions.archive;

import static de.hybris.platform.xyformsbackoffice.constants.XyformsbackofficeConstants.BUILDER;
import static de.hybris.platform.xyformsbackoffice.constants.XyformsbackofficeConstants.ORBEON;

import de.hybris.platform.xyformsservices.enums.YFormDefinitionStatusEnum;
import de.hybris.platform.xyformsservices.model.YFormDefinitionModel;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Button;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionListener;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.actions.impl.DefaultActionRenderer;
import com.hybris.cockpitng.engine.CockpitWidgetEngine;

public class ArchiveYFormDefinitionRenderer extends DefaultActionRenderer<java.lang.String, java.lang.Object>
{
	protected static final String UNARCHIVE_TITLE = "title.unarchive";
	protected static final String ARCHIVE_TITLE = "title.archive";
	protected static final String BUTTON_TOGGLE_CSS = "yforms-button--toggle";

	public boolean isArchived(final ActionContext<?> ctx)
	{
		boolean archived = false;

		if (ctx.getData() instanceof YFormDefinitionModel)
		{
			final YFormDefinitionModel yformDefinition = (YFormDefinitionModel) ctx.getData();
			archived = YFormDefinitionStatusEnum.DISABLED.equals(yformDefinition.getStatus());

			// the form builder cannot be enabled, since it is only a placeholder
			archived = archived
					&& !(ORBEON.equals(yformDefinition.getApplicationId()) && BUILDER.equals(yformDefinition.getFormId()));
		}

		return archived;
	}

	@Override
	protected String getLocalizedName(final ActionContext<?> context)
	{
		return isArchived(context) ? context.getLabel(UNARCHIVE_TITLE) : context.getLabel(ARCHIVE_TITLE);
	}

	@Override
	public void render(final Component parent, final CockpitAction<String, Object> action, final ActionContext<String> context,
			final boolean updateMode, final ActionListener<Object> listener)
	{
		super.render(parent, action, context, updateMode, listener);

		final boolean isArchived = isArchived(context);
		final HtmlBasedComponent container = getOrCreateContainer(parent);
		final Button toggleArchiveButton = (Button) container.getFirstChild();

		if (toggleArchiveButton != null) {
			if (isArchived)
			{
				toggleArchiveButton.addSclass(BUTTON_TOGGLE_CSS);
			}
			else
			{
				toggleArchiveButton.removeSclass(BUTTON_TOGGLE_CSS);
			}
		}
	}
}
