/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicewebservices.dto.C360Cart;
import de.hybris.platform.assistedservicewebservices.dto.C360CartDataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;


public class C360CartAdapter extends C360FragmentDataAdapter<CartData>
{
    @Override
    public Customer360DataWsDTO adapt(CartData data)
    {
        C360Cart c360cart = new C360Cart();
        // The data is an empty cart when customer without any cart, so need to check code
        if (data != null && data.getCode() != null)
        {
            C360CartDataWsDTO cartDataWsDTO = getDataMapper().map(data, C360CartDataWsDTO.class);
            c360cart.setCart(cartDataWsDTO);
        }

        return c360cart;
    }

}
