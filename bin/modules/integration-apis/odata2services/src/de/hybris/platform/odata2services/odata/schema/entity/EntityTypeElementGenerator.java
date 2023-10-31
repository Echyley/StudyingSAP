/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.EntityType;

/**
 * A marker interface for the convenience of encapsulating the generics and finding all implementors, which determines a
 * generator responsible for EDMX entity types generation from a single {@link TypeDescriptor}
 */
public interface EntityTypeElementGenerator extends SchemaElementGenerator<List<EntityType>, TypeDescriptor>
{
}

