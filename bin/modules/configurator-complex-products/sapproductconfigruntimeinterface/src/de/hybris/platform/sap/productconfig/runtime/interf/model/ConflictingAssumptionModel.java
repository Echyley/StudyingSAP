/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model;

/**
 * Conflicting assumption (part of a {@link SolvableConflictModel} which can be retracted
 */
public interface ConflictingAssumptionModel extends BaseModel
{

	/**
	 * @param csticName
	 */
	void setCsticName(String csticName);

	/**
	 * @return Language independent name of cstic which causes the conflicting assumption
	 */
	String getCsticName();

	/**
	 * @param valueName
	 */
	void setValueName(String valueName);

	/**
	 * @return Language independent name of value which causes the conflicting assumption
	 */
	String getValueName();

	/**
	 * @param instanceId
	 */
	void setInstanceId(String instanceId);

	/**
	 * @return the instanceId
	 */
	String getInstanceId();

	/**
	 * @param assumptionId
	 */
	void setId(String assumptionId);

	/**
	 * @return Assumption ID
	 */
	String getId();

}
