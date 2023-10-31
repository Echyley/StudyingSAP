/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Required;

import com.sap.jdsr.passport.DSRPassport;
import com.sap.jdsr.passport.EncodeDecode;

import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;
import de.hybris.platform.integrationservices.passport.SapPassportBuilder;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMPassportService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DefaultSapS4OMPassportService implements SapS4OMPassportService {
	private static final String SAP_PASSPORT_VERSION_KEY = "saps4omservices.sap.passport.version";
	private static final int DEFAULT_SAP_PASSPORT_VERSION = 3;
	private IntegrationServicesConfiguration configuration;
	private ConfigurationService configurationService;

	@Override
	public String generate(String action)
	{
		final SapPassportBuilder builder = SapPassportBuilder.newSapPassportBuilder();
		final DSRPassport passport = builder.withVersion(getSapPassportVersion())
				.withTraceFlag(0)
				.withSystemId(getConfiguration().getSapPassportSystemId())
				.withService(getConfiguration().getSapPassportServiceValue())
				.withUser(getConfiguration().getSapPassportUser())
				.withAction(action)
				.withActionType(11)
				.withPrevSystemId("")
				.withTransId(getUuidAsString())
				.withClientNumber("")
				.withSystemType(1)
				.withRootContextId(getUuidAsBytes())
				.withConnectionId(getUuidAsBytes())
				.withConnectionCounter(0)
				.build();

		final byte[] passportBytes = EncodeDecode.encodeBytePassport(passport);

		return Hex.encodeHexString(passportBytes);
	}

	protected IntegrationServicesConfiguration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Get a UUID
	 * @return UUID
	 */
	protected byte[] getUuidAsBytes()
	{
		final UUID uuid = UUID.randomUUID();
		final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());

		return byteBuffer.array();
	}

	/**
	 * Get a UUID as String
	 * @return UUID as String
	 */
	protected String getUuidAsString()
	{
		final StringBuilder uuidString = new StringBuilder();
		final byte[] uuidBytes = getUuidAsBytes();
		for (final byte uuidByte : uuidBytes)
		{
			uuidString.append(String.format("%02x", uuidByte));
		}
		return uuidString.toString().toUpperCase();
	}

	@Required
	public void setConfiguration(final IntegrationServicesConfiguration config)
	{
		this.configuration = config;
	}
	
	
	/**
	 * Get Passport Version from the configuration, if no configuration is found return default value =
	 * {@value #DEFAULT_SAP_PASSPORT_VERSION}
	 * 
	 * 
	 * @return the PassportVersion as a Integer
	 */
	private Integer getSapPassportVersion()
	{
		return getIntegerProperty(SAP_PASSPORT_VERSION_KEY, DEFAULT_SAP_PASSPORT_VERSION);
	}
	
	/**
	 *
	 * Get Configuration by property using ConfigurationService, if there is no configuration return defaultValue
	 * 
	 * @param property
	 * @param defaultValue
	 * @return the Configuration as a Integer
	 */
	private int getIntegerProperty(final String property, final int defaultValue)
	{
		try
		{
			return getConfigurationService().getConfiguration().getInt(property);
		}
		catch (final NoSuchElementException | ConversionException e)
		{
			return defaultValue;
		}
	}
	
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
}
