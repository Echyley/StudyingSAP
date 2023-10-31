/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.task.impl.gateways;

import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.task.impl.AuxiliaryTablesGatewayFactory;
import de.hybris.platform.task.impl.AuxiliaryTablesTaskProviderTestHelper;

import javax.annotation.Resource;

import org.junit.Before;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;


public abstract class BaseGatewayTest extends ServicelayerBaseTest
{

	@Resource
	protected TaskService taskService;

	@Resource
	protected JdbcTemplate jdbcTemplate;

	protected AuxiliaryTablesTaskProviderTestHelper testHelper;
	@Resource
	protected AuxiliaryTablesGatewayFactory auxiliaryTablesGatewayFactory;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;

	@Before
	public void setupBaseGatewayTest()
	{
		testHelper = new AuxiliaryTablesTaskProviderTestHelper(taskService, jdbcTemplate, typeService,
				auxiliaryTablesGatewayFactory, modelService);
	}

	protected void disableTaskEngine()
	{

		testHelper.disableTaskEngine();
	}

	protected void enableTaskEngine()
	{
		testHelper.enableTaskEngine();
	}

	protected void assertTableExists(final String tableName)
	{

		testHelper.assertTableExists(tableName);
	}

	protected void assertTableNotExists(final String tableName)
	{
		testHelper.assertTableNotExists(tableName);
	}

	protected void prepareTable(final BaseGateway gateway)
	{
		cleanUpTable(gateway);
		gateway.createTable();


		assertTableExists(gateway.getTableName());
	}


	protected void cleanUpTable(final BaseGateway gateway)
	{
		try
		{
			gateway.dropTable();
		}
		catch (final Exception e)
		{
			LoggerFactory.getLogger(getClass()).warn("exception while dropping table {}", gateway.getTableName(), e);
		}
	}


}
