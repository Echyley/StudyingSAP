/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model;

import javax.validation.constraints.NotNull;

/**
 * A factory to create {@link AttributeValueAccessor}s
 */
public interface AttributeValueAccessorFactory
{
    /**
     * Creates an {@link AttributeValueAccessor} for the given {@link TypeAttributeDescriptor}
     * @param descriptor The TypeAttributeDescriptor to associate with this AttributeValueAccessor
     * @return The corresponding AttributeValueAccessor for the descriptor. If the condition isn't met to create the appropriate accessor,
     * a default value accessor is returned.
     */
    AttributeValueAccessor create(@NotNull TypeAttributeDescriptor descriptor);
}
