/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model;

import java.util.Set;

import javax.validation.constraints.NotNull;

/**
 * An abstraction of various graph operations that can be done, such as
 * breadth-first search, depth-first search, rotations, heuristic searches,
 * finding minimum spanning trees, finding max-flow min-cuts, and more performed
 * on the integration object in the context of the implementation.
 */
public interface IntegrationObjectGraphOperations
{
	/**
	 * Finds all related references to the provided type descriptor.
	 *
	 * @param d a type descriptor to find related references from
	 * @return a set of types related to the source type. If there are no other related types from
	 * the provided type descriptor, the provided type descriptor gets returned.
	 */
	@NotNull
	Set<TypeDescriptor> findTypesRelatedTo(TypeDescriptor d);

	/**
	 * Finds all related reference types to the provided item code.
	 *
	 * @param itemCode item code of the type descriptor to find related references from
	 * @return a set of types related to the type. If there are no other related types from
	 * the provided type descriptor, the type descriptor with item code matching the provided value gets returned. If there is no
	 * type descriptor with a matching item code in the context integration object, then an empty {@link Set} is returned.
	 * @see TypeDescriptor#getItemCode()
	 */
	@NotNull
	Set<TypeDescriptor> findTypesRelatedTo(String itemCode);
}
