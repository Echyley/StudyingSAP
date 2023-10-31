/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMock;
import de.hybris.platform.sap.productconfig.runtime.mock.ConfigMockFactory;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;


/**
 * Manages the creation of mock implementations for base product and variant product codes
 */
public class RunTimeConfigMockFactory implements ConfigMockFactory
{
	private CommonI18NService i18NService;
	private ProviderFactory providerFactory;

	@Override
	public ConfigMock createConfigMockForProductCode(final String productCode, final String variantProductCode)
	{
		BaseRunTimeConfigMockImpl mock = null;

		switch (productCode)
		{
			case "CPQ_HOME_THEATER":
				mock = new CPQHomeTheaterPocConfigMockImpl();
				break;

			case "CPQ_LAPTOP":
				mock = new CPQLaptopPocConfigMockImpl();
				break;

			case "CONF_BANDSAW_ML":
				mock = new CPQBandsawMockImpl();
				break;

			case "CONF_SCREWDRIVER_S":
				mock = new CPQScrewdriverMockImpl(variantProductCode);
				break;

			case "CONF_CAMERA_SL":
				mock = new DigitalCameraMockImpl(variantProductCode);
				break;

			case "CONF_HOME_THEATER_ML":
				mock = new HomeTheaterMockImpl();
				break;


			default:
				if (productCode.startsWith("CONF_M_PIPE"))
				{
					mock = new ConfPipeMockImpl(variantProductCode);
				}
				else
				{
					mock = new YSapSimplePocConfigMockImpl();
				}
				break;
		}
		mock.setI18NService(getI18NService());
		mock.setProviderFactory(getProviderFactory());
		return mock;
	}

	@Override
	public ConfigMock createConfigMockForProductCode(final String productCode)
	{
		return createConfigMockForProductCode(productCode, null);
	}

	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}

	public void setI18NService(final CommonI18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}
}
