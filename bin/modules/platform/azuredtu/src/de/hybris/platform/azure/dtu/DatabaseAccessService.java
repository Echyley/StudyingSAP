/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.azure.dtu;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Provides functionality to check the database.
 */
public interface DatabaseAccessService {

	/**
	 * Checks for existence of desired table within the given database schema.
	 *
	 * @param jdbcTemplate {@link JdbcTemplate}
	 * @param schemaName The name of the schema
	 * @param tableName The name of the view or table to search for
	 *
	 * @return True if the table or view exists.
	 */
	boolean checkIfTableExists(JdbcTemplate jdbcTemplate, String schemaName, String tableName) throws SQLException;

	/**
	 * Checks for a permission for the securable class: Database.
	 *
	 * @param jdbcTemplate {@link JdbcTemplate}
	 * @param permission The requested permission e.g. VIEW DATABASE STATE
	 * @return TRUE if permission is granted and the user has the access to read this information from the database
	 * @throws SQLException on connection error
	 */
	boolean checkPermission(JdbcTemplate jdbcTemplate, String permission) throws SQLException;

	/**
	 * Check if the database is azure compatible.
	 *
	 * @return TRUE for compatibility.
	 */
	boolean isAzureCompatible();
}
