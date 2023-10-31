/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl;

import de.hybris.platform.core.locking.ItemLockedForProcessingException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.suspend.SystemIsSuspendedException;
import de.hybris.platform.inboundservices.persistence.ContextItemModelService;
import de.hybris.platform.inboundservices.persistence.ItemPersistenceService;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookExecutor;
import de.hybris.platform.integrationservices.search.ItemNotFoundException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

/**
 * Default implementation for {@link ItemPersistenceService}
 */
public class DefaultItemPersistenceService implements ItemPersistenceService
{
	private final ContextItemModelService contextItemModelService;
	private final ModelService modelService;
	private final PersistenceHookExecutor hookExecutor;

	/**
	 * Instantiates this service
	 *
	 * @param itemModelService a context item model service implementation to be used by this service
	 * @param modelService     a model service implementation to be used by this service
	 * @param executor         an executor to executor pre and post persist hooks
	 */
	public DefaultItemPersistenceService(@NotNull final ContextItemModelService itemModelService,
	                                     @NotNull final ModelService modelService,
	                                     @NotNull final PersistenceHookExecutor executor)
	{
		Preconditions.checkArgument(itemModelService != null, "ItemModelService cannot be null");
		Preconditions.checkArgument(modelService != null, "ModelService cannot be null");
		Preconditions.checkArgument(executor != null, "PersistenceHookExecutor cannot be null");
		this.contextItemModelService = itemModelService;
		this.modelService = modelService;
		this.hookExecutor = executor;
	}

	@Override
	@Transactional(value = "txManager")
	public void persist(final PersistenceContext context)
	{
		try
		{
			save(context);
		}
		catch (final SystemIsSuspendedException | ItemLockedForProcessingException | SystemException e)
		{
			throw new PersistenceFailedException(context, e);
		}
	}

	private void save(final PersistenceContext context)
	{
		final ItemModel item = retrieveItem(context);
		final Optional<ItemModel> hookItem = hookExecutor.runPrePersistHook(item, context);
		if (hookItem.isPresent())
		{
			modelService.saveAll();
			hookExecutor.runPostPersistHook(item, context);
		}
	}

	private ItemModel retrieveItem(final PersistenceContext context)
	{
		return Optional.ofNullable(contextItemModelService.findOrCreateItem(context))
		               .orElseThrow(() -> new ItemNotFoundException(context.getIntegrationItem()));
	}
}
