/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.service.predicate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

@UnitTest
@RunWith(Parameterized.class)
public class MultiCatalogAwareItemAttributePredicateTest
{
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

	private static final String SOME_TYPE_CODE = "SomeTypeCode";

	@InjectMocks
	private MultiCatalogAwareItemAttributePredicate multiCatalogAwareItemAttributePredicateTest;

	@Mock
	private Predicate<AttributeDescriptorModel> isCollectionPredicate;

	@Mock
	private AssignableFromAttributePredicate assignableFromAttributePredicate;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private TypeModel attributeType;

	@Before
	public void setup()
	{

		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn(SOME_TYPE_CODE);

	}

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
	public boolean isCollection;

	@Parameter(1)
	public boolean isAssignableFromAttribute;

	@Parameter(2)
	public boolean isAllPredicatePass;

	@Test
	public void whenNeedVerifyAllThePredicated()
	{

		//GIVEN
		when(isCollectionPredicate.test(attributeDescriptor)).thenReturn(isCollection);
		when(assignableFromAttributePredicate.test(attributeDescriptor)).thenReturn(isAssignableFromAttribute);

		//WHEN
		final boolean result = multiCatalogAwareItemAttributePredicateTest.test(attributeDescriptor);

		//THEN
		assertEquals(isAllPredicatePass, result);
		if (isAllPredicatePass) {
			verify(assignableFromAttributePredicate, times(1)).test(attributeDescriptor);
		}

	}


}
