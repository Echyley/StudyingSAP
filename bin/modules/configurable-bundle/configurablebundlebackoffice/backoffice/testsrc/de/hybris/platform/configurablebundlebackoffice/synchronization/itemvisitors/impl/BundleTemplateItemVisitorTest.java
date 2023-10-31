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
package de.hybris.platform.configurablebundlebackoffice.synchronization.itemvisitors.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.*;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import org.junit.Test;


@UnitTest
public class BundleTemplateItemVisitorTest
{
	private final BundleTemplateItemVisitor itemVisitor = new BundleTemplateItemVisitor();

	@Test
	public void shouldAddRequiredBundleTemplatesToTheListOfSynchornizationDependencies() throws Exception
	{
		//given
		final BundleTemplateModel bundleTemplate = createRootBundleTemplate();
		//when
		final List<ItemModel> itemModels = itemVisitor.visit(bundleTemplate, null, null);
		//then
		assertThat(itemModels).contains(bundleTemplate.getRequiredBundleTemplates().stream().toArray(BundleTemplateModel[]::new));
	}

	@Test
	public void shouldAddDependentBundleTemplatesToTheListOfSynchornizationDependencies() throws Exception
	{
		//given
		final BundleTemplateModel bundleTemplate = createRootBundleTemplate();
		//when
		final List<ItemModel> itemModels = itemVisitor.visit(bundleTemplate, null, null);
		//then
		assertThat(itemModels).contains(bundleTemplate.getDependentBundleTemplates().stream().toArray(BundleTemplateModel[]::new));
	}

	@Test
	public void shouldAddChildTemplatesToTheListOfSynchornizationDependencies() throws Exception
	{
		//given
		final BundleTemplateModel bundleTemplate = createRootBundleTemplate();
		//when
		final List<ItemModel> itemModels = itemVisitor.visit(bundleTemplate, null, null);
		//then
		assertThat(itemModels).contains(bundleTemplate.getChildTemplates().stream().toArray(BundleTemplateModel[]::new));
	}

	@Test
	public void shouldAddStatusToTheListOfSynchornizationDependencies() throws Exception
	{
		//given
		final BundleTemplateModel bundleTemplate = createRootBundleTemplate();
		//when
		final List<ItemModel> itemModels = itemVisitor.visit(bundleTemplate, null, null);
		//then
		assertThat(itemModels).contains(bundleTemplate.getStatus());
	}

	private BundleTemplateModel createRootBundleTemplate()
	{
		final BundleTemplateModel bundle = new BundleTemplateModel();
		bundle.setBundleSelectionCriteria(createBundleSelectionCriteria());
		bundle.setRequiredBundleTemplates(newArrayList(createBundleTemplate()));
		bundle.setDependentBundleTemplates(newArrayList(createBundleTemplate()));
		bundle.setChangeProductPriceBundleRules(newArrayList(createProductPriceBundleRule()));
		bundle.setDisableProductBundleRules(newArrayList(createDisableProductBundleRule()));
		bundle.setProducts(newArrayList(createProduct()));
		bundle.setChildTemplates(newArrayList(createBundleTemplate()));
		bundle.setStatus(createStatus());
		return bundle;
	}

	private BundleTemplateStatusModel createStatus()
	{
		return new BundleTemplateStatusModel();
	}

	private BundleTemplateModel createBundleTemplate()
	{
		return new BundleTemplateModel();
	}

	private ProductModel createProduct()
	{
		return new ProductModel();
	}

	private DisableProductBundleRuleModel createDisableProductBundleRule()
	{
		return new DisableProductBundleRuleModel();
	}

	private ChangeProductPriceBundleRuleModel createProductPriceBundleRule()
	{
		return new ChangeProductPriceBundleRuleModel();
	}

	private BundleSelectionCriteriaModel createBundleSelectionCriteria()
	{
		return new PickExactlyNBundleSelectionCriteriaModel();
	}
}
