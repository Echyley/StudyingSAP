/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.azure.dtu.impl;

import de.hybris.platform.azure.dtu.DatabaseAccessService;
import de.hybris.platform.util.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Provides database functions to check for permissions and table access.
 */
public class DefaultDatabaseAccessService implements DatabaseAccessService
{
	private static final String STATEMENT_PERMISSION = "SELECT * FROM fn_my_permissions(?, ?) where permission_name = ?";
	private static final String DEFAULT_SECURABLE = null;
	private static final String DEFAULT_SECURABLE_CLASS = "Database";

	/**
	 * Checks for existence of desired table within the given database schema.
	 *
	 * @param jdbcTemplate {@link JdbcTemplate}
	 * @param schemaName The name of the schema
	 * @param tableName The name of the view or table to search for
	 *
	 * @return True if the table or view exists.
	 */
	@Override
	public boolean checkIfTableExists(final JdbcTemplate jdbcTemplate, final String schemaName, final String tableName)
			throws SQLException
	{
		try (final Connection connection = jdbcTemplate.getDataSource().getConnection())
		{
			try (final ResultSet resultSet = connection.getMetaData().getTables(null, schemaName, tableName, null))
			{
				final boolean tableExists = resultSet.next();
				return tableExists;
			}
		}
	}

	/**
	 * Checks for a permission for the securable class: Database.
	 *
	 * @param jdbcTemplate {@link JdbcTemplate}
	 * @param permission The requested permission e.g. VIEW DATABASE STATE
	 * @return TRUE if permission is granted and the user has the access to read this information from the database
	 * @throws SQLException on connection error
	 */
	@Override
	public boolean checkPermission (final JdbcTemplate jdbcTemplate, final String permission) throws SQLException
	{
		return this.checkPermission(jdbcTemplate, DEFAULT_SECURABLE_CLASS, permission);
	}

	/**
	 * Checks for a permission.
	 *
	 * @param jdbcTemplate {@link JdbcTemplate}
	 * @param securableClass e.g. Database, Server or Object
	 * @param permission The requested permission e.g. VIEW DATABASE STATE
	 * @return TRUE if permission is granted and the user has the access to read this information from the database
	 * @throws SQLException on connection error
	 */
	private boolean checkPermission(final JdbcTemplate jdbcTemplate, final String securableClass, final String permission) throws SQLException
	{
		if (!this.isAzureCompatible()) {
			return false;
		}

		try (final Connection connection = jdbcTemplate.getDataSource().getConnection())
		{
			final PreparedStatement stmt = connection.prepareStatement(STATEMENT_PERMISSION);
			stmt.setString(1, DEFAULT_SECURABLE);
			stmt.setString(2, securableClass);
			stmt.setString(3, permission);

			try (final ResultSet resultSet = stmt.executeQuery())
			{
				final boolean permissionGranted = resultSet.next();
				return permissionGranted;
			}
		}
	}

	@Override
	public boolean isAzureCompatible ()
	{
		return Config.isSQLServerUsed();
	}
}
