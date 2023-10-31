/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external;

/**
 * Context attribute
 */
public interface ContextAttribute
{

	/**
	 * Set value
	 * 
	 * @param value
	 */
	void setValue(String value);

	/**
	 * @return Value
	 */
	String getValue();

	/**
	 * Set Name
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * @return Name
	 */
	String getName();

}
