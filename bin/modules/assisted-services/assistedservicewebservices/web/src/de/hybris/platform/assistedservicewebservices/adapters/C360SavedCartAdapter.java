/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicewebservices.dto.C360CartDataWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360SavedCart;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;
import de.hybris.platform.commercefacades.order.data.CartData;


public class C360SavedCartAdapter extends C360FragmentDataAdapter<CartData>
{
    @Override
    public Customer360DataWsDTO adapt(CartData data)
    {
        C360SavedCart c360SavedCart = new C360SavedCart();
        C360CartDataWsDTO cartDataWsDTO = getDataMapper().map(data, C360CartDataWsDTO.class);
        c360SavedCart.setSavedCart(cartDataWsDTO);

        return c360SavedCart;
    }
}
