/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.List;

/**
 * An abstraction of the reference path search between type descriptors.
 */
public interface ReferencePathFinder
{
	/**
	 * Finds all possible reference paths between the specified given item types.
	 *
	 * @param src  a type to find references from
	 * @param dest a type to find reference paths to
	 * @return a list of paths leading from the source type to the destination type or an empty list, if there are no paths from
	 * the source to the destination. The paths are ordered from shortest to longest in the list.
	 */
	List<ReferencePath> findAllPaths(TypeDescriptor src, TypeDescriptor dest);
}
