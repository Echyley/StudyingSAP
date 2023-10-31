/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.exceptions;

import de.hybris.platform.servicelayer.exceptions.SystemException;

public class DigitalPaymentsUnknownException extends SystemException {
    public DigitalPaymentsUnknownException() {
        super("Unknown Exception Occurred");
    }

    public DigitalPaymentsUnknownException(String message) {
        super(message);
    }

    public DigitalPaymentsUnknownException(String message, Throwable cause) {
        super(message, cause);
    }
}
