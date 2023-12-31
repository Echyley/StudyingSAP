/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.apiregistryservices.dao.CredentialDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


public class DefaultCredentialDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private CredentialDao credentialDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/apis.impex", "UTF-8");
	}

	@Test
	public void getDestinationsByChannel() throws Exception
	{
		List destinationsByChannel = credentialDao.getAllExposedOAuthCredentialsByClientId("kyma1");
		assertEquals(1, destinationsByChannel.size());
		destinationsByChannel = credentialDao.getAllExposedOAuthCredentialsByClientId("kyma");
		assertEquals(2, destinationsByChannel.size());
		destinationsByChannel = credentialDao.getAllExposedOAuthCredentialsByClientId("not_exist");
		assertTrue(CollectionUtils.isEmpty(destinationsByChannel));
	}
}
