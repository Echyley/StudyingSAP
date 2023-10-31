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
package de.hybris.platform.configurablebundlebackoffice.widgets.populator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.navigation.NavigationNode;
import com.hybris.backoffice.navigation.impl.SimpleNode;
import com.hybris.cockpitng.core.context.CockpitContext;
import com.hybris.cockpitng.core.context.impl.DefaultCockpitContext;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.tree.node.DynamicNode;


@UnitTest
@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class BundleTemplatesTreeModelPopulatorTest extends AbstractCockpitngUnitTest<BundleTemplatesTreeModelPopulator>
{
	@InjectMocks
	private BundleTemplatesTreeModelPopulator populator;

	@Mock
	private LabelService labelService;

	@Mock
	private ModelService modelService;

	@Mock
	private BundleTemplateModel bundleTemplateModel;

	@Override
	protected Class<? extends BundleTemplatesTreeModelPopulator> getWidgetType()
	{
		return BundleTemplatesTreeModelPopulator.class;
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenInvokedWithNonDynamicNode()
	{
		populator.getChildren(new SimpleNode("id"));
	}

	@Test
	public void shouldReturnEmptyCollectionForChildren()
	{
		final DynamicNode node = new DynamicNode("id", populator);

		final List<NavigationNode> result = populator.getChildren(node);

		assertThat(result).isEmpty();
	}

	@Test
	public void shouldGetChildrenMainNodeFromTheSelectionContextForTheProvidedRootNode()
	{
		final CockpitContext cockpitContext = new DefaultCockpitContext();
		cockpitContext.setParameter("dynamicNodeSelectionContext", Collections.singletonList(bundleTemplateModel));

		final DynamicNode node = new DynamicNode("id", true, populator);
		node.setContext(cockpitContext);

		final List<NavigationNode> result = populator.getChildren(node);

		assertThat(result).hasSize(1);

		verify(modelService).refresh(bundleTemplateModel);
	}

	@Test
	public void shouldGetChildrenNodesForRootNodeWithBundlePackageData()
	{
		final BundleTemplateModel child1 = mock(BundleTemplateModel.class);
		final BundleTemplateModel child2 = mock(BundleTemplateModel.class);

		when(bundleTemplateModel.getChildTemplates()).thenReturn(Arrays.asList(child1, child2));

		final DynamicNode node = new DynamicNode("id", populator);
		node.setData(bundleTemplateModel);

		final List<NavigationNode> result = populator.getChildren(node);

		assertThat(result).hasSize(2);
	}
}
