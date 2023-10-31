/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.servicelayer.web.session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PersistedSessionTest
{

	private static final String SESSION_ID = "sessionId";
	private static final int CLUSTER_ID = 1;
	private static final String EXTENSION = "extension";
	private static final String CONTEXT_ROOT = "contextRoot";
	private static final int ZERO_SESSION_TIMEOUT = 0;
	private static final int NEGATIVE_SESSION_TIMEOUT = -20;
	private static final int POSITIVE_SESSION_TIMEOUT = 1;
	private static final int WAITING_TIME_1 = 200;
	private static final int WAITING_TIME_2 = 1100;
	private PersistedSession persistedSession;

	@Before
	public void setUp()
	{
		persistedSession = spy(new PersistedSession(SESSION_ID, CLUSTER_ID, EXTENSION, CONTEXT_ROOT));
	}

	@Test
	public void shouldSessionNotExpireWhenSessionTimeoutEqualZero()
	{
		//given
		doReturn(ZERO_SESSION_TIMEOUT).when(persistedSession).getMaxInactiveIntervalInSeconds();

		//when
		final boolean isExpired = persistedSession.isExpired();

		//then
		assertThat(isExpired).isFalse();
	}

	@Test
	public void shouldSessionNotExpireWhenSessionTimeoutLessThanZero()
	{
		//given
		doReturn(NEGATIVE_SESSION_TIMEOUT).when(persistedSession).getMaxInactiveIntervalInSeconds();

		//when
		final boolean isExpired = persistedSession.isExpired();

		//then
		assertThat(isExpired).isFalse();
	}

	@Test
	public void shouldSessionNotExpireWhenSessionTimeoutMoreThanZeroAndSessionTimeoutNotExceeded()
	{
		//given
		doReturn(POSITIVE_SESSION_TIMEOUT).when(persistedSession).getMaxInactiveIntervalInSeconds();

		//then
		await().pollDelay(WAITING_TIME_1, TimeUnit.MILLISECONDS)
		       .untilAsserted(() -> assertThat(persistedSession.isExpired()).isFalse());
	}

	@Test
	public void shouldSessionExpireWhenSessionTimeoutMoreThanZeroAndSessionTimeoutExceeded() throws InterruptedException
	{
		//given
		doReturn(POSITIVE_SESSION_TIMEOUT).when(persistedSession).getMaxInactiveIntervalInSeconds();

		//then
		await().pollDelay(WAITING_TIME_2, TimeUnit.MILLISECONDS)
		       .untilAsserted(() -> assertThat(persistedSession.isExpired()).isTrue());
	}

}
