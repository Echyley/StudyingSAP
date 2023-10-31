/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.threadcontext.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultThreadContextTest
{
	@Before
	public void setup()
	{

	}

	@Test
	public void testGetAttributeShouldGetParentAttributes()
	{
		// given
		List<String> level1AttributeNames = Arrays.asList("level1Attribute_1", "level1Attribute_2");
		List<String> level2AttributeNames = Arrays.asList("level2Attribute_1", "level2Attribute_2");
		List<String> level3AttributeNames = Arrays.asList("level3Attribute_1", "level3Attribute_1");

		DefaultThreadContext level1Context = createContext(null, level1AttributeNames);
		DefaultThreadContext level2Context = createContext(level1Context, level2AttributeNames);
		DefaultThreadContext level3Context = createContext(level2Context, level3AttributeNames);

		Assert.assertNull(level1Context.getAttribute("notExist"));
		Assert.assertNull(level2Context.getAttribute("notExist"));
		Assert.assertNull(level3Context.getAttribute("notExist"));

		for (String name : level1AttributeNames)
		{
			Assert.assertNotNull(level1Context.getAttribute(name));
			Assert.assertNotNull(level2Context.getAttribute(name));
			Assert.assertNotNull(level3Context.getAttribute(name));
		}

		for (String name : level2AttributeNames)
		{
			Assert.assertNull(level1Context.getAttribute(name));
			Assert.assertNotNull(level2Context.getAttribute(name));
			Assert.assertNotNull(level3Context.getAttribute(name));
		}

		for (String name : level3AttributeNames)
		{
			Assert.assertNull(level1Context.getAttribute(name));
			Assert.assertNull(level2Context.getAttribute(name));
			Assert.assertNotNull(level3Context.getAttribute(name));
		}
	}

	private DefaultThreadContext createContext(DefaultThreadContext parent, List<String> attributeNames)
	{
		DefaultThreadContext context;
		if (parent != null)
		{
			context = new DefaultThreadContext(parent);
		}
		else
		{
			context = new DefaultThreadContext();
		}

		attributeNames.forEach(name -> context.setAttribute(name, new Object()));

		return context;
	}
}