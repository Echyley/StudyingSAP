/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu.impl;

import de.hybris.platform.azure.dtu.DatabaseAccessService;
import de.hybris.platform.azure.dtu.DatabaseUtilizationService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Provides base functionality to access the azure data base.
 *
 * @param <T> The desired type to read
 */
public abstract class AbstractAzureDatabaseService<T> implements InitializingBean, BeanNameAware, DatabaseUtilizationService
{
	private static final String PERMISSION_VIEW_DATABASE_STATE = "VIEW DATABASE STATE";

	protected final JdbcTemplate jdbcTemplate;

	private DatabaseAccessService databaseAccessService;
	private boolean active;
	private String statusReason;
	private String beanName;
	private final String schemaName;
	private final String viewName;

	/**
	 * Constructor.
	 *
	 * @param jdbcTemplate {@link JdbcTemplate}
	 * @param schemaName The schema name of the table
	 * @param viewName The table name
	 */
	protected AbstractAzureDatabaseService(final JdbcTemplate jdbcTemplate,
	                                       final String schemaName,
	                                       final String viewName)
	{
		this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate);
		this.schemaName = schemaName;
		this.viewName = viewName;
	}

	@Override
	public boolean isActive()
	{
		return this.active;
	}

	@Override
	public String getStatusReason()
	{
		return this.statusReason;
	}

	protected abstract String getQuery();

	protected Optional<T> query(final PreparedStatementSetter pss, final ResultSetExtractor<T> rse)
	{
		if (!this.isActive())
		{
			return Optional.empty();
		}
		try
		{
			return Optional.ofNullable(this.jdbcTemplate.query(this::prepareStatement, pss, rse));
		}
		catch (final AzureDatabaseConnectionValidationException e)
		{
			LoggerFactory.getLogger(getClass()).trace("Query not executed.", e);
			return Optional.empty();
		}
	}

	protected PreparedStatement prepareStatement(final Connection connection) throws SQLException
	{
		if (!this.assertValidConnection(connection))
		{
			throw new AzureDatabaseConnectionValidationException("invalid connection");
		}
		return connection.prepareStatement(this.getQuery());
	}

	/**
	 * Assert that the connection is valid.
	 *
	 * @param connection The connection
	 * @return True for valid connection
	 * @throws SQLException On error requesting the connection
	 */
	protected boolean assertValidConnection(final Connection connection) throws SQLException
	{
		return true;
	}

	public void setDatabaseAccessService(final DatabaseAccessService databaseAccessService) {
		this.databaseAccessService = databaseAccessService;
	}

	@Override
	public void afterPropertiesSet()
	{
		if (this.databaseAccessService == null) {
			LoggerFactory.getLogger(getClass()).info(
					"Use DefaultDatabaseAccessService since '{}' (bean: '{}') is not configured well by a spring configuration",
					getClass().getName(), beanName);
			this.databaseAccessService = new DefaultDatabaseAccessService();
		}

 		this.active = this.databaseAccessService.isAzureCompatible() && this.existsView();
		if (this.active)
		{
			return;
		}

		if (this.hasPermissionToAccessView(PERMISSION_VIEW_DATABASE_STATE))
		{
			this.statusReason = String.format("View '%s.%s' is not available also if the permission '%s' is granted.",
					schemaName, viewName, PERMISSION_VIEW_DATABASE_STATE);
		}
		else
		{
			this.statusReason = String.format("Permission '%s' required to access view '%s.%s'.",
					PERMISSION_VIEW_DATABASE_STATE, schemaName, viewName);
		}

		LoggerFactory.getLogger(getClass()).warn(
				"Deactivated database utilization service '{}' (bean: '{}'). No more utilisation data can be collected. Reason: {}",
				getClass().getName(), beanName, statusReason);
	}

	private boolean existsView()
	{
		try
		{
			return this.databaseAccessService.checkIfTableExists(jdbcTemplate, schemaName, viewName);
		}
		catch (final SQLException e)
		{
			LoggerFactory.getLogger(getClass()).error("Error connecting database.", e);
			return false;
		}
	}

	private boolean hasPermissionToAccessView (final String permission)
	{
		try
		{
			return this.databaseAccessService.checkPermission(jdbcTemplate, permission);
		}
		catch (final SQLException e)
		{
			LoggerFactory.getLogger(getClass()).warn("Error requesting permissions.", e);
			return false;
		}
	}

	@Override
	public void setBeanName(final String beanName)
	{
		this.beanName = beanName;
	}
}
