/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.audit;

import de.hybris.platform.audit.actions.AuditableActionHandler;

import java.util.function.Supplier;

public final class AuditableActionsUtil
{
	private AuditableActionsUtil()
	{
	}

	public static Supplier<AuditableActionHandler> getAuditableActionHandlerFactory()
	{
		return AuditableActions.getAuditableActionHandlerFactory();
	}

	public static void setAuditableActionHandlerFactory(final Supplier<AuditableActionHandler> factory)
	{
		AuditableActions.setAuditableActionHandlerFactory(factory);
	}
}

