/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.navigations.populator.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.junit.MockitoRule;


@UnitTest
@RunWith(Parameterized.class)
public class NavigationNodeDataToModelPositionPopulatorTest
{
	private static final String NODE_UID = "UID";

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

	@Mock
	private AbstractPopulatingConverter<CMSNavigationNodeModel, NavigationNodeData> navigationModelToDataConverter;

	@InjectMocks
	private NavigationNodeDataToModelPositionPopulator populator;

	@Mock
	private NavigationNodeData source;
	@Mock
	private NavigationNodeData currentNavigationNode;

	private final CMSNavigationNodeModel target = new CMSNavigationNodeModel();

	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
				{ 0, 1, 0 },
				{ -100, 1, 0 }, //PositionIsLessThanZero
				{ 2, 1, 2 }, //LastPosition
				{ 1000, 1, 2 }, //LastPositionWhenExceeds
				{ 1, 2, 1 } //MiddlePosition
		});
	}

	@Parameter
	public int sourcePosition;

	@Parameter(1)
	public int currentNavigationNodePosition;

	@Parameter(2)
	public int expectedPosition;

	@Before
	public void setup()
	{
		final CMSNavigationNodeModel parent = new CMSNavigationNodeModel();
		parent.setUid("parent-node");
		final CMSNavigationNodeModel node1 = mock(CMSNavigationNodeModel.class);
		final CMSNavigationNodeModel node3 = mock(CMSNavigationNodeModel.class);
		when(node1.getUid()).thenReturn("node-1");
		when(node3.getUid()).thenReturn("node-3");
		parent.setChildren(Arrays.asList(node1, target, node3));

		target.setUid(NODE_UID);
		target.setParent(parent);
		when(navigationModelToDataConverter.convert(target)).thenReturn(currentNavigationNode);
	}

	@Test
	public void testPopulateNavigationNodeData()
	{
		when(source.getPosition()).thenReturn(sourcePosition);
		when(currentNavigationNode.getPosition()).thenReturn(currentNavigationNodePosition);

		populator.populate(source, target);
		assertThat(target.getParent().getChildren().size(), is(3));
		assertThat(target.getParent().getChildren().get(expectedPosition), is(target));
	}


}
