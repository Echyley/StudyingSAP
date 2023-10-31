/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.version;

import java.util.Collection;

import org.mockito.ArgumentMatcher;


public class ContainsAllArgumentMatcher<T> implements ArgumentMatcher<Collection>
{
	private T[] elems;

	public ContainsAllArgumentMatcher(T... elems) {
		this.elems = elems;
	}

	@Override
	public boolean matches(Collection collection)
	{
		return (collection.size() == elems.length && //
				containsAll(collection));
	}

	protected boolean containsAll(final Collection collection)
	{
		for (final T singleElem : elems)
		{
			if (!collection.contains(singleElem))
			{
				return false;
			}
		}
		return true;
	}
}
