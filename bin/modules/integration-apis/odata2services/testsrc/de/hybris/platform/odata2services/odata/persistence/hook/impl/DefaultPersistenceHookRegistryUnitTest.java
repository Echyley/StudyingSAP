/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPersistenceHookRegistryUnitTest
{
	private static final String INTEGRATION_KEY = "integrationKey|Value";

	@InjectMocks
	private DefaultPersistenceHookRegistry registry;
	@Mock(lenient = true)
	private ApplicationContext context;

	@Before
	public void setUp()
	{
		registry = new DefaultPersistenceHookRegistry();
	}

	@Test
	public void testSetApplicationContext()
	{
		doReturn(ImmutableMap.of("hook", mock(PrePersistHook.class))).when(context).getBeansOfType(PrePersistHook.class);
		doReturn(ImmutableMap.of("hook", mock(PostPersistHook.class))).when(context).getBeansOfType(PostPersistHook.class);

		registry.setApplicationContext(context);

		assertThat(registry.getPrePersistHook("hook", INTEGRATION_KEY))
				.isNotNull()
				.isInstanceOf(PrePersistHook.class);
		assertThat(registry.getPostPersistHook("hook", INTEGRATION_KEY))
				.isNotNull()
				.isInstanceOf(PostPersistHook.class);
	}

	@Test
	public void testGetPrePersistHookIgnoresNullNames()
	{
		assertThat(registry.getPrePersistHook(null, INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testGetPrePersistHookIgnoresEmptyNames()
	{
		assertThat(registry.getPrePersistHook("", INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testGetPrePersistHookReturnsNoHookWhenPrePersistHookNameIsNull() {
		final var context = mock(PersistenceContext.class);
		doReturn(null).when(context).getPrePersistHook();

		final var hook = registry.getPrePersistHook(context);

		assertThat(hook).isEmpty();
	}

	@Test
	public void testGetPrePersistHookReturnsNoHookWhenHookWithTheContextNameNotFound() {
		final var context = mock(PersistenceContext.class);
		doReturn("hook not existing in the registry").when(context).getPrePersistHook();

		final var hook = registry.getPrePersistHook(context);

		assertThat(hook).isEmpty();
	}

	@Test
	public void testGetPrePersistHookReturnsHookWhenHookWithTheContextNameFound() {
		final var name = "existing";
		final var hook = mock(PrePersistHook.class);
		final var context = mock(PersistenceContext.class);
		doReturn(name).when(context).getPrePersistHook();
		registry.addHook(name, hook);

		final var preHook = registry.getPrePersistHook(context);

		assertThat(preHook).isNotEmpty();
	}

	@Test
	public void testGetPostPersistHookIgnoresNullNames()
	{
		assertThat(registry.getPostPersistHook(null, INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testGetPostPersistHookIgnoresEmptyNames()
	{
		assertThat(registry.getPostPersistHook("", INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testGetPostPersistHookReturnsNoHookWhenPostPersistHookNameIsNull() {
		final var context = mock(PersistenceContext.class);
		doReturn(null).when(context).getPostPersistHook();

		final var hook = registry.getPostPersistHook(context);

		assertThat(hook).isEmpty();
	}

	@Test
	public void testGetPostPersistHookReturnsNoHookWhenHookWithTheContextNameNotFound() {
		final var context = mock(PersistenceContext.class);
		doReturn("hook not existing in the registry").when(context).getPostPersistHook();

		final var hook = registry.getPostPersistHook(context);

		assertThat(hook).isEmpty();
	}

	@Test
	public void testGetPostPersistHookReturnsHookWhenHookWithTheContextNameFound() {
		final var name = "existing";
		final var hook = mock(PostPersistHook.class);
		final var context = mock(PersistenceContext.class);
		doReturn(name).when(context).getPostPersistHook();
		registry.addHook(name, hook);

		final var postHook = registry.getPostPersistHook(context);

		assertThat(postHook).isNotEmpty();
	}

	@Test
	public void testPrePersistHookDoesNotExist()
	{
		assertThatThrownBy(() -> registry.getPrePersistHook("preHookName", INTEGRATION_KEY))
				.isInstanceOf(PersistHookNotFoundException.class)
				.hasMessageContaining("preHookName");
	}

	@Test
	public void testPostPersistHookDoesNotExist()
	{
		assertThatThrownBy(() -> registry.getPostPersistHook("postHookName", INTEGRATION_KEY))
				.isInstanceOf(PersistHookNotFoundException.class)
				.hasMessageContaining("postHookName");
	}

	@Test
	public void testAddPerPersistHook()
	{
		final PrePersistHook hook = mock(PrePersistHook.class);
		registry.addHook("preHook", hook);
		assertThat(registry.getPrePersistHook("preHook", INTEGRATION_KEY)).isSameAs(hook);
	}

	@Test
	public void testAddPostPersistHook()
	{
		final PostPersistHook hook = mock(PostPersistHook.class);
		registry.addHook("postHook", hook);
		assertThat(registry.getPostPersistHook("postHook", INTEGRATION_KEY)).isSameAs(hook);
	}
}