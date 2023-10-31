/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.pcmbackoffice.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.nullable;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.genericsearch.GenericSearchQuery;
import de.hybris.platform.genericsearch.GenericSearchService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.enums.BackofficeSpecialCollectionType;
import com.hybris.backoffice.model.BackofficeObjectCollectionItemReferenceModel;
import com.hybris.backoffice.model.BackofficeObjectSpecialCollectionModel;


@RunWith(MockitoJUnitRunner.class)
public class ShortcutsServiceTest
{

	private String ENUMERATION_CODE = "BackofficeSpecialCollectionType";

	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private GenericSearchService genericSearchService;
	@Mock
	private TypeService typeService;

	@InjectMocks
	private ShortcutsService shortcutsService;


	@Test
	public void shouldReturnNullWhenGetAllCollectionList()
	{
		//given
		final BackofficeObjectSpecialCollectionModel collectionModel = mock(BackofficeObjectSpecialCollectionModel.class);

		//then
		when(genericSearchService.search(Mockito.any(GenericSearchQuery.class))).thenReturn(null);
		assertThat(shortcutsService.getAllCollectionList(collectionModel)).isEmpty();
	}

	@Test
	public void shouldReturnPKListWhenGetAllCollectionList()
	{
		//given
		final BackofficeObjectSpecialCollectionModel collectionModel = mock(BackofficeObjectSpecialCollectionModel.class);
		final SearchResult searchResult = mock(SearchResult.class);
		final List<BackofficeObjectCollectionItemReferenceModel> backofficeObjectCollectionItemReferenceModelList = new ArrayList<>();
		final BackofficeObjectCollectionItemReferenceModel backofficeObjectCollectionItemReferenceModel = mock(
				BackofficeObjectCollectionItemReferenceModel.class);
		backofficeObjectCollectionItemReferenceModelList.add(backofficeObjectCollectionItemReferenceModel);
		final ProductModel product = mock(ProductModel.class);
		when(backofficeObjectCollectionItemReferenceModel.getProduct()).thenReturn(product);
		when(searchResult.getResult()).thenReturn(backofficeObjectCollectionItemReferenceModelList);
		when(genericSearchService.search(Mockito.any(GenericSearchQuery.class))).thenReturn(searchResult);

		//then
		assertThat(shortcutsService.getAllCollectionList(collectionModel))
				.isEqualTo(backofficeObjectCollectionItemReferenceModelList);
	}

	@Test
	public void shouldDeleteNotExistReferenceAndReturnPKListWhenGetAllCollectionList()
	{
		//given
		final BackofficeObjectSpecialCollectionModel collectionModel = mock(BackofficeObjectSpecialCollectionModel.class);
		final SearchResult searchResult = mock(SearchResult.class);
		final List<BackofficeObjectCollectionItemReferenceModel> backofficeObjectCollectionItemReferenceModelList = new ArrayList<>();
		final BackofficeObjectCollectionItemReferenceModel backofficeObjectCollectionItemReferenceModel = mock(
				BackofficeObjectCollectionItemReferenceModel.class);
		backofficeObjectCollectionItemReferenceModelList.add(backofficeObjectCollectionItemReferenceModel);
		when(backofficeObjectCollectionItemReferenceModel.getProduct()).thenReturn(null);
		when(searchResult.getResult()).thenReturn(backofficeObjectCollectionItemReferenceModelList);
		when(genericSearchService.search(Mockito.any(GenericSearchQuery.class))).thenReturn(searchResult);

		//then
		assertThat(shortcutsService.getAllCollectionList(collectionModel))
				.isEqualTo(backofficeObjectCollectionItemReferenceModelList);
		verify(modelService).remove(backofficeObjectCollectionItemReferenceModel);
	}

	@Test
	public void verifyInsertProductToCollectionlist()
	{
		//given
		final BackofficeObjectCollectionItemReferenceModel collectionItemReferenceModel = mock(
				BackofficeObjectCollectionItemReferenceModel.class);
		when(modelService.create(BackofficeObjectCollectionItemReferenceModel.class)).thenReturn(collectionItemReferenceModel);

		//when
		shortcutsService.insertProductToCollectionlist(mock(ProductModel.class),
				mock(BackofficeObjectSpecialCollectionModel.class));

		//then
		verify(modelService).save(collectionItemReferenceModel);
	}

	@Test
	public void verifyDeleteProductFromCollectionlist()
	{
		//given
		final BackofficeObjectCollectionItemReferenceModel collectionItemReferenceModel = mock(
				BackofficeObjectCollectionItemReferenceModel.class);
		final SearchResult<BackofficeObjectCollectionItemReferenceModel> searchResult = mock(SearchResult.class);
		final List<BackofficeObjectCollectionItemReferenceModel> result = new ArrayList<>();
		result.add(collectionItemReferenceModel);

		when(searchResult.getResult()).thenReturn(result);
		when(genericSearchService.<BackofficeObjectCollectionItemReferenceModel> search(Mockito.any(GenericSearchQuery.class)))
				.thenReturn(searchResult);

		//when
		shortcutsService.deleteProductFromCollectionlist(mock(ProductModel.class),
				mock(BackofficeObjectSpecialCollectionModel.class));

		//then
		verify(modelService).remove(collectionItemReferenceModel);
	}

	@Test
	public void shouldReturnCollectionModelWhenGetCollectionNotNull()
	{
		//given
		final String collectionCode = "collectionCode";
		final BackofficeObjectSpecialCollectionModel collectionModel = mock(BackofficeObjectSpecialCollectionModel.class);
		final EnumerationValueModel evm = mock(EnumerationValueModel.class);
		final BackofficeSpecialCollectionType collectionType = BackofficeSpecialCollectionType.BLOCKEDLIST;
		final UserModel currentUser = mock(UserModel.class);

		final SearchResult<BackofficeObjectSpecialCollectionModel> searchResult = mock(SearchResult.class);
		final List<BackofficeObjectSpecialCollectionModel> result = new ArrayList<>();
		result.add(collectionModel);

		when(searchResult.getResult()).thenReturn(result);
		when(genericSearchService.<BackofficeObjectSpecialCollectionModel> search(nullable(GenericSearchQuery.class)))
				.thenReturn(searchResult);

		when(typeService.getEnumerationValue(ENUMERATION_CODE, collectionCode)).thenReturn(evm);
		when(modelService.get(nullable(PK.class))).thenReturn(collectionType);
		when(userService.getCurrentUser()).thenReturn(currentUser);

		//then
		assertThat(shortcutsService.initCollection(collectionCode)).isEqualTo(collectionModel);
	}

	@Test
	public void shouldReturnCollectionModelWhenGetCollectionIsNull()
	{
		//given
		final String collectionCode = "collectionCode";
		final BackofficeObjectSpecialCollectionModel collectionModel = mock(BackofficeObjectSpecialCollectionModel.class);

		final EnumerationValueModel evm = mock(EnumerationValueModel.class);
		final BackofficeSpecialCollectionType collectionType = BackofficeSpecialCollectionType.BLOCKEDLIST;
		final UserModel currentUser = mock(UserModel.class);

		final BackofficeObjectSpecialCollectionModel expectedCollectionModel = collectionModel;
		expectedCollectionModel.setUser(currentUser);
		expectedCollectionModel.setCollectionType(collectionType);

		final SearchResult<BackofficeObjectSpecialCollectionModel> searchResult = mock(SearchResult.class);
		final List<BackofficeObjectSpecialCollectionModel> result = new ArrayList<>();

		when(searchResult.getResult()).thenReturn(result);
		when(genericSearchService.<BackofficeObjectSpecialCollectionModel> search(nullable(GenericSearchQuery.class)))
				.thenReturn(searchResult);

		when(typeService.getEnumerationValue(ENUMERATION_CODE, collectionCode)).thenReturn(evm);
		when(modelService.get(nullable(PK.class))).thenReturn(collectionType);
		when(modelService.create(BackofficeObjectSpecialCollectionModel.class)).thenReturn(collectionModel);
		when(userService.getCurrentUser()).thenReturn(currentUser);

		//then
		assertThat(shortcutsService.initCollection(collectionCode)).isEqualTo(expectedCollectionModel);
	}

	@Test
	public void shouldReturnTrueWhenCollectionContainsItem()
	{
		//given
		final SearchResult<Object> searchResult = mock(SearchResult.class);

		when(searchResult.getCount()).thenReturn(1);
		when(genericSearchService.search(Mockito.any(GenericSearchQuery.class))).thenReturn(searchResult);

		//then
		assertThat(
				shortcutsService.collectionContainsItem(mock(ProductModel.class), mock(BackofficeObjectSpecialCollectionModel.class)))
						.isTrue();
	}

	@Test
	public void shouldReturnFalseWhenCollectionNotContainsItem()
	{
		//given
		final SearchResult<Object> searchResult = mock(SearchResult.class);

		when(searchResult.getCount()).thenReturn(0);
		when(genericSearchService.search(Mockito.any(GenericSearchQuery.class))).thenReturn(searchResult);

		//then
		assertThat(
				shortcutsService.collectionContainsItem(mock(ProductModel.class), mock(BackofficeObjectSpecialCollectionModel.class)))
						.isFalse();
	}
}
