/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.facade;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assume;
import org.junit.Test;

/**
 * Integration test for {@link HacPerformanceTestsFacade}
 */
@IntegrationTest
public class HacPerformanceTestsFacadeTest extends ServicelayerBaseTest
{
	private static final String CREATE_TEST_TABLE_STATEMENT = "CREATE TABLE hacperftest ( col1 VARCHAR(10), col2 VARCHAR(10) )";
	private static final String DROP_TEST_TABLE_STATEMENT = "DROP TABLE hacperftest";
	private static final String ALTER_TEST_TABLE_STATEMENT = "ALTER TABLE hacperftest DROP COLUMN col2";
	private static final String SELECT_TEST_TABLE_STATEMENT = "SELECT col1, col2 FROM hacperftest";
	private static final String ILLEGAL_STATEMENT = SELECT_TEST_TABLE_STATEMENT + ";" + ALTER_TEST_TABLE_STATEMENT;

	@Resource
	private TypeService typeService;
	@Resource
	private HacPerformanceTestsFacade hacPerformanceTestsFacade;

	@Test
	public void testExecuteSqlTestForLegalStatement()
	{
		final ComposedTypeModel catalogTypeModel = typeService.getComposedTypeForClass(CatalogModel.class);
		final String sql = "SELECT PK FROM " + catalogTypeModel.getTable();
		final Map<String, Object> results = hacPerformanceTestsFacade.executeSqlTest(sql, 0, 0);

		assertThat(results).hasSize(3)
		                   .containsKeys("statementsCount", "statementsPerSecond", "statementsPerSecond");

	}

	@Test
	public void testExecuteSqlTestForIllegalStatement() throws SQLException
	{
		final boolean supportedDB = Config.isHSQLDBUsed() || Config.isSQLServerUsed() || Config.isMySQLUsed();
		Assume.assumeTrue("Specific DBs only", supportedDB);

		try
		{
			executeStatement(CREATE_TEST_TABLE_STATEMENT);

			final Map<String, Object> results = hacPerformanceTestsFacade.executeSqlTest(ILLEGAL_STATEMENT, 0, 0);

			assertThat(results).isNotEmpty();
			final boolean resultSetExist = executeStatement(SELECT_TEST_TABLE_STATEMENT);
			assertThat(resultSetExist).isTrue();
		}
		finally
		{
			executeStatement(DROP_TEST_TABLE_STATEMENT);
		}
	}

	protected boolean executeStatement(final String sql) throws SQLException
	{
		final HybrisDataSource dataSource = Registry.getCurrentTenantNoFallback().getDataSource();
		try (final Connection connection = dataSource.getConnection();
		     final PreparedStatement statement = connection.prepareStatement(sql))
		{
			return statement.execute();
		}
	}
}
