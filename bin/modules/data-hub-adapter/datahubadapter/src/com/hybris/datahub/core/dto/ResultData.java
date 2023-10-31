/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.datahub.core.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Result of manipulating data items. Contains information about how many items were affected by the data change
 * operation.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultData implements Serializable
{
	private int numberProcessed;

	public ResultData()
	{
		// Empty constructor
	}

	public int getNumberProcessed()
	{
		return numberProcessed;
	}

	public void setNumberProcessed(int numberProcessed)
	{
		this.numberProcessed = numberProcessed;
	}
}
