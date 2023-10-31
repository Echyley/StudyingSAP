/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import org.apache.commons.lang.NotImplementedException;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


/**
 * Reads conflicts from SSC and adapts them to our model representation of conflicts
 */
public interface SolvableConflictAdapter
{

	/**
	 * Transfers the conflicts from SSC representation to model representation
	 *
	 * @param configSession
	 * @param configId
	 *           ID of desired configuration in configSession
	 * @param configModel
	 */
	void transferSolvableConflicts(IConfigSession configSession, String configId, ConfigModel configModel);

	/**
	 * Retrieves the assumptionId for a cstic and value which is to be retracted
	 *
	 * @param csticModel
	 * @param configModel
	 * @return Assumption ID
	 */
	default String getAssumptionId(final CsticModel csticModel, final ConfigModel configModel)
	{
		throw new NotImplementedException("method getAssumptionId not implemented by class "+this.getClass().getSimpleName());
	}

}
