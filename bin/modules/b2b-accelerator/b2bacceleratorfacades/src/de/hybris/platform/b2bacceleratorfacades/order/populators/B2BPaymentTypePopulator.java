/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link CheckoutPaymentType} to {@link B2BPaymentTypeData}.
 */
public class B2BPaymentTypePopulator implements Populator<CheckoutPaymentType, B2BPaymentTypeData>
{
    private TypeService typeService;

    @Override
    public void populate(final CheckoutPaymentType source, final B2BPaymentTypeData target)
    {
        CheckoutPaymentType checkoutPaymentTypeValue = source;
        if (checkoutPaymentTypeValue == null)
        {
            checkoutPaymentTypeValue = CheckoutPaymentType.ACCOUNT;
        }
        target.setCode(checkoutPaymentTypeValue.getCode());
        target.setDisplayName(getTypeService().getEnumerationValue(checkoutPaymentTypeValue).getName());
    }

    protected TypeService getTypeService()
    {
        return typeService;
    }

    @Required
    public void setTypeService(final TypeService typeService)
    {
        this.typeService = typeService;
    }

}
