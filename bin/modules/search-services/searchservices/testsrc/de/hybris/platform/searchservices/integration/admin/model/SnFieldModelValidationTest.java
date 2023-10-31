/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.integration.admin.model;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.searchservices.model.SnFieldModel;
import de.hybris.platform.searchservices.model.SnIdentifierConstraintModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.model.constraints.AbstractConstraintModel;
import de.hybris.platform.validation.model.constraints.NotBlankConstraintModel;
import de.hybris.platform.validation.services.ValidationService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SnFieldModelValidationTest extends ServicelayerTransactionalTest
{
	private static final String FIELD_ID_QUALIFIER = String.join(".", SnFieldModel._TYPECODE,
			SnFieldModel.ID);

	@Resource
	private ModelService modelService;

	@Resource
	private ValidationService validationService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/impex/essentialdata-searchservices-validation.impex", "UTF-8");
		validationService.reloadValidationEngine();
	}

	@Test
	public void testMissingId() {
		//given
		final SnFieldModel field = modelService.create(SnFieldModel.class);

		//when
		final Set<HybrisConstraintViolation> validations = validationService.validate(field);

		//then
		Assert.assertNotNull("The violation set should not be null", validations);
		Assert.assertEquals("There should be two violation of constraints", 1, validations.size());
		Assert.assertThat(getConstraints(validations),
				Matchers.everyItem(Matchers.instanceOf(NotBlankConstraintModel.class)));
		Assert.assertThat(getQualifiers(validations),
				Matchers.contains(FIELD_ID_QUALIFIER));
	}

	@Test
	public void testInvalidId() {
		//given
		final SnFieldModel field = modelService.create(SnFieldModel.class);
		field.setId("_1");

		//when
		final Set<HybrisConstraintViolation> validations = validationService.validate(field);

		//then
		Assert.assertNotNull("The violation set should not be null", validations);
		Assert.assertEquals("There should be two violation of constraints", 1, validations.size());
		Assert.assertThat(getConstraints(validations),
				Matchers.everyItem(Matchers.instanceOf(SnIdentifierConstraintModel.class)));
		Assert.assertThat(getQualifiers(validations),
				Matchers.contains(FIELD_ID_QUALIFIER));
	}

	private List<String> getQualifiers(final Set<HybrisConstraintViolation> validations)
	{
		return validations.stream().map(HybrisConstraintViolation::getQualifier).collect(Collectors.toList());
	}


	private List<AbstractConstraintModel> getConstraints(final Set<HybrisConstraintViolation> validations)
	{
		return validations.stream().map(HybrisConstraintViolation::getConstraintModel).collect(Collectors.toList());
	}
}
