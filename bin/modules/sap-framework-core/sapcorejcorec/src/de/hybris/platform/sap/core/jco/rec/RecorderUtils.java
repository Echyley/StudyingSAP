/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;

import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.sap.core.jco.rec.constants.SapcorejcorecConstants;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;



/**
 * Collection of final string definitions and methods that are used in the JCoRecorder.
 */
public class RecorderUtils
{

	/** The absolute path where to find the validation xsd schema-file. */
	public static final String SCHEMA_FILE_RELATIVE_PATH = "src/de/hybris/platform/sap/core/jco/rec/version100/validation100.xsd";

	/** Separator for function-keys used by the Recorder to identify different executions of the same function. */
	public static final String FUNCTIONKEY_SEPERATOR = "::";
	/**
	 * Separator for record names when retrieving a record from inside a function.<br/>
	 * functionName#parameterType#recordName where parameterType equals INPUT, OUTPUT, CHANGING or TABLES.
	 */
	public static final String RECORDNAME_SEPERATOR = "#";
	
	private RecorderUtils()
	{
		
	}
	/**
	 * Generates the functionKey for the given functionName and the given counter-value.
	 * 
	 * @param name
	 *           of the function.
	 * @param count
	 *           executionCounter of the function.
	 * @return Returns 'name::count'.
	 */
	public static String getFunctionKey(final String name, final int count)
	{
		return name + FUNCTIONKEY_SEPERATOR + count;
	}

	/**
	 * Concatenates the folder of the extension that contains the schema file with the relative path to the schema file.
	 * 
	 * @return Returns the concatenation.
	 */
	public static String getSchemaFilePath()
	{
		final ExtensionInfo extensionInfo = Utilities.getExtensionInfo(SapcorejcorecConstants.EXTENSIONNAME);

		if (extensionInfo == null)
		{
			throw new JCoRecRuntimeException("Could not find extension with extension name " + SapcorejcorecConstants.EXTENSIONNAME
					+ " for recording.");
		}

		try
		{
			String path = extensionInfo.getExtensionDirectory().getCanonicalPath();
			path += File.separator + SCHEMA_FILE_RELATIVE_PATH;
			return path;
		}
		catch (final IOException e)
		{
			throw new JCoRecRuntimeException("Could not determine extension directory due to IOException", e);
		}
	}
}
