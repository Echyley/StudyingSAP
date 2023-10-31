/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.order.EntryGroup;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test to check when an entry can be modified
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BundleOrderEntryModifiableCheckerTest
{
	@Mock
	private BundleTemplateService bundleTemplateService;
	@InjectMocks
	private final BundleOrderEntryModifiableChecker checker = new BundleOrderEntryModifiableChecker();

	private EntryGroup entryGroup;

	@Before
	public void setUp()
	{
		entryGroup = new EntryGroup();
		entryGroup.setGroupNumber(5);
		entryGroup.setExternalReferenceId("bundleID");
		checker.setAutoPickEnabled(true);
	}

	@After
	public void tearDown()
	{
		checker.setAutoPickEnabled(false);
	}

	@Test
	public void testShouldMakeBundleEntriesNotUpdatableIfAutoPick()
	{
		when(bundleTemplateService.getBundleEntryGroup(any(AbstractOrderEntryModel.class))).thenReturn(entryGroup);
		when(bundleTemplateService.getBundleTemplateForCode(any())).thenReturn(new BundleTemplateModel());
		when(bundleTemplateService.isAutoPickComponent(any(BundleTemplateModel.class))).thenReturn(true);

		final AbstractOrderEntryModel entryToUpdate = new CartEntryModel();
		entryToUpdate.setEntryGroupNumbers(Collections.singleton(5));
		Assert.assertFalse(checker.canModify(entryToUpdate));
	}

	@Test
	public void standAloneEntriesCanBeUpdated()
	{
		when(bundleTemplateService.getBundleEntryGroup(any(AbstractOrderEntryModel.class))).thenReturn(null);

		final AbstractOrderEntryModel entryToUpdate = new CartEntryModel();
		entryToUpdate.setEntryGroupNumbers(Collections.singleton(1));
		Assert.assertTrue(checker.canModify(entryToUpdate));
	}

}
