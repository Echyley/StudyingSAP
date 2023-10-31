/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata.persistence.hooks;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.impl.DefaultPersistenceHookRegistry;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.rules.ExternalResource;

public class SamplePostPersistHook extends ExternalResource implements PostPersistHook, de.hybris.platform.inboundservices.persistence.hook.PostPersistHook
{
	private static final BiConsumer<ItemModel, PersistenceContext> DEFAULT_PROCEDURE = (it, ctx) -> {};

	private boolean executed;
	private BiConsumer<ItemModel, PersistenceContext> executeProcedure = DEFAULT_PROCEDURE;

	@Override
	public void execute(final ItemModel item)
	{
		execute(item, null);
	}

	@Override
	public void execute(final ItemModel item, final PersistenceContext context)
	{
		executed = true;
		executeProcedure.accept(item, context);
	}

	public boolean isExecuted()
	{
		return executed;
	}

	public void givenDoesInExecute(final Consumer<ItemModel> proc)
	{
		executeProcedure = (it, ctx) -> proc.accept(it);
	}

	public void doesInExecute(final BiConsumer<ItemModel, PersistenceContext> proc)
	{
		executeProcedure = proc;
	}

	@Override
	public void after()
	{
		executed = false;
		executeProcedure = DEFAULT_PROCEDURE;
	}

	public void setHookRegistry(final DefaultPersistenceHookRegistry registry)
	{
		registry.addHook("samplePostPersistHook", this);
	}
}
