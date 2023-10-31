/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.interceptor;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;


/**
 * This interceptor validates that not all payment types are excluded.
 */
public class B2BUnitPaymentTypesValidateInterceptor implements ValidateInterceptor<B2BUnitModel>
{
	private static final String ERROR_EXCLUDED_ALL_PAYMENT_TYPES = "error.b2bunit.excludedallpaymenttypes";
	private static final String ERROR_NONROOT_EXCLUDED_PAYMENT_TYPES = "error.b2bunit.nonroot.excludedpaymenttypes";

	private EnumerationService enumerationService;
	private L10NService l10NService;

	@Override
	public void onValidate(final B2BUnitModel model, final InterceptorContext ctx) throws InterceptorException
	{

		if (model.getB2bExcludedPaymentTypes() != null) {
			if (Boolean.FALSE.equals(model.getIsRoot()) && !model.getB2bExcludedPaymentTypes().isEmpty()) {
				throw new InterceptorException(getL10NService().getLocalizedString(ERROR_NONROOT_EXCLUDED_PAYMENT_TYPES));
			}

			if (model.getB2bExcludedPaymentTypes().containsAll(getEnumerationService().getEnumerationValues(CheckoutPaymentType._TYPECODE)))
			{
				throw new InterceptorException(getL10NService().getLocalizedString(ERROR_EXCLUDED_ALL_PAYMENT_TYPES));
			}
		}
	}

	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

}
