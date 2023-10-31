/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test;

import de.hybris.platform.util.Utilities;

import java.io.File;


/**
 * Utility for test files.
 */
public class TestFileUtility
{

	private TestFileUtility() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Reads the file within the requested extension using the file path.
	 * 
	 * @param extensionsName
	 *           extension name
	 * @param extensionFilePath
	 *           file name
	 * @return file
	 */
	public static File getFile(final String extensionsName, final String extensionFilePath)
	{
		final String extensionPath = Utilities.getPlatformConfig().getExtensionInfo("sapcorejco").getExtensionDirectory()
				.getAbsolutePath();
		return new File(extensionPath + File.separator + extensionFilePath);
	}

}
