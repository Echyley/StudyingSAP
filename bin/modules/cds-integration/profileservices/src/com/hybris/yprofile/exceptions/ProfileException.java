/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.exceptions;

public class ProfileException extends RuntimeException {

    private static final long serialVersionUID = 7904082974137909548L;

    public ProfileException() {
        super();
    }

    public ProfileException(String message) {
        super(message);
    }

    public ProfileException(String message, Throwable cause) {
        super(message, cause);
    }
}
