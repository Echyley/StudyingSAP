/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomiser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;

/**
 * OpenApiCustomiser implementation class that sort paths.
 */
public class PathsSortCustomizer implements OpenApiCustomiser
{
	@Override
	public void customise(final OpenAPI openApi)
	{
		if (openApi.getPaths() != null && !openApi.getPaths().isEmpty())
		{
			openApi.setPaths(sortPaths(openApi.getPaths()));
		}
	}

	private Paths sortPaths(final Paths paths)
	{
		final Paths sortedPaths = new Paths();
		final List<String> names = new ArrayList<>(paths.keySet());
		names.sort(String::compareTo);
		names.forEach(n -> sortedPaths.addPathItem(n, paths.get(n)));
		return sortedPaths;
	}
}
