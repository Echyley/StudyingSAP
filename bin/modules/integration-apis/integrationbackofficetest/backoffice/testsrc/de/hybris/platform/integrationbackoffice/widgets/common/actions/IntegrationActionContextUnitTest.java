/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.actions.ActionContext;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationActionContextUnitTest
{
	private static final ActionContext<Object> actionContext = mock(ActionContext.class);
	private final IntegrationActionContext integrationActionContext = new IntegrationActionContext(actionContext);

	private static final Object nonCollection = new Object();

	private static final Collection<Object> emptyCollection = new ArrayList();
	private static final Collection<Object> collection = Arrays.asList(new Object(), new Object());

	@Test
	public void getContextObjectsSizeMustMatchSizeOfCollectionWithMultipleValues()
	{
		integrationActionContext.setData(collection);
		final List<Object> ctxObjs = integrationActionContext.getContextObjects();
		assertThat(ctxObjs).containsExactlyElementsOf(collection);
	}

	@Test
	public void getContextObjectsSizeMustMatchSizeOfCollectionWithSingleValue()
	{
		integrationActionContext.setData(nonCollection);
		final List<Object> ctxObjs = integrationActionContext.getContextObjects();
		assertThat(ctxObjs).containsExactly(nonCollection);
	}

	@Test
	public void getContextObjectsReturnEmptyListWhenNull()
	{
		integrationActionContext.setData(null);
		final List<Object> ctxObjs = integrationActionContext.getContextObjects();
		assertEquals("Null data should return empty list.", ctxObjs, Collections.emptyList());
	}

	@Test
	public void isCollectionEmptyWhenDataIsNull()
	{
		integrationActionContext.setData(null);
		final boolean result = integrationActionContext.isDataNotPresent();
		assertTrue("Null collection should be considered empty (return true)", result);
	}

	@Test
	public void isCollectionEmptyWhenDataIsNonEmptyCollection()
	{
		integrationActionContext.setData(collection);
		final boolean result = integrationActionContext.isDataNotPresent();
		assertFalse("Collection with items should return false", result);
	}

	@Test
	public void isCollectionEmptyWhenDataIsEmptyCollection()
	{
		integrationActionContext.setData(emptyCollection);
		final boolean result = integrationActionContext.isDataNotPresent();
		assertTrue("Empty collection should return true", result);

	}
}
