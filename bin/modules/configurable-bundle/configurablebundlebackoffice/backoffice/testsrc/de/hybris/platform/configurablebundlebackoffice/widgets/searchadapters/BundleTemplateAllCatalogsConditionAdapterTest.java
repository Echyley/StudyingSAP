/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundlebackoffice.widgets.searchadapters;

import static de.hybris.platform.configurablebundlebackoffice.assertions.BackofficeAssertions.assertThat;
import static de.hybris.platform.configurablebundlebackoffice.widgets.searchadapters.SearchConditionDataIterator.createSearchConditionDataIterator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BundleTemplateAllCatalogsConditionAdapterTest extends BundleTemplateConditionAdapterTestBase
{
	private final BundleTemplateConditionAdapter adapter = new BundleTemplateAllCatalogsConditionAdapter();

	@Test
	public void shouldAddPackageLevelTemplateBundlesFromAllCatalogsCondition()
	{
		final AdvancedSearchData searchData = new AdvancedSearchData();
		final NavigationNode node = getNodeFactory().createAllCatalogsNode();
		//when
		adapter.addSearchCondition(searchData, node);
		//then
		final Iterator<SearchConditionData> iterator = createSearchConditionDataIterator(searchData);
		assertThat(iterator.next()).hasField(BundleTemplateModel.PARENTTEMPLATE).hasOperator(ValueComparisonOperator.IS_EMPTY)
				.hasEmptyValue();
	}


	@Test
	public void shouldRejectCatalogNodes()
	{
		//given
		final NavigationNode node = getNodeFactory().createCatalogNode(catalog);
		//when
		final boolean canHandle = adapter.canHandle(node);
		//then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldRejectCatalogVersionNode()
	{
		//given
		final NavigationNode node = getNodeFactory().createCatalogVersionNode(staged);
		//when
		final boolean canHandle = adapter.canHandle(node);
		//then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldAcceptAllCatalogNode()
	{
		//given
		final NavigationNode node = getNodeFactory().createAllCatalogsNode();
		//when
		final boolean canHandle = adapter.canHandle(node);
		//then
		assertThat(canHandle).isTrue();
	}

}
