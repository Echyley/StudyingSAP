/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

import de.hybris.platform.servicelayer.exceptions.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalPaymentsServerException extends SystemException {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalPaymentsServerException.class);

    public DigitalPaymentsServerException() {
        super("Error in digital payments");
        LOG.info("Digital Payment Server is failing.");
    }

    public DigitalPaymentsServerException(String message) {
        super(message);
    }

    public DigitalPaymentsServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
