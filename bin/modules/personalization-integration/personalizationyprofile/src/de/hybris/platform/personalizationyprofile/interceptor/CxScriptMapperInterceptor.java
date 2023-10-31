/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.interceptor;

import de.hybris.platform.personalizationintegration.model.CxMapperScriptModel;
import de.hybris.platform.personalizationyprofile.event.InvalidateConsumptionLayerUserSegmentsProviderCacheEvent;
import de.hybris.platform.personalizationyprofile.segment.ConsumptionLayerUserSegmentsProvider;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.springframework.beans.factory.annotation.Required;


/**
 * Interceptor called when CxScriptMapper model is created, edited or removed.<br/>
 *
 * CxScriptMapper has 'requiredField' attribute which is used by ConsumptionLayerUserSegmentsProvider to build fields
 * parameter for profile service.<br/>
 * This interceptor reset fields for ConsumptionLayerUserSegmentsProvider to make it build fields again.
 *
 */
public class CxScriptMapperInterceptor implements RemoveInterceptor<CxMapperScriptModel>, ValidateInterceptor<CxMapperScriptModel>
{
	private ConsumptionLayerUserSegmentsProvider consumptionLayerUserSegmentsProvider;
	private EventService eventService;

	@Override
	public void onRemove(final CxMapperScriptModel model, final InterceptorContext ctx) throws InterceptorException
	{
		eventService.publishEvent(new InvalidateConsumptionLayerUserSegmentsProviderCacheEvent());
	}

	@Override
	public void onValidate(final CxMapperScriptModel model, final InterceptorContext ctx) throws InterceptorException
	{
		if (ctx.isNew(model) || ctx.isModified(model, CxMapperScriptModel.REQUIREDFIELDS)
				|| ctx.isModified(model, CxMapperScriptModel.GROUP) || ctx.isModified(model, CxMapperScriptModel.DISABLED))
		{
			eventService.publishEvent(new InvalidateConsumptionLayerUserSegmentsProviderCacheEvent());
		}
	}

	protected ConsumptionLayerUserSegmentsProvider getConsumptionLayerUserSegmentsProvider()
	{
		return consumptionLayerUserSegmentsProvider;
	}

	@Required
	public void setConsumptionLayerUserSegmentsProvider(
			final ConsumptionLayerUserSegmentsProvider consumptionLayerUserSegmentsProvider)
	{
		this.consumptionLayerUserSegmentsProvider = consumptionLayerUserSegmentsProvider;
	}

	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
