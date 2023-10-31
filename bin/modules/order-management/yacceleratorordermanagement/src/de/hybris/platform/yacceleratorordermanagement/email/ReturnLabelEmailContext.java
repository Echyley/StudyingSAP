/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.yacceleratorordermanagement.email;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnProcessModel;


/**
 * Velocity context for return label email.
 */
public class ReturnLabelEmailContext extends AbstractEmailContext<ReturnProcessModel>
{
	private String orderCode;

	@Override
	public void init(final ReturnProcessModel returnProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(returnProcessModel, emailPageModel);
		orderCode = returnProcessModel.getReturnRequest().getOrder().getCode();
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

	public String getOrderCode()
	{
		return orderCode;
	}

	@Override
	protected LanguageModel getEmailLanguage(final ReturnProcessModel returnProcessModel)
	{
		return returnProcessModel.getReturnRequest().getOrder().getLanguage();
	}

}
