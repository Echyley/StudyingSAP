/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu.impl;

import de.hybris.platform.azure.dtu.DatabaseUtilization;
import de.hybris.platform.azure.dtu.DatabaseUtilizationService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

/**
 * Azure implementation of {@link DatabaseUtilizationService}
 */
public class AzureDatabaseUtilizationService extends AbstractAzureDatabaseService<List<DatabaseUtilization>>
{
	protected static final String QUERY = "SELECT end_time, avg_cpu_percent, avg_data_io_percent, avg_log_write_percent, avg_memory_usage_percent, xtp_storage_percent FROM sys.dm_db_resource_stats  WHERE end_time > ? ORDER BY end_time DESC";

	private static final String VIEW_DM_DB_RESOURCE_STATS = "dm_db_resource_stats";
	private static final String SCHEMA_SYS = "sys";

	public AzureDatabaseUtilizationService(final JdbcTemplate jdbcTemplate)
	{
		super(jdbcTemplate, SCHEMA_SYS, VIEW_DM_DB_RESOURCE_STATS);
	}

	@Override
	public List<DatabaseUtilization> getUtilization(final Duration duration)
	{
		final Instant fromTimePoint = Instant.now().minus(duration);

		final Optional<List<DatabaseUtilization>> result = super.query(
				new PreparedStatementInstantSetter(fromTimePoint, 1), new DatabaseUtilizationExtractor());

		return result.orElse(Collections.emptyList());
	}

	@Override
	protected String getQuery()
	{
		return QUERY;
	}

	public static class DatabaseUtilizationExtractor extends RowMapperResultSetExtractor<DatabaseUtilization>
	{
		private static final int EXPECTED_MAX_SIZE = (int) (4 * 60 * 1.1);

		public DatabaseUtilizationExtractor()
		{
			super(DatabaseUtilizationExtractor::mapToDatabaseUtilization, EXPECTED_MAX_SIZE);
		}

		private static DatabaseUtilization mapToDatabaseUtilization(final ResultSet rs, final int rowNum) throws SQLException
		{
			final DatabaseUtilization.DatabaseUtilizationBuilder metricBuilder = new DatabaseUtilization.DatabaseUtilizationBuilder();

			// Corrected problem with not setting milliseconds from the database when the getTimestamp(String s, Calendar cal) method is called
			final Instant originalTimestamp = rs.getTimestamp("end_time").toInstant();
			final Calendar utcCalendarWithModifiedMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			utcCalendarWithModifiedMillis.set(Calendar.MILLISECOND, originalTimestamp.get(ChronoField.MILLI_OF_SECOND));

			final Instant utcTimestampWithModifiedMillis = rs.getTimestamp("end_time", utcCalendarWithModifiedMillis).toInstant();

			metricBuilder.withObservationTime(utcTimestampWithModifiedMillis)
			             .withCpuUtilization(rs.getDouble("avg_cpu_percent"))
			             .withIoUtilization(rs.getDouble("avg_data_io_percent"))
			             .withLogWriteUtilization(rs.getDouble("avg_log_write_percent"))
			             .withMemoryUtilization(rs.getDouble("avg_memory_usage_percent"))
			             .withStorageUtilization(rs.getDouble("xtp_storage_percent"));
			return metricBuilder.build();
		}
	}

	protected static class PreparedStatementInstantSetter implements PreparedStatementSetter
	{
		private final Instant instant;
		private final int index;

		public PreparedStatementInstantSetter(final Instant instant, final int index)
		{
			this.instant = Objects.requireNonNull(instant);
			this.index = index;
		}

		@Override
		public void setValues(final PreparedStatement ps) throws SQLException
		{
			final Calendar utcCalendarWithModifiedMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			utcCalendarWithModifiedMillis.set(Calendar.MILLISECOND, instant.get(ChronoField.MILLI_OF_SECOND));

			ps.setTimestamp(index, Timestamp.from(instant), utcCalendarWithModifiedMillis);
		}
	}

}
