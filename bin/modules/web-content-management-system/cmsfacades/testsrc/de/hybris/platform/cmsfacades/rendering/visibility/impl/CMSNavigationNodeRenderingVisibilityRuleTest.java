/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.rendering.visibility.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.booleanThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSNavigationNodeRenderingVisibilityRuleTest
{
	@InjectMocks
	private CMSNavigationNodeRenderingVisibilityRule renderingVisibilityRule;

	@Mock
	private CMSNavigationNodeModel nodeModel;

	@Test
	public void shouldReturnTrueIfNodeIsVisible()
	{
		// GIVEN
		when(nodeModel.isVisible()).thenReturn(true);

		// WHEN
		final boolean result = renderingVisibilityRule.isVisible(nodeModel);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnFalseIfNodeIsNotVisible()
	{
		// GIVEN
		when(nodeModel.isVisible()).thenReturn(false);

		// WHEN
		final boolean result = renderingVisibilityRule.isVisible(nodeModel);

		// THEN
		assertFalse(result);
	}
}
