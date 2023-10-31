/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.xyformsfacades.utils;

import javax.xml.namespace.NamespaceContext;

import java.util.Iterator;


public class YFormNamespaceContext implements NamespaceContext
{
	@Override
	public String getNamespaceURI(final String prefix)
	{
		switch (prefix) {
			case "xh":
				return "http://www.w3.org/1999/xhtml";
			case "xf":
				return "http://www.w3.org/2002/xforms";
			default:
				return null;
		}
	}

	@Override
	public String getPrefix(final String namespaceURI)
	{
		return null;
	}

	@Override
	public Iterator<String> getPrefixes(final String namespaceURI)
	{
		return null;
	}
}
