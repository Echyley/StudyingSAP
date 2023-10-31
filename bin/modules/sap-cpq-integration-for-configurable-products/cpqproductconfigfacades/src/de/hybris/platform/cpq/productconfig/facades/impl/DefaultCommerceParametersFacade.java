/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import de.hybris.platform.cpq.productconfig.facades.CommerceParametersFacade;
import de.hybris.platform.cpq.productconfig.facades.data.CommerceParameters;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;


/**
 * Default implementation of {@link CommerceParametersFacade}
 */
public class DefaultCommerceParametersFacade implements CommerceParametersFacade
{
	private final CommonI18NService commonI18NService;
	private final I18NService i18NService;


	/**
	 * Default Constructor. Expects all required dependencies as constructor args
	 *
	 * @param i18NService
	 *           I18N service
	 * @param commonI18NService
	 *           CommonI18N service
	 */
	public DefaultCommerceParametersFacade(final I18NService i18NService, final CommonI18NService commonI18NService)
	{
		super();
		this.i18NService = i18NService;
		this.commonI18NService = commonI18NService;
	}


	@Override
	public CommerceParameters prepareCommerceParameters()
	{
		final CommerceParameters parameters = new CommerceParameters();
		parameters.setLocale(getI18NService().getCurrentLocale().toLanguageTag());
		parameters.setCurrency(getCommonI18NService().getCurrentCurrency().getIsocode());
		return parameters;
	}


	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}


	protected I18NService getI18NService()
	{
		return i18NService;
	}
}
