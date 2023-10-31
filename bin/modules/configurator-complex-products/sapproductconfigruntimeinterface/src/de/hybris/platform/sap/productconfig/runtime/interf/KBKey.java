/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf;

import java.io.Serializable;
import java.util.Date;


/**
 * Key to identify a knowledge base (KB) for product configuration. This Object is immutable.
 *
 */
public interface KBKey extends Serializable
{

	/**
	 * @return the product code
	 */
	String getProductCode();

	/**
	 * @return the knowledge base name
	 */
	String getKbName();

	/**
	 * @return the knowledge base logical system
	 */
	String getKbLogsys();

	/**
	 * @return the knowledge base version
	 */
	String getKbVersion();

	/**
	 * @return the date
	 */
	Date getDate();

}