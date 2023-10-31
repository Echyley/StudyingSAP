/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * Velocity context for return label email.
 */
public class ReturnLabelEmailContext extends AbstractEmailContext<ReturnProcessModel>
{
	private transient Converter<OrderModel, OrderData> orderConverter;
	private OrderData orderData;

	@Override
	public void init(final ReturnProcessModel returnProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(returnProcessModel, emailPageModel);
		orderData = getOrderConverter().convert(returnProcessModel.getReturnRequest().getOrder());
	}

	@Override
	protected BaseSiteModel getSite(final ReturnProcessModel returnProcessModel)
	{
		return returnProcessModel.getReturnRequest().getOrder().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final ReturnProcessModel returnProcessModel)
	{
		return (CustomerModel) returnProcessModel.getReturnRequest().getOrder().getUser();
	}

	protected Converter<OrderModel, OrderData> getOrderConverter()
	{
		return orderConverter;
	}

	public void setOrderConverter(final Converter<OrderModel, OrderData> orderConverter)
	{
		this.orderConverter = orderConverter;
	}

	public OrderData getOrder()
	{
		return orderData;
	}

	@Override
	protected LanguageModel getEmailLanguage(final ReturnProcessModel returnProcessModel)
	{
		return returnProcessModel.getReturnRequest().getOrder().getLanguage();
	}

}
