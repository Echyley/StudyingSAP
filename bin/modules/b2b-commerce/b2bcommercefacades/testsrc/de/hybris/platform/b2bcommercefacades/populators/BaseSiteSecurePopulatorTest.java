/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.populators;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BaseSiteSecurePopulatorTest
{
	private final BaseSiteSecurePopulator baseSiteSecurePopulator = new BaseSiteSecurePopulator();

	@Mock
	private BaseSiteModel baseSiteModel;
	private BaseSiteData baseSiteData;

	@Test
	public void populateTrueFromBaseSiteModelToBaseSiteData()
	{
		baseSiteData = new BaseSiteData();
		assertFalse(baseSiteData.isEnableRegistration());
		when(baseSiteModel.isEnableRegistration()).thenReturn(true);

		baseSiteSecurePopulator.populate(baseSiteModel, baseSiteData);

		verify(baseSiteModel).isEnableRegistration();
		assertTrue(baseSiteData.isEnableRegistration());
		assertFalse(baseSiteData.isRequiresAuthentication());
	}

	@Test
	public void populateFalseFromBaseSiteModelToBaseSiteData()
	{
		baseSiteData = new BaseSiteData();
		assertFalse(baseSiteData.isEnableRegistration());
		when(baseSiteModel.isEnableRegistration()).thenReturn(false);

		baseSiteSecurePopulator.populate(baseSiteModel, baseSiteData);

		verify(baseSiteModel).isEnableRegistration();
		assertFalse(baseSiteData.isEnableRegistration());
		assertFalse(baseSiteData.isRequiresAuthentication());
	}
}
