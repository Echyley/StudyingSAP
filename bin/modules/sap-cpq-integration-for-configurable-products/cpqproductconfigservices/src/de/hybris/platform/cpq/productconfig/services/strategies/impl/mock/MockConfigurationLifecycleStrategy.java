/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl.mock;

import de.hybris.platform.core.Registry;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.strategies.ConfigurationLifecycleStrategy;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;


/**
 * Mock implementation of {@link ConfigurationLifecycleStrategy}
 */
public class MockConfigurationLifecycleStrategy implements ConfigurationLifecycleStrategy
{

	private static final Logger LOG = Logger.getLogger(MockConfigurationLifecycleStrategy.class);

	/**
	 * Indicates: Configuration belongs to mock engine
	 */
	public static final String PREFIX_MOCK = "MOCK_";


	/**
	 * used for unit test mode
	 */
	static final String SERIALIZED_CONFIG_FOR_UNIT_TEST = "{\"configuration\":{\"productSystemId\": \"CONF_CAMERA_BUNDLE\", \"completed\": true}}";


	/**
	 * Fallback product
	 */
	public static final String FALLBACK_PRODUCT = "CONF_CAMERA_BUNDLE";

	/**
	 * Fallback owner
	 */
	public static final String FALLBACK_OWNER = "TESTUNIT";

	/**
	 * Represents mocked configuration persistence
	 */
	final Map<String, String> configurationMap = new HashMap<>();
	final Set<String> pernamentSet = new HashSet<>();

	private boolean useFallback;


	/**
	 * default constructor
	 */
	public MockConfigurationLifecycleStrategy()
	{
		super();
		defaultFallbackBehaviour();
	}

	private void defaultFallbackBehaviour()
	{
		useFallback = true;
	}


	@Override
	public ConfigurationSummaryData getConfigurationSummary(final String configId)
	{
		final String configuration = getConfigWithFallBack(configId);
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try
		{
			final ConfigurationSummaryData data = mapper.readValue(configuration, ConfigurationSummaryData.class);
			final BigDecimal totalPrice = data.getConfiguration().getTotalPrice();
			LOG.info("Parsed configuration with TotaPrice: " + (totalPrice != null ? totalPrice.toPlainString() : null));
			return data;
		}
		catch (final IOException e)
		{
			LOG.warn("Wrong mocked configuration format for configurationId " + configId, e);
			throw new IllegalStateException("Wrong mocked configuration format for configurationId " + configId);
		}
	}

	protected String getConfigWithFallBack(final String configId)
	{
		return getConfigWithFallBack(configId, FALLBACK_OWNER);
	}

	protected String getConfigWithFallBack(final String configId, final String ownweId)
	{
		String configuration = configurationMap.get(configId);
		if (null == configuration)
		{
			if (useFallback)
			{
				LOG.info(String.format("Config '%s' is not known to MOCK, yet! - creating config on the fly with owner '%s'.",
						configId, ownweId));
				final String productCode = extractProductIdFromConfigId(configId);
				final String temporaryConfigId = createConfiguration(productCode, ownweId);
				configuration = configurationMap.get(temporaryConfigId);
				configurationMap.remove(temporaryConfigId);
				if (isMockConfig(configId))
				{
					configurationMap.put(configId, configuration);
				}
			}
			else
			{
				throw new IllegalStateException(String.format("Config '%s' is not known to MOCK!)", configId));
			}
		}
		return configuration;
	}


	protected boolean isMockConfig(final String configId)
	{
		return configId.startsWith(PREFIX_MOCK);
	}

	@Override
	public String createConfiguration(final String productCode, final String ownerId)
	{
		final String propertyName = "cpqproductconfigservices.mock." + productCode;
		final String configurationSummary = getMockedConfigurationString(propertyName);
		if (StringUtils.isNotEmpty(configurationSummary))
		{
			final String configurationId = getConfigIdForMock(productCode);
			configurationMap.put(configurationId, configurationSummary);
			return configurationId;
		}
		else
		{
			throw new IllegalStateException("Mocked configuration can not be created for productCode " + productCode);
		}
	}


	@Override
	public String cloneConfiguration(final String configId, final boolean isPermanent)
	{
		ensureIsMockConfig(configId);
		final String clonedConfiguration = getConfigWithFallBack(configId, null);
		Preconditions.checkNotNull(clonedConfiguration, "At this point cloned config must exist");

		final String clonedConfigurationId = getConfigIdForMock(extractProductIdFromConfigId(configId));
		configurationMap.put(clonedConfigurationId, clonedConfiguration);
		if (isPermanent)
		{
			pernamentSet.add(clonedConfigurationId);
		}
		return clonedConfigurationId;


	}

	@Override
	public boolean deleteConfiguration(final String configId)
	{
		pernamentSet.remove(configId);
		final String removedConfiguration = configurationMap.remove(configId);
		if (removedConfiguration != null)
		{
			return true;
		}
		else
		{
			LOG.error("Configuration not found for deletion for configurationId : " + configId);
			return false;
		}
	}

	protected String extractProductIdFromConfigId(final String configId)
	{
		final String[] components = configId.split("@");
		String productCode;
		if (components.length > 1)
		{
			productCode = components[components.length - 1];
		}
		else
		{
			productCode = FALLBACK_PRODUCT;
			LOG.info(String.format("Mock config id '%s' does not contain a product code, using fallback product '%s' instead.",
					configId, FALLBACK_PRODUCT));
		}
		return productCode;
	}

	protected String getConfigIdForMock(final String productCode)
	{
		return PREFIX_MOCK + UUID.randomUUID().toString() + "@" + productCode;
	}

	protected String getMockedConfigurationString(final String propertyName)
	{
		if (Registry.hasCurrentTenant())
		{
			return Config.getString(propertyName, "");
		}
		else
		{
			return propertyName.endsWith("WRONG_PRODUCT_CODE") ? "" : SERIALIZED_CONFIG_FOR_UNIT_TEST;
		}
	}

	@Override
	public void makeConfigurationPermanent(final String configId)
	{
		ensureIsMockConfig(configId);
		getConfigWithFallBack(configId);
		pernamentSet.add(configId);
	}

	protected void ensureIsMockConfig(final String configId)
	{
		if (!isMockConfig(configId))
		{
			throw new IllegalStateException(String.format("Config '%s' is not a MOCK configuration!", configId));
		}
	}

	/**
	 * @return get the fallback state
	 */
	public boolean isUseFallback()
	{
		return useFallback;
	}

	/**
	 * restores default behavior of mock
	 */
	public void restoreDefaultFallbackBehaviour()
	{
		defaultFallbackBehaviour();
	}

	/**
	 * @param useFallback
	 *           <code>true</code> if the mock should create a configuration on the fly if it is not known
	 */
	public void setUseFallback(final boolean useFallback)
	{
		this.useFallback = useFallback;
	}


}
