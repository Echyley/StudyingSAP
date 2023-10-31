/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.aspects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.hmc.model.SavedValueEntryModel;
import de.hybris.platform.hmc.model.SavedValuesModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.savedvalues.ItemModificationHistoryService;
import com.hybris.backoffice.solrsearch.events.SolrIndexSynchronizationStrategy;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectAccessException;


@RunWith(MockitoJUnitRunner.class)
public class ObjectFacadeSolrIndexingAspectTest
{

	private static final String PRODUCT_TYPE = "Product";
	private static final PK CHANGED_PRODUCT_PK = PK.fromLong(1L);
	private static final PK FAILED_PRODUCT_PK = PK.fromLong(2L);

	@Spy
	@InjectMocks
	private ObjectFacadeSolrIndexingAspect solrIndexingAspect;
	@Mock
	private SolrIndexSynchronizationStrategy synchronizationStrategy;
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


		doReturn(false).when(solrIndexingAspect).isReverseCategoryIndexLookupEnabled();
		doReturn(mock(Configuration.class)).when(configurationService).getConfiguration();
	}

	@Test
	public void shouldUpdateIndexForChangedModel()
	{
		solrIndexingAspect.updateChanged(new JoinPointStub(changedProduct), null);

		final ArgumentCaptor<String> typeCodeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> pkCaptor = ArgumentCaptor.forClass(List.class);

		verify(synchronizationStrategy).updateItems(typeCodeCaptor.capture(), pkCaptor.capture());

		assertThat(typeCodeCaptor.getValue()).isEqualToIgnoringCase(PRODUCT_TYPE);
		assertThat(pkCaptor.getValue()).containsExactly(CHANGED_PRODUCT_PK);
	}

	@Test
	public void shouldCleanIndexForRemovedModel()
	{
		solrIndexingAspect.updateRemoved(new JoinPointStub(changedProduct), null);

		final ArgumentCaptor<String> typeCodeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> pkCaptor = ArgumentCaptor.forClass(List.class);

		verify(synchronizationStrategy).removeItems(typeCodeCaptor.capture(), pkCaptor.capture());

		assertThat(typeCodeCaptor.getValue()).isEqualToIgnoringCase(PRODUCT_TYPE);
		assertThat(pkCaptor.getValue()).containsExactly(CHANGED_PRODUCT_PK);
	}

	@Test
	public void shouldUpdateIndexForChangedModels()
	{
		final ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();
		result.addFailedObject(failedProduct, new ObjectAccessException("", new RuntimeException()));

		solrIndexingAspect.updateChanged(new JoinPointStub(Collections.singletonList(changedProduct)), result);

		final ArgumentCaptor<String> typeCodeCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<List> pkCaptor = ArgumentCaptor.forClass(List.class);

		verify(synchronizationStrategy).updateItems(typeCodeCaptor.capture(), pkCaptor.capture());

		assertThat(typeCodeCaptor.getValue()).isEqualToIgnoringCase(PRODUCT_TYPE);
		assertThat(pkCaptor.getValue()).containsExactly(CHANGED_PRODUCT_PK);
	}

	@Test
	public void shouldUpdateIndexForAddedProductToCategory()
	{
		//given
		final ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();
		final CategoryModel csm = mock(CategoryModel.class);

		final List<SavedValuesModel> savedValuesModelList = createSavedValueModel(true);
		when(itemModificationHistoryService.getSavedValues(csm)).thenReturn(savedValuesModelList);
		when(modelService.getModelType(csm)).thenReturn(CategoryModel._TYPECODE);

		//when
		solrIndexingAspect.updateChanged(new JoinPointStub(csm), result);

		//then
		verify(synchronizationStrategy).updateItems(ProductModel._TYPECODE, Lists.newArrayList(CHANGED_PRODUCT_PK));
	}

	@Test
	public void shouldUpdateIndexForRemovedProductFromCategory()
	{
		//given
		final ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();
		final CategoryModel categoryModel = mock(CategoryModel.class);

		final List<SavedValuesModel> savedValuesModelList = createSavedValueModel(false);
		when(itemModificationHistoryService.getSavedValues(categoryModel)).thenReturn(savedValuesModelList);
		when(modelService.getModelType(categoryModel)).thenReturn(CategoryModel._TYPECODE);

		//when
		solrIndexingAspect.updateChanged(new JoinPointStub(categoryModel), result);

		//then
		verify(synchronizationStrategy).updateItems(ProductModel._TYPECODE, Lists.newArrayList(CHANGED_PRODUCT_PK));
	}

	@Test
	public void shouldNotUpdateIndexFoProductsInCategoryWhenProductsHaveNotChanged()
	{
		//given
		final ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();
		final CategoryModel categoryModel = mock(CategoryModel.class);

		final List<SavedValuesModel> savedValuesModelList = createSavedValueModelWithoutProductsModification();
		when(itemModificationHistoryService.getSavedValues(categoryModel)).thenReturn(savedValuesModelList);
		when(modelService.getModelType(categoryModel)).thenReturn(CategoryModel._TYPECODE);

		//when
		solrIndexingAspect.updateChanged(new JoinPointStub(categoryModel), result);

		//then
		verify(synchronizationStrategy).updateItems(ProductModel._TYPECODE, Lists.emptyList());
	}

	private List<SavedValuesModel> createSavedValueModelWithoutProductsModification()
	{
		final SavedValueEntryModel savedValueEntry = mock(SavedValueEntryModel.class);
		when(savedValueEntry.getModifiedAttribute()).thenReturn(CategoryModel.NAME);

		final SavedValuesModel savedValue = mock(SavedValuesModel.class);
		when(savedValue.getSavedValuesEntries()).thenReturn(Sets.newSet(savedValueEntry));
		when(savedValue.getCreationtime()).thenReturn(new Date());

		return Lists.newArrayList(savedValue);
	}

	private List<SavedValuesModel> createSavedValueModel(final boolean addProduct)
	{
		final SavedValueEntryModel savedValueEntry = mock(SavedValueEntryModel.class);

		if (addProduct)
		{
			mockAddProduct(savedValueEntry);
		}
		else
		{
			mockRemoveProduct(savedValueEntry);
		}

		when(savedValueEntry.getModifiedAttribute()).thenReturn(CategoryModel.PRODUCTS);
		when(changedProduct.getModifiedtime()).thenReturn(new Date());

		final SavedValuesModel savedValue = mock(SavedValuesModel.class);

		when(savedValue.getCreationtime()).thenReturn(new Date());
		when(savedValue.getSavedValuesEntries()).thenReturn(Sets.newSet(savedValueEntry));

		return Lists.newArrayList(savedValue);
	}

	private void mockAddProduct(final SavedValueEntryModel savedValueEntry)
	{
		when(savedValueEntry.getOldValue()).thenReturn(Lists.emptyList());
		when(savedValueEntry.getNewValue()).thenReturn(Lists.newArrayList(changedProduct));
	}

	private void mockRemoveProduct(final SavedValueEntryModel savedValueEntry)
	{
		when(savedValueEntry.getOldValue()).thenReturn(Lists.newArrayList(changedProduct));
		when(savedValueEntry.getNewValue()).thenReturn(Lists.emptyList());
	}

	@Test
	public void includeRequiredProductsShouldKeepPreAddedProducts()
	{
		//given
		doReturn(true).when(solrIndexingAspect).isReverseCategoryIndexLookupEnabled();

		final CategoryModel category = mock(CategoryModel.class);

		final PK originalPK = PK.fromLong(1);

		final JoinPoint jp = new JoinPointStub(category);
		final Map<String, List<PK>> data = new HashMap<>();
		data.put(CategoryModel._TYPECODE, Collections.singletonList(originalPK));

		final PK pk = PK.fromLong(1);
		doReturn(Optional.of(Collections.singleton(pk))).when(solrIndexingAspect).extractProductsFromModifiedCategories(any());

		//when
		solrIndexingAspect.includeRequiredProducts(jp, data);

		//then
		assertThat(data.get(ProductModel._TYPECODE)).contains(pk, originalPK);
	}

	@Test
	public void extractProductsFromModifiedCategoriesShouldFetchProductsFromPassedCategories()
	{
		//given
		final CategoryModel category1 = mock(CategoryModel.class);
		final CategoryModel category2 = mock(CategoryModel.class);

		final PK p1 = PK.fromLong(1);
		final PK p2 = PK.fromLong(2);
		final PK p3 = PK.fromLong(3);

		doReturn(Set.of(p1)).when(solrIndexingAspect).findAllProductsInSubTree(category1);
		doReturn(Set.of(p2, p3)).when(solrIndexingAspect).findAllProductsInSubTree(category2);

		//when
		final Optional<Set<PK>> result = solrIndexingAspect
				.extractProductsFromModifiedCategories(List.of(category1, category2, "Irrelevant data"));

		//then
		assertThat(result).isNotEmpty();
		assertThat(result).contains(Set.of(p1, p2, p3));
	}

	@Test
	public void extractProductsFromModifiedCategoriesShouldFetchProductsFromPassedSingleCategory()
	{
		//given
		final CategoryModel category = mock(CategoryModel.class);

		final PK p1 = PK.fromLong(1);
		final PK p2 = PK.fromLong(2);

		doReturn(Set.of(p1, p2)).when(solrIndexingAspect).findAllProductsInSubTree(category);

		//when
		final Optional<Set<PK>> result = solrIndexingAspect.extractProductsFromModifiedCategories(category);

		//then
		assertThat(result).isNotEmpty();
		assertThat(result).contains(Set.of(p1, p2));
	}

	@Test
	public void extractProductsFromModifiedCategoriesShouldReturnEmptyOnEmptyData()
	{
		//when
		final Optional<Set<PK>> result = solrIndexingAspect.extractProductsFromModifiedCategories(null);

		//then
		assertThat(result).isEmpty();
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
