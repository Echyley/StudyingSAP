/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.property;

import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;


/**
 * Utility class for PropertyAccessTest.
 */
public class PropertyAccessTestUtil
{

	/**
	 * 
	 * @return canonical path of extension directory sapcoretest
	 * @throws IOException
	 *            IOException
	 */
	public static String getCanonicalPathOfExtensionSapCoreTest() throws IOException
	{
		return Utilities.getExtensionInfo("sapcoretest").getExtensionDirectory().getCanonicalPath() + File.separator;
	}
}
