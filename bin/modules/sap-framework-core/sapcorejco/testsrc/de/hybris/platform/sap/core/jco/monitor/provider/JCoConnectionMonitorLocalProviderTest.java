/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.monitor.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.platform.sap.core.jco.monitor.JCoMonitorException;
import de.hybris.platform.sap.core.jco.test.SapcoreJCoJUnitTest;


/**
 * Test class for JCoConnectionMonitorLocalProviderTest.
 */
@ContextConfiguration(locations =
{ "../JCoConnectionMonitorTest-spring.xml" })
@SuppressWarnings("javadoc")
public class JCoConnectionMonitorLocalProviderTest extends SapcoreJCoJUnitTest
{

	@Resource(name = "sapCoreJCoConnectionMonitorLocalProvider")
	private JCoConnectionMonitorLocalProvider classUnderTest;
	
	@Resource(name = "sapCoreJCoConnectionMonitorLocalContext")
	private JCoConnectionMonitorTestContext localContext;
	
	@Override
	public void setUp() {
		classUnderTest.setLocalContext(localContext);
	}
	@Test
	public void testGetOverview() throws JCoMonitorException, IOException
	{
		final String overview = classUnderTest.createSnapshotXML();
		assertNotNull(overview);
	}
	
	@Test
	public void testCreateSnapshotFile() throws Exception {
		String expectedMessage = " has successfully been generated!";
		String message = classUnderTest.createSnapshotFile();
		assertTrue(message.contains(expectedMessage));
	}

	@Test
	public void testGetTotalCount() throws Exception {
		assertEquals(7, classUnderTest.getTotalCount().intValue());
	}
	
	@Test
	public void testGetLongLifetimeCount() throws Exception {
		assertEquals(2, classUnderTest.getLongRunnerCount().intValue());
	}
	
	@Test
	public void testGetPoolLimitReached() throws Exception {
		assertEquals(1, classUnderTest.getPoolLimitReachedCount().intValue());
	}
}
