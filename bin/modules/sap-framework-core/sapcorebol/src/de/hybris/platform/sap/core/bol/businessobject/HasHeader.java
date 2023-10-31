/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject;



/**
 * Allows generic access to objects, which has an header object.
 */
public interface HasHeader
{

	/**
	 * Returns the header business object.
	 * 
	 * @return header business object
	 */
	public BusinessObjectBase getHeader();

}
