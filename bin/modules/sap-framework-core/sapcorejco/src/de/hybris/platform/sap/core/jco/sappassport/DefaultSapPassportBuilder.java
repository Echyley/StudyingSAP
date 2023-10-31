/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;

import com.google.common.base.Preconditions;
import com.sap.jdsr.passport.DSRPassport;

public class DefaultSapPassportBuilder
{
	private Integer version;
	private Integer traceFlag;
	private String systemId;
	private Integer service;
	private String user;
	private String action;
	private Integer actionType;
	private String prevSystemId;
	private String transId;
	private String clientNumber;
	private Integer systemType;
	private byte[] rootContextId;
	private byte[] connectionId;
	private Integer connectionCounter;

	private DefaultSapPassportBuilder()
	{
		// not instantiable
	}

	public static DefaultSapPassportBuilder newSapPassportBuilder()
	{
		return new DefaultSapPassportBuilder();
	}

	public DefaultSapPassportBuilder withVersion(final Integer version)
	{
		this.version = version;
		return this;
	}

	public DefaultSapPassportBuilder withTraceFlag(final Integer traceFlag)
	{
		this.traceFlag = traceFlag;
		return this;
	}

	public DefaultSapPassportBuilder withSystemId(final String systemId)
	{
		this.systemId = systemId;
		return this;
	}

	public DefaultSapPassportBuilder withService(final Integer service)
	{
		this.service = service;
		return this;
	}

	public DefaultSapPassportBuilder withUser(final String user)
	{
		this.user = user;
		return this;
	}

	public DefaultSapPassportBuilder withAction(final String action)
	{
		this.action = action;
		return this;
	}

	public DefaultSapPassportBuilder withActionType(final Integer actionType)
	{
		this.actionType = actionType;
		return this;
	}

	public DefaultSapPassportBuilder withPrevSystemId(final String prevSystemId)
	{
		this.prevSystemId = prevSystemId;
		return this;
	}

	public DefaultSapPassportBuilder withTransId(final String transId)
	{
		this.transId = transId;
		return this;
	}

	public DefaultSapPassportBuilder withClientNumber(final String clientNumber)
	{
		this.clientNumber = clientNumber;
		return this;
	}

	public DefaultSapPassportBuilder withSystemType(final Integer systemType)
	{
		this.systemType = systemType;
		return this;
	}

	public DefaultSapPassportBuilder withRootContextId(final byte[] rootContextId)
	{
		this.rootContextId = rootContextId;
		return this;
	}

	public DefaultSapPassportBuilder withConnectionId(final byte[] connectionId)
	{
		this.connectionId = connectionId;
		return this;
	}

	public DefaultSapPassportBuilder withConnectionCounter(final Integer connectionCounter)
	{
		this.connectionCounter = connectionCounter;
		return this;
	}

	/**
	 * returns the DSRPassport
	 * 
	 * @return {@link DSRPassport}
	 * 
	 */
	public DSRPassport build()
	{
		Preconditions.checkArgument(version != null, "version cannot be null");
		Preconditions.checkArgument(traceFlag != null, "traceFlag cannot be null");
		Preconditions.checkArgument(systemId != null, "systemId cannot be null");
		Preconditions.checkArgument(service != null, "service cannot be null");
		Preconditions.checkArgument(user != null, "user cannot be null");
		Preconditions.checkArgument(action != null, "action cannot be null");
		Preconditions.checkArgument(actionType != null, "actionType cannot be null");
		Preconditions.checkArgument(prevSystemId != null, "prevSystemId cannot be null");
		Preconditions.checkArgument(transId != null, "transId cannot be null");
		Preconditions.checkArgument(clientNumber != null, "clientNumber cannot be null");
		Preconditions.checkArgument(systemType != null, "systemType cannot be null");
		Preconditions.checkArgument(rootContextId != null, "rootContextId cannot be null");
		Preconditions.checkArgument(connectionId != null, "connectionId cannot be null");
		Preconditions.checkArgument(connectionCounter != null, "connectionCounter cannot be null");

		return new DSRPassport(version, traceFlag, systemId, service, user, action, actionType, prevSystemId, transId, clientNumber,
				systemType, rootContextId, connectionId, connectionCounter);

	}
}
