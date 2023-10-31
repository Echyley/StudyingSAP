/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.core.common.message.Message;


/**
 *
 */
@SuppressWarnings("squid:S2160")
public class OrderMgmtMessage extends Message
{
	private String processStep = "";

	/**
	 * @param type
	 */
	public OrderMgmtMessage(final int type)
	{
		super(type);
	}

	/**
	 * @return the processStep
	 */
	public String getProcessStep()
	{
		return processStep;
	}

	/**
	 * @param processStep
	 *           the processStep to set
	 */
	public void setProcessStep(final String processStep)
	{
		this.processStep = processStep;
	}

}
