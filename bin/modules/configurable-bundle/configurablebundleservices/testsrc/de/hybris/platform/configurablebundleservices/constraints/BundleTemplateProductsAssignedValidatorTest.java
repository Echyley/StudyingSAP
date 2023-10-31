/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.constraints;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;

import javax.validation.ConstraintValidatorContext;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link BundleTemplateProductsAssignedValidator}
 */
@RunWith(MockitoJUnitRunner.class)
public class BundleTemplateProductsAssignedValidatorTest
{
	private static final PK PK_CONST = PK.fromLong(1);

	private BundleTemplateProductsAssignedValidator validator = new BundleTemplateProductsAssignedValidator();

	@Mock
	private BundleTemplateModel bundleTemplateModel;

	@Mock
	private ConstraintValidatorContext constraintValidatorContext;

	@Test
	public void testIsValidNewBundleTemplate() {
		final boolean isValid = validator.isValid(bundleTemplateModel, constraintValidatorContext);
		assertTrue(isValid);
	}

	@Test
	public void testIsValidWithChildTemplates() {
		given(bundleTemplateModel.getPk()).willReturn(PK_CONST);
		final BundleTemplateModel childTemplate = mock(BundleTemplateModel.class);
		given(bundleTemplateModel.getChildTemplates()).willReturn(Collections.singletonList(childTemplate));

		final boolean isValid = validator.isValid(bundleTemplateModel, constraintValidatorContext);
		assertTrue(isValid);
	}

	@Test
	public void testIsValidWithProducts() {
		given(bundleTemplateModel.getPk()).willReturn(PK_CONST);
		final ProductModel product = mock(ProductModel.class);
		given(bundleTemplateModel.getProducts()).willReturn(Collections.singletonList(product));

		final boolean isValid = validator.isValid(bundleTemplateModel, constraintValidatorContext);
		assertTrue(isValid);
	}

	@Test
	public void testIsValidWithoutProductsAndChildTemplates() {
		given(bundleTemplateModel.getPk()).willReturn(PK_CONST);

		final boolean isValid = validator.isValid(bundleTemplateModel, constraintValidatorContext);
		assertFalse(isValid);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testIsValidNullBundleTemplate() {
		validator.isValid(null, constraintValidatorContext);
	}
}
