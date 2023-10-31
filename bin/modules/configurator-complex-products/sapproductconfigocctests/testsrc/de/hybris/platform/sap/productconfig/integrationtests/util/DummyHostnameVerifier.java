/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integrationtests.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


public class DummyHostnameVerifier implements HostnameVerifier
{
	@Override
	public boolean verify(final String hostname, final SSLSession session)
	{
		return true;
	}
}
