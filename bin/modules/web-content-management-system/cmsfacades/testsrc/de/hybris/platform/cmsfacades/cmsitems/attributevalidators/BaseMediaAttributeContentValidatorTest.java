/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.attributevalidators;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class BaseMediaAttributeContentValidatorTest
{
	private static final java.lang.String EN = "en";
	private static final String UUID = "media-uuid";

	@Mock
	private LanguageFacade languageFacade;
	
	@InjectMocks
	private BaseMediaAttributeContentValidator validator;
	
	@Mock
	private LanguageData languageData;

	@Mock
	private AttributeDescriptorModel attribute;

	@Before
	public void setup()
	{
		when(languageData.getIsocode()).thenReturn(EN);
		when(languageData.isRequired()).thenReturn(true);
		when(languageFacade.getLanguages()).thenReturn(Arrays.asList(languageData));
	}

	@Test
	public void whenMapContainsRequiredLanguages_shouldNotAddErrors()
	{
		final Map<String, String> map = new HashMap<>();
		map.put(EN, UUID);
		final List<ValidationError> errors = validator.validate(map, attribute);
		assertThat(errors, empty());
	}

	@Test
	public void whenMapDoNotContainRequiredLanguages_shoulAddErrors()
	{
		final Map<String, String> map = new HashMap<>();
		map.put("DE", UUID);
		final List<ValidationError> errors = validator.validate(map, attribute);
		assertThat(errors, not(empty()));
	}

	@Test
	public void whenMapIsNull_shoulAddErrors()
	{
		final List<ValidationError> errors = validator.validate(null, attribute);
		assertThat(errors, not(empty()));
	}
}
