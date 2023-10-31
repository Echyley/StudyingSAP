/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;

@UnitTest
public class PathsSortCustomizerTest
{
	private PathsSortCustomizer pathsSortCustomizer;

	@Before
	public void setup()
	{
		pathsSortCustomizer = new PathsSortCustomizer();
	}

	@Test
	public void shouldNotThrowExceptionWhenNoPathsExist()
	{
		final OpenAPI api = new OpenAPI();
		try
		{
			pathsSortCustomizer.customise(api);
		}
		catch (Exception e)
		{
			Assert.fail("should not throw exception when no paths exist");
		}
	}

	@Test
	public void shouldSortPathsByName()
	{
		final OpenAPI api = new OpenAPI();
		final Paths paths = new Paths();
		paths.addPathItem("/foo", preparePathItem());
		paths.addPathItem("/bar", preparePathItem());
		api.setPaths(paths);

		Assert.assertEquals("/foo", api.getPaths().keySet().toArray()[0]);
		Assert.assertEquals("/bar", api.getPaths().keySet().toArray()[1]);
		pathsSortCustomizer.customise(api);
		Assert.assertEquals("/bar", api.getPaths().keySet().toArray()[0]);
		Assert.assertEquals("/foo", api.getPaths().keySet().toArray()[1]);
	}

	private PathItem preparePathItem()
	{
		final PathItem item = new PathItem();
		item.setGet(new Operation());
		return item;
	}
}
