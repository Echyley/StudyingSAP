/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSession;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;



/**
 * Unit tests for {@link SessionAccessServiceImpl}
 */
@UnitTest
public class SessionAccessServiceImplTest
{
	private static final Logger LOGGER = Logger.getLogger(SessionAccessServiceImplTest.class);
	private static final String SESSION_ID = "123";
	private long startTime;
	private long firstThreadTime;
	private long secondThreadTime;
	private static final String configId = "1";

	SessionAccessServiceImpl classUnderTest = new SessionAccessServiceImpl();
	private final Session session2 = new DefaultSession();
	private final ProductConfigSessionAttributeContainer sessionContainer = new ProductConfigSessionAttributeContainer();

	private static final Object FIRST_TEST_THREAD_BLOCKED = new Object();
	private static final Object SECOND_TEST_THREAD_BLOCKED_OR_FINISHED = new Object();
	private static final long MAX_WAIT_TIME = 1000;

	private class SessionAccessThread extends Thread
	{
		long endTime = 0;

		@Override
		public void run()
		{
			classUnderTest.retrieveSessionAttributeContainer();
			endTime = System.currentTimeMillis();
			LOGGER.info("Thread terminated after: " + (endTime - startTime) + "ms!");
		}
	}

	@Mock
	private SessionService mockSessionService;
	@Mock
	private Session mockSession;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(mockSession.getSessionId()).thenReturn(SESSION_ID);
		when(mockSessionService.getCurrentSession()).thenReturn(mockSession);
		when(mockSessionService.getAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(sessionContainer);

		classUnderTest.setSessionService(mockSessionService);
	}

	@Test
	public void testCartEntryConfigId()
	{
		final String configId = "1";
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(configId, classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testUIStatus()
	{
		final String cartEntryKey = "X";
		final Object status = "S";
		classUnderTest.setUiStatusForCartEntry(cartEntryKey, status);
		assertEquals(status, classUnderTest.getUiStatusForCartEntry(cartEntryKey));
		classUnderTest.removeUiStatusForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getUiStatusForCartEntry(cartEntryKey));
	}

	@Test
	public void testUIStatusProduct()
	{
		final String productKey = "X";
		final Object status = "S";
		classUnderTest.setUiStatusForProduct(productKey, status);
		assertEquals(status, classUnderTest.getUiStatusForProduct(productKey));
		classUnderTest.removeUiStatusForProduct(productKey);
		assertNull(classUnderTest.getUiStatusForProduct(productKey));
	}

	@Test
	public void testConfigIdForCartEntry()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(cartEntryKey, classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testConfigIdForCartEntryEnsureLastAssignmentWins()
	{
		final String cartEntryKey = "X";
		final String cartEntryKeySecond = "Y";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKeySecond, configId);
		assertEquals(cartEntryKeySecond, classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testConfigIdForCartEntryEnsureFirstAssignmentGone()
	{
		final String cartEntryKey = "X";
		final String cartEntryKeySecond = "Y";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKeySecond, configId);
		assertNull(classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testRemoveConfigIdForCartEntry()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeConfigIdForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryDraftConfigRemoved()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		classUnderTest.setDraftConfigIdForCartEntry(cartEntryId, configId);
		classUnderTest.setConfigIdForProduct(productKey, configId);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryId);
		//We expect that the corresponding product/cartEntry entry is gone!
		assertNull(classUnderTest.getConfigIdForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryConfigMap()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryConfigDraftMap()
	{
		final String cartEntryKey = "X";
		classUnderTest.setDraftConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getDraftConfigIdForCartEntry(cartEntryKey));
	}


	@Test
	public void testRetrieveSessionAttributeContainerThreadsWait() throws InterruptedException
	{
		delaySynchronizedCalls();

		final SessionAccessThread firstThread = new SessionAccessThread();
		synchronized (FIRST_TEST_THREAD_BLOCKED)
		{
			firstThread.start();
			FIRST_TEST_THREAD_BLOCKED.wait(MAX_WAIT_TIME);
		}
		final SessionAccessThread secondThread = new SessionAccessThread();
		secondThread.start();
		synchronized (SECOND_TEST_THREAD_BLOCKED_OR_FINISHED)
		{
			// wait until second thread is in state BLOCKED waiting to retrieve the session attribute,
			// the let the first thread continue			 
			Awaitility.await().until(() -> State.BLOCKED.equals(secondThread.getState()));
			SECOND_TEST_THREAD_BLOCKED_OR_FINISHED.notify();
		}

		waitForBothThreadsFinished(firstThread, secondThread);
		final String errorMsg = String.format(
				"Second thread was expected to finish NOT earlier as it does need to wait: firstThreadTime=%d, secondThreadTime=%d",
				firstThreadTime, secondThreadTime);
		assertTrue(errorMsg, secondThreadTime > firstThreadTime);
		LOGGER.info("Second thread is later by: " + (secondThreadTime - firstThreadTime));
	}

	@Test
	public void testRetrieveSessionAttributeContainerThreadsDontWait() throws InterruptedException
	{
		delaySynchronizedCalls();

		final SessionAccessThread firstThread = new SessionAccessThread();
		synchronized (FIRST_TEST_THREAD_BLOCKED)
		{
			// start the thread, wait until it enters the sycronized block and notifies us.
			firstThread.start();
			FIRST_TEST_THREAD_BLOCKED.wait(MAX_WAIT_TIME);
			//Now simulate a different user session so that threads don't need to wait for each other
			when(mockSessionService.getCurrentSession()).thenReturn(session2);
		}

		// start the second thread, wait until it finishes and notify the first thread
		final SessionAccessThread secondThread = new SessionAccessThread();
		secondThread.start();
		secondThread.join(MAX_WAIT_TIME);
		synchronized (SECOND_TEST_THREAD_BLOCKED_OR_FINISHED)
		{
			SECOND_TEST_THREAD_BLOCKED_OR_FINISHED.notify();
		}

		waitForBothThreadsFinished(firstThread, secondThread);
		final String errorMsg = String.format(
				"Second thread was expected to finish earlier as it does NOT need to wait: firstThreadTime=%d, secondThreadTime=%d",
				firstThreadTime, secondThreadTime);
		assertTrue(errorMsg, secondThreadTime < firstThreadTime);

	}

	protected void waitForBothThreadsFinished(final SessionAccessThread firstThread, final SessionAccessThread secondThread)
			throws InterruptedException
	{
		firstThread.join(MAX_WAIT_TIME);
		secondThread.join(MAX_WAIT_TIME);
		assertEquals("First Thread did not terminate!", State.TERMINATED, firstThread.getState());
		assertEquals("Second Thread did not terminate!", State.TERMINATED, secondThread.getState());
		firstThreadTime = firstThread.endTime - startTime;
		secondThreadTime = secondThread.endTime - startTime;
		LOGGER.info("first Thread took: " + firstThreadTime + "ms, second thread took: " + secondThreadTime + "ms");
	}

	protected void delaySynchronizedCalls()
	{
		startTime = System.currentTimeMillis();
		delayNextCall(true);
	}

	protected void delayNextCall(final boolean delay)
	{
		doAnswer(new Answer()
		{
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable
			{
				if (delay)
				{
					synchronized (FIRST_TEST_THREAD_BLOCKED)
					{
						// make sure second thread does not wait
						delayNextCall(false);
						FIRST_TEST_THREAD_BLOCKED.notify();
					}
					synchronized (SECOND_TEST_THREAD_BLOCKED_OR_FINISHED)
					{
						SECOND_TEST_THREAD_BLOCKED_OR_FINISHED.wait(MAX_WAIT_TIME);
					}
				}

				final long start = System.currentTimeMillis();
				Awaitility.await().until(() -> System.currentTimeMillis() > start + 1);
				return null;
			}
		}).when(mockSessionService).getAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
	}


	@Test
	public void testPurge()
	{
		classUnderTest.purge();
		verify(mockSessionService).setAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, null);
	}

	@Test
	public void testLinkWithProductCode()
	{
		final String pCode = "123";
		assertNull(classUnderTest.getConfigIdForProduct(pCode));
		classUnderTest.setConfigIdForProduct(pCode, configId);
		assertEquals(configId, classUnderTest.getConfigIdForProduct(pCode));
	}

	@Test
	public void testLinkWithProductCodeIsCleanedUp()
	{
		final String pCode = "123";
		classUnderTest.setConfigIdForProduct(pCode, configId);
		classUnderTest.removeConfigIdForProduct(pCode);
		assertNull(classUnderTest.getConfigIdForProduct(pCode));
	}

	@Test
	public void testGetProductForConfigId()
	{
		final String pCode = "123";
		classUnderTest.setConfigIdForProduct(pCode, configId);
		final String result = classUnderTest.getProductForConfigId(configId);
		assertNotNull(result);
		assertEquals(pCode, result);
	}

	@Test
	public void testGetProductForConfigIdNoProduct()
	{
		assertNull(classUnderTest.getProductForConfigId(configId));
	}

	@Test
	public void testGetProductForConfigIdNull()
	{
		//fill at least one entry into the cache
		final String pCode = "123";
		classUnderTest.setConfigIdForProduct(pCode, configId);
		assertNull(classUnderTest.getProductForConfigId(null));
	}

	@Test
	public void testFindInMapNullEntriesWork()
	{
		final Map<String, String> map = new HashMap<>();
		map.put("cart entry key", null);
		final List<String> result = classUnderTest.findConfigIdInMap(configId, map);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetCartEntryForDraftConfigIdReturnsNull()
	{
		assertNull(classUnderTest.getCartEntryForDraftConfigId(configId));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCartEntryForDraftConfigIdThrowsExpection()
	{
		final Map<String, String> cartEntryDraftConfigurations = new HashMap<>();
		final String configId_1 = "config_1";
		cartEntryDraftConfigurations.put("key_1", configId_1);
		cartEntryDraftConfigurations.put("key_2", configId_1);
		cartEntryDraftConfigurations.put("key_3", "config_2");
		sessionContainer.setCartEntryDraftConfigurations(cartEntryDraftConfigurations);

		classUnderTest.getCartEntryForDraftConfigId(configId_1);
	}

	@Test
	public void testGetCartEntryForDraftConfigId()
	{
		final Map<String, String> cartEntryDraftConfigurations = new HashMap<>();
		final String configId_2 = "config_2";
		final String key_2 = "key_2";
		cartEntryDraftConfigurations.put("key_1", "config_1");
		cartEntryDraftConfigurations.put(key_2, configId_2);
		cartEntryDraftConfigurations.put("key_3", "config_3");
		sessionContainer.setCartEntryDraftConfigurations(cartEntryDraftConfigurations);

		assertEquals(key_2, classUnderTest.getCartEntryForDraftConfigId(configId_2));
	}
}
