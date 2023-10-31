/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.hook;

import de.hybris.platform.inboundservices.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * A provider of persistence hooks. Each provider implementation decides what the persistence hooks are and how
 * they can be resolved by the context. For example, some providers may look in the Spring application context, some
 * providers may use dynamic scripts for the hooks, some providers may use external microservices for the hooks, etc.
 * <p>Implementations are recommended to use a very specific schema for the hook name, that is not used by any other provider, e.g.
 * {@code "bean://"}, {@code "script://"}, {@code "http://"}, etc., so that the provider could be addressed specifically, if the
 * same hook exists in mulitple providers. Also, the implementations may choose to support a hook name without a schema at all. In
 * this case a provider placed first in the executor
 * (see {@link de.hybris.platform.inboundservices.persistence.hook.impl.DefaultPersistenceHookExecutor#setHookProviders(List)})
 * will override hooks with the same name in the providers following it in the list.</p>
 */
public interface PersistenceHookProvider
{
	/**
	 * Retrieves a {@code PrePersistHook} by the hook name.
	 *
	 * @param context context of the data being persisted.
	 * @return a hook matching the persistence context or an {@code Optional.empty()}, if there is no hook for the provided context.
	 */
	Optional<PrePersistHook> getPrePersistHook(@NotNull PersistenceContext context);

	/**
	 * Retrieves a {@code PostPersistHook} by the hook name.
	 *
	 * @param context context of the data being persisted.
	 * @return a hook matching the persistence context or an {@code Optional.empty()}, if there is no hook for the provided context..
	 */
	Optional<PostPersistHook> getPostPersistHook(@NotNull PersistenceContext context);
}
