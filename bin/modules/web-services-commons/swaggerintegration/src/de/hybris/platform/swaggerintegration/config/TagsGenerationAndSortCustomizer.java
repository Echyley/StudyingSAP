/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springdoc.core.customizers.OpenApiCustomiser;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * OpenApiCustomiser implementation that generates "tags" property that lists the sorted tags.
 * Even springdoc.swagger-ui.tagsSorter is defined to sort tags by swagger-ui, tags are not sorted in generated api-docs.yaml without this customiser class.
 */
public class TagsGenerationAndSortCustomizer implements OpenApiCustomiser
{
	@Override
	public void customise(final OpenAPI openApi)
	{
		if (openApi.getTags() == null && openApi.getPaths() != null)
		{
			generateAndSortTags(openApi);
		}
		else if (openApi.getTags() != null)
		{
			sortExistingTags(openApi);
		}
	}

	private void sortExistingTags(final OpenAPI openApi)
	{
		if (openApi.getTags() != null && !openApi.getTags().isEmpty())
		{
			final List<Tag> sortedTags = new ArrayList<>(openApi.getTags());
			sortedTags.sort(Comparator.comparing(Tag::getName));
			openApi.setTags(sortedTags);
		}
	}

	private void generateAndSortTags(final OpenAPI openApi)
	{
		final Stream<Operation> operationStream = getOperations(openApi.getPaths());
		final List<String> tagNames = new ArrayList<>(
				operationStream.flatMap(o -> o.getTags().stream()).collect(Collectors.toSet()));
		tagNames.sort(String::compareTo);
		openApi.setTags(createTags(tagNames));
	}

	private List<Tag> createTags(final List<String> tagNames)
	{
		final List<Tag> tags = new ArrayList<>(tagNames.size());
		tagNames.forEach(name -> {
			final Tag e = new Tag();
			e.setName(name);
			tags.add(e);
		});
		return tags;
	}

	private Stream<Operation> getOperations(final Paths paths)
	{
		return paths.values().stream().flatMap(item -> item.readOperations().stream());
	}

}
