/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookProvider;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Preconditions;

/**
 * An implementation of the {@link PersistenceHookRegistry}, which loads persistence hooks from the Spring
 * application context. Spring Bean names/ids become names of the hooks in this registry.
 * @deprecated use {@link PersistenceHookProvider} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public class DefaultPersistenceHookRegistry implements PersistenceHookRegistry, ApplicationContextAware, PersistenceHookProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPersistenceHookRegistry.class);

	private final Map<String, PrePersistHook> prePersistHooks = new HashMap<>();
	private final Map<String, PostPersistHook> postPersistHooks = new HashMap<>();

	@Override
	public PrePersistHook getPrePersistHook(final String hookName, final String integrationKey)
	{
		return getHook(prePersistHooks, "Pre", hookName, integrationKey);
	}

	@Override
	public PostPersistHook getPostPersistHook(final String hookName, final String integrationKey)
	{
		return getHook(postPersistHooks, "Post", hookName, integrationKey);
	}

	public void addHook(final String hookName, final PrePersistHook hook)
	{
		prePersistHooks.put(hookName, hook);
	}

	public void addHook(final String hookName, final PostPersistHook hook)
	{
		postPersistHooks.put(hookName, hook);
	}

	protected <T> T getHook(final Map<String, T> hooks, final String prefix, final String hookName, final String integrationKey)
	{
		final T hook = hooks.get(hookName);
		if (!StringUtils.isEmpty(hookName) && hook == null)
		{
			LOG.error("{}PersistHook [{}] does not exist. Payload will not be persisted.", prefix, hookName);
			throw new PersistHookNotFoundException(String.format("%sPersistHook [%s] does not exist. Payload will not be persisted.", prefix , hookName), integrationKey);
		}
		return hook;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		prePersistHooks.putAll(applicationContext.getBeansOfType(PrePersistHook.class));
		postPersistHooks.putAll(applicationContext.getBeansOfType(PostPersistHook.class));
	}

	@Override
	public Optional<de.hybris.platform.inboundservices.persistence.hook.PrePersistHook> getPrePersistHook(
			@NotNull final PersistenceContext context)
	{
		Preconditions.checkArgument(context != null, "Context must not be null");
		return Optional.ofNullable(prePersistHooks.get(context.getPrePersistHook()))
		               .map(PrePersistHookAdapter::new);
	}

	@Override
	public Optional<de.hybris.platform.inboundservices.persistence.hook.PostPersistHook> getPostPersistHook(
			@NotNull final PersistenceContext context)
	{
		Preconditions.checkArgument(context != null, "Context must not be null");
		return Optional.ofNullable(postPersistHooks.get(context.getPostPersistHook()))
				.map(PostPersistHookAdapter::new);
	}
}
