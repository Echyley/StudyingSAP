/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.exception;

import de.hybris.platform.servicelayer.exceptions.BusinessException;

public class C4CServiceException extends BusinessException {
    public C4CServiceException(String message) {
        super(message);
    }

    public C4CServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
