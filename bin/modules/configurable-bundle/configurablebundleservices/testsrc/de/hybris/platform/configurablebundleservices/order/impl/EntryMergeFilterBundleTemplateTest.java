/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class EntryMergeFilterBundleTemplateTest
{
	@InjectMocks
	private final EntryMergeFilterBundleTemplate filter = new EntryMergeFilterBundleTemplate();
	@Mock
	private BundleTemplateService bundleTemplateService;

	@Test
	public void shouldSkipStandalones()
	{
//		when(bundleTemplateService.getBundleEntryGroup(any())).thenReturn(null);
		assertTrue(filter.apply(new AbstractOrderEntryModel(), new AbstractOrderEntryModel()).booleanValue());
	}

	@Test
	public void shouldBlockBundleAndStandalone()
	{
		final AbstractOrderEntryModel bundled = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel standalone = new AbstractOrderEntryModel();
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(1));
		when(bundleTemplateService.getBundleEntryGroup(bundled)).thenReturn(group);
//		when(bundleTemplateService.getBundleEntryGroup(standalone)).thenReturn(null);

		assertFalse(filter.apply(bundled, standalone).booleanValue());
		assertFalse(filter.apply(standalone, bundled).booleanValue());
	}

	@Test
	public void shouldBlockFromDifferentBundles()
	{
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		final EntryGroup group1 = new EntryGroup();
		group1.setGroupNumber(Integer.valueOf(1));
		final EntryGroup group2 = new EntryGroup();
		group2.setGroupNumber(Integer.valueOf(5));
		when(bundleTemplateService.getBundleEntryGroup(entry1)).thenReturn(group1);
		when(bundleTemplateService.getBundleEntryGroup(entry2)).thenReturn(group2);

		assertFalse(filter.apply(entry1, entry2).booleanValue());
		assertFalse(filter.apply(entry2, entry1).booleanValue());
	}

	@Test
	public void shouldMergeFromOneBundle()
	{
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(Integer.valueOf(1));
		when(bundleTemplateService.getBundleEntryGroup(any())).thenReturn(group);

		assertTrue(filter.apply(entry1, entry2).booleanValue());
		assertTrue(filter.apply(entry2, entry1).booleanValue());
	}
}
