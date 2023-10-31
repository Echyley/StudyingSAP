/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package ydocumentcartpackage.persistence.polyglot.repository.documentcart.cart;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.jdbc.DatabaseNameResolver;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Suppliers;

public class DataSourceFactory
{
	public static final String PROPERTY_STORAGE_DATASOURCE = "ydocumentcart.storage.datasource";

	private final Supplier<DataSource> hikariDataSource;

	/**
	 * @deprecated use {@link #DataSourceFactory(HikariDataSourceFactory)}
	 */
	@Deprecated(since = "2005", forRemoval = true)
	public DataSourceFactory(final ConfigurationService configurationService)
	{
		this(configurationService, null);
	}

	/**
	 * @deprecated use {@link #DataSourceFactory(HikariDataSourceFactory)}
	 */
	@Deprecated(since = "2105", forRemoval = true)
	public DataSourceFactory(final ConfigurationService configurationService, final MetricRegistry metricRegistry)
	{
		this(new HikariDataSourceFactory(configurationService, metricRegistry));
	}

	public DataSourceFactory(final HikariDataSourceFactory hikariDataSourceFactory)
	{
		this.hikariDataSource = Suppliers.memoize(hikariDataSourceFactory::getObject);
	}


	public DataSource getObject()
	{
		final SupportedOptions datasource = getDataSourceConfiguration();

		switch (datasource)
		{
			case HIKARI:
				return hikariDataSource.get();
			case TENANT:
				return getTenantDataSource();
			default:
				throw new IllegalArgumentException("unsupported option " + datasource);
		}
	}

	public Class<?> getObjectType()
	{
		return DataSource.class;
	}

	private HybrisDataSource getTenantDataSource()
	{
		return Registry.getCurrentTenantNoFallback().getMasterDataSource();
	}

	private SupportedOptions getDataSourceConfiguration()
	{
		final String tenant = Config.getString(PROPERTY_STORAGE_DATASOURCE, "tenant");
		if (tenant.isBlank())
		{
			return SupportedOptions.TENANT;
		}
		return SupportedOptions.valueOfCode(tenant.toLowerCase(Locale.ROOT));
	}

	public String getDatabaseName()
	{
		final SupportedOptions datasource = getDataSourceConfiguration();

		switch (datasource)
		{
			case HIKARI:
				return DatabaseNameResolver.guessDatabaseNameFromURL(
						Config.getString(HikariDataSourceFactory.PROPERTY_STORAGE_HIKARI_JDBC_URL, ""));
			case TENANT:
				return getTenantDataSource().getDatabaseName();
			default:
				throw new IllegalArgumentException("unsupported option " + datasource);
		}
	}

	private enum SupportedOptions
	{
		TENANT("tenant"), HIKARI("hikari");

		private static final Map<String, SupportedOptions> BY_CODE;

		static
		{
			BY_CODE = Arrays.stream(SupportedOptions.values()).collect(Collectors.toMap(SupportedOptions::getCode,
					Function.identity()));
		}

		private final String code;

		SupportedOptions(final String code)
		{
			this.code = code;
		}

		public static SupportedOptions valueOfCode(final String code)
		{
			if (!BY_CODE.containsKey(code))
			{
				throw new IllegalArgumentException("no value for code " + code);
			}
			return BY_CODE.get(code);
		}

		private String getCode()
		{
			return code;
		}
	}

}
