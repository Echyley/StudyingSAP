/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.dataloader.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DataloaderSourceParametersTest
{

	private static final String CLIENT = "001";
	private static final String DESTINATION = "refcDest";
	private static final String INSTANCE_NO = "7";
	private static final String LOGON_GROUP = "PUBLIC";
	private static final String MSG_SERVER = "msgServer.wdf.sap.corp";
	private static final String INITIAL = "initial123";
	private static final String SERVER_RFC_DEST = "serverRfcDEst";
	private static final String SYSTEM_ID = "50";
	private static final String HOST = "host.wdf.sap.corp";
	private static final String USER = "LASTNAME";
	private DataloaderSourceParameters classUndertest;

	@Before
	public void setUp()
	{
		classUndertest = new DataloaderSourceParameters();
	}

	@Test
	public void testGetterSetter()
	{
		classUndertest.setClient(CLIENT);
		classUndertest.setClientRfcDestination(DESTINATION);
		classUndertest.setInstanceno(INSTANCE_NO);
		classUndertest.setLogonGroup(LOGON_GROUP);
		classUndertest.setMsgServer(MSG_SERVER);
		classUndertest.setPassword(INITIAL);
		classUndertest.setServerRfcDestination(SERVER_RFC_DEST);
		classUndertest.setSysId(SYSTEM_ID);
		classUndertest.setTargetHost(HOST);
		classUndertest.setUseLoadBalance(true);
		classUndertest.setUser(USER);

		assertEquals(CLIENT, classUndertest.getClient());
		assertEquals(DESTINATION, classUndertest.getClientRfcDestination());
		assertEquals(INSTANCE_NO, classUndertest.getInstanceno());
		assertEquals(LOGON_GROUP, classUndertest.getLogonGroup());
		assertEquals(MSG_SERVER, classUndertest.getMsgServer());
		assertEquals(INITIAL, classUndertest.getPassword());
		assertEquals(SERVER_RFC_DEST, classUndertest.getServerRfcDestination());
		assertEquals(SYSTEM_ID, classUndertest.getSysId());
		assertEquals(HOST, classUndertest.getTargetHost());
		assertTrue(classUndertest.isUseLoadBalance());
		assertEquals(USER, classUndertest.getUser());

	}
}
