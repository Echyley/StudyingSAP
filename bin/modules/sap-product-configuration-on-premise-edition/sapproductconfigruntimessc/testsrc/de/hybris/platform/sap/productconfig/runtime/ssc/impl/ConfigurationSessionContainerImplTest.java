/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


@UnitTest
public class ConfigurationSessionContainerImplTest
{
	ConfigurationSessionContainerImpl classUnderTest = new ConfigurationSessionContainerImpl();
	private static final String qualifiedId = "A";
	private static final String qualifiedId2 = "B";
	private static final String qualifiedId3 = "C";
	private static final String qualifiedId4 = "D";
	private static final String qualifiedId5 = "E";
	private static final String qualifiedId6 = "F";
	private static final String qualifiedId7 = "G";
	private static final String qualifiedId8 = "H";
	private static final String qualifiedId9 = "I";
	private static final String qualifiedId10 = "J";
	private static final String qualifiedId11 = "K";

	@Mock
	private IConfigSession configSession;
	@Mock
	private IConfigSession configSession2;
	@Mock
	private IConfigSession configSession3;
	@Mock
	private IConfigSession configSession4;
	@Mock
	private IConfigSession configSession5;
	@Mock
	private IConfigSession configSession6;
	@Mock
	private IConfigSession configSession7;
	@Mock
	private IConfigSession configSession8;
	@Mock
	private IConfigSession configSession9;
	@Mock
	private IConfigSession configSession10;
	@Mock
	private IConfigSession configSession11;

	@Before
	public void setUp()
	{
		final ConfigurationSessionContainerImpl classUnderTest = new ConfigurationSessionContainerImpl();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testStore()
	{
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		assertEquals(configSession, classUnderTest.retrieveConfigSession(qualifiedId));
	}

	@Test(expected = IllegalStateException.class)
	public void testReleaseSession()
	{
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		classUnderTest.releaseSession(qualifiedId);
		classUnderTest.retrieveConfigSession(qualifiedId);
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveEmptyMap()
	{
		classUnderTest.retrieveConfigSession(qualifiedId);
	}

	@Test
	public void testStackTrace()
	{
		final StringBuilder topLinesOfStacktrace = classUnderTest.getTopLinesOfStacktrace(5);
		assertNotNull(topLinesOfStacktrace);
		assertTrue(topLinesOfStacktrace.toString().length() > 0);
		assertFalse("We don't want to see the first two entries", topLinesOfStacktrace.toString().contains("java.lang.Thread"));
		System.out.println(topLinesOfStacktrace);
	}

	@Test
	public void testReleaseSession_isClosed()
	{
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		classUnderTest.releaseSession(qualifiedId);
		verify(configSession).closeSession();
	}

	@Test
	public void testReleaseSession_noError()
	{
		classUnderTest.releaseSession(qualifiedId); //session is not known
		verifyNoInteractions(configSession);
	}

	@Test
	public void testStoreManyConfigurations() throws InterruptedException
	{
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId2, configSession2);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId3, configSession3);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId4, configSession4);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId5, configSession5);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId6, configSession6);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId7, configSession7);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId8, configSession8);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId9, configSession9);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId10, configSession10);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId11, configSession11);

		assertEquals(configSession, classUnderTest.retrieveConfigSession(qualifiedId));
		assertEquals(configSession2, classUnderTest.retrieveConfigSession(qualifiedId2));
		assertEquals(configSession3, classUnderTest.retrieveConfigSession(qualifiedId3));
		assertEquals(configSession4, classUnderTest.retrieveConfigSession(qualifiedId4));
		assertEquals(configSession5, classUnderTest.retrieveConfigSession(qualifiedId5));
		assertEquals(configSession6, classUnderTest.retrieveConfigSession(qualifiedId6));
		assertEquals(configSession7, classUnderTest.retrieveConfigSession(qualifiedId7));
		assertEquals(configSession8, classUnderTest.retrieveConfigSession(qualifiedId8));
		assertEquals(configSession9, classUnderTest.retrieveConfigSession(qualifiedId9));
		assertEquals(configSession10, classUnderTest.retrieveConfigSession(qualifiedId10));
		assertEquals(configSession11, classUnderTest.retrieveConfigSession(qualifiedId11));
	}

	@Test
	public void testAllowOneConfiguration() throws InterruptedException
	{
		classUnderTest.setMaxNumberOfConfigurations(1);
		classUnderTest.storeConfiguration(qualifiedId6, configSession6);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId9, configSession9);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId2, configSession2);

		try
		{
			classUnderTest.retrieveConfigSession(qualifiedId6);
			fail("Expected an IllegalStateException to be thrown");
		}
		catch (final IllegalStateException e)
		{
		}
		verify(configSession6).closeSession();

		try
		{
			classUnderTest.retrieveConfigSession(qualifiedId9);
			fail("Expected an IllegalStateException to be thrown");
		}
		catch (final IllegalStateException e)
		{
		}
		verify(configSession9).closeSession();

		assertEquals(configSession2, classUnderTest.retrieveConfigSession(qualifiedId2));
	}

	@Test
	public void testAllowTwoConfiguration() throws InterruptedException
	{
		classUnderTest.setMaxNumberOfConfigurations(2);
		classUnderTest.storeConfiguration(qualifiedId6, configSession6);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId9, configSession9);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId2, configSession2);

		try
		{
			classUnderTest.retrieveConfigSession(qualifiedId6);
			fail("Expected an IllegalStateException to be thrown");
		}
		catch (final IllegalStateException e)
		{
		}
		verify(configSession6).closeSession();

		assertEquals(configSession9, classUnderTest.retrieveConfigSession(qualifiedId9));
		assertEquals(configSession2, classUnderTest.retrieveConfigSession(qualifiedId2));
	}

	@Test
	public void testStoreMoreThanMaxNumberOfSessions() throws InterruptedException
	{
		classUnderTest.setMaxNumberOfConfigurations(5);
		classUnderTest.storeConfiguration(qualifiedId6, configSession6);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId9, configSession9);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId2, configSession2);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId7, configSession7);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId4, configSession4);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId5, configSession5);

		try
		{
			classUnderTest.retrieveConfigSession(qualifiedId6);
			fail("Expected an IllegalStateException to be thrown");
		}
		catch (final IllegalStateException e)
		{
		}
		verify(configSession6).closeSession();

		assertEquals(configSession9, classUnderTest.retrieveConfigSession(qualifiedId9));
		assertEquals(configSession2, classUnderTest.retrieveConfigSession(qualifiedId2));
		assertEquals(configSession7, classUnderTest.retrieveConfigSession(qualifiedId7));
		assertEquals(configSession4, classUnderTest.retrieveConfigSession(qualifiedId4));
		assertEquals(configSession5, classUnderTest.retrieveConfigSession(qualifiedId5));
	}

	@Test
	public void testStoreMoreThanMaxNumberOfSessions_moreSessions() throws InterruptedException
	{
		classUnderTest.setMaxNumberOfConfigurations(7);
		classUnderTest.storeConfiguration(qualifiedId6, configSession6);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId9, configSession9);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId2, configSession2);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId7, configSession7);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId4, configSession4);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId5, configSession5);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId, configSession);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId3, configSession3);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId11, configSession11);
		Thread.sleep(5);
		classUnderTest.storeConfiguration(qualifiedId8, configSession8);

		try
		{
			classUnderTest.retrieveConfigSession(qualifiedId6);
			fail("Expected an IllegalStateException to be thrown");
		}
		catch (final IllegalStateException e)
		{
		}
		verify(configSession6).closeSession();

		try
		{
			classUnderTest.retrieveConfigSession(qualifiedId9);
			fail("Expected an IllegalStateException to be thrown");
		}
		catch (final IllegalStateException e)
		{
		}
		verify(configSession9).closeSession();

		try
		{
			classUnderTest.retrieveConfigSession(qualifiedId2);
			fail("Expected an IllegalStateException to be thrown");
		}
		catch (final IllegalStateException e)
		{
		}
		verify(configSession2).closeSession();

		assertEquals(configSession7, classUnderTest.retrieveConfigSession(qualifiedId7));
		assertEquals(configSession4, classUnderTest.retrieveConfigSession(qualifiedId4));
		assertEquals(configSession5, classUnderTest.retrieveConfigSession(qualifiedId5));
		assertEquals(configSession, classUnderTest.retrieveConfigSession(qualifiedId));
		assertEquals(configSession3, classUnderTest.retrieveConfigSession(qualifiedId3));
		assertEquals(configSession11, classUnderTest.retrieveConfigSession(qualifiedId11));
		assertEquals(configSession8, classUnderTest.retrieveConfigSession(qualifiedId8));
	}

}