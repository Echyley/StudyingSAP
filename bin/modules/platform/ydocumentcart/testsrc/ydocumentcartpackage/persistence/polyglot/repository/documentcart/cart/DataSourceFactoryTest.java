/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package ydocumentcartpackage.persistence.polyglot.repository.documentcart.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.BulkPropertyConfigSwitcher;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.zaxxer.hikari.HikariDataSource;

@IntegrationTest
public class DataSourceFactoryTest extends ServicelayerBaseTest
{
	private final BulkPropertyConfigSwitcher properties = new BulkPropertyConfigSwitcher();

	HikariDataSourceFactory hikariCartDataSource;

	@Before
	public void setUp()
	{
		hikariCartDataSource = Mockito.mock(HikariDataSourceFactory.class);
	}

	@After
	public void tearDown()
	{
		properties.switchAllBack();
	}

	@Test
	public void shouldReturnTenantDataSourceWhenConfiguredThisWay()
	{
		properties.switchToValue(DataSourceFactory.PROPERTY_STORAGE_DATASOURCE, "tenant");


		final DataSource dataSource = new DataSourceFactory(hikariCartDataSource).getObject();

		assertThat(dataSource).isInstanceOf(HybrisDataSource.class)
		                      .isEqualTo(Registry.getCurrentTenantNoFallback().getDataSource());

		verify(hikariCartDataSource, never()).getObject();
	}

	@Test
	public void shouldReturnTenantDataSourceWhenConfiguredThisWayButInDifferentCase()
	{
		properties.switchToValue(DataSourceFactory.PROPERTY_STORAGE_DATASOURCE, "tEnAnT");

		final DataSource dataSource = new DataSourceFactory(hikariCartDataSource).getObject();

		assertThat(dataSource).isInstanceOf(HybrisDataSource.class)
		                      .isEqualTo(Registry.getCurrentTenantNoFallback().getDataSource());

		verify(hikariCartDataSource, never()).getObject();
	}

	@Test
	public void shouldReturnTenantDataSourceWhenConfigurationIsEmpty()
	{
		properties.switchToValue(DataSourceFactory.PROPERTY_STORAGE_DATASOURCE, "");

		final DataSource dataSource = new DataSourceFactory(hikariCartDataSource).getObject();

		assertThat(dataSource).isInstanceOf(HybrisDataSource.class)
		                      .isEqualTo(Registry.getCurrentTenantNoFallback().getDataSource());

		verify(hikariCartDataSource, never()).getObject();
	}

	@Test
	public void shouldReturnTenantDataSourceWhenConfigurationIsBlank()
	{
		properties.switchToValue(DataSourceFactory.PROPERTY_STORAGE_DATASOURCE, "        ");

		final DataSource dataSource = new DataSourceFactory(hikariCartDataSource).getObject();

		assertThat(dataSource).isInstanceOf(HybrisDataSource.class)
		                      .isEqualTo(Registry.getCurrentTenantNoFallback().getDataSource());

		verify(hikariCartDataSource, never()).getObject();
	}

	@Test
	public void shouldReturnHikariDataSourceWhenConfigurationIsEmpty()
	{
		properties.switchToValue(DataSourceFactory.PROPERTY_STORAGE_DATASOURCE, "hikari");
		final HikariDataSource hikariDataSource = mock(HikariDataSource.class);
		when(hikariCartDataSource.getObject()).thenReturn(hikariDataSource);

		final DataSource dataSource = new DataSourceFactory(hikariCartDataSource).getObject();

		assertThat(dataSource).isInstanceOf(HikariDataSource.class);
		verify(hikariCartDataSource).getObject();
	}

	@Test
	public void shouldThrowExceptionWhenHikariIsSelectedButCreatingDataSourceFailed()
	{
		properties.switchToValue(DataSourceFactory.PROPERTY_STORAGE_DATASOURCE, "hikari");
		when(hikariCartDataSource.getObject()).thenThrow(new IllegalStateException("some exception"));

		final DataSourceFactory dataSourceFactory = new DataSourceFactory(hikariCartDataSource);
		assertThatThrownBy(dataSourceFactory::getObject).isNotNull()
		                                                .isInstanceOf(IllegalStateException.class)
		                                                .hasMessage("some exception");
	}


	@Test
	public void shouldThrowExceptionWhenConfiguredWithInvalidValue()
	{
		properties.switchToValue(DataSourceFactory.PROPERTY_STORAGE_DATASOURCE, "invalidValue");

		final DataSourceFactory dataSourceFactory = new DataSourceFactory(hikariCartDataSource);
		assertThatThrownBy(dataSourceFactory::getObject).isNotNull().isInstanceOf(IllegalArgumentException.class);

		verify(hikariCartDataSource, never()).getObject();
	}

}
