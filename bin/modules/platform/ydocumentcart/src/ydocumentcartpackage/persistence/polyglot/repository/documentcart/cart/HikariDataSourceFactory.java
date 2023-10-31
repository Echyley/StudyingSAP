/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package ydocumentcartpackage.persistence.polyglot.repository.documentcart.cart;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Objects;

import javax.sql.DataSource;

import org.apache.commons.configuration.Configuration;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class HikariDataSourceFactory
{
	public static final String PROPERTY_STORAGE_HIKARI_JDBC_DRIVER = "ydocumentcart.storage.jdbc.driver";
	public static final String PROPERTY_STORAGE_HIKARI_JDBC_URL = "ydocumentcart.storage.jdbc.url";
	public static final String PROPERTY_STORAGE_HIKARI_JDBC_USER = "ydocumentcart.storage.jdbc.user";
	public static final String PROPERTY_STORAGE_HIKARI_JDBC_PASSWORD = "ydocumentcart.storage.jdbc.password";
	public static final String PROPERTY_STORAGE_HIKARI_JDBC_POOL_SIZE = "ydocumentcart.storage.jdbc.pool.size";
	private static final int DEFAULT_JDBC_POOL_SIZE = 10;

	private final ConfigurationService configurationService;
	private final MetricRegistry metricRegistry;

	/**
	 * @deprecated use {@link #HikariDataSourceFactory(ConfigurationService, MetricRegistry)}
	 */
	@Deprecated(since = "2005", forRemoval = true)
	public HikariDataSourceFactory(final ConfigurationService configurationService)
	{
		this(configurationService, null);
	}

	public HikariDataSourceFactory(final ConfigurationService configurationService, final MetricRegistry metricRegistry)
	{
		this.configurationService = Objects.requireNonNull(configurationService, "configurationService mustn't be null.");
		this.metricRegistry = metricRegistry;
	}

	public DataSource getObject()
	{
		final Configuration config = configurationService.getConfiguration();
		final HikariConfig poolConfig = new HikariConfig();

		poolConfig.setDriverClassName(config.getString(PROPERTY_STORAGE_HIKARI_JDBC_DRIVER));
		poolConfig.setJdbcUrl(config.getString(PROPERTY_STORAGE_HIKARI_JDBC_URL));
		poolConfig.setUsername(config.getString(PROPERTY_STORAGE_HIKARI_JDBC_USER));
		poolConfig.setPassword(config.getString(PROPERTY_STORAGE_HIKARI_JDBC_PASSWORD));
		poolConfig.setPoolName(getCurrentTenantId());
		poolConfig.setMaximumPoolSize(config.getInt(PROPERTY_STORAGE_HIKARI_JDBC_POOL_SIZE, DEFAULT_JDBC_POOL_SIZE));

		if (metricRegistry != null)
		{
			poolConfig.setMetricRegistry(metricRegistry);
		}

		addNamedProperties(config, poolConfig);

		return new HikariDataSource(poolConfig);
	}

	public Class<DataSource> getObjectType()
	{
		return DataSource.class;
	}

	private String getCurrentTenantId()
	{
		return Registry.getCurrentTenant().getTenantID();
	}

	private void addNamedProperties(final Configuration config, final HikariConfig poolConfig)
	{
		final String propsPrefix = "ydocumentcart.storage.jdbc.props";
		config.getKeys("ydocumentcart.storage.jdbc.props").forEachRemaining(key -> {
			if (key.length() <= propsPrefix.length())
			{
				return;
			}
			final String propertyName = key.substring(propsPrefix.length() + 1);
			poolConfig.addDataSourceProperty(propertyName, config.getString(key));
		});
	}
}
