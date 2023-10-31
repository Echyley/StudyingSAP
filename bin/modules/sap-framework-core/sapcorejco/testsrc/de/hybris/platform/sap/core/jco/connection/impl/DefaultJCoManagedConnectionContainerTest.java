/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;




/**
 * Test for DefaultJCoManagedConnectionContainerTest.
 */
@UnitTest
public class DefaultJCoManagedConnectionContainerTest
{

	@InjectMocks
	private DefaultJCoManagedConnectionContainer defaultJCoManagedConnectionContainer;
	
	@Mock
	private JCoConnection connection1;

	@Mock
	private JCoConnection connection2;

	@Before
	public void setup() throws Exception
	{
		
		MockitoAnnotations.initMocks(this);
		
	}

	/**
	 * Self-explanatory.
	 */
	@Test
	public void removeConnectionTest()
	{
		
		defaultJCoManagedConnectionContainer.managedConnections.put("conn1", connection1);
		defaultJCoManagedConnectionContainer.managedConnections.put("conn2", connection2);
		defaultJCoManagedConnectionContainer.removeConnection(connection1);
		Assert.assertEquals(defaultJCoManagedConnectionContainer.managedConnections.size(), 1);

	}

}
