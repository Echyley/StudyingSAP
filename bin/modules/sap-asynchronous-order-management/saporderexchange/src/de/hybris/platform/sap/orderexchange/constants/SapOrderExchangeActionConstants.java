/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.constants;

public class SapOrderExchangeActionConstants {

    private SapOrderExchangeActionConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ERROR_END_MESSAGE = "Sending to ERP went wrong.";

    public static final String ERP_ORDER_SEND_COMPLETION_EVENT = "ERPOrderSendCompletionEvent_";

}
