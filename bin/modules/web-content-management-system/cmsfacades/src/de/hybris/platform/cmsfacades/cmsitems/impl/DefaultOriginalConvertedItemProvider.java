/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.impl;

import de.hybris.platform.cms2.common.service.SessionCachedContextProvider;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemContextProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Provider responsible for storing (in a stack-like data structure) context information per transaction.
 * It contains map representation of the item model.
 */
public class DefaultOriginalConvertedItemProvider implements CMSItemContextProvider<Map<String, Object>>
{
	private SessionCachedContextProvider sessionCachedContextProvider;
	private static final String SESSION_ORIGINAL_CONVERTED_ITEM = "sessionOriginalConvertedItem";

	@Override
	public void initializeItem(final Map<String, Object> item)
	{
		getSessionCachedContextProvider().addItemToListCache(SESSION_ORIGINAL_CONVERTED_ITEM, item);
	}

	@Override
	public Map<String, Object> getCurrentItem()
	{
		final List<Object> originalItemSet = getSessionCachedContextProvider().getAllItemsFromListCache(SESSION_ORIGINAL_CONVERTED_ITEM);
		if (originalItemSet.isEmpty())
		{
			throw new IllegalStateException("There is no current item model. Please Initialize with #initializeItem before using this method.");
		}
		Optional<Object> optionalItem =
				originalItemSet.stream().skip((long)originalItemSet.size() - 1).findFirst();
		if(optionalItem.isPresent()){
			return (Map<String, Object>) optionalItem.get();
		}else{
			return Collections.emptyMap();
		}
	}

	@Override
	public void finalizeItem()
	{
		final List<Object> originalItemSet = getSessionCachedContextProvider().getAllItemsFromListCache(SESSION_ORIGINAL_CONVERTED_ITEM);
		if (!originalItemSet.isEmpty())
		{
			originalItemSet.remove((originalItemSet.size() - 1));
		}
	}

	protected SessionCachedContextProvider getSessionCachedContextProvider()
	{
		return sessionCachedContextProvider;
	}

	@Required
	public void setSessionCachedContextProvider(
			final SessionCachedContextProvider sessionCachedContextProvider)
	{
		this.sessionCachedContextProvider = sessionCachedContextProvider;
	}
}
