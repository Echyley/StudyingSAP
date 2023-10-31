/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock;

/**
 * Factory to create {@link ConfigMock}
 */
public interface ConfigMockFactory
{
	/**
	 * Creates a mock engine for the given product code
	 *
	 * @param productCode
	 * @return ConfigMock
	 */
	ConfigMock createConfigMockForProductCode(final String productCode);

	/**
	 * Creates a mock engine for the given product and variant code
	 *
	 * @param productCode
	 * @param variantProductCode
	 * @return ConfigMock
	 */
	ConfigMock createConfigMockForProductCode(final String productCode, final String variantProductCode);
}
