/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link B2BCostCenterData} with {@link B2BCostCenterModel}.
 *
 * @deprecated Since 6.0. Use
 *             {@link de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BCostCenterReversePopulator}
 *             instead.
 */
@Deprecated(since = "6.0", forRemoval = true)
public class B2BCostCenterReversePopulator implements Populator<B2BCostCenterData, B2BCostCenterModel>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private CommonI18NService commonI18NService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.converter.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final B2BCostCenterData source, final B2BCostCenterModel target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName());
		final B2BUnitModel b2BUnitModel = getB2BUnitService().getUnitForUid(source.getUnit().getUid());
		target.setUnit(b2BUnitModel);
		final CurrencyModel currencyModel = getCommonI18NService().getCurrency(source.getCurrency().getIsocode());
		target.setCurrency(currencyModel);
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		b2BUnitService = b2bUnitService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
