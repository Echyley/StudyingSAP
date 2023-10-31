/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.events;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.google.common.base.Preconditions;

/**
 * Base event for all events related to the outbound sync workflow. It contains the cronjob PK to know what cronjob the event
 * is related to. Events should be implemented immutable.
 */
public abstract class OutboundSyncEvent extends AbstractEvent
{
	private static final long serialVersionUID = -4624300105163373328L;

	private final PK cronJobPk;

	protected OutboundSyncEvent(@NotNull final PK cronJobPk)
	{
		Preconditions.checkArgument(cronJobPk != null, "CronJob PK cannot be null");
		this.cronJobPk = cronJobPk;
	}

	public PK getCronJobPk()
	{
		return cronJobPk;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		final OutboundSyncEvent that = (OutboundSyncEvent) o;
		return new EqualsBuilder()
				.append(getCronJobPk(), that.getCronJobPk())
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getCronJobPk());
	}
}
