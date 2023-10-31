/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service;

import de.hybris.platform.cissapdigitalpayment.client.SapDigitalPaymentClient;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;

/**
 * Digital Payment Configuration Service which helps in generating client for digital payments
 */
public interface DigitalPaymentsConfigurationService {

    /**
     * Get Digital Payments Client
     * @return Rest Client for digital payments
     */
    SapDigitalPaymentClient getSapDigitalPaymentsClient();

    /**
     * Get Digital Payments Client
     * @param sapDigitalPaymentConfig sap digital payment config
     * @return Rest Client for digital payments
     */
    SapDigitalPaymentClient getSapDigitalPaymentsClient(SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig);
}
