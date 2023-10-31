/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions.savedqueries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.platformbackoffice.services.converters.BackofficeSavedQueryValueConverter;

import java.util.Collection;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;


@RunWith(MockitoJUnitRunner.class)
public class DefaultSaveQueryActionCheckerTest
{

	@Mock
	private TypeFacade typeFacade;
	@Mock
	private BackofficeSavedQueryValueConverter converter;

	private DefaultSaveQueryActionChecker checker = new DefaultSaveQueryActionChecker();

	@Before
	public void setUp()
	{
		checker.setTypeFacade(typeFacade);
		checker.setConverters(Lists.newArrayList(converter));
	}

	@Test
	public void shouldReturnListOfNonConvertableQualifiers() throws TypeNotFoundException
	{
		// given
		final String field1 = "code";
		final String field2 = "catalogVersion";
		final String field3 = "articleStatus";
		final String typeCode = "Product";

		final DataType dataType = mock(DataType.class);
		final AdvancedSearchData advancedSearchData = mock(AdvancedSearchData.class);
		given(advancedSearchData.getTypeCode()).willReturn(typeCode);
		given(advancedSearchData.getSearchFields()).willReturn(Sets.newHashSet(field1, field2, field3));
		given(typeFacade.load(typeCode)).willReturn(dataType);

		mockDataAttribute(dataType, advancedSearchData, field1, true);
		mockDataAttribute(dataType, advancedSearchData, field2, true);
		mockDataAttribute(dataType, advancedSearchData, field3, false);

		// when
		final Collection<SaveQueryInvalidCondition> invalidConditions = checker.check(advancedSearchData);

		// then
		assertThat(invalidConditions).isNotNull();
		assertThat(invalidConditions).hasSize(1);
		assertThat(invalidConditions).element(0).isEqualTo(new SaveQueryInvalidCondition(field3));
	}

	private DataAttribute mockDataAttribute(final DataType dataType, final AdvancedSearchData advancedSearchData,
			final String qualifier, final boolean canConvert)
	{
		final FieldType fieldType = mock(FieldType.class);
		given(fieldType.getName()).willReturn(qualifier);
		final DataAttribute dataAttribute = mock(DataAttribute.class);
		given(dataType.getAttribute(qualifier)).willReturn(dataAttribute);
		given(dataAttribute.getQualifier()).willReturn(qualifier);

		final SearchConditionData searchConditionData = mock(SearchConditionData.class);
		given(searchConditionData.getFieldType()).willReturn(fieldType);
		given(advancedSearchData.getConditions(qualifier)).willReturn(Lists.newArrayList(searchConditionData));

		given(converter.canHandle(dataAttribute)).willReturn(canConvert);

		return dataAttribute;
	}

}
