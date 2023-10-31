/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.controller.platform;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.actions.AuditableActionHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mockito.ArgumentCaptor;

public class ConfigurationControllerTestAuditableHelper
{

	static Optional<AuditableActions.Action> getAuditableAction(final AuditableActionHandler testAuditableActionHandler,
	                                                            final String actionName)
	{
		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		return argCaptor.getAllValues()
		                .stream()
		                .filter(action -> actionName.equals(
				                action.getActionName()))
		                .findFirst();
	}

	static List<AuditableActions.Action> getAllAuditableAction(final AuditableActionHandler testAuditableActionHandler,
	                                                           final String actionName)
	{
		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		return argCaptor.getAllValues()
		                .stream()
		                .filter(action -> actionName.equals(
				                action.getActionName()))
		                .collect(Collectors.toList());
	}

}
