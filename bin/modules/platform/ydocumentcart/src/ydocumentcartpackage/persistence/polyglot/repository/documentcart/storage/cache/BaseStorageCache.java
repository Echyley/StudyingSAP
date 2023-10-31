/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.cache;

import de.hybris.platform.persistence.polyglot.model.Identity;

import java.util.Optional;

import ydocumentcartpackage.persistence.polyglot.repository.documentcart.Document;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.Query;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.QueryResult;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.Storage;

public abstract class BaseStorageCache implements StorageCache
{

	@Override
	public QueryResult findInCache(final Query query, final LoadFromStorageStrategy targetFunction)
	{
		final boolean findInStorageFirst = isFindInStorageFirst(query);
		if (isCacheActive() && !findInStorageFirst)
		{
			final Optional<QueryResult> cachedResult = findInCache(query);

			if (cachedResult.isPresent())
			{
				return cachedResult.get();
			}
		}

		final QueryResult queryResult = findInStorage(query, targetFunction);

		cacheDocuments(queryResult);

		if (isCacheActive() && findInStorageFirst)
		{
			return findInCache(query).orElse(QueryResult.empty());
		}

		return queryResult;
	}

	/**
	 * Method to verify whether a query should be called to the storage first
	 *
	 * @param query the query used to find documents
	 * @return <code>true</code> if query should be called to the storage first
	 */
	protected abstract boolean isFindInStorageFirst(final Query query);

	protected abstract void cacheDocuments(final QueryResult queryResult);

	protected abstract QueryResult findInStorage(final Query baseQuery, final LoadFromStorageStrategy targetFunction);

	/**
	 * Method to find a query result in cache. Is called only if {@see #isCacheActive} returns <code>true</code>
	 *
	 * @param query the query used to find documents
	 * @return <code>Optional.empty()</code> if no documents has been found in cache that meets the query, otherwise an optional
	 * of {@see QueryResult}
	 */
	protected abstract Optional<QueryResult> findInCache(Query query);

	@Override
	public CacheContext initCacheContext(final Storage storage)
	{
		return createCacheContext(storage, getFlushAction());
	}

	protected abstract CacheFlushAction getFlushAction();

	protected abstract CacheContext createCacheContext(Storage storage,
	                                                   CacheFlushAction flushAction);

	protected interface CacheFlushAction
	{
		void flushCacheEntry(Storage storage, Identity identity, DocumentCacheEntry entry);
	}

	static class DocumentCacheEntry
	{
		private final Document document;
		private boolean removed;
		private boolean dirty;

		DocumentCacheEntry(final Document document)
		{
			this(document, false);
		}

		DocumentCacheEntry(final Document document, final boolean dirty)
		{
			this.document = document;
			this.dirty = dirty;
		}

		void markAsRemoved()
		{
			removed = true;
			dirty = true;
		}

		Document getDocument()
		{
			return document;
		}

		boolean isRemoved()
		{
			return removed;
		}

		boolean isDirty()
		{
			return dirty;
		}
	}
}
