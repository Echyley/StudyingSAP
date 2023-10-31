/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.services;

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;


/**
 * Helper class to generate unique UI keys.<br>
 */
public interface UniqueKeyGenerator
{
	/**
	 * @param instance
	 *           instance data
	 * @return unique key for a UIGroup representing an Instance
	 */
	String generateGroupIdForInstance(final InstanceModel instance);

	/**
	 * @param instance
	 *           instance the group belongs to
	 * @param csticModelGroup
	 *           group data
	 * @return unique key for a UIGroup representing an Group
	 */
	String generateGroupIdForGroup(final InstanceModel instance, final CsticGroup csticModelGroup);

	/**
	 * @param uiGroupId
	 *           UIGroup id
	 * @return extract instance id from the UIGroup Id
	 */
	String retrieveInstanceId(final String uiGroupId);

	/**
	 * @param uiGroupId
	 *           UIGroup id
	 * @return extract group name from the UIGroup Id
	 */
	String retrieveGroupName(final String uiGroupId);

	/**
	 * Generates a Unique Key for a cstic, o a cstic value, if provided.
	 *
	 * @param model
	 *           cstic model
	 * @param value
	 *           cstic value model. If <code>null<code> the key is only specific for the cstic
	 * @param prefix
	 *           key prefix containing group and/or instance components
	 * @return unique key
	 */
	String generateCsticId(final CsticModel model, final CsticValueModel value, final String prefix);

	/**
	 * splits the given cstic UI key into its components
	 *
	 * @param csticUiKey
	 * @return a csticQualifier
	 */
	CsticQualifier splitId(String csticUiKey);

	/**
	 * creates a cstic ui key from the provided components
	 *
	 * @param csticQualifier
	 * @return unique Key
	 */
	String generateId(CsticQualifier csticQualifier);

	/**
	 * @param groupId
	 *           group id
	 * @return extracted instance name from group id
	 */
	String extractInstanceNameFromGroupId(final String groupId);

	/**
	 * @return the instance separator
	 */
	String getInstanceSeparator();

	/**
	 * @return the key separator
	 */
	String getKeySeparator();

	/**
	 * @return the key separator for split statement
	 */
	String getKeySeparatorSplit();

}
