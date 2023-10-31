/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.service.predicate;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.junit.MockitoRule;


@UnitTest
@RunWith(Parameterized.class)
public class AndAttributePredicateTest
{
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	@Mock
	private Predicate<AttributeDescriptorModel> predicate1;

	@Mock
	private Predicate<AttributeDescriptorModel> predicate2;

	@InjectMocks
	private AndAttributePredicate andAttributePredicate;

	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
				{ true, true, true },
				{ true, false, false },
				{ false, true, false },
				{ false, false, false }
		});
	}

	@Parameter
	public boolean isPredicate1Pass;

	@Parameter(1)
	public boolean isPredicate2Pass;

	@Parameter(2)
	public boolean isAllPredicatePass;

	@Before
	public void setUp()
	{
		andAttributePredicate.setPredicates(Arrays.asList(predicate1, predicate2));
	}

	@Test
	public void givenAllPredicatesPass()
	{
		// GIVEN
		when(predicate1.test(attributeDescriptor)).thenReturn(isPredicate1Pass);
		when(predicate2.test(attributeDescriptor)).thenReturn(isPredicate2Pass);

		// WHEN
		boolean result = andAttributePredicate.test(attributeDescriptor);

		// THEN
		assertEquals(isAllPredicatePass, result);
	}

}
