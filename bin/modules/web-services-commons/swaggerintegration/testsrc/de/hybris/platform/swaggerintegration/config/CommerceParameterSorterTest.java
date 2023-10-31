/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CommerceParameterSorterTest
{
	private final CommerceParameterSorter commerceParameterSorter = new CommerceParameterSorter();

	@Mock
	private HandlerMethod handlerMethod;

	@Before
	public void setup()
	{
	}

	@Test
	public void emptyOperationShouldNotThrowException()
	{
		final Operation operation = new Operation();
		commerceParameterSorter.customize(operation, handlerMethod);
		assertNull(operation.getParameters());
	}

	@Test
	public void nullOperationShouldNotThrowException()
	{
		try
		{
			commerceParameterSorter.customize(null, handlerMethod);
		}
		catch (final Throwable t)
		{
			fail();
		}
	}

	@Test
	public void withSingleParameterShouldKeepIt()
	{
		final Operation operation = prepareOperationWithParams("param");

		commerceParameterSorter.customize(operation, handlerMethod);

		assertEquals(1, operation.getParameters().size());
		assertEquals("param", operation.getParameters().get(0).getName());
	}

	@Test
	public void withUnsortedParametersShouldSort()
	{
		final Operation operation = prepareOperationWithParams("fields", "baseSiteId", "userId");

		commerceParameterSorter.customize(operation, handlerMethod);

		assertEquals(3, operation.getParameters().size());
		assertEquals("baseSiteId", operation.getParameters().get(0).getName());
		assertEquals("fields", operation.getParameters().get(1).getName());
		assertEquals("userId", operation.getParameters().get(2).getName());
	}

	@Test
	public void withSortedParametersShouldKeepSorted()
	{
		final Operation operation = prepareOperationWithParams("count", "fields", "productCode");

		commerceParameterSorter.customize(operation, handlerMethod);

		assertEquals(3, operation.getParameters().size());
		assertEquals("count", operation.getParameters().get(0).getName());
		assertEquals("fields", operation.getParameters().get(1).getName());
		assertEquals("productCode", operation.getParameters().get(2).getName());
	}

	private Operation prepareOperationWithParams(final String... params)
	{
		final Operation operation = new Operation();
		Arrays.stream(params).forEach(name -> operation.addParametersItem(new Parameter().name(name)));
		return operation;
	}
}
