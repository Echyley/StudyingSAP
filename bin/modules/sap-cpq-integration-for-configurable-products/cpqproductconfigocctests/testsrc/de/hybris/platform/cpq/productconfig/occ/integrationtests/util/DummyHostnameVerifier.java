/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.integrationtests.util;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


/**
 * dummy class for tests
 */
public class DummyHostnameVerifier implements HostnameVerifier
{
	@Override
	public boolean verify(final String hostname, final SSLSession session)
	{
		return true;
	}
}
