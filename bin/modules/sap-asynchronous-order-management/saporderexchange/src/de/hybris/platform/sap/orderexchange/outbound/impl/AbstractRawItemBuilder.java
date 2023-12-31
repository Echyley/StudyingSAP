/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.sap.orderexchange.outbound.RawItemBuilder;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.servicelayer.model.AbstractItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;



/**
 * Default raw item builder delegating the creation of the individual lines of the raw item to the registered instances
 * of {@link RawItemContributor}. The results are merged into one list. Fields not provided by all contributors are
 * defaulted to ""
 *
 * @param <T> The item model for which the raw item shall be assembled
 */
public abstract class AbstractRawItemBuilder<T extends AbstractItemModel> implements RawItemBuilder<T>
{
	private List<RawItemContributor<T>> contributors = Collections.emptyList();
	private final Set<String> columns = new ConcurrentSkipListSet<>();

	@Override
	public List<RawItemContributor<T>> getContributors()
	{
		return contributors;
	}

	@Override
	public Set<String> getColumns()
	{
		if (columns.isEmpty())
		{
			for (final RawItemContributor<T> contributor : getContributors())
			{
				columns.addAll(contributor.getColumns());
			}
		}
		return columns;
	}

	@Override
	public void setContributors(final List<RawItemContributor<T>> contributors)
	{
		this.contributors = contributors;
	}

	@Override
	public List<Map<String, Object>> rowsAsNameValuePairs(final T model)
	{

		final List<Map<String, Object>> allRowsAsNameValue = new ArrayList<>();
		final Set<String> columnSet = getColumns();
		contributors.forEach(contributor -> contributor.createRows(model).forEach(row -> {
			columnSet.forEach(column -> row.putIfAbsent(column, ""));
			allRowsAsNameValue.add(row);
		}));

		if (isDebug())
		{
			dumpRowsToLogger(allRowsAsNameValue);
		}
		return allRowsAsNameValue;
	}

	private void dumpRowsToLogger(final List<Map<String, Object>> allRowsAsNameValue)
	{
		final Logger logger = getLogger();
		logger.debug("Created name/value pairs from " + contributors.size() + " contributors");
		for (final Map<String, Object> row : allRowsAsNameValue)
		{
			logger.debug("Row:");
			for (final Entry<String, Object> entry : row.entrySet())
			{
				final Object value = entry.getValue();
				if (value == null || value instanceof String && ((String) value).isEmpty())
				{
					continue;
				}
				logger.debug(entry.getKey() + "=" + value);
			}
		}
	}

	@Override
	public void addContributor(final RawItemContributor<T> c)
	{
		this.contributors.add(c);
	}

	protected boolean isDebug()
	{
		return getLogger().isDebugEnabled();
	}

	protected abstract Logger getLogger();

}
