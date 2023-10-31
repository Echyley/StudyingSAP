/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.azure.dtu.DatabaseUtilization;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import de.hybris.platform.azure.dtu.DatabaseAccessService;

@UnitTest
public class AzureDatabaseUtilizationServiceTest
{

	@Rule
	public MockitoRule mockitorule = MockitoJUnit.rule();

	@Mock
	private DatabaseAccessService databaseAccessService;

	@Mock
	private JdbcTemplate jdbcTemplate;

	private AzureDatabaseUtilizationService azureDatabaseUtilizationService;

	@Before
	public void setUp()
	{
		Mockito.when(this.databaseAccessService.isAzureCompatible()).thenReturn(Boolean.TRUE);

		this.azureDatabaseUtilizationService = new AzureDatabaseUtilizationService(jdbcTemplate);
		this.azureDatabaseUtilizationService.setDatabaseAccessService(this.databaseAccessService);
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowException()
	{
		this.azureDatabaseUtilizationService.getUtilization(null);
	}

	@Test
	public void shouldDeliverUtilization() throws SQLException
	{
		final Instant start = Instant.parse("2017-10-03T10:15:30.00Z");
		final Instant end = Instant.parse("2017-10-03T10:16:30.00Z");
		final Duration duration = Duration.between(start, end);

		Mockito.when(this.databaseAccessService.checkIfTableExists(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.TRUE);
		this.azureDatabaseUtilizationService.afterPropertiesSet();

		final DatabaseUtilization dbUtilization = DatabaseUtilization.builder().build();
		final List<DatabaseUtilization> oneResultDbUtilization = Collections.singletonList(dbUtilization);
		Mockito.when(this.jdbcTemplate.query(Mockito.any(PreparedStatementCreator.class),
				Mockito.any(PreparedStatementSetter.class), Mockito.any())).thenReturn(oneResultDbUtilization);

		final List<DatabaseUtilization> result = this.azureDatabaseUtilizationService.getUtilization(duration);

		Assertions.assertThat(result).isNotEmpty();
		Assertions.assertThat(result.size()).isEqualTo(1);
	}

	@Test
	public void shouldDeliverEmptyUtilizationList() throws SQLException
	{
		final Instant start = Instant.parse("2017-10-03T10:15:30.00Z");
		final Instant end = Instant.parse("2017-10-03T10:16:30.00Z");
		final Duration duration = Duration.between(start, end);

		Mockito.when(this.databaseAccessService.checkIfTableExists(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.TRUE);
		this.azureDatabaseUtilizationService.afterPropertiesSet();

		final List<DatabaseUtilization> emptyResultDbUtilization = Collections.emptyList();
		Mockito.when(this.jdbcTemplate.query(Mockito.any(PreparedStatementCreator.class),
				Mockito.any(PreparedStatementSetter.class), Mockito.any())).thenReturn(emptyResultDbUtilization);

		final List<DatabaseUtilization> result = this.azureDatabaseUtilizationService.getUtilization(duration);

		Assertions.assertThat(result).isEmpty();
	}

	@Test
	public void shouldDeliverEmptyUtilizationListOnConnectionValidationError() throws SQLException
	{
		final Instant start = Instant.parse("2017-10-03T10:15:30.00Z");
		final Instant end = Instant.parse("2017-10-03T10:16:30.00Z");
		final Duration duration = Duration.between(start, end);

		Mockito.when(this.databaseAccessService.checkIfTableExists(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.TRUE);
		this.azureDatabaseUtilizationService.afterPropertiesSet();

		Mockito.when(this.jdbcTemplate.query(Mockito.any(PreparedStatementCreator.class),
				Mockito.any(PreparedStatementSetter.class), Mockito.any()))
				.thenThrow(AzureDatabaseConnectionValidationException.class);

		final List<DatabaseUtilization> result = this.azureDatabaseUtilizationService.getUtilization(duration);

		Assertions.assertThat(result).isEmpty();
	}

	@Test
	public void shouldDeliverEmptyUtilizationListForInactiveService() throws SQLException
	{
		final Instant start = Instant.parse("2017-10-03T10:15:30.00Z");
		final Instant end = Instant.parse("2017-10-03T10:16:30.00Z");
		final Duration duration = Duration.between(start, end);

		Mockito.when(this.databaseAccessService.checkIfTableExists(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.FALSE);
		this.azureDatabaseUtilizationService.afterPropertiesSet();

		final List<DatabaseUtilization> result = this.azureDatabaseUtilizationService.getUtilization(duration);

		Assertions.assertThat(result).isEmpty();
	}

	@Test
	public void shouldBeActive() throws SQLException
	{
		Mockito.when(this.databaseAccessService.checkIfTableExists(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.TRUE);
		Mockito.when(this.databaseAccessService.isAzureCompatible()).thenReturn(Boolean.TRUE);

		this.azureDatabaseUtilizationService.afterPropertiesSet();

		final boolean active = this.azureDatabaseUtilizationService.isActive();

		Assertions.assertThat(active).isTrue();
	}

	@Test
	public void shouldBeInactiveForSQLServerAndMissingView() throws SQLException
	{
		Mockito.when(this.databaseAccessService.checkIfTableExists(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.FALSE);
		Mockito.when(this.databaseAccessService.isAzureCompatible()).thenReturn(Boolean.TRUE);

		this.azureDatabaseUtilizationService.afterPropertiesSet();

		final boolean active = this.azureDatabaseUtilizationService.isActive();

		Assertions.assertThat(active).isFalse();
	}

	@Test
	public void shouldBeInactiveForNonSQLServer() throws SQLException
	{
		Mockito.when(this.databaseAccessService.checkIfTableExists(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.FALSE);
		Mockito.when(this.databaseAccessService.isAzureCompatible()).thenReturn(Boolean.FALSE);

		this.azureDatabaseUtilizationService.afterPropertiesSet();

		final boolean active = this.azureDatabaseUtilizationService.isActive();

		Assertions.assertThat(active).isFalse();
	}

}
