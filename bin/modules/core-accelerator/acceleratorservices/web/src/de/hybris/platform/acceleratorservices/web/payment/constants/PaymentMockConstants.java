/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.web.payment.constants;

public final class PaymentMockConstants {

    public static final String HOP_MOCK = "/hop-mock";
    public static final String SOP_MOCK = "/sop-mock";
    public static final String PROCESS = "/process";
    public static final String HOP_MOCK_PROCESS = HOP_MOCK + PROCESS;
    public static final String SOP_MOCK_PROCESS = SOP_MOCK + PROCESS;

    private PaymentMockConstants()
    {
        //empty to avoid instantiating this constant class
    }

}
