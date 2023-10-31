/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.widgets.deeplink;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import de.hybris.platform.genericsearch.GenericSearchService;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;

import com.hybris.cockpitng.widgets.deeplink.handler.impl.DefaultOpenDeepLinkHandler;
import com.hybris.cockpitng.widgets.deeplink.handler.exceptions.NotUniqueException;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeOpenDeepLinkHandlerTest
{
	@Spy
	@InjectMocks
	private DefaultBackofficeOpenDeepLinkHandler handler;

	@Mock
	private GenericSearchService genericSearchService;

	@Test(expected = NotUniqueException.class)
	public void shouldThrowNotUniqueExceptionWhenNoUniqueResultFound() throws NotUniqueException
	{
		final DefaultOpenDeepLinkHandler.TreeNode<Object> rootTreeNode = createConditionTreeNodeMock();

		final SearchResult<Object> searchResult = mock(SearchResult.class);
		final var resultList = new ArrayList<>();
		resultList.add(new Object());
		resultList.add(new Object());
		when(genericSearchService.search(any(GenericQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(resultList);

		handler.findItem(rootTreeNode);

		verify(genericSearchService, times(2)).search(any(GenericQuery.class));
	}

	@Test
	public void shouldFindItemWhenOnlyOneItemFound() throws NotUniqueException
	{
		final DefaultOpenDeepLinkHandler.TreeNode<Object> rootTreeNode = createConditionTreeNodeMock();

		final SearchResult<Object> searchResult = mock(SearchResult.class);
		final var resultList = new ArrayList<>();
		final var item = new Object();
		resultList.add(item);
		when(genericSearchService.search(any(GenericQuery.class))).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(resultList);

		final Optional<Object> result = handler.findItem(rootTreeNode);

		assertThat(result).contains(item);
		verify(genericSearchService, times(2)).search(any(GenericQuery.class));
	}

	private DefaultOpenDeepLinkHandler.TreeNode<Object> createConditionTreeNodeMock()
	{
		final var TYPE_CODE_CATALOG_VERSION = "CatalogVersion";
		final var QUALIFIER_CATALOG_VERSION = "CatalogVersion";
		final var TYPE_CODE_CATALOG = "Catalog";
		final var QUALIFIER_CATALOG = "catalog";
		final var QUALIFIER_VERSION = "version";
		final var QUALIFIER_VALUE_VERSION = "Staged";
		final var QUALIFIER_ID = "id";
		final var QUALIFIER_VALUE_ID = "electronicsProductCatalog";
		final DataType catalogVersionType = mock(DataType.class);
		final DataType catalogType = mock(DataType.class);
		when(catalogVersionType.getCode()).thenReturn(TYPE_CODE_CATALOG_VERSION);
		when(catalogType.getCode()).thenReturn(TYPE_CODE_CATALOG);

		final DefaultOpenDeepLinkHandler.TreeNode<Object> rootTreeNode = new DefaultOpenDeepLinkHandler.TreeNode<>(
				QUALIFIER_CATALOG_VERSION, catalogVersionType, null);
		final DefaultOpenDeepLinkHandler.TreeNode<Object> catalogTreeNode = new DefaultOpenDeepLinkHandler.TreeNode<>(
				QUALIFIER_CATALOG, catalogType, null);
		final DefaultOpenDeepLinkHandler.TreeNode<Object> versionTreeNode = new DefaultOpenDeepLinkHandler.TreeNode<>(
				QUALIFIER_VERSION, null, QUALIFIER_VALUE_VERSION);
		final List<DefaultOpenDeepLinkHandler.TreeNode<Object>> rootTreeNodeChildren = new ArrayList<>();
		rootTreeNodeChildren.add(catalogTreeNode);
		rootTreeNodeChildren.add(versionTreeNode);
		rootTreeNode.setChildren(rootTreeNodeChildren);

		final DefaultOpenDeepLinkHandler.TreeNode<Object> idTreeNode = new DefaultOpenDeepLinkHandler.TreeNode<>(QUALIFIER_ID, null,
				QUALIFIER_VALUE_ID);
		final List<DefaultOpenDeepLinkHandler.TreeNode<Object>> catalogTreeNodeChildren = new ArrayList<>();
		catalogTreeNodeChildren.add(idTreeNode);
		catalogTreeNode.setChildren(catalogTreeNodeChildren);

		return rootTreeNode;
	}
}
