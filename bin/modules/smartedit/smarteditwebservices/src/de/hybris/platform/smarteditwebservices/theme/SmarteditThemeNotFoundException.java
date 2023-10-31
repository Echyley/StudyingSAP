/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.smarteditwebservices.theme;


/**
 * Thrown when a Theme was not found
 */
public class SmarteditThemeNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6032378484891557978L;

    public SmarteditThemeNotFoundException(final String message) {
        super(message);
    }
}
