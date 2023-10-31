/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.yorderfulfillment.actions;

import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 * Class for checking if the Hybris order has been exported successfully to the SAP/ERP back-ends .
 */
public class CheckOrderSendStatusAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

    @Override
    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();

        ExportStatus sendStatus = order.getExportStatus();

        if (sendStatus.equals(ExportStatus.EXPORTED)) {
            return Transition.OK;
        } else {
            return Transition.NOK;
        }

    }

}


