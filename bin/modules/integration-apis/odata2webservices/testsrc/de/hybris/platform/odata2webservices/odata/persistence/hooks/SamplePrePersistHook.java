/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata.persistence.hooks;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.impl.DefaultPersistenceHookRegistry;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.rules.ExternalResource;

public class SamplePrePersistHook extends ExternalResource implements PrePersistHook, de.hybris.platform.inboundservices.persistence.hook.PrePersistHook
{
	private static final BiFunction<ItemModel, PersistenceContext, Optional<ItemModel>> DEFAULT_EXECUTION = (it, ctx) -> Optional.of(it);

	private boolean executed;
	private BiFunction<ItemModel, PersistenceContext, Optional<ItemModel>> executeImplementation = DEFAULT_EXECUTION;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		return execute(item, null);
	}

	@Override
	public Optional<ItemModel> execute(final ItemModel item, final PersistenceContext context)
	{
		executed = true;
		return executeImplementation.apply(item, context);
	}

	public boolean isExecuted()
	{
		return executed;
	}

	public void givenDoesInExecute(final Function<ItemModel, Optional<ItemModel>> function)
	{
		final BiFunction<ItemModel, PersistenceContext, Optional<ItemModel>> f = (it, ctx) -> function.apply(it);
		doesInExecute(f);
	}

	public void doesInExecute(final BiFunction<ItemModel, PersistenceContext, Optional<ItemModel>> function)
	{
		executeImplementation = function;
	}

	@Override
	public void after()
	{
		executed = false;
		executeImplementation = DEFAULT_EXECUTION;
	}

	public void setHookRegistry(final DefaultPersistenceHookRegistry registry)
	{
		registry.addHook("samplePrePersistHook", this);
	}
}