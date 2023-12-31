/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.Map;


/**
 * Populator like helper object to map a single characteristic and all child objects, such as domain values, from the
 * product configuration model to the corresponding DAOs and vice versa.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface CsticTypeMapper
{
	/**
	 * Characteristics that are assigned by the system author may not be changed by the front-end user, hence they should
	 * be considered read-only.
	 */
	String READ_ONLY_AUTHOR = "S";


	/**
	 * Maps a single characteristic. Model -> DTO.
	 *
	 * @param configModel
	 *           configuration model as context
	 * @param model
	 *           source - characteristic Model
	 * @param groupName
	 *           name of the group, this characteristic belongs to
	 * @param nameMap
	 *           cache for hybris classification system access
	 * @return target - characteristic DTO
	 */
	CsticData mapCsticModelToData(ConfigModel configModel, CsticModel model, String groupName,
			Map<String, ClassificationSystemCPQAttributesContainer> nameMap);


	/**
	 * Updates a single characteristic. DTO -> Model.
	 *
	 * @param data
	 *           source - characteristic DTO
	 * @param model
	 *           target - characteristic Model
	 */
	void updateCsticModelValuesFromData(CsticData data, CsticModel model);


	/**
	 * Generates a key that identifies this characteristic uniquely within this configuration.
	 *
	 * @param model
	 *           characteristic model
	 * @param groupName
	 *           ui group name the cstic belongs to
	 *
	 * @return unique key
	 */
	String generateUniqueKey(CsticModel model, String groupName);

}
