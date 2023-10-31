/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;

/**
 * Implementation is moved to the runtime interface extension. This class is needed due to the compatibility reasons
 * 
 * @see UniqueKeyGeneratorImpl
 */
public class UniqueUIKeyGeneratorImpl implements UniqueUIKeyGenerator
{

	private UniqueKeyGenerator keyGenerator;

	@Override
	public String generateGroupIdForInstance(final InstanceModel instance)
	{
		return keyGenerator.generateGroupIdForInstance(instance);
	}

	@Override
	public String generateGroupIdForGroup(final InstanceModel instance, final CsticGroup csticModelGroup)
	{
		return keyGenerator.generateGroupIdForGroup(instance, csticModelGroup);
	}

	@Override
	public String retrieveInstanceId(final String uiGroupId)
	{
		return keyGenerator.retrieveInstanceId(uiGroupId);
	}

	@Override
	public String retrieveGroupName(final String uiGroupId)
	{
		return keyGenerator.retrieveGroupName(uiGroupId);
	}

	@Override
	public String generateCsticId(final CsticModel model, final CsticValueModel value, final String prefix)
	{
		return keyGenerator.generateCsticId(model, value, prefix);
	}

	@Override
	public CsticQualifier splitId(final String csticUiKey)
	{
		return keyGenerator.splitId(csticUiKey);
	}

	@Override
	public String generateId(final CsticQualifier csticQualifier)
	{
		return keyGenerator.generateId(csticQualifier);
	}

	@Override
	public String extractInstanceNameFromGroupId(final String groupId)
	{
		return keyGenerator.extractInstanceNameFromGroupId(groupId);
	}

	@Override
	public String getInstanceSeparator()
	{
		return keyGenerator.getInstanceSeparator();
	}

	@Override
	public String getKeySeparator()
	{
		return keyGenerator.getKeySeparator();
	}

	@Override
	public String getKeySeparatorSplit()
	{
		return keyGenerator.getKeySeparatorSplit();
	}

	protected UniqueKeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	public void setKeyGenerator(final UniqueKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

}
