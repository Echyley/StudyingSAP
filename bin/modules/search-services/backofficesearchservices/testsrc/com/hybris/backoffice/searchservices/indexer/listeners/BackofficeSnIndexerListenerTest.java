/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.indexer.listeners;
import com.hybris.backoffice.searchservices.model.BackofficeIndexedTypeToSearchservicesIndexConfigModel;
import com.hybris.backoffice.searchservices.services.impl.BackofficeSearchservicesFacetSearchConfigService;

import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BackofficeSnIndexerListenerTest
{
	private static final String PRODUCT_TYPECODE = "Product";
	private static final String INDEX_TYPE_ID= "Backoffice-Product";
	private static final String INDEX_TYPE_ID1= "Other-Product";

	@Mock
	private BackofficeSearchservicesFacetSearchConfigService backofficeFacetSearchConfigService;

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private BackofficeIndexedTypeToSearchservicesIndexConfigModel backofficeIndexedTypeToSearchservicesIndexConfigModel;

	@Mock
	private ModelService modelService;

	@Mock
	private SnIndexTypeModel snIndexTypeModel;

	@Mock
	private SnIndexType snIndexType;

	@InjectMocks
	private final BackofficeSnIndexerListener backofficeSnIndexerListener = new BackofficeSnIndexerListener();

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		when(backofficeFacetSearchConfigService.findSearchConfigForTypeCodeAndIndexTypeId(PRODUCT_TYPECODE, INDEX_TYPE_ID)).thenReturn(Optional.of(backofficeIndexedTypeToSearchservicesIndexConfigModel));
		when(indexerContext.getIndexType()).thenReturn(snIndexType);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.getSnIndexType()).thenReturn(snIndexTypeModel);
		when(snIndexType.getItemComposedType()).thenReturn(PRODUCT_TYPECODE);
		when(snIndexTypeModel.getId()).thenReturn(INDEX_TYPE_ID);
	}

	@Test
	public void shouldSetIndexToActiveWhenFullIndexIsFull()
	{
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.FULL);
		when(snIndexType.getId()).thenReturn(INDEX_TYPE_ID);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(false);

		backofficeSnIndexerListener.afterIndex(indexerContext);

		verify(modelService).save(any());
	}

	@Test
	public void shouldNotSetIndexToActiveWhenFullIndexIsActive()
	{
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.FULL);
		when(snIndexType.getId()).thenReturn(INDEX_TYPE_ID);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(true);

		backofficeSnIndexerListener.afterIndex(indexerContext);

		verify(modelService, never()).save(any());
	}

	@Test
	public void shouldNotSetIndexToActiveWhenIsNotFullIndex()
	{
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.INCREMENTAL);
		when(snIndexType.getId()).thenReturn(INDEX_TYPE_ID);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(false);

		backofficeSnIndexerListener.afterIndex(indexerContext);

		verify(modelService, never()).save(any());
	}

	@Test
	public void shouldNotSetIndexToActiveWhenIndexTypeIdNotMatch()
	{
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.FULL);
		when(snIndexType.getId()).thenReturn(INDEX_TYPE_ID1);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(false);

		backofficeSnIndexerListener.afterIndex(indexerContext);

		verify(modelService, never()).save(any());
	}

}
