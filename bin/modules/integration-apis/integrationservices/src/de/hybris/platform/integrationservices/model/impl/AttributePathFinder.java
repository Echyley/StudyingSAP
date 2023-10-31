/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

/**
 * A helper class for finding {@link ReferencePath}s.
 */
public class AttributePathFinder implements ReferencePathFinder
{
	private static final int MAX_ITERATIONS = 1000;

	@Override
	public List<ReferencePath> findAllPaths(final TypeDescriptor src, final TypeDescriptor dest)
	{
		if (src == null || dest == null || isNotSameIntegrationObject(src, dest))
		{
			return Collections.emptyList();
		}

		final List<ReferencePath> found = new ArrayList<>();
		List<ReferencePath> paths = Lists.newArrayList(new EmptyReferencePath(src));
		int loopGuard = 0; // guards against bugs in the expand() implementations and prevents infinite loops
		do
		{
			final List<ReferencePath> completed = paths.stream()
					.filter(p -> dest.equals(p.getDestination()))
					.collect(Collectors.toList());
			found.addAll(completed);
			paths.removeAll(completed);
			paths = paths.stream()
					.map(ReferencePath::expand)
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
		} while (! paths.isEmpty() && (loopGuard++ < MAX_ITERATIONS));
		return found;
	}

	private boolean isNotSameIntegrationObject(final TypeDescriptor src, final TypeDescriptor dest)
	{
		return !Objects.equals(src.getIntegrationObjectCode(), dest.getIntegrationObjectCode());
	}
}
