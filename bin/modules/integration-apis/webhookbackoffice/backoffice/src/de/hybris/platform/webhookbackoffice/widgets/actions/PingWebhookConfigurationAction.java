/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.widgets.actions;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;
import java.util.Objects;

/**
 * Action to send WebhookConfigurationModel to widget
 */
public class PingWebhookConfigurationAction extends AbstractComponentWidgetAdapterAware
        implements CockpitAction<WebhookConfigurationModel, String> {

    @Override
    public ActionResult<String> perform(ActionContext<WebhookConfigurationModel> actionContext)
    {
        sendOutput("templateWebhookConfiguration", actionContext.getData());
        return new ActionResult<>(ActionResult.SUCCESS);
    }

    @Override
    public boolean canPerform(final ActionContext<WebhookConfigurationModel> ctx)
    {
        return Objects.nonNull(ctx.getData());
    }
}