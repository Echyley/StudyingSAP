/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import java.util.List;


/**
 * Represents a conflict accessible to the conflict solver
 */
public interface SolvableConflictModel extends BaseModel
{

	/**
	 * Sets description
	 *
	 * @param description
	 */
	void setDescription(String description);

	/**
	 * @return the description
	 */
	String getDescription();

	/**
	 * @param assumptions
	 */
	void setConflictingAssumptions(List<ConflictingAssumptionModel> assumptions);

	/**
	 * @return the conflictingAssumptions attached to this conflict
	 */
	List<ConflictingAssumptionModel> getConflictingAssumptions();

	/**
	 * @param id
	 *           ID of solvable conflict
	 */
	void setId(String id);

	/**
	 * @return ID of conflict. Is not stable during multiple roundtrips to SSC
	 */
	String getId();

}
