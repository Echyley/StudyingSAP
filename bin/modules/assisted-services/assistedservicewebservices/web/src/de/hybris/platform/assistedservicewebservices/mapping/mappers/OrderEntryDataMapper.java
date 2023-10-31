/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapping.mappers;

import de.hybris.platform.assistedservicewebservices.dto.C360CartEntryWsDTO;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

public class OrderEntryDataMapper extends AbstractCustomMapper<OrderEntryData, C360CartEntryWsDTO>
{
    @Override
    public void mapAtoB(OrderEntryData orderEntryData, C360CartEntryWsDTO c360CartEntryWsDTO, MappingContext context) {
        if(orderEntryData.getProduct() != null)
        {
            c360CartEntryWsDTO.setProductCode(orderEntryData.getProduct().getCode());
        }

        if(orderEntryData.getBasePrice() != null)
        {
            c360CartEntryWsDTO.setBasePrice(orderEntryData.getBasePrice().getFormattedValue());
        }

        if(orderEntryData.getTotalPrice() != null)
        {
            c360CartEntryWsDTO.setTotalPrice(orderEntryData.getTotalPrice().getFormattedValue());
        }
    }
}
