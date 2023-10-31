/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.util;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.Logger;


/**
 * Check and Handle the duplicated import of same impex resource.
 */
public final class ImpexDuplicatedImportHandleUtils
{
	private static final Logger LOG = Logger.getLogger(ImpexDuplicatedImportHandleUtils.class);
	/**
	 * map Initial capacity, there are more than 900 imported files are recorded in the map
	 **/
	private static final Integer MAP_INITIAL_CAPACITY = 1024;

	/**
	 * A map used to count the number of times each file is imported
	 **/
	private static final ConcurrentMap<String, Integer> FILE_IMPORT_COUNTER_MAP = new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);


	private ImpexDuplicatedImportHandleUtils()
	{
		throw new IllegalAccessError("Utility class may not be instantiated");
	}

	public static ConcurrentMap<String, Integer> getFileImportCounterMap()
	{
		return FILE_IMPORT_COUNTER_MAP;
	}

	/**
	 * record import info and print duplicated import info
	 * params: the name of imported impex file
	 */
	public static void logDuplicatedImportInfo(String file)
	{
		// if the resource file has been imported, update and print duplicated import info
		if (FILE_IMPORT_COUNTER_MAP.containsKey(file))
		{
			// update the number of times the file is imported
			FILE_IMPORT_COUNTER_MAP.put(file, FILE_IMPORT_COUNTER_MAP.get(file) + 1);

			String resourceName = file;
			if (resourceName.startsWith("/"))
			{
				resourceName = resourceName.substring(1);
			}
			URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
			if (url != null)
			{
				LOG.info(
						"Repeated importing resource file: " + url.getPath() + " , imported times " + FILE_IMPORT_COUNTER_MAP.get(file)
								.toString() + " .");
			}
			else
			{
				LOG.debug("Can not find the resource path of file: " + file + " !");
			}

		}
		// if the resource file is imported the first time, record import info in FILE_IMPORT_COUNTER_MAP
		else
		{
			FILE_IMPORT_COUNTER_MAP.put(file, 1);
		}
	}

}
