/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocc.util;

public interface SearchQueryCodec<QUERY>
{
	QUERY decodeQuery(String query);

	String encodeQuery(QUERY query);
}
