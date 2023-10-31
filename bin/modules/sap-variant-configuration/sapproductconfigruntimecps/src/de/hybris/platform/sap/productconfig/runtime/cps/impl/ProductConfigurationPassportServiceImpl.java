/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;
import de.hybris.platform.integrationservices.passport.SapPassportBuilder;
import de.hybris.platform.sap.productconfig.runtime.cps.ProductConfigurationPassportService;

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.jdsr.passport.DSRPassport;
import com.sap.jdsr.passport.EncodeDecode;


public class ProductConfigurationPassportServiceImpl implements ProductConfigurationPassportService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductConfigurationPassportServiceImpl.class);
	private final IntegrationServicesConfiguration integrationServicesConfiguration;

	/**
	 * Default Constructor. Expects all required dependencies as constructor args
	 *
	 * @param integrationServicesConfiguration
	 *           Configuration of default passport attributes
	 */

	public ProductConfigurationPassportServiceImpl(final IntegrationServicesConfiguration integrationServicesConfiguration)
	{
		super();
		this.integrationServicesConfiguration = integrationServicesConfiguration;
	}

	@Override
	public String generate(final String actionId)
	{
		final SapPassportBuilder builder = SapPassportBuilder.newSapPassportBuilder();
		final String transactionId = getUuidAsString();
		final DSRPassport passport = builder.withVersion(3).withTraceFlag(0) //
				.withSystemId(getIntegrationServicesConfiguration().getSapPassportSystemId())//
				.withService(getIntegrationServicesConfiguration().getSapPassportServiceValue())//
				.withUser(getIntegrationServicesConfiguration().getSapPassportUser())//
				.withAction(actionId)//
				.withActionType(Integer.valueOf(11)) // 11 stands for 'HTTP'
				.withPrevSystemId("")//
				.withTransId(transactionId)//
				.withClientNumber("")//
				.withSystemType(1)//
				.withRootContextId(getUuidAsBytes())//
				.withConnectionId(getUuidAsBytes())//
				.withConnectionCounter(0)//
				.build();

		if (LOGGER.isDebugEnabled())
		{

			LOGGER.debug(String.format("Generating passport for transaction Id: %s", transactionId));
		}
		final byte[] passportBytes = EncodeDecode.encodeBytePassport(passport);

		return Hex.encodeHexString(passportBytes);
	}



	protected IntegrationServicesConfiguration getIntegrationServicesConfiguration()
	{
		return integrationServicesConfiguration;
	}

	protected byte[] getUuidAsBytes()
	{
		final UUID uuid = UUID.randomUUID();
		final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());

		return byteBuffer.array();

	}

	public String getUuidAsString()
	{
		final StringBuilder uuid = new StringBuilder();
		final byte[] uuidBytes = getUuidAsBytes();
		for (final byte uuidByte : uuidBytes)
		{
			uuid.append(String.format("%02x", uuidByte));
		}
		//it is fine to use US locale for upper case conversion here as we are dealing with 0..9, A..F characters
		return uuid.toString().toUpperCase(Locale.US);

	}

}
