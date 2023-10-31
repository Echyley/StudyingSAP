/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info;

import de.hybris.platform.integrationservices.model.ReferencePath;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates a SpEL expression for deriving root item type when evaluated.
 */
public class RootTypeExpressionGenerator implements ExpressionGenerator
{
	/**
	 * {@inheritDoc}
	 *
	 * @return a SpEL expression or {@code null}, if the root item cannot be navigated from the provided {@code itemType}
	 * or the provided {@code itemType} is {@code null}.
	 */
	@Override
	public String generateExpression(final TypeDescriptor itemType)
	{
		if (itemType == null)
		{
			return null;
		}

		final List<ReferencePath> paths = itemType.getPathsToRoot();
		return paths.isEmpty()
				? null
				: ("#{" + pathToRootType(paths) + "}");
	}

	private String pathToRootType(final List<ReferencePath> paths)
	{
		final List<String> propPaths = paths.stream()
				.map(ReferencePath::toPropertyPath)
				.map(p -> p.isEmpty() ? "itemtype" : (p + ".itemtype"))
				.map(this::collectionSafePath)
				.sorted(this::byLength)
				.collect(Collectors.toList());
		return propPaths.get(0).replace(".", "?.");
	}

	private String collectionSafePath(final String propPath)
	{
		String path = propPath;
		for (int idx = path.lastIndexOf("[0]"); idx > 0 ; idx = path.lastIndexOf("[0]", idx - 1))
		{
			final String collectionPath = removeLeadingOpeningParentheses(path.substring(0, idx));
			path = "(" + collectionPath + ".empty ? null : " + path + ")";
		}
		return path;
	}

	private String removeLeadingOpeningParentheses(final String s)
	{
		return s.startsWith("(") ? removeLeadingOpeningParentheses(s.substring(1)) : s;
	}

	private int byLength(final String s1, final String s2)
	{
		return s1.length() - s2.length();
	}
}
