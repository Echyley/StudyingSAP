/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.cmsitems.converter.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class AttributeStrategyConverterTest
{
	@InjectMocks
	private DefaultAttributeStrategyConverterProvider attributeStrategyConverter;

	@Mock
	private Predicate<AttributeDescriptorModel> predicate1;
	@Mock
	private Predicate<AttributeDescriptorModel> predicate2;
	@Mock
	private Predicate<AttributeDescriptorModel> predicate3;

	@Mock
	private AttributeContentConverter attributeContentConverter1;
	@Mock
	private AttributeContentConverter attributeContentConverter2;
	@Mock
	private AttributeContentConverter attributeContentConverter3;


	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	@Before
	public void setup() throws Exception
	{
		attributeStrategyConverter.setAttributeContentConverters(asList(attributeContentConverter1,
				attributeContentConverter2,
				attributeContentConverter3));
		attributeStrategyConverter.afterPropertiesSet();

		when(attributeContentConverter1.getConstrainedBy()).thenReturn(predicate1);
		when(attributeContentConverter2.getConstrainedBy()).thenReturn(predicate2);
		when(attributeContentConverter3.getConstrainedBy()).thenReturn(predicate3);
	}

	@Test
	public void givenThatOnlyOnePredicateIsTrueShouldExecuteOnlyOneConverter() throws Exception
	{
		when(predicate1.test(attributeDescriptor)).thenReturn(true);
		when(predicate2.test(attributeDescriptor)).thenReturn(false);
		when(predicate3.test(attributeDescriptor)).thenReturn(false);

		AttributeContentConverter attributeContentConverter = attributeStrategyConverter.getContentConverter(attributeDescriptor);

		assertThat(attributeContentConverter, is(attributeContentConverter1));
	}


	@Test
	public void givenThatTwoPredicatesAreTrueShouldReturnTheLastConversionDefined()
	{
		when(predicate1.test(attributeDescriptor)).thenReturn(false);
		when(predicate2.test(attributeDescriptor)).thenReturn(true);
		when(predicate3.test(attributeDescriptor)).thenReturn(true);

		AttributeContentConverter attributeContentConverter = attributeStrategyConverter.getContentConverter(attributeDescriptor);

		assertThat(attributeContentConverter, is(attributeContentConverter3));
	}


	@Test
	public void givenThatNoneOfThePredicatesAreTrueShouldReturnNull()
	{
		when(predicate1.test(attributeDescriptor)).thenReturn(false);
		when(predicate2.test(attributeDescriptor)).thenReturn(false);
		when(predicate3.test(attributeDescriptor)).thenReturn(false);

		AttributeContentConverter attributeContentConverter = attributeStrategyConverter.getContentConverter(attributeDescriptor);

		assertThat(attributeContentConverter, nullValue());
	}

}
