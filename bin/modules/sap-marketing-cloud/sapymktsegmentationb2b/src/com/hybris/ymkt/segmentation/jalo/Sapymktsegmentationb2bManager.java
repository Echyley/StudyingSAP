/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.segmentation.jalo;

import de.hybris.platform.core.Registry;

import com.hybris.ymkt.segmentation.constants.Sapymktsegmentationb2bConstants;



/**
 * This is the extension manager of the Sapymktsegmentationb2b extension.
 */
public class Sapymktsegmentationb2bManager extends GeneratedSapymktsegmentationb2bManager
{
	/**
	 * Get the valid instance of this manager.
	 * 
	 * @return the current instance of this manager
	 */
	public static Sapymktsegmentationb2bManager getInstance()
	{
		return (Sapymktsegmentationb2bManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
				.getExtension(Sapymktsegmentationb2bConstants.EXTENSIONNAME);
	}

}
