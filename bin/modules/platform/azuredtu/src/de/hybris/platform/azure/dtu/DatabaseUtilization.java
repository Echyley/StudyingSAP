/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu;

import java.time.Instant;
import java.util.Objects;

public class DatabaseUtilization
{
	private final Instant observationTime;
	private final double cpuUtilization;
	private final double ioUtilization;
	private final double logWriteUtilization;
	private final double memoryUtilization;
	private final double storageUtilization;

	private DatabaseUtilization(final Instant observationTime, final double cpuUtilization, final double ioUtilization,
	                            final double logWriteUtilization,
	                            final double memoryUtilization, final double storageUtilization)
	{
		this.observationTime = observationTime;
		this.cpuUtilization = cpuUtilization;
		this.ioUtilization = ioUtilization;
		this.logWriteUtilization = logWriteUtilization;
		this.memoryUtilization = memoryUtilization;
		this.storageUtilization = storageUtilization;
	}

	public static DatabaseUtilizationBuilder builder()
	{
		return new DatabaseUtilizationBuilder();
	}

	public double getCpuUtilization()
	{
		return cpuUtilization;
	}

	public double getIoUtilization()
	{
		return ioUtilization;
	}

	public double getLogWriteUtilization()
	{
		return logWriteUtilization;
	}

	public double getMemoryUtilization()
	{
		return memoryUtilization;
	}

	public double getStorageUtilization()
	{
		return storageUtilization;
	}

	public Instant getObservationTime()
	{
		return observationTime;
	}

	@Override
	public String toString()
	{
		return "DatabaseUtilization{" +
				"observationTime=" + observationTime +
				", cpuUtilization=" + cpuUtilization +
				", ioUtilization=" + ioUtilization +
				", logWriteUtilization=" + logWriteUtilization +
				", memoryUtilization=" + memoryUtilization +
				", storageUtilization=" + storageUtilization +
				'}';
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof DatabaseUtilization))
		{
			return false;
		}
		final DatabaseUtilization that = (DatabaseUtilization) o;
		return Double.compare(that.cpuUtilization, cpuUtilization) == 0 &&
				Double.compare(that.ioUtilization, ioUtilization) == 0 &&
				Double.compare(that.logWriteUtilization, logWriteUtilization) == 0 &&
				Double.compare(that.memoryUtilization, memoryUtilization) == 0 &&
				Double.compare(that.storageUtilization, storageUtilization) == 0 &&
				Objects.equals(observationTime, that.observationTime);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(observationTime, cpuUtilization, ioUtilization, logWriteUtilization, memoryUtilization,
				storageUtilization);
	}

	public static final class DatabaseUtilizationBuilder
	{
		private Instant observationTime;
		private double cpuUtilization;
		private double ioUtilization;
		private double logWriteUtilization;
		private double memoryUtilization;
		private double storageUtilization;

		public DatabaseUtilizationBuilder withObservationTime(final Instant observationTime)
		{
			this.observationTime = observationTime;
			return this;
		}

		public DatabaseUtilizationBuilder withCpuUtilization(final double cpuUtilization)
		{
			this.cpuUtilization = cpuUtilization;
			return this;
		}

		public DatabaseUtilizationBuilder withIoUtilization(final double ioUtilization)
		{
			this.ioUtilization = ioUtilization;
			return this;
		}

		public DatabaseUtilizationBuilder withLogWriteUtilization(final double logWriteUtilization)
		{
			this.logWriteUtilization = logWriteUtilization;
			return this;
		}

		public DatabaseUtilizationBuilder withMemoryUtilization(final double memoryUtilization)
		{
			this.memoryUtilization = memoryUtilization;
			return this;
		}

		public DatabaseUtilizationBuilder withStorageUtilization(final double storageUtilization)
		{
			this.storageUtilization = storageUtilization;
			return this;
		}

		public DatabaseUtilization build()
		{
			return new DatabaseUtilization(observationTime, cpuUtilization, ioUtilization, logWriteUtilization, memoryUtilization,
					storageUtilization);
		}
	}


}
