/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDataSourceFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDataSourceFactory.class);
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private int initialSize;
	private int maxActive;
	private String validationQuery;
	private Map<String, String> validationQueries;

	public Map<String, String> getValidationQueries() {
		return validationQueries;
	}

	public void setValidationQueries(final Map<String, String> validationQueries) {
		this.validationQueries = validationQueries;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(final String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(final int initialSize) {
		this.initialSize = initialSize;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(final int maxActive) {
		this.maxActive = maxActive;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(final String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public abstract DataSource create();

	protected String determineValidationQuery() {
		final String driver = getDriverClassName();
		String result = getValidationQueries().get(driver);
		if (result != null) {
			LOG.info("Using validation query '{}' for known database driver {}, ignoring set query '{}'", result,
					driver, getValidationQuery());
		} else {
			result = getValidationQuery();
			if (result.isEmpty()) {
				LOG.warn("Validation query for DataSource not set");
			} else {
				LOG.info("Using validation query '{}' for unknown database driver {}", result, driver);
			}
		}
		return result;
	}
}
