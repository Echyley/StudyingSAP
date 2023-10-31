/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.variant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import de.hybris.platform.variants.model.VariantProductModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.AccessException;
import org.springframework.expression.TypedValue;

import com.google.common.collect.Lists;
import com.hybris.backoffice.variants.BackofficeVariantsService;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.i18n.CockpitLocaleService;


@RunWith(MockitoJUnitRunner.class)
public class VariantAttributePropertyAccessorTest
{

	@Mock
	private TypeFacade typeFacade;
	@Mock
	private CockpitLocaleService cockpitLocaleService;
	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private BackofficeVariantsService backofficeVariantsService;
	@InjectMocks
	private VariantAttributePropertyAccessor propertyAccessor;


	@Test
	public void shouldReadUnlocalizedProperty() throws AccessException
	{
		// given
		final VariantProductModel variant = mock(VariantProductModel.class);
		final String qualifier = "name";
		final DataAttribute dataAttribute = mock(DataAttribute.class);
		final Object propertyValue = new Object();

		given(typeFacade.getAttribute(variant, qualifier)).willReturn(dataAttribute);
		given(dataAttribute.isLocalized()).willReturn(false);
		given(backofficeVariantsService.getVariantAttributeValue(variant, qualifier)).willReturn(propertyValue);

		// when
		final TypedValue value = propertyAccessor.read(null, variant, qualifier);

		// then
		assertThat(value.getValue()).isEqualTo(propertyValue);
	}

	@Test
	public void shouldReadLocalizedProperty() throws AccessException
	{
		// given
		final VariantProductModel variant = mock(VariantProductModel.class);
		final String qualifier = "name";
		final DataAttribute dataAttribute = mock(DataAttribute.class);
		final Map<Locale, Object> propertyValue = new HashMap<>();
		propertyValue.put(Locale.ENGLISH, "House");
		propertyValue.put(Locale.GERMAN, "Hause");
		final String username = "John";

		given(typeFacade.getAttribute(variant, qualifier)).willReturn(dataAttribute);
		given(dataAttribute.isLocalized()).willReturn(true);
		given(backofficeVariantsService.getLocalizedVariantAttributeValue(variant, qualifier)).willReturn(propertyValue);
		given(cockpitUserService.getCurrentUser()).willReturn(username);
		given(cockpitLocaleService.getEnabledDataLocales(username)).willReturn(Lists.newArrayList(Locale.ENGLISH));

		// when
		final TypedValue value = propertyAccessor.read(null, variant, qualifier);

		// then
		assertThat(value.getValue()).isInstanceOf(Map.class);
		assertThat(((Map) value.getValue()).size()).isEqualTo(1);
		assertThat(((Map) value.getValue()).get(Locale.ENGLISH)).isEqualTo("House");
	}

	@Test
	public void shouldWriteUnlocalizedProperty() throws AccessException
	{
		// given
		final VariantProductModel variant = mock(VariantProductModel.class);
		final String qualifier = "name";
		final DataAttribute dataAttribute = mock(DataAttribute.class);
		final Object propertyValue = new Object();

		given(typeFacade.getAttribute(variant, qualifier)).willReturn(dataAttribute);
		given(dataAttribute.isLocalized()).willReturn(false);

		// when
		propertyAccessor.write(null, variant, qualifier, propertyValue);

		// then
		verify(backofficeVariantsService).setVariantAttributeValue(variant, qualifier, propertyValue);
		verifyNoMoreInteractions(backofficeVariantsService);
	}

	@Test
	public void shouldWriteLocalizedProperty() throws AccessException
	{
		// given
		final VariantProductModel variant = mock(VariantProductModel.class);
		final String qualifier = "name";
		final DataAttribute dataAttribute = mock(DataAttribute.class);
		final Map<Locale, Object> propertyValue = new HashMap<>();
		propertyValue.put(Locale.ENGLISH, "House");
		propertyValue.put(Locale.GERMAN, "Hause");
		final String username = "John";

		given(typeFacade.getAttribute(variant, qualifier)).willReturn(dataAttribute);
		given(dataAttribute.isLocalized()).willReturn(true);
		given(cockpitUserService.getCurrentUser()).willReturn(username);
		given(cockpitLocaleService.getEnabledDataLocales(username)).willReturn(Lists.newArrayList(Locale.ENGLISH));

		// when
		propertyAccessor.write(null, variant, qualifier, propertyValue);

		// then
		verify(backofficeVariantsService).setLocalizedVariantAttributeValue(eq(variant), eq(qualifier),
				argThat(new ArgumentMatcher<Map<Locale, Object>>()
				{
					@Override
					public boolean matches(Map<Locale, Object> localeObjectMap) {
						return localeObjectMap.size() == 1 && Objects.equals(localeObjectMap.get(Locale.ENGLISH), "House");
					}
				}));
		verifyNoMoreInteractions(backofficeVariantsService);
	}
}
