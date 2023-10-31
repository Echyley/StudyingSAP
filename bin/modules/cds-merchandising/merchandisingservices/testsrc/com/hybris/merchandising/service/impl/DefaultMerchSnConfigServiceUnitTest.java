/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.searchservices.admin.dao.SnIndexTypeDao;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.client.MerchCatalogServiceProductDirectoryClient;
import com.hybris.merchandising.dao.MerchSnConfigDao;
import com.hybris.merchandising.model.MerchSnConfigModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMerchSnConfigServiceUnitTest
{
	private static final String INDEX_TYPE_1_ID = "snIndexType1";
	private static final String BASE_SITE = "electronics";
	private static final String PRODUCT_DIRECTORY_ID = "productDirectoryId";
	
	@Mock
	private MerchSnConfigDao merchSnConfigDao;
	@Mock
	private MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient;
	@Mock
	private ModelService modelService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private ImpersonationService impersonationService;
	@Mock
	private SnIndexTypeDao snIndexTypeDao;

	@InjectMocks
	private DefaultMerchSnConfigService service;

	@Mock
	MerchSnConfigModel config;
	@Mock
	SnIndexTypeModel indexTypeModel;
	@Mock
	BaseSiteModel baseSite;
	@Mock
	LanguageModel languageModel;
	@Mock
	CurrencyModel currencyModel;


	@Test
	public void testGetMerchConfigForIndexType()
	{
		//given
		when(snIndexTypeDao.findIndexTypeById(INDEX_TYPE_1_ID)).thenReturn(Optional.of(indexTypeModel));
		when(merchSnConfigDao.findByIndexedType(indexTypeModel)).thenReturn(Optional.of(config));

		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForIndexedType(INDEX_TYPE_1_ID);

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(config, result.get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMerchConfigForIndexTypeWhenNull()
	{
		//when
		service.getMerchConfigForIndexedType((String) null);
	}

	@Test
	public void testGetMerchConfigForIndexTypeModel()
	{
		//given
		when(merchSnConfigDao.findByIndexedType(indexTypeModel)).thenReturn(Optional.of(config));

		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForIndexedType(indexTypeModel);

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(config, result.get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMerchConfigForIndexTypeModelWhenNull()
	{
		//when
		service.getMerchConfigForIndexedType((SnIndexTypeModel) null);
	}

	@Test
	public void testGetMerchConfigForCurrentBaseSite()
	{
		//given
		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(merchSnConfigDao.findByBaseSite(baseSite)).thenReturn(Optional.of(config));

		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForCurrentBaseSite();

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(config, result.get());
	}

	@Test
	public void testUpdateMerchConfig()
	{
		//when
		service.updateMerchConfig(config);

		//then
		verify(modelService).save(config);
	}

	@Test
	public void testDeleteProductDirectory()
	{
		//given
		when(config.getCdsIdentifier()).thenReturn(PRODUCT_DIRECTORY_ID);

		//when
		service.deleteProductDirectory(config);

		//then
		verify(impersonationService).executeInContext(any(), any());
	}

	@Test
	public void testDeleteProductDirectoryWhenCdsIdentifierIsEmpty()
	{
		//when
		service.deleteProductDirectory(config);

		//then
		verifyNoInteractions(impersonationService);
	}

	@Test
	public void testUpdateProductDirectory()
	{
		//given
		final MerchSnConfigModel merchConfig = createMerchConfig();
		merchConfig.setCdsIdentifier(PRODUCT_DIRECTORY_ID);

		//when
		service.createOrUpdateProductDirectory(merchConfig, false);

		//then
		verify(impersonationService).executeInContext(any(), any());
		verify(modelService, never()).save(merchConfig);

	}

	@Test
	public void testCreateProductDirectory() throws Throwable
	{
		//given
		final MerchSnConfigModel merchConfig = createMerchConfig();
		when(impersonationService.executeInContext(any(), any())).thenReturn(PRODUCT_DIRECTORY_ID);

		//when
		service.createOrUpdateProductDirectory(merchConfig, false);

		//then
		assertEquals(PRODUCT_DIRECTORY_ID, merchConfig.getCdsIdentifier());
		verify(modelService, never()).save(merchConfig);
	}

	@Test
	public void testCreateProductDirectoryWithSave() throws Throwable
	{
		//given
		final MerchSnConfigModel merchConfig = createMerchConfig();
		when(impersonationService.executeInContext(any(), any())).thenReturn(PRODUCT_DIRECTORY_ID);

		//when
		service.createOrUpdateProductDirectory(merchConfig, true);

		//then
		assertEquals(PRODUCT_DIRECTORY_ID, merchConfig.getCdsIdentifier());
		verify(modelService).save(merchConfig);
	}

	private MerchSnConfigModel createMerchConfig()
	{
		final MerchSnConfigModel merchConfig = new MerchSnConfigModel();
		merchConfig.setEnabled(true);
		merchConfig.setDefaultLanguage(languageModel);
		merchConfig.setCurrency(currencyModel);
		merchConfig.setSnIndexType(indexTypeModel);
		merchConfig.setBaseSite(baseSite);
		when(baseSite.getUid()).thenReturn(BASE_SITE);
		when(indexTypeModel.getId()).thenReturn(INDEX_TYPE_1_ID);

		return merchConfig;
	}
}
