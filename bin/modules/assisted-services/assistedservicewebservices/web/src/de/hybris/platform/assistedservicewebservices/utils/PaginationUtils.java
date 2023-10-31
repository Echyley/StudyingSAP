/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.utils;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;

import java.util.List;


/**
 * Utility class for building Pagination data.
 */
public class PaginationUtils
{

	private PaginationUtils()
	{
	}

	public static PageableData createPageableData(final int currentPage, final int pageSize, final String sort)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);
		return pageableData;
	}

	public static PaginationData buildPaginationData(final PageableData pageableData, final List data)
	{
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(pageableData.getCurrentPage());
		paginationData.setPageSize(pageableData.getPageSize());
		paginationData.setSort(pageableData.getSort());
		paginationData.setTotalNumberOfResults(data.size());
		// if data is zero, return 1. else return ceil of quantityOfResult/pageSize
		paginationData.setNumberOfPages(Math.max((int) Math.ceil((double) data.size() / pageableData.getPageSize()), 1));
		return paginationData;
	}
}
