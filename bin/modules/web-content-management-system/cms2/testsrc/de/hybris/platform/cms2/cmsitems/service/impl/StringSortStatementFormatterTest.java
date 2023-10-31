/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.cmsitems.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;

import java.util.Arrays;
import java.util.Collection;

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
public class StringSortStatementFormatterTest
{
	@InjectMocks
	private StringSortStatementFormatter stringFormatter;

	@Mock
	AttributeDescriptorModel attributeDescriptor;
	@Mock
	private TypeModel attributeType;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
				{ "java.lang.String", true }, { "String", false }, { "INVALID", false }
		});
	}

	@Parameter
	public String className;

	@Parameter(1)
	public boolean isApplicable;

	@Test
	public void testClassNameIsApplicable()
	{
		when(attributeDescriptor.getAttributeType()).thenReturn(attributeType);
		when(attributeType.getCode()).thenReturn(className);

		final boolean result = stringFormatter.isApplicable(attributeDescriptor);

		assertEquals(isApplicable, result);
	}

}
