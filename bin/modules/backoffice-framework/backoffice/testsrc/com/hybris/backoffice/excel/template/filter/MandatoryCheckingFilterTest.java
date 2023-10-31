/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.excel.template.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import org.mockito.Mockito;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MandatoryCheckingFilterTest
{

	private MandatoryCheckingFilter filter = new MandatoryCheckingFilter();

	@Test
	public void shouldReturnFalseWhenAttributeDescriptorIsOptional()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		given(model.getOptional()).willReturn(true);
		Mockito.lenient().when(model.getPrivate()).thenReturn(false);

		// when
		final boolean result = filter.test(model);

		// then
		assertFalse(result);
	}

	@Test
	public void shouldReturnTrueWhenAttributeDescriptorIsNotOptional()
	{
		// given
		final AttributeDescriptorModel model = mock(AttributeDescriptorModel.class);
		given(model.getOptional()).willReturn(false);
		given(model.getPrivate()).willReturn(false);

		// when
		final boolean result = filter.test(model);

		// then
		assertTrue(result);
	}

}
