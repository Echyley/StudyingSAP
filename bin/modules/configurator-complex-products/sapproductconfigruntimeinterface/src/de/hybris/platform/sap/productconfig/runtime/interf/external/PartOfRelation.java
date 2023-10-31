/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.external;

/**
 * Represents instance/ sub instance relation, which is named 'PartOf'
 */
public interface PartOfRelation
{

	/**
	 * Sets author
	 * 
	 * @param author
	 */
	void setAuthor(String author);


	/**
	 * @return Author
	 */
	String getAuthor();

	/**
	 * Sets class type, one typical value is '300'
	 * 
	 * @param classType
	 */
	void setClassType(String classType);

	/**
	 * @return class type
	 */
	String getClassType();

	/**
	 * Sets object key, for material items this is the product ID.
	 * 
	 * @param objectKey
	 */
	void setObjectKey(String objectKey);

	/**
	 * @return Object key.
	 */
	String getObjectKey();

	/**
	 * Sets object type, product or an abstract product representative
	 * 
	 * @param objectType
	 */
	void setObjectType(String objectType);

	/**
	 * @return Object type
	 */
	String getObjectType();

	/**
	 * Sets position in the BOM
	 * 
	 * @param posNr
	 */
	void setPosNr(String posNr);

	/**
	 * @return Position number
	 */
	String getPosNr();

	/**
	 * Sets parent instance ID
	 * 
	 * @param parentInstId
	 */
	void setParentInstId(String parentInstId);

	/**
	 * @return Parent instance ID
	 */
	String getParentInstId();

	/**
	 * Sets child instance ID
	 * 
	 * @param instId
	 */
	void setInstId(String instId);

	/**
	 * @return Child instance ID
	 */
	String getInstId();
}
