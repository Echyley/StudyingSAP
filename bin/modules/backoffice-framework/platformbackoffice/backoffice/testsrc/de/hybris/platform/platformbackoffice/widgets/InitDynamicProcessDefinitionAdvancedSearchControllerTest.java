/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.AbstractInitAdvancedSearchAdapter;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;


@DeclaredInput(value = AbstractInitAdvancedSearchAdapter.SOCKET_IN_NODE_SELECTED, socketType = NavigationNode.class)
public class InitDynamicProcessDefinitionAdvancedSearchControllerTest
		extends AbstractWidgetUnitTest<InitDynamicProcessDefinitionAdvancedSearchController>
{

	@Spy
	@InjectMocks
	private InitDynamicProcessDefinitionAdvancedSearchController controller;

	@Test
	public void shouldAddSearchConditionsProperlyWhenActiveConditionDoesNotExist()
	{
		// given
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final String key = InitDynamicProcessDefinitionAdvancedSearchController.ACTIVE;

		// when
		controller.addSearchDataConditions(searchData, Optional.empty());

		// then
		assertThat(searchData.getConditions(key)).isNotNull();
	}

	@Test
	public void shouldNotAddAnySearchConditionsWhenSearchDataIsNull()
	{
		// given
		final AdvancedSearchData searchData = null;

		// when
		controller.addSearchDataConditions(searchData, Optional.empty());

		// then
		assertThat(searchData).isNull();
	}

	@Override
	protected InitDynamicProcessDefinitionAdvancedSearchController getWidgetController()
	{
		return controller;
	}
}
