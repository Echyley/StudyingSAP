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
package de.hybris.platform.configurablebundlebackoffice.widgets.explorertree.refreshstrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import java.util.Collection;

import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class ExplorerTreeBundleTemplatesDynamicNodeRefreshStrategyTest
{
	private ExplorerTreeBundleTemplatesDynamicNodeRefreshStrategy strategy = new ExplorerTreeBundleTemplatesDynamicNodeRefreshStrategy();

	@Test
	public void shouldRefreshParentNode()
	{
		//given
		final BundleTemplateModel child = new BundleTemplateModel();
		final BundleTemplateModel parent = new BundleTemplateModel();
		child.setParentTemplate(parent);
		//when
		final Collection relatedObjectsToRefresh = strategy.findRelatedObjectsToRefresh(child);
		//then
		assertThat(relatedObjectsToRefresh).containsExactly(parent);
	}

	@Test
	public void shouldProvideEmptyListIfInputObjectIsNotBundleTemplate()
	{
		//given
		final Object object = new Object();
		//when
		final Collection relatedObjectsToRefresh = strategy.findRelatedObjectsToRefresh(object);
		//then
		assertThat(relatedObjectsToRefresh).isEmpty();
	}

	@Test
	public void shouldHaveNoRelatedObjectToRefreshWhenUpdatingTopLevelBundle()
	{
		//given
		final BundleTemplateModel parent = new BundleTemplateModel();
		//when
		final Collection relatedObjectsToRefresh = strategy.findRelatedObjectsToRefresh(parent);
		//then
		assertThat(relatedObjectsToRefresh).isEmpty();
	}

	@Test
	public void shouldHaveNoRelatedObjectsToRefreshIfNodeIsMarkedForDeletion() throws Exception
	{
		//given
		final BundleTemplateModel child = Mockito.mock(BundleTemplateModel.class);
		final BundleTemplateModel parent = Mockito.mock(BundleTemplateModel.class);
		final ItemModelContext context = Mockito.mock(ItemModelContext.class);
		given(child.getItemModelContext()).willReturn(context);
		given(child.getParentTemplate()).willReturn(parent);
		given(context.isRemoved()).willReturn(Boolean.TRUE);
		//when
		final Collection relatedObjectsToRefresh = strategy.findRelatedObjectsToRefresh(child);
		//then
		assertThat(relatedObjectsToRefresh).isEmpty();
	}
}
