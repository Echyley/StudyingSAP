/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import com.sap.conn.jco.ext.ClientPassportManager;
import com.sap.jdsr.passport.DSRPassport;
import com.sap.jdsr.passport.DSRPassportJco;
import com.sap.jdsr.writer.DsrFactory;
import com.sap.jdsr.writer.DsrIPassport;

import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.sap.core.configuration.rfc.dao.SAPRFCDestinationDao;
import de.hybris.platform.servicelayer.config.ConfigurationService;


/**
 * Default Client Passport Manager for the {@link ClientPassportManager}.
 */
public class DefaultClientPassportManager implements ClientPassportManager
{

	private static final String SAP_PASSPORT_SYSTEM_ID_PROPERTY_KEY = "sapcorejco.sap.passport.systemid";
	private static final String DEFAULT_SAP_PASSPORT_SYSTEM_ID = "Hybris";

	private static final String SAP_PASSPORT_SERVICE_PROPERTY_KEY = "sapcorejco.sap.passport.service";
	private static final int DEFAULT_SAP_PASSPORT_SERVICE = 0;

	private static final String SAP_PASSPORT_TRACE_FLAG_KEY = "sapcorejco.sap.passport.traceFlag";
	private static final String DEFAULT_SAP_PASSPORT_TRACE_FLAG = "0x0000";

	private static final String SAP_PASSPORT_VERSION_KEY = "sapcorejco.sap.passport.version";
	private static final int DEFAULT_SAP_PASSPORT_VERSION = 3;

	private static final String SAP_PASSPORT_ACTION_TYPE_KEY = "sapcorejco.sap.passport.actionType";
	private static final int DEFAULT_SAP_PASSPORT_ACTION_TYPE = 5;

	private static final String SAP_PASSPORT_PREV_SYSTEM_ID_KEY = "sapcorejco.sap.passport.prevSystemId";
	private static final String DEFAULT_SAP_PASSPORT_PREV_SYSTEM_ID = "Hybris";

	private static final String SAP_PASSPORT_SYSTEM_TYPE_KEY = "sapcorejco.sap.passport.systemType";
	private static final int DEFAULT_SAP_PASSPORT_SYSTEM_TYPE = 39;

	private static final String DEFAULT_SAP_PASSPORT_USER = "";

	private static final int UUID_BYTE_SIZE = 16;

	private ConfigurationService configurationService;

	private SAPRFCDestinationDao rfcDestinationDao;

	private DefaultClientPassportManager(ConfigurationService configurationService, SAPRFCDestinationDao rfcDestinationDao)
	{
		// not instantiable
		super();
		this.configurationService = configurationService;
		this.rfcDestinationDao = rfcDestinationDao;
	}
	/**
	 *
	 * Factory method to create {@link DefaultClientPassportManager}
	 * 
	 * @param tenantService
	 *                             {@link ConfigurationService} to set
	 * @param rfcDestinationDao
	 *                             {@link SAPRFCDestinationDao} to set
	 * @return the {@link DefaultClientPassportManager}
	 */
	public static DefaultClientPassportManager newClientPassportManager(ConfigurationService configurationService,
			SAPRFCDestinationDao rfcDestinationDao)
	{
		return new DefaultClientPassportManager(configurationService, rfcDestinationDao);
	}


	/**
	 * Returns the {@link SAPRFCDestinationDao}.
	 * 
	 * @return SAPRFCDestinationDao
	 */
	public SAPRFCDestinationDao getRfcDestinationDao()
	{
		return rfcDestinationDao;
	}

	/**
	 * Returns the {@link ConfigurationService}.
	 * 
	 * @return ConfigurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * 
	 * @return DSRPassport with configuration values are returned.
	 */
	@Override
	public DsrIPassport callStarted(int clientID, String systemID, String functionName)
	{

		final DefaultSapPassportBuilder builder = DefaultSapPassportBuilder.newSapPassportBuilder();
		final DSRPassport passport = builder.withVersion(getSapPassportVersion())
				.withTraceFlag(getSapPassportTraceFlag())
				.withSystemId(getSapPassportSystemId())
				.withService(getSapPassportServiceValue())
				.withUser(getSapPassportUser(systemID))
				.withAction(functionName)
				.withActionType(getSapPassportActionType())
				.withPrevSystemId(getSapPassportPrevSystemId())
				.withTransId(getUuidAsString())
				.withClientNumber("")
				.withSystemType(getSapPassportSystemType())
				.withRootContextId(getUuidAsBytes())
				.withConnectionId(getUuidAsBytes())
				.withConnectionCounter(0)
				.build();

		DSRPassportJco eppIn = (DSRPassportJco) DsrFactory.makeDsrPassport();
		eppIn.setByNetPassport(passport.getNetPassport());
		return eppIn;

	}

	@Override
	public void callFinished(int clientID, long bytesSent, long bytesReceived)
	{
		// do nothing
	}

	/**
	 * Get Trace flag level from the configuration, if no configuration is found return default value =
	 * {@value #DEFAULT_SAP_PASSPORT_TRACE_FLAG}
	 * 
	 * 
	 * @return the TraceFlag level as a Integer
	 */
	private Integer getSapPassportTraceFlag()
	{
		String traceFlag = getStringProperty(SAP_PASSPORT_TRACE_FLAG_KEY, DEFAULT_SAP_PASSPORT_TRACE_FLAG);

		return Integer.decode(traceFlag);
	}


	/**
	 * 
	 * @param systemID
	 *                    the systemId is used to filter the RFC destinations , if no configuration is found return default
	 *                    value = {@value #DEFAULT_SAP_PASSPORT_USER}
	 * @return UserID configured in the RFC destination.
	 */
	private String getSapPassportUser(String systemID)
	{
		Optional<SAPRFCDestinationModel> rfcDestination = getRfcDestinationDao().findRfcDestinations().stream()
				.filter(rfcDestinationModel -> rfcDestinationModel.getSid().equalsIgnoreCase(systemID)).findFirst();

		return rfcDestination.isPresent() ? rfcDestination.get().getUserid() : DEFAULT_SAP_PASSPORT_USER;

	}

	/**
	 * Get System Type from the configuration, if no configuration is found return default value =
	 * {@value #DEFAULT_SAP_PASSPORT_SYSTEM_TYPE}
	 * 
	 * 
	 * @return the SystemType level as a Integer
	 */
	private Integer getSapPassportSystemType()
	{
		return getIntegerProperty(SAP_PASSPORT_SYSTEM_TYPE_KEY, DEFAULT_SAP_PASSPORT_SYSTEM_TYPE);
	}

	/**
	 * Get Previous SystemId from the configuration, if no configuration is found return default value =
	 * {@value #DEFAULT_SAP_PASSPORT_PREV_SYSTEM_ID}
	 * 
	 * 
	 * @return the PrevSystemId as a String
	 */
	private String getSapPassportPrevSystemId()
	{
		return getStringProperty(SAP_PASSPORT_PREV_SYSTEM_ID_KEY, DEFAULT_SAP_PASSPORT_PREV_SYSTEM_ID);
	}



	/**
	 * Get Action Type from the configuration, if no configuration is found return default value =
	 * {@value #DEFAULT_SAP_PASSPORT_ACTION_TYPE}
	 * 
	 * 
	 * @return the ActionType as a Integer
	 */
	private Integer getSapPassportActionType()
	{
		return getIntegerProperty(SAP_PASSPORT_ACTION_TYPE_KEY, DEFAULT_SAP_PASSPORT_ACTION_TYPE);
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
	 * Get SystemId from the configuration, if no configuration is found return default value =
	 * {@value #DEFAULT_SAP_PASSPORT_SYSTEM_ID}
	 * 
	 * 
	 * @return the SystemId as a String
	 */

	private String getSapPassportSystemId()
	{
		return getStringProperty(SAP_PASSPORT_SYSTEM_ID_PROPERTY_KEY, DEFAULT_SAP_PASSPORT_SYSTEM_ID);
	}

	/**
	 * Get Service Value from the configuration, if no configuration is found return default value =
	 * {@value #DEFAULT_SAP_PASSPORT_SERVICE}
	 * 
	 * 
	 * @return the ServiceValue as a String
	 */
	private int getSapPassportServiceValue()
	{
		return getIntegerProperty(SAP_PASSPORT_SERVICE_PROPERTY_KEY, DEFAULT_SAP_PASSPORT_SERVICE);
	}

	/**
	 *
	 * Get Configuration by property using ConfigurationService, if there is no configuration return defaultValue
	 * 
	 * @param property
	 * @param defaultValue
	 * @return the Configuration as a String
	 */
	private String getStringProperty(final String property, final String defaultValue)
	{
		return getConfigurationService().getConfiguration().getString(property, defaultValue);
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
		return getConfigurationService().getConfiguration().getInt(property, defaultValue);
	}

	/**
	 *
	 * Generate randomUUID
	 * 
	 * @return the randaomUUID as a String
	 */
	private String getUuidAsString()
	{
		return com.sap.jdsr.util.ConvertHelper.byteArrayToHex(getBytesFromUUID(UUID.randomUUID()));
	}

	/**
	 *
	 * Generate randomUUID
	 * 
	 * @return the randaomUUID as a bytess
	 */
	private static byte[] getUuidAsBytes()
	{
		return UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
	}

	private static byte[] getBytesFromUUID(UUID uuid)
	{
		ByteBuffer bBuf = ByteBuffer.wrap(new byte[UUID_BYTE_SIZE]);
		bBuf.putLong(uuid.getMostSignificantBits());
		bBuf.putLong(uuid.getLeastSignificantBits());
		return bBuf.array();
	}


}
