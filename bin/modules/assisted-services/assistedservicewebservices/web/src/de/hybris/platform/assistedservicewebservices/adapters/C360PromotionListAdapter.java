/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicepromotionfacades.customer360.CSAPromoData;
import de.hybris.platform.assistedservicewebservices.dto.C360PromoWsDTO;
import de.hybris.platform.assistedservicewebservices.dto.C360PromotionList;
import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class C360PromotionListAdapter extends C360FragmentDataAdapter<List<CSAPromoData>>
{
    @Override
    public Customer360DataWsDTO adapt(List<CSAPromoData> data)
    {
        final C360PromotionList c360PromotionList = new C360PromotionList();
        List<C360PromoWsDTO> promoWsDTOList = Collections.emptyList();
        if (data != null)
        {
            promoWsDTOList = data.stream().map(promo -> getDataMapper().map(promo, C360PromoWsDTO.class)).collect(Collectors.toList());
        }
        c360PromotionList.setPromotions(promoWsDTOList);
        return c360PromotionList;
    }
}
