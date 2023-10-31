/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.aspects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.savedvalues.ItemModificationHistoryService;
import com.hybris.backoffice.searchservices.events.SearchservicesIndexSynchronizationStrategy;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectAccessException;


@RunWith(MockitoJUnitRunner.class)
public class ObjectFacadeSearchservicesIndexingAspectTest
{

	private static final String PRODUCT_TYPE = "Product";
	private static final PK CHANGED_PRODUCT_PK = PK.fromLong(1L);
	private static final PK FAILED_PRODUCT_PK = PK.fromLong(2L);

	@Spy
	@InjectMocks
	private ObjectFacadeSearchservicesIndexingAspect searchservicesIndexingAspect;
	@Mock
	private SearchservicesIndexSynchronizationStrategy synchronizationStrategy;
	@Mock
	private ItemModificationHistoryService itemModificationHistoryService;
	@Mock
	private ModelService modelService;
	@Mock
	private ProductModel changedProduct;
	@Mock
	private ProductModel failedProduct;
	@Mock
	private ConfigurationService configurationService;

	@Before
	public void setUp()
	{
		doReturn(CHANGED_PRODUCT_PK).when(changedProduct).getPk();
		doReturn(PRODUCT_TYPE).when(modelService).getModelType(changedProduct);

		Mockito.lenient().when(failedProduct.getPk()).thenReturn(FAILED_PRODUCT_PK);
		Mockito.lenient().when(modelService.getModelType(failedProduct)).thenReturn(PRODUCT_TYPE);

		doReturn(mock(Configuration.class)).when(configurationService).getConfiguration();
	}

	@Test
	public void shouldCleanIndexForRemovedModel()
	{
		searchservicesIndexingAspect.updateRemoved(new JoinPointStub(changedProduct), null);

		final ArgumentCaptor<String> typeCodeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> pkCaptor = ArgumentCaptor.forClass(List.class);

		verify(synchronizationStrategy).removeItems(typeCodeCaptor.capture(), pkCaptor.capture());

		assertThat(typeCodeCaptor.getValue()).isEqualToIgnoringCase(PRODUCT_TYPE);
		assertThat(pkCaptor.getValue()).containsExactly(CHANGED_PRODUCT_PK);
	}

	@Test
	public void shouldCleanIndexForRemovedModels()
	{
		final ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();
		result.addFailedObject(failedProduct, new ObjectAccessException("", new RuntimeException()));

		searchservicesIndexingAspect.updateRemoved(new JoinPointStub(Collections.singletonList(changedProduct)), result);

		final ArgumentCaptor<String> typeCodeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> pkCaptor = ArgumentCaptor.forClass(List.class);

		verify(synchronizationStrategy).removeItems(typeCodeCaptor.capture(), pkCaptor.capture());

		assertThat(typeCodeCaptor.getValue()).isEqualToIgnoringCase(PRODUCT_TYPE);
		assertThat(pkCaptor.getValue()).containsExactly(CHANGED_PRODUCT_PK);
	}

	@Test
	public void shouldUpdateIndexForUpdatedModel()
	{
		searchservicesIndexingAspect.updateChanged(new JoinPointStub(changedProduct), null);

		final ArgumentCaptor<String> typeCodeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> pkCaptor = ArgumentCaptor.forClass(List.class);

		verify(synchronizationStrategy).updateItems(typeCodeCaptor.capture(), pkCaptor.capture());

		assertThat(typeCodeCaptor.getValue()).isEqualToIgnoringCase(PRODUCT_TYPE);
		assertThat(pkCaptor.getValue()).containsExactly(CHANGED_PRODUCT_PK);
	}


	@Test
	public void shouldUpdateIndexForUpdatedModels()
	{
		final ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();
		result.addFailedObject(failedProduct, new ObjectAccessException("", new RuntimeException()));

		searchservicesIndexingAspect.updateChanged(new JoinPointStub(Collections.singletonList(changedProduct)), result);

		final ArgumentCaptor<String> typeCodeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> pkCaptor = ArgumentCaptor.forClass(List.class);

		verify(synchronizationStrategy).updateItems(typeCodeCaptor.capture(), pkCaptor.capture());

		assertThat(typeCodeCaptor.getValue()).isEqualToIgnoringCase(PRODUCT_TYPE);
		assertThat(pkCaptor.getValue()).containsExactly(CHANGED_PRODUCT_PK);
	}

	static class JoinPointStub implements JoinPoint
	{
		private final Object[] args;

		public JoinPointStub(final Object... args)
		{
			this.args = args;
		}

		@Override
		public String toShortString()
		{
			return null;
		}

		@Override
		public String toLongString()
		{
			return null;
		}

		@Override
		public Object getThis()
		{
			return null;
		}

		@Override
		public Object getTarget()
		{
			return null;
		}

		@Override
		public Object[] getArgs()
		{
			return args;
		}

		@Override
		public Signature getSignature()
		{
			return null;
		}

		@Override
		public SourceLocation getSourceLocation()
		{
			return null;
		}

		@Override
		public String getKind()
		{
			return null;
		}

		@Override
		public StaticPart getStaticPart()
		{
			return null;
		}
	}

}
