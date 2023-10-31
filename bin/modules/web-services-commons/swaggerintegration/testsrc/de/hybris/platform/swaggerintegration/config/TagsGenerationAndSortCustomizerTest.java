/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.tags.Tag;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class TagsGenerationAndSortCustomizerTest
{
	private TagsGenerationAndSortCustomizer tagsGenerationAndSortCustomizer;

	@Before
	public void setUp()
	{
		tagsGenerationAndSortCustomizer = new TagsGenerationAndSortCustomizer();
	}

	@Test
	public void shouldSortTagsWhenTagsExists()
	{
		final OpenAPI api = new OpenAPI();
		final List<Tag> tags = new ArrayList<>();
		final Tag foo = new Tag();
		foo.setName("Foo");
		foo.setDescription("Foo Controller");
		tags.add(foo);

		final Tag bar = new Tag();
		bar.setName("Bar");
		tags.add(bar);

		api.setTags(tags);
		Assert.assertEquals("Foo", api.getTags().get(0).getName());
		Assert.assertEquals("Bar", api.getTags().get(1).getName());

		tagsGenerationAndSortCustomizer.customise(api);
		Assert.assertEquals("Bar", api.getTags().get(0).getName());
		Assert.assertEquals("Foo", api.getTags().get(1).getName());
		Assert.assertEquals("Foo Controller", api.getTags().get(1).getDescription());
	}

	@Test
	public void shouldGenerateAndSortWhenNoTags()
	{
		final OpenAPI api = new OpenAPI();
		final Paths paths = new Paths();
		paths.addPathItem("/foo", preparePathItem( "Foo"));
		paths.addPathItem("/bar", preparePathItem( "Bar"));
		api.setPaths(paths);

		Assert.assertNull(api.getTags());
		tagsGenerationAndSortCustomizer.customise(api);
		Assert.assertEquals("Bar", api.getTags().get(0).getName());
		Assert.assertEquals("Foo", api.getTags().get(1).getName());
	}

	private PathItem preparePathItem(final String tagName)
	{
		final PathItem item = new PathItem();
		final Operation operation = new Operation();
		operation.setTags(List.of(tagName));
		item.setGet(operation);
		return item;
	}

}
