/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Used to build A B2BDocument query.
 */
public class B2BDocumentQueryBuilder
{

	private final int currentPage;
	private final int pageSize;
	private final String sort;
	private final boolean isAsc;
	private final Map<String, Object> criterias = new HashMap<>();


	public B2BDocumentQueryBuilder(final int currentPage, final int pageSize, final String sort, final boolean isAsc)
	{
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.sort = sort;
		this.isAsc = isAsc;
	}

	public B2BDocumentQueryBuilder addCriteria(final String criteriaName, final Object criteriaValue)
	{
		criterias.put(criteriaName, criteriaValue);
		return this;
	}

	public AccountSummaryDocumentQuery build()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(currentPage);
		pageableData.setPageSize(pageSize);
		pageableData.setSort(sort);

		final AccountSummaryDocumentQuery query = new AccountSummaryDocumentQuery(pageableData, this.isAsc);

		for (final Entry<String, Object> criteriaEntry : criterias.entrySet())
		{
			query.addCriteria(criteriaEntry.getKey(), criteriaEntry.getValue());
		}

		return query;
	}

	public void addAllCriterias(final Map<String, Object> criterias)
	{
		this.criterias.putAll(criterias);
	}
}
