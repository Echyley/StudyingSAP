/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ruleenginebackoffice.widgets.listview.renderer;

import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.common.AbstractWidgetComponentRenderer;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.monitor.RuleEngineRulesModuleMonitor;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Span;

import static de.hybris.platform.ruleengine.util.RuleEngineUtils.isDroolsKieModuleDeployed;


public class RulesModuleDeploymentStatusCellRenderer<R extends AbstractRulesModuleModel>
		extends AbstractWidgetComponentRenderer<Listcell, ListColumn, R>
{

	private static final String IMG_TITLE_STATUS_UNDEPLOYED = "rules.module.deployment.status.undeployed";
	private static final String IMG_TITLE_STATUS_DEPLOYED = "rules.module.deployment.status.deployed";
	private static final String IMG_TITLE_STATUS_PENDING = "rules.module.deployment.status.pending";
	private RuleEngineRulesModuleMonitor<DroolsKIEModuleModel> ruleEngineRulesModuleMonitor;

	@Override
	public void render(final Listcell parent, final ListColumn columnConfiguration, final R rulesModule, final DataType dataType,
			final WidgetInstanceManager widgetInstanceManager)
	{
		final HtmlBasedComponent image = getLabelImg(rulesModule);

		UITools.modifySClass(parent, "yw-listview-cell-restricted", true);

		parent.appendChild(image);

		fireComponentRendered(image, parent, columnConfiguration, rulesModule);
		fireComponentRendered(parent, parent, columnConfiguration, rulesModule);
	}

	protected HtmlBasedComponent getLabelImg(final AbstractRulesModuleModel rulesModule)
	{
		final Span span = new Span();
		span.setSclass("font-icon-container");

		// Create new Image
		final Button iconButton = new Button();
		
		iconButton.setSclass("cng-action-icon cng-font-icon yw-listview-cell-rule-status-icon");

		span.appendChild(iconButton);

		if (rulesModule instanceof DroolsKIEModuleModel)
		{
			final DroolsKIEModuleModel droolsRulesModule = (DroolsKIEModuleModel) rulesModule;

			final boolean rulesModuleDeployed = getRuleEngineRulesModuleMonitor().isRulesModuleDeployed(droolsRulesModule);
			if (rulesModuleDeployed)
			{
				if (isDroolsKieModuleDeployed(droolsRulesModule))
				{
					iconButton.setTooltiptext(Labels.getLabel(IMG_TITLE_STATUS_DEPLOYED));
					iconButton.addSclass("font-icon--status-positive");
					span.addSclass("rule-status-icon-green-color");
				}
				else
				{
					iconButton.setTooltiptext(Labels.getLabel(IMG_TITLE_STATUS_PENDING));
					iconButton.addSclass("font-icon--status-critical");
					span.addSclass("rule-status-icon-yellow-color");
				}
			}
			else
			{
				iconButton.setTooltiptext(Labels.getLabel(IMG_TITLE_STATUS_UNDEPLOYED));
				iconButton.addSclass("font-icon--status-negative");
				span.addSclass("rule-status-icon-red-color");
			}
		}

		return span;
	}

	protected RuleEngineRulesModuleMonitor<DroolsKIEModuleModel> getRuleEngineRulesModuleMonitor()
	{
		return ruleEngineRulesModuleMonitor;
	}

	@Required
	public void setRuleEngineRulesModuleMonitor(
			final RuleEngineRulesModuleMonitor<DroolsKIEModuleModel> ruleEngineRulesModuleMonitor)
	{
		this.ruleEngineRulesModuleMonitor = ruleEngineRulesModuleMonitor;
	}
}
