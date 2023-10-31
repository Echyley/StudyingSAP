/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerRequest;
import de.hybris.platform.searchservices.indexer.service.SnIndexerResponse;
import de.hybris.platform.searchservices.indexer.service.SnIndexerService;
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerContext;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.searchservices.model.BackofficeIndexedTypeToSearchservicesIndexConfigModel;
import com.hybris.backoffice.searchservices.services.impl.BackofficeSearchservicesFacetSearchConfigService;

public class DirectSearchservicesIndexSynchronizationStrategyTest
{
	public static final String PRODUCT_TYPECODE = "Product";
	public static final long PK_1 = 1L;
	public static final String INDEX_TYPE_ID = "test_backoffice_product";

	@Mock
	private ComposedTypeModel typeModel;

	@Mock
	private SnIndexTypeModel snIndexTypeModel;

	@Mock
	private BackofficeIndexedTypeToSearchservicesIndexConfigModel backofficeIndexedTypeToSearchservicesIndexConfigModel;

	@Mock
	private SnIndexerService snIndexerService;

	@Mock
	private BackofficeSearchservicesFacetSearchConfigService backofficeFacetSearchConfigService;

	@Mock
	private TypeService typeService;

	@InjectMocks
	private final DirectSearchservicesIndexSynchronizationStrategy strategy = new DirectSearchservicesIndexSynchronizationStrategy();

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		when(typeService.getTypeForCode(PRODUCT_TYPECODE)).thenReturn(typeModel);
		when(snIndexTypeModel.getIndexerCronJobs()).thenReturn(new ArrayList<>());
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.getSnIndexType()).thenReturn(snIndexTypeModel);
		when(backofficeFacetSearchConfigService.getIndexedTypeModel(PRODUCT_TYPECODE)).thenReturn(snIndexTypeModel);
		when(backofficeFacetSearchConfigService.findSearchConfigForTypeCode(PRODUCT_TYPECODE)).thenReturn(backofficeIndexedTypeToSearchservicesIndexConfigModel);
		when(snIndexTypeModel.getId()).thenReturn(INDEX_TYPE_ID);
	}

	@Test
	public void testRemoveItemWhenIndexInitialized() throws SnIndexerException
	{
		//given
		final SnIndexerRequest indexerRequest = mock(SnIndexerRequest.class);
		final SnIndexerResponse snIndexerResponse = mock(SnIndexerResponse.class);
		when(snIndexerService.createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), any())).thenReturn(indexerRequest);
		when(snIndexerService.index(any())).thenReturn(snIndexerResponse);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(true);

		//when
		strategy.removeItem(PRODUCT_TYPECODE, PK_1);

		//then
		final ArgumentCaptor<List> indexerItemSourceOperationsList = ArgumentCaptor.forClass(List.class);
		verify(snIndexerService).createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), indexerItemSourceOperationsList.capture());

		final List<SnIndexerItemSourceOperation> indexerItemSourceOperations = indexerItemSourceOperationsList.getValue();
		assertThat(indexerItemSourceOperations).hasSize(1);
		final SnIndexerItemSourceOperation indexerItemSourceOperation = indexerItemSourceOperations.get(0);
		assertThat(indexerItemSourceOperation.getDocumentOperationType()).isEqualTo(SnDocumentOperationType.DELETE);
		assertThat(indexerItemSourceOperation.getIndexerItemSource().getPks(new DefaultSnIndexerContext())).hasSize(1);
		assertThat(indexerItemSourceOperation.getIndexerItemSource().getPks(new DefaultSnIndexerContext()))
				.containsExactly(PK.fromLong(PK_1));
		verify(snIndexerService).index(indexerRequest);
	}

	@Test
	public void testNotRemoveItemWhenIndexNotInitialized() throws SnIndexerException
	{
		//given
		final SnIndexerRequest indexerRequest = mock(SnIndexerRequest.class);
		final SnIndexerResponse snIndexerResponse = mock(SnIndexerResponse.class);
		when(snIndexerService.createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), any())).thenReturn(indexerRequest);
		when(snIndexerService.index(any())).thenReturn(snIndexerResponse);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(false);

		//when
		strategy.removeItem(PRODUCT_TYPECODE, PK_1);

		//then
		final ArgumentCaptor<List> indexerItemSourceOperationsList = ArgumentCaptor.forClass(List.class);
		verify(snIndexerService, never()).createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), indexerItemSourceOperationsList.capture());
	}

	@Test
	public void testUpdateItemWhenIndexInitialized() throws SnIndexerException
	{
		//given
		final SnIndexerRequest indexerRequest = mock(SnIndexerRequest.class);
		final SnIndexerResponse snIndexerResponse = mock(SnIndexerResponse.class);
		when(snIndexerService.createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), any())).thenReturn(indexerRequest);
		when(snIndexerService.index(any())).thenReturn(snIndexerResponse);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(true);

		//when
		strategy.updateItem(PRODUCT_TYPECODE, PK_1);

		//then
		final ArgumentCaptor<List> indexerItemSourceOperationsList = ArgumentCaptor.forClass(List.class);
		verify(snIndexerService).createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), indexerItemSourceOperationsList.capture());

		final List<SnIndexerItemSourceOperation> indexerItemSourceOperations = indexerItemSourceOperationsList.getValue();
		assertThat(indexerItemSourceOperations).hasSize(1);
		final SnIndexerItemSourceOperation indexerItemSourceOperation = indexerItemSourceOperations.get(0);
		assertThat(indexerItemSourceOperation.getDocumentOperationType()).isEqualTo(SnDocumentOperationType.CREATE_UPDATE);
		assertThat(indexerItemSourceOperation.getIndexerItemSource().getPks(new DefaultSnIndexerContext())).hasSize(1);
		assertThat(indexerItemSourceOperation.getIndexerItemSource().getPks(new DefaultSnIndexerContext()))
				.containsExactly(PK.fromLong(PK_1));
		verify(snIndexerService).index(indexerRequest);
	}

	@Test
	public void testNotUpdateItemWhenIndexNotInitialized() throws SnIndexerException
	{
		//given
		final SnIndexerRequest indexerRequest = mock(SnIndexerRequest.class);
		final SnIndexerResponse snIndexerResponse = mock(SnIndexerResponse.class);
		when(snIndexerService.createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), any())).thenReturn(indexerRequest);
		when(snIndexerService.index(any())).thenReturn(snIndexerResponse);
		when(backofficeIndexedTypeToSearchservicesIndexConfigModel.isActive()).thenReturn(false);

		//when
		strategy.updateItem(PRODUCT_TYPECODE, PK_1);

		//then
		final ArgumentCaptor<List> indexerItemSourceOperationsList = ArgumentCaptor.forClass(List.class);
		verify(snIndexerService, never()).createIncrementalIndexerRequest(eq(INDEX_TYPE_ID), indexerItemSourceOperationsList.capture());
	}

}
