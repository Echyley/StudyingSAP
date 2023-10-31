/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.search;

import de.hybris.platform.core.model.ItemModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Represents the search result from the {@link RootItemSearchService}
 */
@Immutable
public final class RootItemSearchResult
{
	/**
	 * Represents a {@link RootItemSearchResult} having no object in result.
	 */
	public static final RootItemSearchResult EMPTY_RESULT = new RootItemSearchResult();

	private final Collection<Object> refPathExecutionResult;

	private RootItemSearchResult()
	{
		this(Collections.emptyList());
	}

	private RootItemSearchResult(final Collection<Object> refPathExecutionResult)
	{
		this.refPathExecutionResult = refPathExecutionResult;
	}

	/**
	 * Creates an instance of {@link RootItemSearchResult} from given collection of reference path execution.
	 *
	 * @param refPathExecutionResult collection of objects after execution of reference path to root
	 * @return an instance from given collection or {@link #EMPTY_RESULT} if collection is null, or empty.
	 */
	public static RootItemSearchResult createFrom(final Collection<Object> refPathExecutionResult)
	{
		return CollectionUtils.isNotEmpty(refPathExecutionResult) ?
				new RootItemSearchResult(new ArrayList<>(refPathExecutionResult)) : EMPTY_RESULT;
	}

	/**
	 * Indicates whether there is any object in reference path execution
	 *
	 * @return {@code true} if there is an object, else {@code false}
	 */
	public boolean hasAnyObjectInRefPathExecutionResult()
	{
		return CollectionUtils.isNotEmpty(refPathExecutionResult);
	}

	/**
	 * Get the root {@link ItemModel}s
	 *
	 * @return Collection of root {@link ItemModel}s, or an empty collection
	 */
	public Collection<ItemModel> getRootItems()
	{
		return refPathExecutionResult.stream()
		                             .filter(ItemModel.class::isInstance)
		                             .map(ItemModel.class::cast)
		                             .collect(Collectors.toList());
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o) return true;
		if (!(o instanceof RootItemSearchResult)) return false;
		final RootItemSearchResult that = (RootItemSearchResult) o;
		return Objects.equals(refPathExecutionResult, that.refPathExecutionResult);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(refPathExecutionResult);
	}

	@Override
	public String toString()
	{
		return "RootItemSearchResult{" +
				"refPathExecutionResult=" + refPathExecutionResult +
				'}';
	}
}
