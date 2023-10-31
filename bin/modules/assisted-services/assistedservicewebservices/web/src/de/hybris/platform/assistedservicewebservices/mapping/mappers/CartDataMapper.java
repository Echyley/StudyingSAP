/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapping.mappers;

import de.hybris.platform.assistedservicewebservices.dto.C360CartDataWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

public class CartDataMapper extends AbstractCustomMapper<CartData, C360CartDataWsDTO>
{
    @Override
    public void mapAtoB(CartData cartData, C360CartDataWsDTO c360CartData, MappingContext context)
    {
        c360CartData.setTotalItemCount(cartData.getTotalUnitCount());
        if(cartData.getTotalPrice() != null) {
            c360CartData.setTotalPrice(cartData.getTotalPrice().getFormattedValue());
        }
    }
}
