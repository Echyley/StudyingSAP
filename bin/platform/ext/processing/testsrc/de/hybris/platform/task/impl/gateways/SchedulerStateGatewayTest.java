/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl.gateways;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.task.impl.AuxiliaryTablesBasedTaskProvider;
import de.hybris.platform.task.impl.AuxiliaryTablesGatewayFactory;
import de.hybris.platform.util.Config;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import jersey.repackaged.com.google.common.collect.ImmutableMap;


@IntegrationTest
public class SchedulerStateGatewayTest extends BaseGatewayTest
{

	private static final Map<Config.DatabaseName, String> CHANGE_SESSION_TIMEZONE_STATEMENTS;
	public static final Logger LOGGER = LoggerFactory.getLogger(SchedulerStateGatewayTest.class);

	static
	{
		CHANGE_SESSION_TIMEZONE_STATEMENTS = ImmutableMap.<Config.DatabaseName, String>builder()
		                                                 // The queries for Scheduler in Oracle do not use the session timezone
		                                                 //.put(Config.DatabaseName.ORACLE, "ALTER SESSION SET TIME_ZONE = '%s'")
		                                                 .build();
	}

	@Resource
	private AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory;

	private SchedulerStateGateway gateway;

	private TimeZone defaultTimeZone;

	@Before
	public void setUp() throws Exception
	{
		disableTaskEngine();

		defaultTimeZone = TimeZone.getDefault();

		gateway = auxiliaryTablesGatewayFactory.getSchedulerStateGateway();

		prepareTable(gateway);
	}

	@After
	public void tearDown() throws Exception
	{
		if (defaultTimeZone != null)
		{
			TimeZone.setDefault(defaultTimeZone);
		}

		cleanUpTable(gateway);

		enableTaskEngine();
	}

	@Test
	public void insertSchedulerRow()
	{
		final boolean b = gateway.insertSchedulerRow(Instant.now(), AuxiliaryTablesBasedTaskProvider.VERSION);

		assertThat(b).isTrue();
	}

	@Test
	public void updateSchedulerRow()
	{
		final Instant oldTimeStamp = Instant.now().minusSeconds(20);
		final boolean b = gateway.insertSchedulerRow(oldTimeStamp, AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(b).isTrue();
		final SchedulerState schedulerState = gateway.getSchedulerTimestamp().orElseThrow();

		final boolean b1 = gateway.updateSchedulerRow(Instant.now(), schedulerState.getLastActiveTs());
		assertThat(b1).isTrue();
	}

	private void assumeCapableOfChangingSessionTimeZone()
	{
		Assume.assumeTrue(CHANGE_SESSION_TIMEZONE_STATEMENTS.containsKey(Config.getDatabaseName()));
	}

	@Test
	public void updateSchedulerRoleWithDBTimezone() throws SQLException, InterruptedException
	{
		assumeCapableOfChangingSessionTimeZone();
		testUpdateSchedulerRowWithDbSessionTimeZone("-07:00");
	}

	@Test
	public void updateSchedulerRoleWithDBTimezone2() throws SQLException, InterruptedException
	{
		assumeCapableOfChangingSessionTimeZone();
		testUpdateSchedulerRowWithDbSessionTimeZone("+07:00");
	}

	private SchedulerStateGateway getCustomizedSchedulerStateGateway(final JdbcTemplate jdbcTemplate)
	{
		return new AuxiliaryTablesGatewayFactory()
		{
			@Override
			public JdbcTemplate getJdbcTemplate()
			{
				return jdbcTemplate;
			}
		}.getSchedulerStateGateway();
	}

	private void testUpdateSchedulerRowWithDbSessionTimeZone(final String x) throws SQLException, InterruptedException
	{
		try (final Connection connection = Registry.getCurrentTenantNoFallback().getDataSource().getConnection())
		{
			final JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
			final SchedulerStateGateway gateway = getCustomizedSchedulerStateGateway(jdbcTemplate);
			alterDbSessionTimeZone(jdbcTemplate, x);

			final boolean b = gateway.insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
			assertThat(b).isTrue();

			TimeUnit.SECONDS.sleep(6);

			final boolean b1 = gateway.updateSchedulerRow(Duration.ofSeconds(3));
			assertThat(b1).isTrue();
		}
	}

	private void alterDbSessionTimeZone(final JdbcTemplate jdbcTemplate, final String x)
	{
		jdbcTemplate.execute(String.format(CHANGE_SESSION_TIMEZONE_STATEMENTS.get(Config.getDatabaseName()), x));
	}

	@Test
	public void getSchedulerTimestamp() throws InterruptedException
	{
		final Instant oldTimeStamp = Instant.now().truncatedTo(ChronoUnit.MILLIS).minusSeconds(20);
		final boolean b = gateway.insertSchedulerRow(oldTimeStamp, AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(b).isTrue();

		Thread.sleep(10);

		final Optional<SchedulerState> schedulerTimestamp = gateway.getSchedulerTimestamp();

		assertThat(schedulerTimestamp).isPresent();
		assertThat(schedulerTimestamp.get().getLastActiveTs().truncatedTo(ChronoUnit.MILLIS)).isLessThan(
				schedulerTimestamp.get().getDbNow());
	}

	@Test
	public void shouldRemoveSchedulerRow()
	{
		//given
		gateway.insertSchedulerRow(AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(gateway.getSchedulerTimestamp()).isPresent();

		//when
		final boolean deleted = gateway.deleteSchedulerRow();

		//then
		assertThat(deleted).isTrue();
		assertThat(gateway.getSchedulerTimestamp()).isNotPresent();
	}

	@Test
	public void getSchedulerTimestampAfterInsertInDifferentTimeZoneWest() throws InterruptedException
	{
		final boolean b = gateway.insertSchedulerRow(null, AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(b).isTrue();

		Thread.sleep(10);
		TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));

		final Optional<SchedulerState> schedulerTimestamp = gateway.getSchedulerTimestamp();

		assertThat(schedulerTimestamp).isPresent();
		assertThat(schedulerTimestamp.get().getLastActiveTs().truncatedTo(ChronoUnit.MILLIS)).isLessThanOrEqualTo(
				schedulerTimestamp.get().getDbNow());

	}


	@Test
	public void getSchedulerTimestampAfterInsertInDifferentTimeZoneEast() throws InterruptedException
	{
		final boolean b = gateway.insertSchedulerRow(null, AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(b).isTrue();

		Thread.sleep(10);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));

		final Optional<SchedulerState> schedulerTimestamp = gateway.getSchedulerTimestamp();

		assertThat(schedulerTimestamp).isPresent();
		assertThat(schedulerTimestamp.get().getLastActiveTs().truncatedTo(ChronoUnit.MILLIS)).isLessThanOrEqualTo(
				schedulerTimestamp.get().getDbNow());
	}

	@Test
	public void getSchedulerTimestampAfterUpdateInDifferentTimeZoneWest() throws InterruptedException
	{
		final boolean b1 = gateway.insertSchedulerRow(null, AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(b1).isTrue();

		Thread.sleep(10);
		final SchedulerState oldState = gateway.getSchedulerTimestamp().orElseThrow();
		assertThat(oldState.getLastActiveTs()).isLessThan(oldState.getDbNow());

		final boolean b2 = gateway.updateSchedulerRow(oldState.getDbNow(), oldState.getLastActiveTs());
		assertThat(b2).isTrue();

		TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
		Thread.sleep(10);
		final Optional<SchedulerState> newState = gateway.getSchedulerTimestamp();

		assertThat(newState).isPresent();
		assertThat(newState.get().getLastActiveTs()).isLessThan(newState.get().getDbNow());
	}

	@Test
	public void getSchedulerTimestampAfterUpdateInDifferentTimeZoneEast() throws InterruptedException
	{

		final boolean b1 = gateway.insertSchedulerRow(null, AuxiliaryTablesBasedTaskProvider.VERSION);
		assertThat(b1).isTrue();

		Thread.sleep(10);
		final SchedulerState oldState = gateway.getSchedulerTimestamp().orElseThrow();
		assertThat(oldState.getLastActiveTs()).isLessThan(oldState.getDbNow());

		final boolean b2 = gateway.updateSchedulerRow(oldState.getDbNow(), oldState.getLastActiveTs());
		assertThat(b2).isTrue();

		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
		Thread.sleep(10);

		final Optional<SchedulerState> newState = gateway.getSchedulerTimestamp();

		assertThat(newState).isPresent();
		assertThat(newState.get().getLastActiveTs()).isLessThan(newState.get().getDbNow());
	}

	@Test
	public void shouldReturnTrueIfTableExists()
	{
		assertTableExists(gateway.getTableName());

		final boolean r = gateway.doesTableExist();

		assertThat(r).isTrue();
	}

	@Test
	public void shouldReturnFalseIfTableDoesNotExist()
	{
		gateway.dropTable();
		assertTableNotExists(gateway.getTableName());

		final boolean r = gateway.doesTableExist();

		assertThat(r).isFalse();
	}

}
