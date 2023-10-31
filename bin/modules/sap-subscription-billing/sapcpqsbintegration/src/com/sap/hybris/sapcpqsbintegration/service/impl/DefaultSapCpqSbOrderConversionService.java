/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.saprevenuecloudorder.service.impl.SapRevenueCloudOrderConversionService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Default Subscription order Service to communicate with Sap subscription system
 */

public class DefaultSapCpqSbOrderConversionService extends SapRevenueCloudOrderConversionService {

    @Override
    public SAPCpiOutboundOrderModel convertOrderToSapCpiOrder(OrderModel orderModel) {
        SAPCpiOutboundOrderModel sapCpiOutboundOrder = super.convertOrderToSapCpiOrder(orderModel);
        populateSapCpiOutboundOrderModel(sapCpiOutboundOrder, orderModel);
        return sapCpiOutboundOrder;
    }

    protected void populateSapCpiOutboundOrderModel(SAPCpiOutboundOrderModel sapCpiOutboundOrder, OrderModel order) {




        sapCpiOutboundOrder.getSapCpiOutboundOrderItems().forEach( item -> {
            order.getEntries().forEach(entry -> {
                //Setting flag OverwriteContractTerms=TRUE, contractStartDate=CURRENT_DATE
                if ( (!CollectionUtils.isEmpty(entry.getCpqSubscriptionDetails())) && StringUtils.equals(item.getProductCode(), entry.getProduct().getSubscriptionCode())) {
                        entry.getCpqSubscriptionDetails().forEach(model -> {
                        if (model.getContractStartDate() != null) {
                            item.setSubscriptionValidFrom(SapRevenueCloudSubscriptionUtil.dateToString(model.getContractStartDate()));
                        }
                        if( (model.getContractLength() != null) && "0".equals(model.getContractLength())){
                            model.setContractLength(null);
                        }

                        if(model.getContractLength() != null || model.getMinimumContractLength() != null){
                            item.setOverwriteContractTerm("true");
                            item.setSubscriptionValidTerm(null); //Setting null as the valid term is passed in CpqSubscriptionDetails
                        }
                    });

                    item.setPricePlanId(null);// Need to pass either subscription price plan r Cpq rateplan
                    item.setCpqSubscriptionDetails(entry.getCpqSubscriptionDetails());
                }
                if(entry.getProduct().getCode() != null && item.getProductCode() == entry.getProduct().getSubscriptionCode()){
                    item.setProductName(entry.getProduct().getCode());
                    item.setProductCode(null);//Need to send either Id or code
                }
            });
        });
    }
}
