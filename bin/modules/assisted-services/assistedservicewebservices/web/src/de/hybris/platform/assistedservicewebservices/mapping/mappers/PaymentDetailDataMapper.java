/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.mapping.mappers;

import de.hybris.platform.assistedservicewebservices.dto.C360PaymentDetailWsDTO;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import ma.glasnost.orika.MappingContext;

public class PaymentDetailDataMapper extends AbstractCustomMapper<CCPaymentInfoData, C360PaymentDetailWsDTO>
{
    @Override
    public void mapAtoB(CCPaymentInfoData paymentInfoData, C360PaymentDetailWsDTO paymentDetailWsDTO, MappingContext context)
    {
        final CardTypeWsDTO cardType = new CardTypeWsDTO();
        if(paymentInfoData.getCardTypeData() != null)
        {
            cardType.setCode(paymentInfoData.getCardTypeData().getCode());
            cardType.setName(paymentInfoData.getCardTypeData().getName());
            paymentDetailWsDTO.setCardType(cardType);
        }
        else if (paymentInfoData.getCardType() != null)
        {
            cardType.setCode(paymentInfoData.getCardType());
            paymentDetailWsDTO.setCardType(cardType);
        }
        paymentDetailWsDTO.setDefaultPayment(paymentInfoData.isDefaultPaymentInfo());
    }
}
