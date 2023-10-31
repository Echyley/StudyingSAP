/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.impex;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;

import javax.annotation.Nonnull;

/**
 * Essential data in a separate impex file that is present on the application classpath.
 */
public abstract class EssentialDataFile
{
	protected final String filePath;

	/**
	 * Instantiates this essential data file
	 *
	 * @param path a path to the impex file resource on the application classpath
	 */
	protected EssentialDataFile(@Nonnull final String path)
	{
		filePath = path;
	}

	/**
	 * Loads data present in this essential data file into the system.
	 *
	 * @throws RuntimeException if import of the data failed.
	 */
	public void loadData()
	{
		try
		{
			IntegrationTestUtil.importClasspathImpEx(filePath);
		}
		catch (final ImpExException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deletes all data loaded into the system from this file
	 */
	public abstract void cleanData();

	@Override
	public int hashCode()
	{
		return filePath.hashCode();
	}

	@Override
	public boolean equals(final Object obj)
	{
		final Class<? extends EssentialDataFile> myClass = this.getClass();
		return obj.getClass().equals(myClass) && myClass.cast(obj).filePath.equals(filePath);
	}

	@Override
	public String toString()
	{
		return filePath;
	}
}
